package grexClasses;

import com.google.gson.Gson;

import io.github.sac.Ack;
import io.github.sac.ReconnectStrategy;
import io.github.sac.Socket;

import static grexClasses.GrexSocket.setSendRoom;

/**
 * Created by boyice on 2/27/2017.
 *
 */
public class SocketCluster {
    private final static Gson gson = new Gson();
    private static Socket ourInstance;
    private static GrexSocket.RET_STATUS SEND_USER;

    static {
        String url = "ws://zotime.ddns.net:3000/socketcluster/";
        ourInstance = new Socket(url);
        ourInstance.connect();
        ourInstance.setReconnection(new ReconnectStrategy().setDelay(2000).setMaxAttempts(30));
    }

    public static void emitRoom(Room room) {
        emitRoom(gson.toJson(room));
    }

    private static void emitRoom(String roomJSON) {
        setSendRoom(GrexSocket.RET_STATUS.NONE);
        ourInstance.emit("add_room", roomJSON, new Ack() {
            @Override
            public void call(String name, Object error, Object data) {
                setSendRoom(GrexSocket.RET_STATUS.valueOf((String) error));
            }
        });
    }

    public static void emitUser() {
        setSendUser(GrexSocket.RET_STATUS.NONE);
        ourInstance.emit("add_user", User.getName(), new Ack() {
            @Override
            public void call(String name, Object error, Object data) {
                setSendUser(GrexSocket.RET_STATUS.valueOf((String) error));
            }
        });
    }

    public static GrexSocket.RET_STATUS getSendUser() {
        return SEND_USER;
    }

    private static void setSendUser(GrexSocket.RET_STATUS sendUser) {
        SocketCluster.SEND_USER = sendUser;
    }
}
