package grexClasses;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;

import static grexClasses.GrexSocket.CONNECTION_STATUS.CONNECTED;
import static grexClasses.GrexSocket.CONNECTION_STATUS.INTERNET_DOWN;
import static grexClasses.GrexSocket.CONNECTION_STATUS.SERVER_DOWN;
import static grexClasses.GrexSocket.RET_STATUS.NONE;


/**
 * Created by Lorenzo on 11/15/2016.
 * TODO: keep a local database of stuff that doesn't emmit successfully aka doesn't get ack'd
 * TODO: when connection is successfully made sync local database with remote database
 * TODO: use acks for all emits
 * TODO: add else waitForConnection() in all emits
 */
public final class GrexSocket {
    public final static Gson gson = new Gson();
    public static CONNECTION_STATUS connection_status;
    public static RET_STATUS loggedIn = NONE;
    public static RET_STATUS signUpStatus = NONE;
    public static SimpleDateFormat DF = new SimpleDateFormat("EEE, MMM d\nh:mm aa z", Locale.US);
    private static RET_STATUS sendRoom = NONE;
    private static RET_STATUS getRooms = NONE;
    private static GrexSocket grexSocket = new GrexSocket();
    private static User user = User.getUser();
    private static Socket mSocket;
    private static ConnectivityManager connectivityManager;

    public static synchronized RET_STATUS getGetRooms() {
        return getRooms;
    }

    private static synchronized void setGetRooms(RET_STATUS status) {
        getRooms = status;
    }

    public static synchronized RET_STATUS getSendRoom() {
        return sendRoom;
    }

    private static synchronized void setSendRoom(RET_STATUS status) {
        sendRoom = status;
    }

    public static synchronized GrexSocket getGrexSocket(Context applicationContext) {
        if (connectivityManager == null) {
            connectivityManager = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            try {
                mSocket = IO.socket("http://zotime.ddns.net:3000").connect();
                if (!hasConnection())
                    waitForConnection();
            } catch (URISyntaxException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return grexSocket;
    }

    private static void waitForConnection() throws InterruptedException {
        int timer = 0;
        while (!hasConnection() && timer < 3) {
            Thread.sleep(1000);
            timer++;
        }
    }

    private static boolean isConnectedToServer() {
        return (mSocket == null || mSocket.connected());
    }

    private static boolean hasConnection() {
        boolean network = isNetworkAvailable();
        boolean server = isConnectedToServer();
        if(!network)
            connection_status = INTERNET_DOWN;
        else if (!server)
            connection_status = SERVER_DOWN;
        else
            connection_status = CONNECTED;
        return network & server;
    }

    private static boolean isNetworkAvailable() {
        if(connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                //String answer;
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                }
                //answer="You are connected to a WiFi Network";
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                }
                //answer="You are connected to a Mobile Network";
                return activeNetworkInfo.isConnected();
            }
        }
        return false;
    }

    public void emitLogin(String email, String password) {
        if (hasConnection())
            mSocket.emit("login", email.trim(), password.trim(), new Ack() {
                @Override
                public void call(Object... args) {
                    loggedIn = RET_STATUS.valueOf((String) args[0]);
                }
            });
    }

    public void emitRegister(String username, String email, String mobile, String password) {
        if(hasConnection())
            mSocket.emit("register", username.trim(), email.trim(), mobile.trim(), password.trim(), new Ack() {
                @Override
                public void call(Object... args) {
                    signUpStatus = RET_STATUS.valueOf((String) args[0]);
                }
            });
    }

    public void emitImage(String username, String photoName, String image) {
        if(hasConnection())
            mSocket.emit("image_upload", username, photoName, image);
    }

    public void emitRoom(Room room) throws InterruptedException {
        if(hasConnection()) {
            emitRoomCore(room);
        } else
            waitForConnection();
    }

    public void emitRoom(String roomString) throws InterruptedException {
        if (hasConnection())
            emitRoomsCore(roomString);
        else
            waitForConnection();
    }

    private void emitRoomsCore(String roomString) {
        emitRoomInnerCore(roomString);
    }

    public void tEmitRoom(Room room) {
        emitRoomCore(room);
    }

    private void emitRoomCore(Room room) {
        String roomJSON = gson.toJson(room);
        emitRoomInnerCore(roomJSON);
    }

    private void emitRoomInnerCore(String roomJSON) {
        setSendRoom(NONE);
        mSocket.emit("new_room", roomJSON, new Ack() {
            @Override
            public void call(Object... args) {
                sendRoom = RET_STATUS.valueOf((String) args[0]);
            }
        });
    }

    public void emitGetRooms() throws InterruptedException {
        if(hasConnection()) {
            emitGetRoomsCore();
        } else
            waitForConnection();
    }

    public void tEmitGetRooms() {
        emitGetRoomsCore();
    }

    private void emitGetRoomsCore() {
        setGetRooms(NONE);
        mSocket.emit("get_rooms", User.getUser().getName(), new Ack() {
            @Override
            public void call(Object... args) {
                JSONArray array = (JSONArray) args[1];
                for (int i = 0; i < array.length(); i++) {
                    try {
                        JSONObject o = (JSONObject) array.get(i);
                        String roomJSON = o.get("room").toString();
                        user.addToRoomsIn(gson.fromJson(roomJSON, Room.class));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                setGetRooms(RET_STATUS.valueOf((String) args[0]));
            }
        });
    }

    public void disconnect() {
        mSocket.emit("disconnect");
        mSocket.disconnect();
    }

    public enum CONNECTION_STATUS {
        CONNECTED,
        SERVER_DOWN,
        INTERNET_DOWN
    }

    public enum RET_STATUS {
        NONE,
        VERIFIED,
        NO_ACCOUNT,
        WRONG_PASSWORD,
        INSERTED,
        SUCCESS
    }
}
