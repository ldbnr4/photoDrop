package tasks;

import grexClasses.GrexSocket;
import grexEnums.RET_STATUS;
import money.cache.grexActivities.SocketActivity;

import static grexClasses.GrexSocket.emit_register;
import static grexClasses.GrexSocket.signUpStatus;

/**
 * Created by Lorenzo on 11/20/2016.
 */

public class InsertUserActivityTask extends SocketActivityTask {

    public InsertUserActivityTask(SocketActivity socketActivity) {
        super(socketActivity, GrexSocket.signUpStatus);
    }

    @Override
    protected RET_STATUS doInBackground(final String... params) {
        if (params.length == 4) {
            Runnable function = new Runnable() {
                @Override
                public void run() {
                    emit_register(params[0], params[1], params[2], params[3]);
                }
            };
            return super.doInBackground("Registering...", function);
        }
        return signUpStatus;
    }

}
