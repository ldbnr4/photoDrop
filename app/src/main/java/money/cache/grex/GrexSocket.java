package money.cache.grex;

import java.net.URISyntaxException;

import enums.RET_STATUS;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static enums.RET_STATUS.NONE;


/**
 * Created by Lorenzo on 11/15/2016.
 */
class GrexSocket {

    private static final Object lock = new Object();
    static RET_STATUS loggedInStatus = NONE;
    private static Socket mSocket;

    static {
        try {
            mSocket = IO.socket("http://zotime.ddns.net:3000");
            mSocket.on("login_status", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    synchronized (lock) {
                        loggedInStatus = RET_STATUS.valueOf((String) args[0]);
                    }
                }
            });

            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    static void login_emit(String email, String password) {
        mSocket.emit("login", email.trim(), password.trim());
    }

    static void register_emit(String email, String password) {
        mSocket.emit("register", email.trim(), password.trim());
    }

    static void disconnect() {
        mSocket.emit("disconnect");
        mSocket.disconnect();
    }

}
