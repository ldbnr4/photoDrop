package money.cache.grexActivities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import butterknife.Bind;
import butterknife.ButterKnife;
import grexClasses.User;

public class HomeActivity extends AppCompatActivity {

    @Bind(R.id.btn_createRoom)
    Button _createRoomBtn;

    //TODO: populate list view eg: https://github.com/ldbnr4/DonateTheDistance/blob/master/app/src/main/java/com/example/lorenzo/donatethedistance/ProfilePageView.java
    @Bind(R.id.list_listOfUserRooms)
    ListView _listView;

    User user = User.getUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        _createRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: send message to server to create a new Room with the user as the host
            }
        });
    }
}
