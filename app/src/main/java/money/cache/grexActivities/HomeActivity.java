package money.cache.grexActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import butterknife.Bind;
import butterknife.ButterKnife;
import grexClasses.GrexSocket;
import grexClasses.LocalDatabase;
import grexClasses.SocketActivity;
import grexInterfaces.SocketTask;
import grexLayout.ConnectivityFragment;
import grexLayout.RoomFeedFragment;
import grexLayout.SpinnerFragment;

import static grexClasses.GrexSocket.RET_STATUS.NONE;
import static grexClasses.GrexSocket.RET_STATUS.SUCCESS;

public class HomeActivity extends SocketActivity implements ConnectivityFragment.OnFragmentInteractionListener {

    @Bind(R.id.btn_createRoom)
    Button mBtnCreateRoom;
    @Bind(R.id.tab_layout)
    TabLayout mTabLayout;
    @Bind(R.id.activity_home)
    LinearLayout mHomeLayout;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private Tab _pastTab;
    private Tab _liveTab;
    private Tab _futureTab;
    private SpinnerFragment fragment;

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

        ViewTreeObserver vto = mHomeLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mHomeLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                new GetRoomsTask().execute();
                setFragment(SpinnerFragment.newInstance());
                setUpTabs();
            }
        });

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
        finish();
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_feed_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    class GetRoomsTask extends SocketTask<Void, Void, Void> {
        private ProgressDialog progressDialog;
        private boolean done = false;
        private GrexSocket grexSocket = GrexSocket.getGrexSocket(HomeActivity.this);

        @Override
        protected void onPreExecute() {
            Runtime.getRuntime().gc();
            progressDialog = ProgressDialog.show(HomeActivity.this,
                    "Hold up!",
                    "Connecting you to the flock...");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                grexSocket.emitGetRooms();
                done = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            if (done) {
                progressDialog.setMessage("Getting your swarms...");
                done = false;
            }
        }

        @Override
        protected void onPostExecute(Void param) {
            progressDialog.dismiss();
            // execution of result of Long time consuming operation
            switch (GrexSocket.connection_status) {
                case CONNECTED:
                    while (GrexSocket.getGetRooms() == NONE) ;
                    if (GrexSocket.getGetRooms() == SUCCESS) {
                        // TODO: sync local database with server
                        Cursor allRooms = LocalDatabase.getInstance(HomeActivity.this).getAllRooms();
                        if (allRooms.getCount() > 0) {
                            allRooms.moveToFirst();
                            while (!allRooms.isAfterLast()) {
                                String roomString = allRooms.getString(0);
                                try {
                                    grexSocket.emitRoom(roomString);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                allRooms.moveToNext();
                            }
                        }
                        setFragment(RoomFeedFragment.newInstance());
                    }
                    break;
                    case INTERNET_DOWN:
                        setFragment(ConnectivityFragment.newInstance("Check your network connection"));
                        cancel(true);
                        break;
                    case SERVER_DOWN:
                        setFragment(ConnectivityFragment.newInstance("Well this is awkward..."));
                        cancel(true);
                        break;
            }
            Runtime.getRuntime().gc();
        }
    }
}
