package tasks;

import grexEnums.RET_STATUS;
import money.cache.grexActivities.SocketActivity;

import static grexClasses.GrexSocket.getRoomsStatus;

/**
 * Created by boyice on 1/31/2017.
 *
 */

public class CreateRoomActivityTask extends SocketActivityTask {
    CreateRoomActivityTask(SocketActivity socketActivity) {
        super(socketActivity, getRoomsStatus);
    }

    @Override
    protected RET_STATUS doInBackground(String... params) {
        return null;
    }
}
