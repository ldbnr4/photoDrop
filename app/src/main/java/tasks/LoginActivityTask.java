package tasks;

import grexEnums.RET_STATUS;
import grexClasses.SocketActivity;

import static grexClasses.GrexSocket.loggedInStatus;
import static grexClasses.GrexSocket.signUpStatus;

/**
 * Created by Lorenzo on 11/15/2016.
 *
 */

public class LoginActivityTask extends SocketActivityTask {

    public LoginActivityTask(final SocketActivity callingActivity) {
        super(callingActivity, loggedInStatus);
    }


    @Override
    protected RET_STATUS doInBackground(final String... params) {
        if(params.length == 2){
            Runnable function = new Runnable() {
                @Override
                public void run() {
                    grexSocket.emitLogin(params[0], params[1]);
                }
            };
            return super.doInBackground("Logging In...", function);
        }
        else
            return signUpStatus;
    }

}

