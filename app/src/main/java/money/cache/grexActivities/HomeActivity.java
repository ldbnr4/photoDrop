package money.cache.grexActivities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import butterknife.Bind;
import butterknife.ButterKnife;
import grexClasses.SocketActivity;
import grexClasses.SocketCluster;
import grexInterfaces.SocketTask;
import grexLayout.ConnectivityFragment;
import grexLayout.RoomFeedFragment;
import grexLayout.SpinnerFragment;

import static grexEnums.RET_STATUS.SUCCESS;


// TODO: 2/23/2017 onPostExecute cant wait. move to another function.
public class HomeActivity extends SocketActivity implements ConnectivityFragment.OnFragmentInteractionListener {

    @Bind(R.id.btn_createRoom)
    FloatingActionButton mBtnCreateRoom;
    @Bind(R.id.tab_layout)
    TabLayout mTabLayout;
    @Bind(R.id.activity_home)
    RelativeLayout mHomeLayout;
    // Acquire a reference to the system Location Manager
    LocationManager locationManager;
    // Define a listener that responds to location updates
    LocationListener locationListener;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private Tab _pastTab;
    private Tab _liveTab;
    private Tab _futureTab;
    private boolean listening = false;
    private ConnectivityFragment internetDownFrag = ConnectivityFragment.newInstance("Check your network connection");
    private RoomFeedFragment roomFeedFrag = RoomFeedFragment.newInstance();
    private ConnectivityFragment serverDownFrag = ConnectivityFragment.newInstance("Well this is awkward...");
    private SpinnerFragment spinnerFragment = new SpinnerFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

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
                SocketCluster.emitGPS(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        new GetRoomsTask().execute();
        setUpTabs();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setUpTabs() {
        _pastTab = mTabLayout.getTabAt(0);
        _liveTab = mTabLayout.getTabAt(1);
        _futureTab = mTabLayout.getTabAt(2);

        //TODO: each tab should load a google cards travel like page
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                if (tab.equals(_liveTab)) {
                    if (!listening) {
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
                        listening = true;
                    }
                } else {
                    //Remove the listener you previously added
                    locationManager.removeUpdates(locationListener);
                    listening = false;
                }
            }

            @Override
            public void onTabUnselected(Tab tab) {

            }

            @Override
            public void onTabReselected(Tab tab) {

            }
        });

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Home Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onFragmentInteraction() {
        new GetRoomsTask().execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_feed_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    void checkGetRoomsStatus() {
        while (SocketCluster.getGetRoomsStat() != SUCCESS) {
            SocketCluster.emitGetRooms();
        }
        roomFeedFrag = RoomFeedFragment.newInstance();
        setFragment(roomFeedFrag);
        Runtime.getRuntime().gc();
    }

    //// TODO: 2/23/2017 Get rid of this task
    class GetRoomsTask extends SocketTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Runtime.getRuntime().gc();
            setFragment(spinnerFragment);
        }

        @Override
        protected Void doInBackground(Void... params) {
            switch (SocketCluster.emitGetRooms()) {
                case CONNECTED:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            checkGetRoomsStatus();
                        }
                    }).start();
                    break;
                case INTERNET_DOWN:
                    setFragment(internetDownFrag);
                    break;
                case SERVER_DOWN:
                    setFragment(serverDownFrag);
                    break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            Runtime.getRuntime().gc();
        }
    }
}
