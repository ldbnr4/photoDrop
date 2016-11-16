package money.cache.grex;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;

import enums.RET_STATUS;

import static enums.RET_STATUS.NONE;
import static enums.RET_STATUS.NO_ACCOUNT;
import static money.cache.grex.GrexSocket.loggedInStatus;
import static money.cache.grex.GrexSocket.login_emit;

/**
 * Created by Lorenzo on 11/15/2016.
 *
 */

class UserLoginTask extends AsyncTask<Void, Void, RET_STATUS> {

    private final LoginActivity callingActivity;
    private String mPassword;
    private String mEmail;
    private EditText _passwordText;
    private ProgressDialog progressDialog;
    private EditText _emailText;


    UserLoginTask(String email, String password, final LoginActivity callingActivity, EditText emailText, EditText passwordText) {
        this.mEmail = email;
        this.mPassword = password;
        this.callingActivity = callingActivity;
        this._passwordText = passwordText;
        this._emailText = emailText;

        progressDialog = new ProgressDialog(callingActivity,
                R.style.AppTheme_Dark_Dialog);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                progressDialog.dismiss();
                callingActivity.onLoginFailed();
            }
        });
    }

    @Override
    protected RET_STATUS doInBackground(Void... params) {
        login_emit(mEmail, mPassword);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return loggedInStatus;


    }

    @Override
    protected void onPostExecute(final RET_STATUS success) {
        progressDialog.dismiss();
        switch (success){
            case VERIFIED :
                this.callingActivity.finish();
                break;

            case WRONG_PASSWORD :
                _passwordText.setError("Wrong Password");
            case NO_ACCOUNT :
                if(success == NO_ACCOUNT)
                    _emailText.setError("No account found for this user.");
            default:
                callingActivity.onLoginFailed();

        }
    }

    @Override
    protected void onCancelled() {
        progressDialog.dismiss();
        this.callingActivity.onLoginFailed();
    }

}

