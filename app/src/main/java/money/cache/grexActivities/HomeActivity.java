package money.cache.grexActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import butterknife.Bind;
import butterknife.ButterKnife;
import grexClasses.GrexSocket;
import grexClasses.User;

//TODO: make a splash screen for whatever page is the home page like 'splash screens -> down ken burns' from UI app

//TODO: the apps home page should be like 'tabs' from the app

public class HomeActivity extends AppCompatActivity {

    @Bind(R.id.btn_createRoom)
    Button _createRoomBtn;

    //TODO: populate list view eg: https://github.com/ldbnr4/DonateTheDistance/blob/master/app/src/main/java/com/example/lorenzo/donatethedistance/ProfilePageView.java
    @Bind(R.id.list_listOfUserRooms)
    ListView _listView;

    public static User user = User.getUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        _createRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateRoomActivity.class);
                startActivity(intent);
                finish();
            }
        });

        GrexSocket.emit_getRooms();
        //TODO: add timeout element
        while(!GrexSocket.roomUpdate);
        System.out.println(user.getRoomsIn().size());
        System.out.println("GOT EM");
    }
}
