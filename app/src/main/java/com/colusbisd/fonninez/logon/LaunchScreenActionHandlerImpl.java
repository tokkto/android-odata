package com.colusbisd.fonninez.logon;

import androidx.fragment.app.Fragment;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import androidx.annotation.NonNull;
import android.widget.Toast;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sap.cloud.mobile.foundation.logging.Logging;
import ch.qos.logback.classic.Level;

import ch.qos.logback.classic.Level;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.sap.cloud.mobile.foundation.common.ClientProvider;
import com.sap.cloud.mobile.foundation.configurationprovider.ConfigurationLoader;
import com.sap.cloud.mobile.foundation.configurationprovider.ConfigurationLoaderCallback;
import com.sap.cloud.mobile.foundation.configurationprovider.ConfigurationProviderError;
import com.sap.cloud.mobile.foundation.configurationprovider.DiscoveryServiceConfigurationProvider;
import com.sap.cloud.mobile.foundation.configurationprovider.ProviderIdentifier;
import com.sap.cloud.mobile.foundation.configurationprovider.ProviderInputs;
import com.sap.cloud.mobile.foundation.configurationprovider.UserInputs;
import com.sap.cloud.mobile.onboarding.activation.ActivationActivity;
import com.sap.cloud.mobile.onboarding.activation.ActivationSettings;
import com.sap.cloud.mobile.onboarding.launchscreen.WelcomeScreenActionHandlerImpl;
import com.sap.cloud.mobile.onboarding.utility.ActivityResultActionHandler;
import com.sap.cloud.mobile.onboarding.utility.OnboardingType;
import java.util.concurrent.CountDownLatch;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import com.colusbisd.fonninez.app.ErrorMessage;
import com.colusbisd.fonninez.app.SAPWizardApplication;
import static com.colusbisd.fonninez.logon.ActivationActionHandlerImpl.DISCOVERY_SVC_EMAIL;
import com.colusbisd.fonninez.R;
import com.colusbisd.fonninez.app.ConfigurationData;
import com.colusbisd.fonninez.app.ErrorHandler;
import com.colusbisd.fonninez.service.SAPServiceManager;

public class LaunchScreenActionHandlerImpl extends WelcomeScreenActionHandlerImpl implements ActivityResultActionHandler {

    private static final String DEFAULT_SECURESTORE_PASSCODE = "defaultpasscode";

	private static final Logger LOGGER = LoggerFactory.getLogger(LaunchScreenActionHandlerImpl.class);
    private enum ConfigurationState {
        Initializing,
        NeedsInput,
        FailedConfiguration,
        SuccessfulConfiguration
    }

    private static final int INPUT_REQUEST = 12345;
    private ConfigurationState configurationState = ConfigurationState.Initializing;
    private ConfigurationLoader configurationLoader;
    private CountDownLatch configLatch = new CountDownLatch(1);
    private OnboardingType configPromptType = null;

    @Override
    public void startStandardOnboarding(final Fragment fragment) {

        Activity activity = fragment.getActivity();
        ConfigurationData configurationData = getSAPWizardApplication(fragment).getConfigurationData();

        if (configurationData.isLoaded()) {

            configurationState = ConfigurationState.SuccessfulConfiguration;
        } else {
            // Start Configuration Loader Threads
            startConfigurationLoader(fragment);
        }

        // Allow the Configuration Loader to Succeed or Fail, Supplying Input When Necessary
        while (configurationState != ConfigurationState.FailedConfiguration
                && configurationState != ConfigurationState.SuccessfulConfiguration) {
            // Wait for the configuration loader to do something
            try {
                configLatch.await();
            } catch (InterruptedException e) {
                if (configurationState == ConfigurationState.NeedsInput) {
                    // Tell the Configuration Loader to Fail
                    configurationLoader.processRequestedInputs(new UserInputs());
                }
                // Drop out of here
                configurationState = ConfigurationState.FailedConfiguration;
                // Reestablish the interrupted state
                Thread.currentThread().interrupt();
            }
            // re-arm the latch for the next iteration
            configLatch = new CountDownLatch(1);

            if (configurationState == ConfigurationState.NeedsInput) {
                // show activation screen
                promptForProviderInput(activity);
            }
        }

        // Successfully Acquired Configuration Information
        // Failures will Fall Through Resulting in Re-invoking this Callback
        if (configurationState == ConfigurationState.SuccessfulConfiguration) {
            if (authenticateWithServer(fragment)) {
                // Successfully OnBoarded with Server - Leave The Launch Screen Activity
                activity.setResult(RESULT_OK);
                activity.finish();
            }
        }

    }
    

    private boolean authenticateWithServer(Fragment fragment) {
        boolean success = false;
        SAPWizardApplication sapWizardApplication = getSAPWizardApplication(fragment);
        ConfigurationData configurationData = sapWizardApplication.getConfigurationData();
        Activity activity = fragment.getActivity();

        String serviceUrl = configurationData.getServiceUrl();

 

		OkHttpClient okHttpClient = ClientProvider.get();

		Request request = new Request.Builder()
				.get()
                 .url(serviceUrl + SAPServiceManager.CONNECTION_ID_FONINEZ)

				.build();

		try (Response response = okHttpClient.newCall(request).execute()) {
			if (response.isSuccessful() && !isSamlChallenge(response)) {
				// Initialize logging
				Logging.initialize(activity.getApplicationContext(), new Logging.ConfigurationBuilder().initialLevel(Level.WARN).logToConsole(true).build());
                success = true;
			} else {
                if (response.code() == 401 || response.code() == 403) {
                    //clear credentials to allow the user to enter new ones
                    sapWizardApplication.getBasicAuthPersistentCredentialStore().deleteAllCredentials();
                }
                activity.runOnUiThread(() -> {
                    if( isSamlChallenge(response) ) {
                        Toast.makeText(activity.getApplicationContext(), R.string.server_auth_not_supported_saml, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.error) + ": " + response.message(), Toast.LENGTH_LONG).show();
                    }
				});
			}
             
 
 
        } catch (IOException e) {
            sapWizardApplication.setIsOnboarded(false);

            activity.runOnUiThread(() -> {
                Toast.makeText(activity.getApplicationContext(),
                        R.string.authentication_failed, Toast.LENGTH_LONG).show();
            });
        }
         
        return success;
    }

    private void startConfigurationLoader(Fragment fragment) {
        Activity activity = fragment.getActivity();
        SAPWizardApplication sapWizardApplication = getSAPWizardApplication(fragment);
        ConfigurationData configurationData = sapWizardApplication.getConfigurationData();
        ErrorHandler errorHandler = sapWizardApplication.getErrorHandler();

        activity.runOnUiThread(() -> {
            configurationLoader = new ConfigurationLoader(activity.getApplicationContext(),
                                                        SAPWizardApplication.APPLICATION_ID,
                                                        new ConfigurationLoaderCallback() {

                @Override
                public void onCompletion(ProviderIdentifier providerId, boolean success) {

                    if (success) {
                        if(configurationData.loadData()) {
                            configurationState = ConfigurationState.SuccessfulConfiguration;
                        } else {
                            configurationState = ConfigurationState.FailedConfiguration;
                        }

                    } else {
                        configurationState = ConfigurationState.FailedConfiguration;
                        Resources resources = sapWizardApplication.getResources();
                        errorHandler.sendErrorMessage(
                                new ErrorMessage(
                                        resources.getString(
                                                R.string.config_loader_complete_error_title),
                                        resources.getString(
                                                R.string.config_loader_complete_error_description)
                                ));
                    }

                    configLatch.countDown();
                }

                @Override
                public void onError(ConfigurationLoader configurationLoader,
                                    ProviderIdentifier providerId,
                                    UserInputs requestedInput,
                                    ConfigurationProviderError error) {
                    // Display the error
                    Resources resources = sapWizardApplication.getResources();
                    errorHandler.sendErrorMessage(new ErrorMessage(
                            resources.getString(R.string.config_loader_on_error_title),
                            String.format(resources.getString(
                                    R.string.config_loader_on_error_description),
                                    providerId.toString(), error.getErrorMessage()
                            )
                    ));

                    if (providerId == ProviderIdentifier.DISCOVERY_SERVICE_CONFIGURATION_PROVIDER) {
                        // Was a supported input required provider - prompt again
                        configurationState = ConfigurationState.NeedsInput;
                        configLatch.countDown();
                    } else {
                        // Not a Supported input required provider - Supply empty input to fail
                        configurationLoader.processRequestedInputs(new UserInputs());
                    }
                }

                @Override
                public void onInputRequired(ConfigurationLoader configurationLoader,
                                            UserInputs requestedInput) {
                    if(!determineProviderInputTypes(requestedInput)) {
                        // No Supported input required providers found
                        // Supply empty input to fail
                        configurationLoader.processRequestedInputs(new UserInputs());
                    } else {
                        // Found supported input required providers - prompt for input
                        configurationState = ConfigurationState.NeedsInput;
                        configLatch.countDown();
                    }
                }

                private boolean determineProviderInputTypes(UserInputs requestedInput) {
                    boolean success = false;

                    // For now the only supported input required provider is Discovery Service
                    // So just check for its presence in the list
                    if (requestedInput.containsKey(ProviderIdentifier.DISCOVERY_SERVICE_CONFIGURATION_PROVIDER)) {
                        configPromptType = OnboardingType.DISCOVERY_SERVICE_ONBOARDING;
                        success = true;
                    }

                    return success;
                }
            });
            configurationLoader.loadConfiguration();
        });

    }
    private void promptForProviderInput(Activity activity) {
        // ActivationActivity
        Intent inputRequest = new Intent(activity, ActivationActivity.class);
        ActivationSettings activationSettings = new ActivationSettings();
        activationSettings.setActivationType(configPromptType);
        activationSettings.setActionHandler("com.colusbisd.fonninez.logon.ActivationActionHandlerImpl");
        activationSettings.setActivationTitle(activity.getString(R.string.application_name));
        activationSettings.saveToIntent(inputRequest);
        activity.startActivityForResult(inputRequest, INPUT_REQUEST);
    }

    @Override
    public void startDemoMode(final Fragment fragment) {
        fragment.getActivity().runOnUiThread(() -> Toast.makeText(fragment.getActivity().getApplicationContext(), "Demo mode onboarding", Toast.LENGTH_LONG).show());
    }

    @Override
    public boolean onActivityResult(Fragment fragment, int reqCode, int resCode, Intent data) {
        if (reqCode == INPUT_REQUEST) {
            switch (resCode) {
                case RESULT_OK:
                    UserInputs inputs = new UserInputs();
                    if (data.hasExtra(DISCOVERY_SVC_EMAIL)) {
                        ProviderInputs providerInputs = new ProviderInputs();
                        providerInputs.addInput(DiscoveryServiceConfigurationProvider.EMAIL_ADDRESS,
                                data.getStringExtra(DISCOVERY_SVC_EMAIL));
                        inputs.addProvider(ProviderIdentifier.DISCOVERY_SERVICE_CONFIGURATION_PROVIDER,
                                providerInputs);
                    }
                    configurationLoader.processRequestedInputs(inputs);
                    break;
                case RESULT_CANCELED:
                    configurationLoader.processRequestedInputs(new UserInputs());
                    break;
            }
        }
        return false;
    }
    // In case of SAML, the response is successfull
    // but header key 'the com.sap.cloud.security.login' is 'login-request'
    private boolean isSamlChallenge(@NonNull Response response) {
        String headerValue = response.header("com.sap.cloud.security.login");
        return headerValue != null && headerValue.equals("login-request");
    }

    private SAPWizardApplication getSAPWizardApplication(Fragment fragment) {
        return (SAPWizardApplication) fragment.getActivity().getApplication();
    }
}
