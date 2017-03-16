package money.cache.grex;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import grexClasses.RecyclerViewItemDecorator;
import grexClasses.User;
import grexClasses.UserFriendsAdapter;

public class UserFriendsActivity extends AppCompatActivity {

    @Bind(R.id.grid_user_friends)
    RecyclerView mUserGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_friends);
        ButterKnife.bind(this);

        mUserGrid.setHasFixedSize(true);
        mUserGrid.setLayoutManager(new GridLayoutManager(this, 3));

        ArrayList<User> friendList = new ArrayList<>();
        for(int i = 0; i < 50; i++){
            friendList.add(new User("Amigo Number"+i));
        }

        mUserGrid.addItemDecoration(new RecyclerViewItemDecorator(10));
        mUserGrid.setAdapter(new UserFriendsAdapter(friendList.toArray(new User[]{})));
    }

    @OnClick(R.id.btn_user_friends_back)
    void goBack() {
        finish();
    }
}
