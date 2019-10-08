package com.colusbisd.fonninez.test.testcases.ui;

import androidx.test.rule.ActivityTestRule;
import static androidx.test.InstrumentationRegistry.getInstrumentation;

import com.colusbisd.fonninez.app.SAPWizardApplication;
import com.colusbisd.fonninez.logon.LogonActivity;
import com.colusbisd.fonninez.test.core.BaseTest;
import com.colusbisd.fonninez.test.core.Constants;
import com.colusbisd.fonninez.test.core.Credentials;
import com.colusbisd.fonninez.test.core.Utils;
import com.colusbisd.fonninez.test.core.WizardDevice;
import com.colusbisd.fonninez.test.pages.DetailPage;
import com.colusbisd.fonninez.test.pages.EntityListPage;
import com.colusbisd.fonninez.test.pages.MasterPage;
import com.colusbisd.fonninez.test.pages.PasscodePage;
import com.colusbisd.fonninez.test.pages.SettingsListPage;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class UsageUploadTest extends BaseTest {

    @Rule
    public ActivityTestRule<LogonActivity> activityTestRule = new ActivityTestRule<>(LogonActivity.class);

    /**
     * Basic usageupload flow
     */
    @Test
    public void testUsageUploadBackgroundLocked() {
        Constants.USAGE_CONSENT = Constants.UsageConsent.ALLOW;
        // This test just tests whether the buttons works as expected
        // no crash and the toast appears or not
        Utils.doOnboarding();

        EntityListPage entityListPage = new EntityListPage();
        entityListPage.clickFirstElement();
        entityListPage.leavePage();

        MasterPage masterPage = new MasterPage();
        masterPage.clickFirstElement();
        masterPage.leavePage();

        DetailPage detailPage = new DetailPage();
        detailPage.clickBack();
        detailPage.leavePage();

        masterPage = new MasterPage();
        masterPage.clickBack();
        masterPage.leavePage();

        entityListPage = new EntityListPage();
        entityListPage.clickSettings();
        entityListPage.leavePage();

        SettingsListPage settingsListPage = new SettingsListPage();

        // Put the application into background and wait until the app is locked
        int lockTimeOut = ((SAPWizardApplication) getInstrumentation().getTargetContext().getApplicationContext())
                .getSecureStoreManager().getPasscodeLockTimeout();
        WizardDevice.putApplicationBackground((lockTimeOut + 1) * 1000, activityTestRule);
        // Reopen app
        WizardDevice.reopenApplication();

        PasscodePage.EnterPasscodePage enterPasscodePage = new PasscodePage().new EnterPasscodePage();
        enterPasscodePage.enterPasscode(Credentials.PASSCODE);
        enterPasscodePage.clickSignIn();
        enterPasscodePage.leavePage();

        settingsListPage.clickUploadUsage();
        settingsListPage.checkUsageUploadToast();
    }

    @Before
    public void usageSetUp() {
    }

    @After
    public void usageTearDown() {
        Constants.USAGE_CONSENT = Constants.UsageConsent.DENY;
    }

}
