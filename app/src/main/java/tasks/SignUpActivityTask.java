package tasks;

import grexEnums.RET_STATUS;
import grexClasses.SocketActivity;

import static grexClasses.GrexSocket.signUpStatus;

/**
 * Created by Lorenzo on 11/20/2016.
 *
 */

public class SignUpActivityTask extends SocketActivityTask {

    public SignUpActivityTask(SocketActivity socketActivity) {
        super(socketActivity, signUpStatus);
    }

    @Override
    protected RET_STATUS doInBackground(final String... params) {
        if (params.length == 4) {
            Runnable function = new Runnable() {
                @Override
                public void run() {
                    grexSocket.emitRegister(params[0], params[1], params[2], params[3]);
                }
            };
            return super.doInBackground("Registering...", function);
        }
        else
            return signUpStatus;
    }

}
