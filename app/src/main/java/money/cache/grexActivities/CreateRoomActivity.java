package money.cache.grexActivities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import grexClasses.GrexSocket;
import grexClasses.Room;
import grexClasses.User;

public class CreateRoomActivity extends AppCompatActivity implements DatePicker.OnFragmentInteractionListener {
    private static final int PICK_IMAGE_REQUEST = 2;

    //TODO: maybe add an image that switches back and forth as public/private switch chnages
    //TODO: Make this look like 'Dialog social' from UI app

    @Bind(R.id.btn_add_image)
    ImageButton _btnAddImg;

    @Bind(R.id.txt_details)
    EditText _txtMomDetails;

    @Bind(R.id.txt_mom_name)
    EditText _txtMomName;

    @Bind(R.id.btn_mom_done)
    ImageButton _btnMomDone;

    @Bind(R.id.btn_begin)
    Button _btnBegin;

    @Bind(R.id.btn_end)
    Button _btnEnd;

    @Bind(R.id.switch_public)
    Switch _switchPublic;

    @Bind(R.id.room_image)
    ImageView _imgRoomImg;

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



        _btnMomDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _btnMomDone.setClickable(false);
                String rmName = _txtMomName.getText().toString();
                boolean pub = _switchPublic.isChecked();
                String begin = _btnBegin.getText().toString();
                String end = _btnEnd.getText().toString();
                String desc = _txtMomDetails.getText().toString();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ((BitmapDrawable)_imgRoomImg.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray();

                Room newRoom = new Room(rmName, pub, User.getUser().name, begin, end, desc);
                //newRoom.setImage(Base64.encodeToString(b, Base64.DEFAULT));
                GrexSocket.emit_newRoom(newRoom);

                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        _btnAddImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));

                _btnAddImg.setImageDrawable(getDrawable(R.drawable._edit_image));
                _imgRoomImg.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
