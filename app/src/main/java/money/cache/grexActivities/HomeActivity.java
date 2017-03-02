package money.cache.grexActivities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Timer;
import java.util.TimerTask;

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

    //Declare the timer
    private final Timer t = new Timer();
    @Bind(R.id.btn_createRoom)
    FloatingActionButton mBtnCreateRoom;
    @Bind(R.id.tab_layout)
    TabLayout mTabLayout;
    @Bind(R.id.activity_home)
    RelativeLayout mHomeLayout;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private Tab _pastTab;
    private Tab _liveTab;
    private Tab _futureTab;

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
                tabSelected(tab);
            }

            @Override
            public void onTabUnselected(Tab tab) {

            }

            @Override
            public void onTabReselected(Tab tab) {

            }
        });

    }

    private void tabSelected(Tab selcted) {
        if (selcted.equals(_pastTab)) {
            System.out.println("PAST");
        } else if (selcted.equals(_liveTab)) {
            System.out.println("LIVE");
        } else if (selcted.equals(_futureTab)) {
            System.out.println("FUTURE");
        }
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
        t.cancel();
        finish();
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_feed_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    void checkGetRoomsStatus() {
        int timer = 0;
        int delay = 250;
        while (timer < 12) {
            if (SocketCluster.getGetRooms() == SUCCESS) break;
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timer++;
        }
        // TODO: sync local database with server
        /*Cursor allRooms = LocalDatabase.getInstance(HomeActivity.this).getAllRooms();
        if (allRooms.getCount() > 0) {
            allRooms.moveToFirst();
            while (!allRooms.isAfterLast()) {
                String roomString = allRooms.getString(0);
                try {
                    GrexSocket.emitRoom(roomString);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LocalDatabase.getInstance(HomeActivity.this).deleteRoom(roomString);
                allRooms.moveToNext();
            }
        }*/
        if (SocketCluster.getGetRooms() == SUCCESS) {
            setFragment(RoomFeedFragment.newInstance());
        } else {
            switch (SocketCluster.emitGetRooms()) {
                case CONNECTED:
                    setFragment(ConnectivityFragment.newInstance("Something really strange is happening..."));
                    break;
                case INTERNET_DOWN:
                    setFragment(ConnectivityFragment.newInstance("Check your network connection"));
                    break;
                case SERVER_DOWN:
                    setFragment(ConnectivityFragment.newInstance("Well this is awkward..."));
                    break;
            }
        }
        Runtime.getRuntime().gc();
    }

    //// TODO: 2/23/2017 Get rid of this task
    class GetRoomsTask extends SocketTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Runtime.getRuntime().gc();
            setFragment(new SpinnerFragment());
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
                    setFragment(ConnectivityFragment.newInstance("Check your network connection"));
                    break;
                case SERVER_DOWN:
                    setFragment(ConnectivityFragment.newInstance("Well this is awkward..."));
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
