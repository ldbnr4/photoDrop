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
import tasks.GetRoomsTask;

import static grexEnums.RET_STATUS.NONE;
import static grexEnums.RET_STATUS.SUCCESS;

//TODO: make a splash screen for whatever page is the home page like 'splash screens -> down ken burns' from UI app

//TODO: the apps home page should be like 'tabs' from the app

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
    private Tab _pastTab;
    private Tab _liveTab;
    private Tab _futureTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Set<Room> roomsIn = mUser.getRoomsIn();
        RecyclerView.Adapter mAdapter = new RoomAdapter(HomeActivity.this, roomsIn);
        mRecyclerView.setAdapter(mAdapter);

        mBtnCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateRoomActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        try {
            setUpTabs();
        } catch (Exception e) {
            e.printStackTrace();
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

    private void setUpTabs() throws Exception {

        if (mTabLayout.getTabCount() == 3) {
            _pastTab = mTabLayout.getTabAt(0);
            _liveTab = mTabLayout.getTabAt(1);
            _futureTab = mTabLayout.getTabAt(2);
        } else
            throw new Exception();


        GetRoomsTask getRoomsTask = new GetRoomsTask(this);
        getRoomsTask.execute();

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
}
