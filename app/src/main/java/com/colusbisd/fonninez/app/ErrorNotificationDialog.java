package com.colusbisd.fonninez.app;

import android.app.Activity;
import android.app.ActivityManager;
import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.colusbisd.fonninez.R;

import java.util.List;

/** This is an activity which is presented as a dialog for presenting
 * error notifications to the user. The notifications can have a short title, a detailed
 * message describing the error and its consequences. Finally, notifications have a so-called fatal
 * flag. It it were true, then the application is killed, after the user pressed the OK button.
 */
public class ErrorNotificationDialog extends Activity {

    public static final String TITLE = "error_title";
    public static final String MSG = "error_msg";
    public static final String FATAL = "isFatal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent startIntent = getIntent();
        String title = startIntent.getStringExtra(TITLE);
        String msg = startIntent.getStringExtra(MSG);
        boolean isFatal = startIntent.getBooleanExtra(FATAL, false);
        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(R.string.ok, null)
                .setOnDismissListener(dialog -> {
                    onDismissed(isFatal);
                }).show();
    }

    private void onDismissed(boolean isFatal) {
        if (isFatal) {
            ActivityManager activityManager = (ActivityManager) ErrorNotificationDialog.this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.AppTask> tasks = activityManager.getAppTasks();
            for (ActivityManager.AppTask task : tasks) {
                task.finishAndRemoveTask();
            }
        } else {
            ErrorNotificationDialog.this.finish();
           //disable the disturbing animation when the dialog is dismissed
           ErrorNotificationDialog.this.overridePendingTransition(0,0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ErrorPresenterByNotification.errorDialogDismissed();
    }
}
