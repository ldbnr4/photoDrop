package money.cache.grexActivities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.Tab;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import grexClasses.Room;
import grexClasses.RoomAdapter;
import grexClasses.User;
import grexEnums.RET_STATUS;

//TODO: make a splash screen for whatever page is the home page like 'splash screens -> down ken burns' from UI app

//TODO: the apps home page should be like 'tabs' from the app

//TODO: communication of data from the server is not in sync with GSON fortmat. Needs to be the same as sent

public class HomeActivity extends SocketActivity {

    public static User mUser = User.getUser();
    @Bind(R.id.btn_createRoom)
    Button mBtnCreateRoom;
    @Bind(R.id.tab_layout)
    TabLayout mTabLayout;
    @Bind(R.id.my_recycler_view)
    RecyclerView mRecyclerView;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //
        Set<Room> roomsIn = mUser.getRoomsIn();

        // specify an adapter (see also next example)
        RecyclerView.Adapter mAdapter = new RoomAdapter(HomeActivity.this, roomsIn);
        mRecyclerView.setAdapter(mAdapter);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onPostCreate(Bundle bundle){
        super.onPostCreate(bundle);
        setUpTabs();
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
    }

    private void setUpTabs() {
        final Tab _pastTab = mTabLayout.newTab().setText("Past");
        final Tab _liveTab = mTabLayout.newTab().setText("Live!");
        final Tab _futureTab = mTabLayout.newTab().setText("Future");

        mTabLayout.addTab(_pastTab);
        mTabLayout.addTab(_liveTab);
        mTabLayout.addTab(_futureTab);

        mBtnCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateRoomActivity.class);
                startActivity(intent);
            }
        });

        //TODO: create a new task to get rooms
        grexSocket.emitGetRooms();
        while (!grexSocket.roomUpdate) ;

        //TODO: each tab should load a google cards travel like page
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                if (tab.equals(_pastTab)) {
                    System.out.println("PAST");
                } else if (tab.equals(_liveTab)) {
                    System.out.println("LIVE");
                } else if (tab.equals(_futureTab)) {
                    System.out.println("FUTURE");
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
}
