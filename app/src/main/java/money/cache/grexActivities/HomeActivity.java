package money.cache.grexActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.Tab;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import grexClasses.GrexSocket;
import grexClasses.Room;
import grexClasses.User;

//TODO: make a splash screen for whatever page is the home page like 'splash screens -> down ken burns' from UI app

//TODO: the apps home page should be like 'tabs' from the app

public class HomeActivity extends AppCompatActivity {

    @Bind(R.id.btn_createRoom)
    Button _createRoomBtn;

    @Bind(R.id.tab_layout)
    TabLayout _tabLayout;

    //TODO: convert listview to cards
    @Bind(R.id.list_listOfUserRooms)
    ListView _listView;

    public static User user = User.getUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        final Tab _pastTab = _tabLayout.newTab().setText("Past");
        final Tab _liveTab = _tabLayout.newTab().setText("Live!");
        final Tab _futureTab = _tabLayout.newTab().setText("Future");

        _tabLayout.addTab(_pastTab);
        _tabLayout.addTab(_liveTab);
        _tabLayout.addTab(_futureTab);

        _createRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateRoomActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //TODO: create a new task to get rooms
        GrexSocket.emit_getRooms();
        //while(!GrexSocket.roomUpdate);
        ArrayAdapter<Room> arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                new ArrayList<>(new ArrayList<>(user.getRoomsIn()))
        );
        _listView.setAdapter(arrayAdapter);

        //TODO: each tab should load a google cards travel like page
        _tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                if(tab.equals(_pastTab)){
                    System.out.println("PAST");
                }
                else if(tab.equals(_liveTab)){
                    System.out.println("LIVE");
                }
                else if(tab.equals(_futureTab)){
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
}
