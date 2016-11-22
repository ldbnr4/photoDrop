package money.cache.grex;

import java.net.URISyntaxException;

import enums.RET_STATUS;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static enums.RET_STATUS.NONE;


/**
 * Created by Lorenzo on 11/15/2016.
 *
 */
public class GrexSocket {

    private static final Object loginLock = new Object();
    private static final Object registerLock = new Object();
    public static RET_STATUS loggedInStatus = NONE;
    public static RET_STATUS signUpStatus = NONE;
    private static Socket mSocket;

    static {
        try {
            mSocket = IO.socket("http://zotime.ddns.net:3000");

            mSocket.on("login_status", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    synchronized (loginLock) {
                        loggedInStatus = RET_STATUS.valueOf((String) args[0]);
                    }
                }
            });

            mSocket.on("register_status", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    synchronized (registerLock) {
                        signUpStatus = RET_STATUS.valueOf((String) args[0]);
                    }
                }
            });

            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void login_emit(String email, String password) {
        mSocket.emit("login", email.trim(), password.trim());
    }

    public static void register_emit(String username, String email, String mobile, String password) {
        mSocket.emit("register", username.trim(), email.trim(), mobile.trim(), password.trim());
    }

    static void disconnect() {
        mSocket.emit("disconnect");
        mSocket.disconnect();
    }

}
