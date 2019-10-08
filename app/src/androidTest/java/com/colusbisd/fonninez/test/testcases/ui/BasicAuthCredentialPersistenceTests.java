package com.colusbisd.fonninez.test.testcases.ui;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.colusbisd.fonninez.app.SAPWizardApplication;
import com.colusbisd.fonninez.logon.BasicAuthPersistentCredentialStore;
import com.colusbisd.fonninez.logon.LogonActivity;
import com.colusbisd.fonninez.test.core.BaseTest;
import com.colusbisd.fonninez.test.core.Constants;
import com.colusbisd.fonninez.test.core.Utils;
import com.colusbisd.fonninez.test.core.factory.LoginPageFactory;
import com.sap.cloud.mobile.foundation.common.ClientProvider;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static com.colusbisd.fonninez.test.core.Constants.APPLICATION_AUTH_TYPE;
import static com.colusbisd.fonninez.test.core.Constants.NETWORK_REQUEST_TIMEOUT;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class BasicAuthCredentialPersistenceTests extends BaseTest {

    @Rule
    public ActivityTestRule<LogonActivity> activityTestRule = new ActivityTestRule<>(LogonActivity.class);

    @Test
    public void basicAuthCredentialsGetReused() throws InterruptedException {
        if (APPLICATION_AUTH_TYPE != Constants.AuthType.BASIC) {
            return;
        }
        Utils.doOnboarding();

        // Clear session cookies so the server will give a basic auth challenge
        Utils.clearSessionCookies();

        // Make another request to the server, basic auth credentials should get reused.
        final boolean[] requestSucceeded = {false};
        CountDownLatch responseLatch = makeRequest(requestSucceeded);

        responseLatch.await(NETWORK_REQUEST_TIMEOUT, TimeUnit.MILLISECONDS);
        synchronized (responseLatch) {
            assertTrue("request didn't succeed after reentering basic auth credentials.", requestSucceeded[0]);
        }
    }

    @Test
    public void basicAuthCredentialsGetCleared() throws InterruptedException {
        if (APPLICATION_AUTH_TYPE != Constants.AuthType.BASIC) {
            return;
        }
        Utils.doOnboarding();

        // Clear session cookies so the server will give a basic auth challenge
        Utils.clearSessionCookies();

        // Clear basic auth credentials as well
        ((SAPWizardApplication)activityTestRule.getActivity().getApplication())
                .getBasicAuthPersistentCredentialStore().deleteAllCredentials();

        // Make another request to the server
        final boolean[] requestSucceeded = {false};
        CountDownLatch responseLatch = makeRequest(requestSucceeded);

        // Expect the basic auth dialog to show again.
        LoginPageFactory.getLoginPage().authenticate();

        responseLatch.await(NETWORK_REQUEST_TIMEOUT, TimeUnit.MILLISECONDS);
        synchronized (responseLatch) {
            assertTrue("request didn't succeed after reentering basic auth credentials.", requestSucceeded[0]);
        }
    }

    CountDownLatch makeRequest(final boolean[] responseResult) {
        Request request = new Request.Builder()
                .get()
                .url(((SAPWizardApplication)activityTestRule.getActivity().getApplication()).getSettingsParameters().getBackendUrl())
                .build();

        CountDownLatch responseLatch = new CountDownLatch(1);
        ClientProvider.get().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                responseLatch.countDown();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                synchronized (responseLatch) {
                    responseResult[0] = true;
                }
                responseLatch.countDown();
            }
        });
        return responseLatch;
    }
}
