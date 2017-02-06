package money.cache.grexActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.Tab;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import butterknife.Bind;
import butterknife.ButterKnife;
import grexClasses.GrexSocket;
import grexClasses.SocketActivity;
import grexClasses.User;
import grexEnums.CONNECTION_STATUS;
import grexEnums.RET_STATUS;
import layout.ConnectivityFragment;

import static grexEnums.RET_STATUS.NONE;
import static grexEnums.RET_STATUS.SUCCESS;

//TODO: make a splash screen for whatever page is the home page like 'splash screens -> down ken burns' from UI app

//TODO: the apps home page should be like 'tabs' from the app

public class HomeActivity extends SocketActivity implements ConnectivityFragment.OnFragmentInteractionListener {

    public static User mUser = User.getUser();
    @Bind(R.id.btn_createRoom)
    Button mBtnCreateRoom;
    @Bind(R.id.tab_layout)
    TabLayout mTabLayout;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private Tab _pastTab;
    private Tab _liveTab;
    private Tab _futureTab;
    private ProgressDialog progressDialog;

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

        setUpTabs();

        new GetRoomsTask().execute();

        if(GrexSocket.connection_status != CONNECTION_STATUS.CONNECTED){
            // Check that the activity is using the layout version with
            // the fragment_container FrameLayout
            if (findViewById(R.id.home_feed_fragment) != null) {

                // However, if we're being restored from a previous state,
                // then we don't need to do anything and should return or else
                // we could end up with overlapping fragments.
                if (savedInstanceState != null) {
                    return;
                }

                // Create a new Fragment to be placed in the activity layout
                ConnectivityFragment firstFragment = ConnectivityFragment.newInstance("This is an error");

                // In case this activity was started with special instructions from an
                // Intent, pass the Intent's extras to the fragment as arguments
                firstFragment.setArguments(getIntent().getExtras());

                // Add the fragment to the 'fragment_container' FrameLayout
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.home_feed_fragment, firstFragment).commit();
            }
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onFail() {
        //TODO: implement logic for failure
    }

    @Override
    public void onSuccess() {
        //TODO: implement logic for success
    }

    @Override
    public void onPostExecute(RET_STATUS retStatResult) {
        //TODO: implement logic based on server response
        if(retStatResult == NONE) return;
        if(retStatResult != SUCCESS){
            onFail();
        }
        else{
            onSuccess();
        }
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

    }

    class GetRoomsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(HomeActivity.this,
                    "ProgressDialog",
                    "Updating your rooms...");
        }

        @Override
        protected Void doInBackground(Void... params) {
            GrexSocket.getGrexSocket().emitGetRooms();
            switch (GrexSocket.connection_status){
                case CONNECTED:
                    int count = 0;
                    while (count < 2) {
                        attemptCommunication();
                        if (GrexSocket.getRoomsStatus == NONE) {
                            count++;
                        } else
                            return null;
                    }
                    break;

                default:
                    break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            // execution of result of Long time consuming operation
            progressDialog.dismiss();
            /*switch (GrexSocket.connection_status){
                case INTERNET_DOWN:
                    noInternet();
                    break;
                case SERVER_DOWN:
                    noServer();
                    break;
            }*/
        }

        private void attemptCommunication(){
            long totalTime = 2500;
            long startTime = System.currentTimeMillis();
            boolean toFinish = false;
            while (!toFinish && GrexSocket.getRoomsStatus == NONE) {
                toFinish = (System.currentTimeMillis() - startTime >= totalTime);
            }
        }
    }
}
