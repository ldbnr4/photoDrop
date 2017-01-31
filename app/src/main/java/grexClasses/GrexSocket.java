package grexClasses;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URISyntaxException;
import java.util.ArrayList;

import grexEnums.RET_STATUS;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import money.cache.grexActivities.HomeActivity;

import static grexEnums.RET_STATUS.NONE;


/**
 * Created by Lorenzo on 11/15/2016.
 *
 */
public class GrexSocket extends AppCompatActivity {

    private static final Object loginLock = new Object();
    private static final Object registerLock = new Object();
    private static final Object roomUpdateLock = new Object();
    public static RET_STATUS loggedInStatus = NONE;
    public static RET_STATUS signUpStatus = NONE;
    public static ArrayList<String> statuses;
    //TODO: convert to RET_STATUS
    public static boolean roomUpdate = false;
    public static User user = User.getUser();
    private static Socket mSocket;
    private static Gson gson = GSON.getInstance();

    //TODO: Check that the device connected to the server via mSocket.connected
    //TODO: look into getting an ACK after emmitting https://github.com/socketio/socket.io-client-java
    //TODO: keep a local database

    private static boolean isConnectedToServer() {
        return (mSocket == null || !mSocket.connected());
    }

    public static void emit_login(String email, String password) {
        if (mSocket.connected()) {
            mSocket.emit("login", email.trim(), password.trim());
        }
    }

    public static void emit_register(String username, String email, String mobile, String password) {

        mSocket.emit("register", username.trim(), email.trim(), mobile.trim(), password.trim());
    }

    public static void emit_image(String username, String photoName, String image) {
        mSocket.emit("image_upload", username, photoName, image);
    }

    public static void emit_newRoom(Room room) {
        if (room.getHost() == null)
            room.setHost("GREX_ORPHAN");

        mSocket.emit("new_room", gson.toJson(room));
    }

    public static void emit_getRooms(){
        //TODO: force mUser.name to be set
        if(User.getUser().name == null)
            User.getUser().name = "GREX_ORPHAN";
        roomUpdate = false;
        mSocket.emit("get_rooms", User.getUser().name);
    }

    public static void disconnect() {
        mSocket.emit("disconnect");
        mSocket.disconnect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isNetworkAvailable()) {
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
                mSocket.on("rooms_fromDB", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        synchronized (roomUpdateLock) {
                            JSONArray array = (JSONArray) args[0];
                            for (int i = 0; i < array.length(); i++) {
                                try {
                                    user.addToRoomsIn(gson.fromJson(array.get(i).toString(), Room.class));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            roomUpdate = true;
                        }
                    }
                });

                mSocket.connect();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            noInternet();
        }
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void showErrorToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    private boolean hasConnection() {
        boolean status = isNetworkAvailable() && isConnectedToServer();
        if (!status) {
            checkConnections();
        }
        return status;
    }

    private void checkConnections() {
        if (!isNetworkAvailable())
            noInternet();
        else if (!isConnectedToServer()) {
            noServer();
        }
    }

    private void noInternet() {
        showErrorToast("Unable to connect to the Internet");
    }

    private void noServer() {
        showErrorToast("Our servers are down :(");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
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
