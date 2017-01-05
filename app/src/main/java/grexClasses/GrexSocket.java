package grexClasses;

import com.google.gson.Gson;

import java.net.URISyntaxException;

import grexEnums.RET_STATUS;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static grexEnums.RET_STATUS.NONE;


/**
 * Created by Lorenzo on 11/15/2016.
 *
 */
public class GrexSocket {

    private static final Object loginLock = new Object();
    private static final Object registerLock = new Object();
    private static final Object roomUpdateLock = new Object();
    public static RET_STATUS loggedInStatus = NONE;
    public static RET_STATUS signUpStatus = NONE;
    private static Socket mSocket;
    private static Gson gson = GSON.getInstance();

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
            mSocket.on("room_update", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    synchronized (roomUpdateLock) {
                        for (Object room : args) {
                            User.getUser().roomSet.add(gson.fromJson((String) room, Room.class));
                        }
                    }
                }
            });

            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void emit_login(String email, String password) {
        mSocket.emit("login", email.trim(), password.trim());
    }

    public static void emit_register(String username, String email, String mobile, String password) {
        mSocket.emit("register", username.trim(), email.trim(), mobile.trim(), password.trim());
    }

    public static void emit_image(String username, String photoName, String image) {
        mSocket.emit("image_upload", username, photoName, image);
    }

    public static void emit_newRoom(Room room) {
        mSocket.emit("new_room", gson.toJson(room));
    }

    public static void disconnect() {
        mSocket.emit("disconnect");
        mSocket.disconnect();
    }

}
