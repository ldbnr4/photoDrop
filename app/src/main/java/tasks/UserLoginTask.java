package tasks;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.EditText;

import grexClasses.ProgressBarActvity;
import grexEnums.RET_STATUS;
import money.cache.grexActivities.R;

import static grexClasses.GrexSocket.emit_login;
import static grexClasses.GrexSocket.loggedInStatus;
import static grexEnums.RET_STATUS.NONE;
import static grexEnums.RET_STATUS.NO_ACCOUNT;

/**
 * Created by Lorenzo on 11/15/2016.
 */

public class UserLoginTask extends AsyncTask<Void, Void, RET_STATUS> {

    private final ProgressBarActvity callingActivity;
    private String mPassword;
    private String mUsername;
    private EditText _passwordText;
    private ProgressDialog progressDialog;
    private EditText _emailText;


    public UserLoginTask(String username, String password, final ProgressBarActvity callingActivity, EditText emailText, EditText passwordText) {
        this.mUsername = username;
        this.mPassword = password;
        this.callingActivity = callingActivity;
        this._passwordText = passwordText;
        this._emailText = emailText;
    }

    public UserLoginTask(String username, String password, final ProgressBarActvity callingActivity) {
        this.mUsername = username;
        this.mPassword = password;
        this.callingActivity = callingActivity;
    }

    @Override
    protected RET_STATUS doInBackground(Void... params) {

        if (callingActivity != null) {
            callingActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog = new ProgressDialog(callingActivity,
                            R.style.AppTheme_Dark_Dialog);

                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Authenticating...");
                    progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            progressDialog.dismiss();
                            callingActivity.onFail();
                            UserLoginTask.this.cancel(true);
                        }
                    });
                    progressDialog.show();
                }
            });
        }

        emit_login(mUsername, mPassword);

        long totalTime = 3000;
        long startTime = System.currentTimeMillis();
        boolean toFinish = false;

        while (!toFinish && loggedInStatus == NONE) {
            toFinish = (System.currentTimeMillis() - startTime >= totalTime);
        }
        return loggedInStatus;
    }

    @Override
    protected void onPostExecute(final RET_STATUS success) {
        progressDialog.dismiss();
        switch (success) {
            case VERIFIED:
                if (callingActivity != null)
                    callingActivity.onSuccess();
                break;

            case WRONG_PASSWORD:
                _passwordText.setError("Wrong Password");
            case NO_ACCOUNT:
                if (success == NO_ACCOUNT)
                    _emailText.setError("No account found for this mUser.");
            default:
                callingActivity.onFail();
                break;
        }
        loggedInStatus = NONE;
    }

    @Override
    protected void onCancelled() {
        progressDialog.dismiss();
        callingActivity.onFail();
        loggedInStatus = NONE;
    }

}

