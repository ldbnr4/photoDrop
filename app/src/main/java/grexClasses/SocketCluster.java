package grexClasses;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Locale;

import grexEnums.CONNECTION_STATUS;
import grexEnums.RET_STATUS;
import io.github.sac.Ack;
import io.github.sac.Socket;
import money.cache.grexActivities.GrexApp;

import static grexEnums.CONNECTION_STATUS.CONNECTED;
import static grexEnums.CONNECTION_STATUS.INTERNET_DOWN;
import static grexEnums.CONNECTION_STATUS.SERVER_DOWN;


/**
 * Created by boyice on 2/27/2017.
 * TODO: make all emission return connection status
 */
public class SocketCluster {
    public final static Gson gson = new Gson();
    public static SimpleDateFormat DF = new SimpleDateFormat("EEE, MMM d\nh:mm aa z", Locale.US);
    private static RET_STATUS SEND_USER;
    private static RET_STATUS SEND_ROOM;
    private static RET_STATUS GET_ROOMS;
    private static CONNECTION_STATUS connection_status;
    private static Socket ourInstance = init();

    private static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) GrexApp.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

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
        return false;
    }

    private static boolean hasConnection() {
        boolean network = isNetworkAvailable();
        boolean server;
        try {
            server = ourInstance.isconnected();
        } catch (NullPointerException e) {
            server = false;
        }
        if (!network)
            connection_status = INTERNET_DOWN;
        else if (!server)
            connection_status = SERVER_DOWN;
        else
            connection_status = CONNECTED;
        return network & server;
    }

    private static Socket init() {
        Socket newSocket = new Socket("ws://zotime.ddns.net:3000/socketcluster/");
        if (isNetworkAvailable()) {
            try {
                newSocket.connect();
                if (newSocket.isconnected()) {
                    connection_status = CONNECTION_STATUS.CONNECTED;
                } else {
                    connection_status = CONNECTION_STATUS.NONE;
                }
            } catch (NullPointerException e) {
                connection_status = CONNECTION_STATUS.SERVER_DOWN;
            }
        } else {
            connection_status = CONNECTION_STATUS.INTERNET_DOWN;
        }
        return newSocket;
    }

    public static CONNECTION_STATUS emitRoom(Room room) {
        return emitRoom(gson.toJson(room));
    }

    private static CONNECTION_STATUS emitRoom(String roomJSON) {
        setSendRoom(RET_STATUS.NONE);
        if (hasConnection()) {
            actEmitRoom(roomJSON);
        } else if (connection_status == SERVER_DOWN) {
            ourInstance = init();
            if (hasConnection()) {
                actEmitRoom(roomJSON);
            }
        }
        return connection_status;
    }

    private static void actEmitRoom(String roomJSON) {
        ourInstance.emit("add_room", roomJSON, new Ack() {
            @Override
            public void call(String name, Object error, Object data) {
                setSendRoom(RET_STATUS.valueOf((String) error));
            }
        });
    }

    public static CONNECTION_STATUS emitUser() {
        setSendUser(RET_STATUS.NONE);
        if (hasConnection()) {
            actEmitUser();
        } else if (connection_status == SERVER_DOWN) {
            ourInstance = init();
            if (hasConnection()) {
                actEmitUser();
            }
        }
        return connection_status;
    }

    private static void actEmitUser() {
        ourInstance.emit("add_user", User.getName(), new Ack() {
            @Override
            public void call(String name, Object error, Object data) {
                setSendUser(RET_STATUS.valueOf((String) error));
            }
        });
    }

    public static CONNECTION_STATUS emitGetRooms() {
        setGetRooms(RET_STATUS.NONE);
        if (hasConnection()) {
            actEmitGetRooms();
        } else if (connection_status == SERVER_DOWN) {
            ourInstance = init();
            if (hasConnection()) {
                actEmitGetRooms();
            }
        }
        return connection_status;
    }

    private static void actEmitGetRooms() {
        ourInstance.emit("get_rooms", User.getName(), new Ack() {
            @Override
            public void call(String name, Object error, Object data) {
                if (error.equals("SUCCESS")) {
                    try {
                        JSONArray array = (JSONArray) ((JSONObject) data).get("rooms");
                        for (int i = 0; i < array.length(); i++) {
                            try {
                                User.getUser().addToRoomsIn(gson.fromJson((String) array.get(i), Room.class));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        User.getUser().paginateRooms();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                setGetRooms(RET_STATUS.valueOf((String) error));
            }
        });
    }

    public static RET_STATUS getSendUser() {
        return SEND_USER;
    }

    private static void setSendUser(RET_STATUS sendUser) {
        SocketCluster.SEND_USER = sendUser;
    }

    public static RET_STATUS getSendRoom() {
        return SEND_ROOM;
    }

    private static void setSendRoom(RET_STATUS ret_status) {
        SocketCluster.SEND_ROOM = ret_status;
    }

    public static RET_STATUS getGetRooms() {
        return GET_ROOMS;
    }

    private static void setGetRooms(RET_STATUS getRooms) {
        GET_ROOMS = getRooms;
    }

    public static void emitImage(String mCurrentPhotoPath, String encImage) {

    }
}
