package money.cache.grexActivities;

//TODO: Allow third party login like Facebook, Instagram or Twitter
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import grexClasses.User;
import grexEnums.RET_STATUS;
import tasks.LoginActivityTask;

/**
 * Created by Lorenzo on 1/4/2017.
 *
 */

public class LoginActivity extends SocketActivity {
    private static final String TAG = "LoginActivity";
    @Bind(R.id.input_username)
    EditText _usernameText;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.btn_login)
    Button _loginButton;
    @Bind(R.id.link_signup)
    TextView _signupLink;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        _loginButton.setEnabled(false);

        _usernameText.setError(null);
        _passwordText.setError(null);

        if (!validate()) {
            _loginButton.setEnabled(true);
            return;
        }

        String email = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        LoginActivityTask mAuthTask = new LoginActivityTask(this);
        mAuthTask.execute(email, password);

    }

    @Override
    public void onFail() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    @Override
    public void onSuccess() {
        User.getUser().name = _usernameText.getText().toString();
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        finish();
    }

    @Override
    public void onPostExecute(RET_STATUS retStatResult) {
        switch (retStatResult) {
            case NONE:
                break;
            case VERIFIED:
                onSuccess();
                break;

            case WRONG_PASSWORD:
                _passwordText.setError("Wrong password");
            case NO_ACCOUNT:
                _usernameText.setError("No account found for this mUser.");
            default:
                onFail();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
        super.onBackPressed();
    }

    public boolean validate() {
        boolean valid = true;

        String email = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty()) {
            _usernameText.setError("This is required");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty()){
            _passwordText.setError("This is required");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
