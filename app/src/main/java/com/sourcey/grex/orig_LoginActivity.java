package com.sourcey.grex;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * A login screen that offers login via email/password.
 */
public class orig_LoginActivity extends AppCompatActivity{

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    public enum RET_STATUS{
        NONE,
        VERIFIED,
        NO_ACCOUNT,
        WRONG_PASSWORD
    }

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private EditText mConfirmPasswordView;

    private Button mEmailSignInButton;
    Button mFirstTimeButton;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://zotime.ddns.net:3000");
        }catch (URISyntaxException e) {
            throw new RuntimeException();
        }
    }

    private static RET_STATUS loggedInStatus = RET_STATUS.NONE;
    private static final Object loggedInLock = new Object();
    private boolean registering = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mConfirmPasswordView = (EditText) findViewById(R.id.input_reEnterPassword);
        mEmailSignInButton = (Button) findViewById(R.id.btn_login);
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.input_email);
        mPasswordView = (EditText) findViewById(R.id.input_password);

        mFirstTimeButton = (Button) findViewById(R.id.btn_signup);

        mSocket.on("login_status", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                synchronized (loggedInLock){
                    loggedInStatus = RET_STATUS.valueOf((String) args[0]);
                }
            }
        });
        mSocket.connect();

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


        mFirstTimeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //showConfirmPassword(mFirstTimeButton.getText().equals(getString(R.string.action_first_time)));
                    }
                });
            }
        });

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handle = false;
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    attemptLogin();
                    handle = true;
                }
                return handle;
            }
        });

        mConfirmPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        mConfirmPasswordView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        //mLoginFormView = findViewById(R.id.username_form);
        //mProgressView = findViewById(R.id.textView);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confPassword = mConfirmPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a confirmation password.
        if(mConfirmPasswordView.getVisibility() == View.VISIBLE && !confPassword.equals(password)){
            mConfirmPasswordView.setError("Your passwords do not match");
            focusView = mConfirmPasswordView;
            cancel = true;
        }

        // Check for a valid password.
        if(TextUtils.isEmpty(password)){
            //mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }
        else if (!isPasswordValid(password)) {
            //mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            //mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            //mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password, registering);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void showConfirmPassword(final boolean show){
        mConfirmPasswordView.setVisibility(show ? View.VISIBLE : View.GONE);
        //mEmailSignInButton.setText(show ? R.string.action_register : R.string.action_sign_in);
        //mFirstTimeButton.setText(show ? R.string.action_returning : R.string.action_first_time);
        mPasswordView.setImeOptions(show ? EditorInfo.IME_ACTION_NEXT : EditorInfo.IME_ACTION_DONE);
        mEmailView.requestFocus();
        registering = show;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        mSocket.disconnect();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private boolean mregister;

        UserLoginTask(String email, String password, boolean register) {
            mEmail = email;
            mPassword = password;
            mregister = register;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            mSocket.emit(mregister ? "register" : "login", mEmail.trim(), mPassword.trim());
            //long diff = d2.getTime() - d1.getTime();

            //long diffSeconds = diff / 1000 % 60;
            while(loggedInStatus == RET_STATUS.NONE);
            switch (loggedInStatus){
                case VERIFIED:
                    return true;
                case WRONG_PASSWORD:
                    return false;
                default:
                    return false;
            }


        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

