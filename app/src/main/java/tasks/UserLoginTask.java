package tasks;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.EditText;

import enums.RET_STATUS;
import money.cache.grex.LoginActivity;
import money.cache.grex.R;

import static enums.RET_STATUS.NONE;
import static enums.RET_STATUS.NO_ACCOUNT;
import static money.cache.grex.GrexSocket.loggedInStatus;
import static money.cache.grex.GrexSocket.login_emit;

/**
 * Created by Lorenzo on 11/15/2016.
 *
 */

public class UserLoginTask extends AsyncTask<Void, Void, RET_STATUS> {

    private final LoginActivity callingActivity;
    private String mPassword;
    private String mEmail;
    private EditText _passwordText;
    private ProgressDialog progressDialog;
    private EditText _emailText;


    public UserLoginTask(String email, String password, final LoginActivity callingActivity, EditText emailText, EditText passwordText) {
        this.mEmail = email;
        this.mPassword = password;
        this.callingActivity = callingActivity;
        this._passwordText = passwordText;
        this._emailText = emailText;
    }

    @Override
    protected RET_STATUS doInBackground(Void... params) {
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
                        callingActivity.onLoginFailed();
                        UserLoginTask.this.cancel(true);
                    }
                });
                progressDialog.show();
            }
        });

        login_emit(mEmail, mPassword);

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
                callingActivity.onLoginSuccess();
                break;

            case WRONG_PASSWORD:
                _passwordText.setError("Wrong Password");
            case NO_ACCOUNT:
                if (success == NO_ACCOUNT)
                    _emailText.setError("No account found for this user.");
            default:
                callingActivity.onLoginFailed();
                break;
        }
        loggedInStatus = NONE;
    }

    @Override
    protected void onCancelled() {
        progressDialog.dismiss();
        callingActivity.onLoginFailed();
        loggedInStatus = NONE;
    }

}

