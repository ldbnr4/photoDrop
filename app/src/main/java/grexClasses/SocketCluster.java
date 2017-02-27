package grexClasses;

import com.google.gson.Gson;

import io.github.sac.Ack;
import io.github.sac.ReconnectStrategy;
import io.github.sac.Socket;

import static grexClasses.GrexSocket.setSendRoom;

/**
 * Created by boyice on 2/27/2017.
 * TODO: create a way to add a user to the rooms collection on the server
 */
public class SocketCluster {
    private final static Gson gson = new Gson();
    private static Socket ourInstance;

    static {
        String url = "ws://zotime.ddns.net:3000/socketcluster/";
        ourInstance = new Socket(url);
        ourInstance.connect();
        ourInstance.setReconnection(new ReconnectStrategy().setDelay(2000).setMaxAttempts(30));
    }

    public static Socket getInstance() {
        if (!ourInstance.isconnected()) {
            ourInstance.connect();
        }
        return ourInstance;
    }

    public static void emitRoom(Room room) {
        String roomJSON = gson.toJson(room);
        emitRoomCore(roomJSON);
    }

    private static void emitRoomCore(String roomJSON) {
        setSendRoom(GrexSocket.RET_STATUS.NONE);
        ourInstance.emit("add_room", roomJSON, new Ack() {
            @Override
            public void call(String name, Object error, Object data) {
                setSendRoom(GrexSocket.RET_STATUS.valueOf((String) error));
            }
        });
    }
}
