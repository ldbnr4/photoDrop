package money.cache.grex;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.keiferstone.nonet.NoNet;

import java.util.ArrayList;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import grexClasses.Room;
import grexClasses.SocketActivity;
import grexClasses.SocketCluster;
import grexClasses.User;
import grexEnums.ROOM_CATEGORY;
import grexLayout.ConnectivityFragment;
import grexLayout.RoomCardFragment;
import grexLayout.RoomFeedFragment;
import grexLayout.SpinnerFragment;

import static grexEnums.RET_STATUS.SUCCESS;

// TODO: 2/23/2017 onPostExecute cant wait. move to another function.
public class HomeActivity extends SocketActivity implements ConnectivityFragment.OnFragmentInteractionListener,
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    public static final double ROOM_RANGE = 0.25;
    public static final int USER_RANGE = 5;
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "REQ_LOC_UPDT_KEY";
    private static final String LOCATION_KEY = "LOC_KEY";
    private static final String LAST_UPDATED_TIME_STRING_KEY = "LAST_UPDT_TIME_KEY";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final String TAG = "HOME_ACTIVITY";
    private final LatLngBounds.Builder builder = new LatLngBounds.Builder();
    @Bind(R.id.btn_createRoom)
    FloatingActionButton mBtnCreateRoom;
    @Bind(R.id.room_card_frame)
    RelativeLayout mRoomCardFrame;
    @Bind(R.id.close_room_card_btn)
    ImageButton mBtnCloseRmCard;
    @Bind(R.id.btn_enterRoom)
    FloatingActionButton mBtnEnterRoom;
    SupportMapFragment mapFragment;
    GoogleMap mGoogleMap;
    Room focusedRoom = null;
    ArrayList<Room> list = new ArrayList<>();
    private ConnectivityFragment internetDownFrag = ConnectivityFragment.newInstance("Check your network connection");
    private ConnectivityFragment serverDownFrag = ConnectivityFragment.newInstance("Well this is awkward...");
    private SpinnerFragment spinnerFragment = new SpinnerFragment();
    private Fragment currentFrag;
    private Runnable getRmRunnable = new Runnable() {
        @Override
        public void run() {
            Runtime.getRuntime().gc();
            setFragment(spinnerFragment);
            switch (SocketCluster.getInstance().emitGetRooms(ROOM_CATEGORY.LIVE, 0)) {
                case SUCCESS:
                    setFragment(new RoomFeedFragment());
                    break;
                case SERVER_DOWN:
                    setFragment(serverDownFrag);
                    break;
                case NO_INTERNET:
                    setFragment(internetDownFrag);
                    break;
                default:
                    throw new NullPointerException();
            }
            Runtime.getRuntime().gc();
        }
    };
    private GoogleApiClient mGoogleApiClient = null;
    private Location mLastLocation = null;
    private LocationRequest mLocationRequest = null;
    private Location mCurrentLocation;
    private String mLastUpdateTime;
    private boolean mRequestingLocationUpdates = true;
    private boolean first = true;


    public static void displayPromptForEnablingGPS(
            final Activity activity) {
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Enable either GPS or any other location"
                + " service to find current location.  Click OK to go to"
                + " location services settings to let you do so.";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                activity.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        updateValuesFromBundle(savedInstanceState);

        if (mRoomCardFrame.getVisibility() == View.VISIBLE)
            mRoomCardFrame.setVisibility(View.GONE);

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            displayPromptForEnablingGPS(this);
        }

        // First we need to check availability of play services
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.home_map_view);

        mapFragment.getMapAsync(this);

        NoNet.monitor(this)
                .poll()
                .banner("No network connection.")
                .start();

        NoNet.configure()
                .endpoint("https://google.com")
                .timeout(5)
                .connectedPollFrequency(60)
                .disconnectedPollFrequency(1);

        mBtnCloseRmCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRoomCardFrame.setVisibility(View.GONE);
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 0));
                mBtnEnterRoom.setVisibility(View.GONE);
                mBtnCreateRoom.setVisibility(View.VISIBLE);
            }
        });


        mBtnCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateRoomActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });


    }

    /**
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }

            return false;
        }

        return true;
    }


    private void buildGoogleApiClient() {
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    /**
     * Starting the location updates
     */
    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private double getDouble(double upper, double lower) {
        return new Random().nextDouble() * (upper - lower) + lower;
    }

    private LatLng[] getCorners(LatLng point, double range) {
        double c = 180 / Math.PI;
        //radius of the earth in miles, radius range in miles,
        int earthRadius = 3959;
        //earth curve change
        double earthDelt = range / (double) earthRadius;
        double latOffset = earthDelt * c;
        double lonOffset = latOffset / Math.cos(point.latitude * Math.PI / 180);
        double latLowerBound = point.latitude - latOffset;
        double latUpperBound = point.latitude + latOffset;
        double lonLowerBound = point.longitude - lonOffset;
        double lonUpperBound = point.longitude + lonOffset;
        return new LatLng[]{new LatLng(latLowerBound, lonLowerBound), new LatLng(latUpperBound, lonUpperBound)};
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and
            // make sure that the Start Updates and Stop Updates buttons are
            // correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
            }

            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocationis not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(
                        LAST_UPDATED_TIME_STRING_KEY);
            }
        }
    }

    private void setFragment(Fragment fragment) {
        if (mRoomCardFrame.getVisibility() != View.VISIBLE) {
            mRoomCardFrame.setVisibility(View.VISIBLE);
        }
        if (fragment != currentFrag) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.home_feed_fragment, fragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
            currentFrag = fragment;
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onFragmentInteraction() {
        //new Thread(getRmRunnable).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
            @Override
            public void onCircleClick(Circle circle) {
                // TODO: 3/6/2017 Add functionality when a circle is clicked on.
                //circle.getCenter();
                for (int i = 0; i < list.size(); i++) {
                    Room room = list.get(i);
                    if (circle.getCenter().latitude == room.lat
                            && circle.getCenter().longitude == room.lon) {
                        focusedRoom = room;
                        setFragment(RoomCardFragment.newInstance());
                        LatLngBounds.Builder tBuilder = new LatLngBounds.Builder();
                        LatLng[] corners = getCorners(new LatLng(room.lat, room.lon), ROOM_RANGE);
                        tBuilder.include(corners[0]);
                        tBuilder.include(corners[1]);
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(tBuilder.build(), 0));
                        mBtnCreateRoom.setVisibility(View.GONE);
                        mBtnEnterRoom.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
        });
        mGoogleMap = googleMap;
    }

    public Room getFocusedRoom() {
        return focusedRoom;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setLastLocation();
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    private void setLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.

                int MY_PERMISSIONS_REQUEST_LOCATION = 5;
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            }
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());

    }

    @Override
    public void onLocationChanged(Location location) {
        if (mLastLocation == null) {
            mLastLocation = location;
        } else if (mLastLocation.equals(location)) {
            return;
        }
        if (first) {

            // Called when a new location is found by the network location provider.
            User user = User.getUser();
            user.location = new LatLng(location.getLatitude(), location.getLongitude());

            double c = 180 / Math.PI;
            //radius of the earth in miles, radius range in miles,
            int earthRadius = 3959, range = 5;
            //earth curve change
            double earthDelt = range / (double) earthRadius;
            double latOffset = earthDelt * c;
            double lonOffset = latOffset / Math.cos(location.getLatitude() * Math.PI / 180);
            double latLowerBound = location.getLatitude() - latOffset;
            double latUpperBound = location.getLatitude() + latOffset;
            double lonLowerBound = location.getLongitude() - lonOffset;
            double lonUpperBound = location.getLongitude() + lonOffset;
            int plots = 20;
            for (int i = 0; i < plots; i++) {
                double aDouble1 = getDouble(latLowerBound, latUpperBound);
                double aDouble2 = getDouble(lonLowerBound, lonUpperBound);
                LatLng randomRoomLocation = new LatLng(aDouble1, aDouble2);
                list.add(new Room("Test Room: " + i, true, "Testing, testing 1, 2, 3.", aDouble1, aDouble2));
                for (int rad = 100; rad <= range * 40; rad += 50) {
                    mGoogleMap.addCircle(new CircleOptions()
                            .center(randomRoomLocation)
                            .radius(rad)
                            .clickable(true)
                            .fillColor(Color.argb(75, 255, 0, 0))
                            .strokeWidth(0)
                            .zIndex(1)
                    );

                }
                LatLng[] corners = getCorners(randomRoomLocation, ROOM_RANGE);
                builder.include(corners[0]);
                builder.include(corners[1]);
            }


            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (SocketCluster.getInstance().emitGPS() == SUCCESS) {
                        while (mGoogleMap == null) ;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                User user1 = User.getUser();
                                //mGoogleMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                                double v = 11265.408 / 2;
                                mGoogleMap.addCircle(new CircleOptions()
                                        .center(user1.location)
                                        .radius(v)
                                        .clickable(true)
                                        .fillColor(Color.argb(128, 255, 255, 102))
                                        .strokeWidth(2)
                                        .strokeColor(Color.WHITE));
                                LatLng[] corners = getCorners(user1.location, USER_RANGE);
                                builder.include(corners[0]);
                                builder.include(corners[1]);
                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 0));
                            }
                        });
                    } else {
                        //setFragment(spinnerFragment);
                    }
                }
            }).start();
            //builder.include(new LatLng(location.getLatitude(), location.getLongitude()));

            //animate camera
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 0));
            first = false;
        }


    }
}
