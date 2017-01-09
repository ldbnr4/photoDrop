package money.cache.grexActivities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CreateRoomActivity extends AppCompatActivity implements DatePicker.OnFragmentInteractionListener {

    //TODO: Make switch text "switch" ;) back and forth from 'Public' to 'Private' (maybe add an image that switches back and forth too)
    //TODO: Make this look like 'Dialog social' from UI app

    @Bind(R.id.btn_begin)
    Button _btnBegin;

    @Bind(R.id.btn_end)
    Button _btnEnd;

    @Bind(R.id.switch_public)
    Switch _switchPublic;

    private DatePicker fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        ButterKnife.bind(this);

        _btnBegin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                fragment = new DatePicker();
                fragment.isStart = true;
                fm.beginTransaction().add(R.id.activity_create_room, fragment).commit();
            }
        });

        _btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new DatePicker();
                fragment.isStart = false;
                getSupportFragmentManager().beginTransaction().add(R.id.activity_create_room, fragment).commit();
            }
        });

        _switchPublic.setText("Public");

        _switchPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_switchPublic.isChecked()){
                    _switchPublic.setText("Public");
                }
                else{
                    _switchPublic.setText("Private");
                }
            }
        });

    }

    @Override
    public void onFragmentInteraction(int hour, int min) {
        String time = hour + ":" + min;
        try{
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm", Locale.US);
            final Date date = sdf.parse(time);
            time = new SimpleDateFormat("K:mm a", Locale.US).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (fragment.isStart) {
            _btnBegin.setText(time);
        } else {
            _btnEnd.setText(time);
        }
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }
}
