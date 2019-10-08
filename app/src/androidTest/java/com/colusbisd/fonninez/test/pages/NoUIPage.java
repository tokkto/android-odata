package com.colusbisd.fonninez.test.pages;

import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;

import com.colusbisd.fonninez.test.core.Constants;
import com.colusbisd.fonninez.test.core.UIElements;

import org.junit.Assert;

public class NoUIPage {

    public NoUIPage() {
        // Check whether the page appeared or not
        // We use uiautomator since the resource is not generated every case
        UiObject helloWorldText = new UiObject(new UiSelector().text(UIElements.NoUIScreen.helloWorldTextID));
        boolean textExists = helloWorldText.waitForExists(Constants.NETWORK_REQUEST_TIMEOUT);

        // Fail the test if the textview is not visible
        Assert.assertTrue(textExists);
    }
}
