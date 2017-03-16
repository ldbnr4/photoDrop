package money.cache.grex;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import grexClasses.RecyclerViewItemDecorator;
import grexClasses.Room;
import grexClasses.UserRoomsAdapter;

public class UserRoomsActivity extends AppCompatActivity {

    @Bind(R.id.list_user_rooms)
    RecyclerView mUserRoomsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_rooms);
        ButterKnife.bind(this);

        mUserRoomsList.setHasFixedSize(true);
        mUserRoomsList.setLayoutManager( new LinearLayoutManager(this));

        ArrayList<Room> rooms = new ArrayList<>();
        for(int i = 0; i < 50; i++){
            rooms.add(new Room("My Favorite Room "+i));
        }

        mUserRoomsList.addItemDecoration(new RecyclerViewItemDecorator(10));
        mUserRoomsList.setAdapter(new UserRoomsAdapter(rooms.toArray(new Room[]{})));

    }

    @OnClick(R.id.btn_user_rooms_back)
    void go_back(){
        finish();
    }
}
