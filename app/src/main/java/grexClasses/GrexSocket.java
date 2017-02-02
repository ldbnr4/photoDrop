package grexClasses;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import grexEnums.CONNECTION_STATUS;
import grexEnums.RET_STATUS;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static grexEnums.CONNECTION_STATUS.INTERNET_DOWN;
import static grexEnums.CONNECTION_STATUS.SERVER_DOWN;
import static grexEnums.RET_STATUS.NONE;


/**
 * Created by Lorenzo on 11/15/2016.
 *
 */
public class GrexSocket{
    private static GrexSocket grexSocket = new GrexSocket();
    private static final Object loginLock = new Object();
    private static final Object registerLock = new Object();
    private static final Object roomUpdateLock = new Object();
    public static CONNECTION_STATUS connection_status;
    public static RET_STATUS loggedInStatus = NONE;
    public static RET_STATUS signUpStatus = NONE;
    public static RET_STATUS getRoomsStatus = NONE;
    private static User user = User.getUser();
    private static Socket mSocket;
    private static Gson gson = GSON.getInstance();
    private static ConnectivityManager connectivityManager;

    //TODO: look into getting an ACK after emmitting https://github.com/socketio/socket.io-client-java
    //TODO: keep a local database

    public static synchronized GrexSocket getGrexSocket(){
        return  grexSocket;
    }

    public static void initConnection(Context applicationContext){
        if(connectivityManager == null) {
            connectivityManager = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            try {
                mSocket = IO.socket("http://zotime.ddns.net:3000");
                setUpListeners();
                mSocket.connect();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private static void setUpListeners() {
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
        mSocket.on("rooms_fromDB", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                synchronized (roomUpdateLock) {
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
                    //TODO: Make server send this as first parameter
                    getRoomsStatus = RET_STATUS.valueOf((String) args[0]);
                }
            }
        });
    }

    public ConnectivityManager getConnectivityManager() {
        return connectivityManager;
    }

    private boolean isConnectedToServer() {
        return (mSocket == null || mSocket.connected());
    }

    private boolean hasConnection() {
        boolean network = isNetworkAvailable();
        boolean server = isConnectedToServer();
        if(!network)
            connection_status = INTERNET_DOWN;
        else if (!server)
            connection_status = SERVER_DOWN;
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
            mSocket.emit("login", email.trim(), password.trim());
    }

    public void emitRegister(String username, String email, String mobile, String password) {
        if(hasConnection())
            mSocket.emit("register", username.trim(), email.trim(), mobile.trim(), password.trim());
    }

    public void emitImage(String username, String photoName, String image) {
        if(hasConnection())
            mSocket.emit("image_upload", username, photoName, image);
    }

    public void emitRoom(Room room) {
        if(hasConnection()) {
            if (room.getHost() == null)
                room.setHost("GREX_ORPHAN");
            String roomJSON = gson.toJson(room);
            mSocket.emit("new_room", roomJSON);
        }
    }

    public void emitGetRooms(){
        if(hasConnection()) {
            //TODO: force mUser.name to be set
            if (User.getUser().name == null)
                User.getUser().name = "GREX_ORPHAN";
            getRoomsStatus = NONE;
            mSocket.emit("get_rooms", User.getUser().name);
        }
    }

    public void disconnect() {
        mSocket.emit("disconnect");
        mSocket.disconnect();
    }

}
