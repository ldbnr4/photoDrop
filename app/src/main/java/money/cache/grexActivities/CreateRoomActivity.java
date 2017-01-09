package money.cache.grexActivities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CreateRoomActivity extends AppCompatActivity implements DatePicker.OnFragmentInteractionListener {

    //TODO: Make switch text "switch" ;) back and forth from 'Public' to 'Private' (maybe add an image that switches back and forth too)
    //TODO: Make this look like 'Dialog social' from UI app

    @Bind(R.id.btn_begin)
    Button _btnBegin;

    @Bind(R.id.btn_end)
    Button _btnEnd;

    boolean startFrag = true;
    private DatePicker fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        ButterKnife.bind(this);

        _btnBegin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startFrag = true;
                FragmentManager fm = getSupportFragmentManager();
                fragment = new DatePicker();
                fm.beginTransaction().add(R.id.activity_create_room, fragment).commit();
            }
        });

        _btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFrag = false;
                fragment = new DatePicker();
                getSupportFragmentManager().beginTransaction().add(R.id.activity_create_room, fragment).commit();
            }
        });

    }

    @Override
    public void onFragmentInteraction(int hour, int min) {
        if (startFrag) {
            _btnBegin.setText(hour + ":" + min);
        } else {
            _btnEnd.setText(hour + ":" + min);
        }
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }
}
