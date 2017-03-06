package money.cache.grexActivities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.keiferstone.nonet.NoNet;

import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import grexClasses.SocketActivity;
import grexClasses.SocketCluster;
import grexClasses.User;
import grexEnums.ROOM_CATEGORY;
import grexLayout.ConnectivityFragment;
import grexLayout.RoomFeedFragment;
import grexLayout.SpinnerFragment;

import static grexEnums.RET_STATUS.SUCCESS;

// TODO: 2/23/2017 onPostExecute cant wait. move to another function.
public class HomeActivity extends SocketActivity implements ConnectivityFragment.OnFragmentInteractionListener, OnMapReadyCallback {

    private final LatLngBounds.Builder builder = new LatLngBounds.Builder();
    @Bind(R.id.btn_createRoom)
    FloatingActionButton mBtnCreateRoom;
    @Bind(R.id.activity_home)
    RelativeLayout mHomeLayout;
    SupportMapFragment mapFragment;
    // Acquire a reference to the system Location Manager
    LocationManager locationManager;
    // Define a listener that responds to location updates
    LocationListener locationListener;
    GoogleMap googleMap;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

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


        mBtnCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateRoomActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
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
                    for (int rad = 50; rad <= range * 40; rad += 50) {
                        googleMap.addCircle(new CircleOptions()
                                .center(randomRoomLocation)
                                .radius(rad)
                                .clickable(true)
                                .fillColor(Color.TRANSPARENT)
                                .strokeWidth(10)
                                .zIndex(1)
                                .strokeColor(Color.argb(50, 255, 0, 0)));

                    }
                    LatLng[] corners = getCorners(randomRoomLocation, 0.25);
                    builder.include(corners[0]);
                    builder.include(corners[1]);
                }
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 0));

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (SocketCluster.getInstance().emitGPS() == SUCCESS) {
                            while (googleMap == null) ;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    User user1 = User.getUser();
                                    //googleMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                                    double v = 11265.408 / 2;
                                    googleMap.addCircle(new CircleOptions()
                                            .center(user1.location)
                                            .radius(v)
                                            .clickable(true)
                                            .fillColor(Color.argb(128, 255, 255, 102))
                                            .strokeWidth(2)
                                            .strokeColor(Color.WHITE));
                                    LatLng[] corners = getCorners(user1.location, 5);
                                    builder.include(corners[0]);
                                    builder.include(corners[1]);
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 0));
                                }
                            });
                        } else {
                            //setFragment(spinnerFragment);
                        }
                        /*if (new Random().nextBoolean()) {
                            User.getUser().lat += 5;
                            User.getUser().lon += 5;
                        }*/
                        //SocketCluster.getInstance().emitRoom(new Room("Everything is Awesome", false, "The best time of your life", User.getUser().lat, User.getUser().lon));
                    }
                }).start();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        if (ActivityCompat.checkSelfPermission(HomeActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            int MY_PERMISSIONS_REQUEST_LOCATION = 5;
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            return;
        }
        //SocketCluster.emitGPS(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener);
        //Remove the listener you previously added
        //locationManager.removeUpdates(locationListener);

        //new Thread(getRmRunnable).start();

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

    @Override
    public void onFragmentInteraction() {
        //new Thread(getRmRunnable).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setFragment(Fragment fragment) {
        if (fragment != currentFrag) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.home_feed_fragment, fragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
            currentFrag = fragment;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
            @Override
            public void onCircleClick(Circle circle) {
                // TODO: 3/6/2017 Add functionality when a circle is clicked on.
            }
        });
        this.googleMap = googleMap;
    }
}
