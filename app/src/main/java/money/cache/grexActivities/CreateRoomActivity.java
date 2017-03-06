package money.cache.grexActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.florent37.singledateandtimepicker.dialog.DoubleDateAndTimePickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import grexClasses.Room;
import grexClasses.SocketActivity;
import grexClasses.SocketCluster;
import grexClasses.User;
import grexEnums.RET_STATUS;
import grexInterfaces.SocketTask;


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
    @Bind(R.id.table_row_createRm_dates)
    TableRow _tblRw_dates;
    private String rmName;
    private boolean pub;
    private String begin;
    private String end;
    private String desc;
    private byte[] b;
    private boolean aBoolean;

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

        _tblRw_dates.setOnClickListener(new View.OnClickListener() {
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
                                _txtFrom.setText(SocketCluster.DF.format(dates.get(0)));
                                _txtTill.setText(SocketCluster.DF.format(dates.get(1)));
                            }
                        }).display();
            }
        });

        _btnMomDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _btnMomDone.setClickable(false);

                //TODO: make sure all fields are filled out
                //TODO: only send rooms to db if they are live...maybe?
                rmName = _txtMomName.getText().toString();
                pub = _switchPublic.isChecked();
                begin = _txtFrom.getText().toString();
                end = _txtTill.getText().toString();
                desc = _txtMomDetails.getText().toString();

                aBoolean = !_imgRoomImg.getDrawable().equals(getDrawable(R.drawable._xlarge_icons_100));
                if (aBoolean) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ((BitmapDrawable) _imgRoomImg.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    b = baos.toByteArray();
                }

                new CreateRoomTask().execute();
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

    class CreateRoomTask extends SocketTask<Void, Void, Void> {
        private ProgressDialog progressDialog;
        private Room room;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(CreateRoomActivity.this,
                    "Hold Up!",
                    "Setting up the venue...");
        }

        @Override
        protected Void doInBackground(Void... params) {
            room = new Room(rmName, pub, desc, 0, 0);
            if (aBoolean) {
                //room.setImage(Base64.encodeToString(b, Base64.DEFAULT));
            }
            SocketCluster.getInstance().emitRoom(room);
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            progressDialog.dismiss();
            if (SocketCluster.getSendRoom() == RET_STATUS.SUCCESS) {
                //TODO: Notify user of success with green check animation
                User.getUser().addToRoomsIn(room);
            } else {
                /*switch (SocketCluster.connection_status) {
                    case CONNECTED:
                        //Need to do some investigating...
                        break;
                    case INTERNET_DOWN:
                    case SERVER_DOWN:
                        //TODO: notify user that it's saved locally and will be sent to server when it can
                        if (LocalDatabase.getInstance(CreateRoomActivity.this.getApplicationContext()).saveRoom(room)) {
                            //Successfully saved
                            showErrorToast("Room saved in local database");
                        } else {
                            //Error saving
                            showErrorToast("Error saving room in local database");
                        }
                        cancel(true);
                        break;
                }*/
            }
            finish();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
