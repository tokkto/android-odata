package com.colusbisd.fonninez.test.pages;

import com.pgssoft.espressodoppio.idlingresources.ViewIdlingResource;
import com.colusbisd.fonninez.test.core.Credentials;
import com.colusbisd.fonninez.test.core.UIElements;
import com.colusbisd.fonninez.test.core.WizardDevice;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class ActivationPage {

    public ActivationPage() {
        ViewIdlingResource viewIdlingResource = (ViewIdlingResource) new ViewIdlingResource(
                withId(UIElements.ActivationPage.startButton)).register();
    }

    public void enterEmailAddress() {
        WizardDevice.fillInputField(UIElements.ActivationPage.emailText, Credentials.EMAIL_ADDRESS);
    }

    public void clickStart() {
        onView(withId(UIElements.ActivationPage.startButton)).perform(closeSoftKeyboard(), click());
    }
}
