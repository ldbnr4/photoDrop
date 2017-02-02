package tasks;

import grexEnums.RET_STATUS;
import grexClasses.SocketActivity;

import static grexClasses.GrexSocket.getRoomsStatus;

/**
 * Created by boyice on 1/31/2017.
 *
 */

public class GetRoomsTask extends SocketActivityTask {

    public GetRoomsTask(SocketActivity socketActivity) {
        super(socketActivity, getRoomsStatus);
    }

    @Override
    protected RET_STATUS doInBackground(String... params) {
        return super.doInBackground("Updating your rooms...", new Runnable() {
            @Override
            public void run() {
                grexSocket.emitGetRooms();
            }
        });
    }
}
