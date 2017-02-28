package grexClasses;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import grexEnums.CONNECTION_STATUS;
import grexEnums.RET_STATUS;
import io.github.sac.Ack;
import io.github.sac.Emitter;
import io.github.sac.Socket;

import static grexEnums.CONNECTION_STATUS.CONNECTED;
import static grexEnums.CONNECTION_STATUS.INTERNET_DOWN;
import static grexEnums.CONNECTION_STATUS.SERVER_DOWN;


/**
 * Created by Lorenzo on 11/15/2016.
 * TODO: keep a local database of stuff that doesn't emmit successfully aka doesn't get ack'd
 * TODO: when connection is successfully made sync local database with remote database
 * TODO: scrapt the socket idea and try a rest api
 */
public final class GrexSocket {

    private final static Gson gson = new Gson();
    private static CONNECTION_STATUS connection_status = CONNECTION_STATUS.NONE;
    private static RET_STATUS loggedIn = RET_STATUS.NONE;
    private static RET_STATUS signUpStatus = RET_STATUS.NONE;
    private static RET_STATUS sendRoom = RET_STATUS.NONE;
    private static RET_STATUS getRooms = RET_STATUS.NONE;
    private static User user = User.getUser();
    private static Socket mSocket;
    private static ConnectivityManager connectivityManager;

    private static RET_STATUS getGetRooms() {
        return getRooms;
    }

    private static void setGetRooms(RET_STATUS status) {
        getRooms = status;
    }

    private static RET_STATUS getSendRoom() {
        return sendRoom;
    }

    static void setSendRoom(RET_STATUS status) {
        sendRoom = status;
    }

    private static void initConnection() {
        if (mSocket == null) {
            // mSocket = IO.socket("http://zotime.ddns.net:3000").connect();
            mSocket.on("serv_rooms", new Emitter.Listener() {
                @Override
                public void call(String name, Object data) {

                }

                public void call(Object... args) {
                    JSONArray array = (JSONArray) args[0];
                    for (int i = 0; i < array.length(); i++) {
                        try {
                            JSONObject o = (JSONObject) array.get(i);
                            String roomJSON = o.get("room").toString();
                            user.addToRoomsIn(gson.fromJson(roomJSON, Room.class));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    setGetRooms(RET_STATUS.SUCCESS);
                }
            });
        }
        if (!mSocket.isconnected()) {
            mSocket.connect();
        }
    }

    private static void waitForConnection(Context applicationContext) throws InterruptedException {
        int timer = 0;
        while (timer < 3) {
            if (hasConnection(applicationContext)) break;
            Thread.sleep(1000);
            timer++;
        }
    }

    private static boolean isConnectedToServer() {
        return (mSocket == null || mSocket.isconnected());
    }

    private static boolean hasConnection(Context applicationContext) {
        initConnection();
        boolean network = isNetworkAvailable(applicationContext);
        boolean server = isConnectedToServer();
        if(!network)
            connection_status = INTERNET_DOWN;
        else if (!server)
            connection_status = SERVER_DOWN;
        else
            connection_status = CONNECTED;
        return network & server;
    }

    private static boolean isNetworkAvailable(Context applicationContext) {
        if (connectivityManager == null) {
            connectivityManager = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        }

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

    private static void emitRoom(String roomString) throws InterruptedException {
        //if (hasConnection(applicationContext))
            emitRoomsCore(roomString);
        //else
        //waitForConnection(applicationContext);
    }

    private static void emitRoomsCore(String roomString) {
        emitRoomInnerCore(roomString);
    }

    private static void emitRoomInnerCore(String roomJSON) {
        setSendRoom(RET_STATUS.NONE);
        mSocket.emit("new_room", roomJSON, new Ack() {
            @Override
            public void call(String name, Object error, Object data) {

            }

            public void call(Object... args) {
                sendRoom = RET_STATUS.valueOf((String) args[0]);
            }
        });
    }

    private static void emitGetRooms(Context applicationContext) throws InterruptedException {
        setGetRooms(RET_STATUS.NONE);
        waitForConnection(applicationContext);
        if (connection_status == CONNECTED) {
            mSocket.emit("get_rooms", User.getName());
        }
    }

    private static void emitImage(String photoName, String image) {
        //if(hasConnection(applicationContext))
        mSocket.emit("image_upload", new String[]{User.getName(), photoName, image});
    }

    private static void emitRoom(Room room) throws InterruptedException {
        //if(hasConnection(applicationContext))
        emitRoomCore(room);
        //else
        //waitForConnection(applicationContext);
    }

    private static void emitRoomCore(Room room) {
        String roomJSON = gson.toJson(room);
        emitRoomInnerCore(roomJSON);
    }

    private void emitLogin(String email, String password) {
        //if (hasConnection(applicationContext))
        mSocket.emit("login", new String[]{email.trim(), password.trim()}, new Ack() {
                @Override
                public void call(String name, Object error, Object data) {

                }

                public void call(Object... args) {
                    loggedIn = RET_STATUS.valueOf((String) args[0]);
                }
            });
    }

    private void emitRegister(String username, String email, String mobile, String password) {
        //if(hasConnection(applicationContext))
        mSocket.emit("register", new String[]{username.trim(), email.trim(), mobile.trim(), password.trim()}, new Ack() {
                @Override
                public void call(String name, Object error, Object data) {

                }

                public void call(Object... args) {
                    signUpStatus = RET_STATUS.valueOf((String) args[0]);
                }
            });
    }

}
