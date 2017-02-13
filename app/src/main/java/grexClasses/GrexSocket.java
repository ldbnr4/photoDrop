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

import grexEnums.CONNECTION_STATUS;
import grexEnums.RET_STATUS;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;

import static grexEnums.CONNECTION_STATUS.CONNECTED;
import static grexEnums.CONNECTION_STATUS.INTERNET_DOWN;
import static grexEnums.CONNECTION_STATUS.SERVER_DOWN;
import static grexEnums.RET_STATUS.NONE;


/**
 * Created by Lorenzo on 11/15/2016.
 *
 */
public class GrexSocket{
    public static CONNECTION_STATUS connection_status;
    public static RET_STATUS loggedIn = NONE;
    public static RET_STATUS sendRoom = NONE;
    public static RET_STATUS signUpStatus = NONE;
    public static RET_STATUS getRooms = NONE;
    public static SimpleDateFormat DF = new SimpleDateFormat("EEE, MMM d\nh:mm aa z", Locale.US);
    private static GrexSocket grexSocket = new GrexSocket();
    private static User user = User.getUser();
    private static Socket mSocket;
    private static Gson gson = GSON.getInstance();
    private static ConnectivityManager connectivityManager;

    //TODO: keep a local database of stuff that doesn't emmit successfully aka doesn't get ack'd
    //TODO: when connection is successfully made sync local database with remote database
    //TODO: use acks for all emits

    public static synchronized GrexSocket getGrexSocket(){
        return  grexSocket;
    }

    public static void initConnection() {
        try {
            mSocket = IO.socket("http://zotime.ddns.net:3000").connect();
            while (!mSocket.connected())
                Thread.sleep(500);
            connection_status = CONNECTED;
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void initConnection(Context applicationContext) {
        if(connectivityManager == null) {
            connectivityManager = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            initConnection();
        }
    }

    public ConnectivityManager getConnectivityManager() {
        return connectivityManager;
    }

    private boolean isConnectedToServer() {
        return (mSocket == null || mSocket.connected());
    }

    public boolean hasConnection() {
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

    private boolean isNetworkAvailable() {
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

    public void emitRoom(Room room) {
        if(hasConnection()) {
            emitRoomCore(room);
        }
    }

    public void tEmitRoom(Room room) {
        emitRoomCore(room);
    }

    private void emitRoomCore(Room room) {
        if (room.getHost() == null)
            room.setHost("GREX_ORPHAN");
        String roomJSON = gson.toJson(room);
        sendRoom = NONE;
        mSocket.emit("new_room", roomJSON, new Ack() {
            @Override
            public void call(Object... args) {
                sendRoom = RET_STATUS.valueOf((String) args[0]);
            }
        });
    }

    public void emitGetRooms(){
        if(hasConnection()) {
            emitGetRoomsCore();
        }
    }

    public void tEmitGetRooms() {
        emitGetRoomsCore();
    }

    private void emitGetRoomsCore() {
        //TODO: force mUser.name to be set
        if (User.getUser().name == null)
            User.getUser().name = "GREX_ORPHAN";
        getRooms = NONE;
        mSocket.emit("get_rooms", User.getUser().name, new Ack() {
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
                getRooms = RET_STATUS.valueOf((String) args[0]);
            }
        });
    }

    public void disconnect() {
        mSocket.emit("disconnect");
        mSocket.disconnect();
    }
}
