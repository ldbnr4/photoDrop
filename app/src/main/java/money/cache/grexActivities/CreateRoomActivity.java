package money.cache.grexActivities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.github.florent37.singledateandtimepicker.dialog.DoubleDateAndTimePickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import grexClasses.Room;
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
    @Bind(R.id.img_createRm_vis)
    ImageView _imgVis;
    @Bind(R.id.txt_createRm_from)
    TextView _txtFrom;
    @Bind(R.id.txt_createRm_till)
    TextView _txtTill;
    @Bind(R.id.img_createRm_dates)
    ImageView _imgDates;
    @Bind(R.id.tr_createRm_rmImg)
    ImageView tr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        ButterKnife.bind(this);

        _switchPublic.setText("Public");

        _switchPublic.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_switchPublic.isChecked()) {
                    _switchPublic.setText("Public");
                } else {
                    _switchPublic.setText("Private");
                }
            }
        });

        _imgVis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _switchPublic.setChecked(!_switchPublic.isChecked());
                _switchPublic.callOnClick();
            }
        });

        _imgDates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DoubleDateAndTimePickerDialog.Builder(CreateRoomActivity.this)
                        //.bottomSheet()
                        .curved()
                        //.minutesStep(15)
                        .mainColor(ContextCompat.getColor(CreateRoomActivity.this,R.color.accent))
                        .backgroundColor(ContextCompat.getColor(CreateRoomActivity.this, R.color.dark_background))
                        .title("Event times")
                        .tab0Text("From")
                        .tab1Text("Till")
                        .mustBeOnFuture()
                        .listener(new DoubleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(List<Date> dates) {
                                SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d\nH:mm aa z", Locale.US);
                                _txtFrom.setText(df.format(dates.get(0)));
                                _txtTill.setText(df.format(dates.get(1)));
                            }
                        }).display();
            }
        });

        _btnMomDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: make sure all fields are filled out
                _btnMomDone.setClickable(false);
                String rmName = _txtMomName.getText().toString();
                boolean pub = _switchPublic.isChecked();
                String begin = _txtFrom.getText().toString();
                String end = _txtTill.getText().toString();
                String desc = _txtMomDetails.getText().toString();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ((BitmapDrawable)_imgRoomImg.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray();

                Room newRoom = new Room(rmName, pub, begin, end, desc);
                newRoom.setImage(Base64.encodeToString(b, Base64.DEFAULT));

                CreateRoomActivityTask task = new CreateRoomActivityTask(CreateRoomActivity.this);
                task.execute(rmName, String.valueOf(pub), begin, end, desc);
            }
        });

        _imgRoomImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select a cover image"), PICK_IMAGE_REQUEST);
            }
        });

    }

    public ImageView getImageView() {
        return _imgRoomImg;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                //Setting the Bitmap to ImageView
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(uri.getEncodedPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int orientation = exif != null ? exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED) : 0;
                int rotate = 0;
                int height = (bitmap.getHeight() * 512 / bitmap.getWidth());
                Bitmap scale = Bitmap.createScaledBitmap(bitmap, 512, height, true);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_NORMAL:
                        rotate = 0;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotate = 270;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotate = 180;
                        break;
                    case ExifInterface.ORIENTATION_UNDEFINED:
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotate = 90;
                        break;
                }

                Matrix matrix = new Matrix();
                matrix.postRotate(rotate);
                _imgRoomImg.setImageBitmap(Bitmap.createBitmap(scale, 0, 0, scale.getWidth(),
                        scale.getHeight(), matrix, true));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
