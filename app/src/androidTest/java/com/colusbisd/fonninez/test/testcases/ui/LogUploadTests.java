package com.colusbisd.fonninez.test.testcases.ui;

import androidx.test.rule.ActivityTestRule;
import static androidx.test.InstrumentationRegistry.getInstrumentation;

import com.colusbisd.fonninez.app.SAPWizardApplication;
import com.colusbisd.fonninez.logon.LogonActivity;
import com.colusbisd.fonninez.test.core.BaseTest;
import com.colusbisd.fonninez.test.core.Credentials;
import com.colusbisd.fonninez.test.core.Utils;
import com.colusbisd.fonninez.test.core.WizardDevice;
import com.colusbisd.fonninez.test.pages.DetailPage;
import com.colusbisd.fonninez.test.pages.EntityListPage;
import com.colusbisd.fonninez.test.pages.MasterPage;
import com.colusbisd.fonninez.test.pages.PasscodePage;
import com.colusbisd.fonninez.test.pages.SettingsListPage;

import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUploadTests extends BaseTest {

    @Rule
    public ActivityTestRule<LogonActivity> activityTestRule = new ActivityTestRule<>(LogonActivity.class);

    @Test
    public void testLogUpload() {
        // This test just tests whether the buttons works as expected
        // no crash and the toast appears or not
        Utils.doOnboarding();

        EntityListPage entityListPage = new EntityListPage();
        entityListPage.clickFirstElement();

        MasterPage masterPage = new MasterPage();
        masterPage.clickFirstElement();

        DetailPage detailPage = new DetailPage();
        detailPage.clickBack();

        masterPage = new MasterPage();
        masterPage.clickBack();

        entityListPage = new EntityListPage();
        entityListPage.clickSettings();
        SettingsListPage settingsListPage = new SettingsListPage();
        setUpLogs();
        settingsListPage.clickUploadLog();
        settingsListPage.checkLogUploadToast();
    }


    @Test
    public void testLogUploadBackgroundLocked() {

        Utils.doOnboarding();

        EntityListPage entityListPage = new EntityListPage();
        entityListPage.clickFirstElement();

        MasterPage masterPage = new MasterPage();
        masterPage.clickFirstElement();

        DetailPage detailPage = new DetailPage();
        detailPage.clickBack();

        masterPage = new MasterPage();
        masterPage.clickBack();
        masterPage.leavePage();

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

        // Logupload flow
        entityListPage = new EntityListPage();
        entityListPage.clickFirstElement();

        masterPage = new MasterPage();
        masterPage.clickBack();

        entityListPage = new EntityListPage();
        entityListPage.clickSettings();
        SettingsListPage settingsListPage = new SettingsListPage();
        setUpLogs();
        settingsListPage.clickUploadLog();
        settingsListPage.checkLogUploadToast();
    }

    private void setUpLogs() {
        Logger LOGGER = LoggerFactory.getLogger(LogonActivity.class);
        LOGGER.error("first error message");
        LOGGER.error("second error message");
        LOGGER.error("third error message");
    }

}
