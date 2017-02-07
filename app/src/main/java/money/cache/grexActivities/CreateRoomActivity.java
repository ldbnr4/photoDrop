package money.cache.grexActivities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.dialog.DoubleDateAndTimePickerDialog;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import grexClasses.SocketActivity;
import tasks.CreateRoomActivityTask;

public class CreateRoomActivity extends SocketActivity {
    private static final int PICK_IMAGE_REQUEST = 2;

    //TODO: maybe add an image that switches back and forth as public/private switch chnages
    //TODO: Make this look like 'Dialog social' from UI app
    //TODO: Allow 'Now' and 'Never' to be options again after mUser changes the times
    //TODO: If start is 'Now' convert to the actual time
    //TODO: Times should have dates and times (eg. Today @ Now, Tomorrow @ 2:30pm, October 10th, 2018 @ 1:15 AM)

    @Bind(R.id.txt_details)
    EditText _txtMomDetails;

    @Bind(R.id.txt_mom_name)
    EditText _txtMomName;

    @Bind(R.id.btn_mom_done)
    ImageButton _btnMomDone;

    @Bind(R.id.switch_public)
    Switch _switchPublic;

    @Bind(R.id.create_event_img)
    ImageView _imgRoomImg;

    SingleDateAndTimePicker picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        ButterKnife.bind(this);

        _imgRoomImg.getLayoutParams().height = 300;
        _imgRoomImg.getLayoutParams().width = 300;

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

        _btnMomDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: create a new task to talk to database
                _btnMomDone.setClickable(false);
                String rmName = _txtMomName.getText().toString();
                boolean pub = _switchPublic.isChecked();
                String begin = "BEGIN";
                String end = "END";
                String desc = _txtMomDetails.getText().toString();

                CreateRoomActivityTask task = new CreateRoomActivityTask(CreateRoomActivity.this);
                task.execute(rmName, String.valueOf(pub), begin, end, desc);
            }
        });

        _imgRoomImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DoubleDateAndTimePickerDialog.Builder(CreateRoomActivity.this)
                        //.bottomSheet()
                        //.curved()
                        //.minutesStep(15)
                        .title("Double")
                        .tab0Text("Depart")
                        .tab1Text("Return")
                        .listener(new DoubleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(List<Date> dates) {

                            }
                        }).display();
                /*Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image*//*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);*/
            }
        });

    }

    public ImageView getImageView() {
        return _imgRoomImg;
    }

    public void onFragmentInteraction(int hour, int min) {
        String time = hour + ":" + min;
        try{
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm", Locale.US);
            final Date date = sdf.parse(time);
            time = new SimpleDateFormat("K:mm a", Locale.US).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        /*if (fragment.isStart) {
            _btnBegin.setText(time);
        } else {
            _btnEnd.setText(time);
        }
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));

                _imgRoomImg.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
