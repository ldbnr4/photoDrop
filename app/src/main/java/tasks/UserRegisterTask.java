package tasks;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import enums.RET_STATUS;
import money.cache.grex.R;
import money.cache.grex.SignupActivity;

import static enums.RET_STATUS.NONE;
import static money.cache.grex.GrexSocket.register_emit;
import static money.cache.grex.GrexSocket.signUpStatus;

/**
 * Created by Lorenzo on 11/20/2016.
 *
 */

public class UserRegisterTask extends AsyncTask<Void, Void, RET_STATUS> {
    private final String username;
    private final String email;
    private final String mobile;
    private final String password;
    private SignupActivity signupActivity;
    private ProgressDialog progressDialog;

    public UserRegisterTask(String username, String email, String mobile, String password, final SignupActivity signupActivity) {
        this.username = username;
        this.email = email;
        this.mobile = mobile;
        this.password = password;
        this.signupActivity = signupActivity;
    }

    @Override
    protected RET_STATUS doInBackground(Void... params) {
        signupActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(signupActivity,
                        R.style.AppTheme_Dark_Dialog);

                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Registering...");
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        progressDialog.dismiss();
                        signupActivity.onSignupFailed();
                        signUpStatus = NONE;
                        UserRegisterTask.this.cancel(true);
                    }
                });
                progressDialog.show();
            }
        });

        register_emit(username, email, mobile, password);

        long totalTime = 3000;
        long startTime = System.currentTimeMillis();
        boolean toFinish = false;

        while (!toFinish && signUpStatus == NONE) {
            toFinish = (System.currentTimeMillis() - startTime >= totalTime);
        }
        return signUpStatus;
    }

    @Override
    protected void onPostExecute(final RET_STATUS success) {
        progressDialog.dismiss();
        switch (success) {
            case INSERTED:
                signupActivity.onSignupSuccess();
                break;

            default:
                signupActivity.onSignupFailed();
                break;
        }
        signUpStatus = NONE;
    }

    @Override
    protected void onCancelled() {
        progressDialog.dismiss();
        signupActivity.onSignupFailed();
        signUpStatus = NONE;
    }
}
