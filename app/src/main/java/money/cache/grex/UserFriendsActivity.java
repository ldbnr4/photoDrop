package money.cache.grex;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserFriendsActivity extends AppCompatActivity {

    @Bind(R.id.grid_user_friends)
    GridView mUserGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_friends);
        ButterKnife.bind(this);

        String[] myStringArray = {"hello", "world"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_gallery_item, myStringArray);

        mUserGrid.setAdapter(adapter);
    }

    @OnClick(R.id.btn_user_friends_back)
    void goBack() {
        finish();
    }
}
