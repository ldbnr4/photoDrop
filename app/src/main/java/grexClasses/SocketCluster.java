package grexClasses;

import com.google.gson.Gson;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import grexEnums.RET_STATUS;
import grexEnums.ROOM_CATEGORY;
import io.github.sac.Ack;
import io.github.sac.BasicListener;
import io.github.sac.ReconnectStrategy;
import io.github.sac.Socket;

import static grexEnums.RET_STATUS.CONNECTED;
import static grexEnums.RET_STATUS.DISCONNECTED;
import static grexEnums.RET_STATUS.NONE;


/**
 * Created by boyice on 2/27/2017.
 * TODO: make all emission return connection status
 */
public class SocketCluster {
    public final static Gson gson = new Gson();
    public static User user = User.getUser();
    public static SimpleDateFormat DF = new SimpleDateFormat("EEE, MMM d\nh:mm aa z", Locale.US);
    private static RET_STATUS SEND_USER;
    private static RET_STATUS SEND_ROOM;
    private static final Socket socket = initSocket();
    private static SocketCluster ourInstance = new SocketCluster();
    private RET_STATUS GET_ROOMS = NONE;

    private static RET_STATUS connection_status = NONE;

    private SocketCluster() {}

    private static Socket initSocket(){
        Socket socket = new Socket("ws://zotime.ddns.net:3000/socketcluster/");
        socket.setListener(new BasicListener() {
            @Override
            public void onConnected(Socket socket, Map<String, List<String>> headers) {
                connection_status = CONNECTED;
            }

            @Override
            public void onDisconnected(Socket socket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) {
                connection_status = DISCONNECTED;
            }

            @Override
            public void onConnectError(Socket socket, WebSocketException exception) {
                connection_status = DISCONNECTED;
            }

            @Override
            public void onAuthentication(Socket socket, Boolean status) {

            }

            @Override
            public void onSetAuthToken(String token, Socket socket) {

            }
        });
        socket.setReconnection(new ReconnectStrategy().setDelay(2000).setMaxAttempts(30));
        socket.connect();
        return socket;
    }

    public static SocketCluster getInstance() {
        return ourInstance;
    }
    //init();

    /*
    //Declare the timer
    private final Timer t = new Timer();
    //Set the schedule function and rate
        t.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        //Called each time when 5000 milliseconds (5 second) (the period parameter)
                        SocketCluster.emitGPS();
                    }
                },
                //Set how long before to start calling the TimerTask (in milliseconds)
                0,
                //Set the amount of time between each execution (in milliseconds)
                5000);

        t.cancel();

    */

    public static RET_STATUS emitUser() {
        return NONE;
    }

    /*
        if (hasConnection()) {
            user.location = location;
            user.lat = String.valueOf(location.getLatitude());
            user.lon = String.valueOf(location.getLongitude());

            socket.emit("user_gps", gson.toJson(user));
        }
        return connection_status;
    }
*/
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

    public static void emitImage(String mCurrentPhotoPath, String encImage) {

    }

    private static void connectToServer() {
        if(!socket.isconnected()){
            socket.connect();
        }

        /*NoNet.check(GrexApp.getContext())
                    .callback(new Monitor.Callback() {
                        @Override
                        public void onConnectionEvent(int connectionStatus) {
                            if (connectionStatus == ConnectionStatus.DISCONNECTED) {
                                connection_status = NO_INTERNET;
                            }
                        }
                    }).start();*/
    }

    public RET_STATUS emitRoom(Room room) {
            /*socket.emit("add_room", gson.toJson(room), new Ack() {
                @Override
                public void call(String name, Object error, Object data) {
                    setSendRoom(RET_STATUS.valueOf((String) error));
                }
            });*/
            return SEND_ROOM;
    }

    /*
        setSendUser(RET_STATUS.NONE);
        if (hasConnection()) {
            actEmitUser();
        } else if (connection_status == SERVER_DOWN) {
            socket = init();
            if (hasConnection()) {
                actEmitUser();
            }
        }
        return connection_status;
    }
*/
    private void actEmitUser() {
        socket.emit("add_user", user.name, new Ack() {
            @Override
            public void call(String name, Object error, Object data) {
                setSendUser(RET_STATUS.valueOf((String) error));
            }
        });
    }

    public RET_STATUS emitGetRooms(ROOM_CATEGORY category, int page) {
            /*socket.emit("get_rooms", new Object[]{user.name, category, page}, new Ack() {
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    GET_ROOMS = RET_STATUS.valueOf((String) error);
                }
            });*/
            return DISCONNECTED;
    }

    // TODO: 3/16/2017 Ideal emission workflow ...ish
    public boolean emitGPS() {
        if(!socket.isconnected()){
            socket.connect();
        }
        if(socket.isconnected()){
            JSONObject object = new JSONObject();
            try {
                object.put("name", user.name);
                object.put("lat", user.location.latitude);
                object.put("lon", user.location.longitude);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            socket.emit("user_gps", object.toString(), new Ack() {
                @Override
                public void call(String name, Object error, Object data) {
                    System.out.println(data);
                    GET_ROOMS = RET_STATUS.valueOf((String) error);
                }
            });
            while(GET_ROOMS == NONE);
            GET_ROOMS = NONE;
            return true;
        }
        else
            return false;
    }
}
