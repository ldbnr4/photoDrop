package money.cache.grex;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class InsideRooomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inside_rooom);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_inside_rm_back)
    void goBack() {
        finish();
    }
}
