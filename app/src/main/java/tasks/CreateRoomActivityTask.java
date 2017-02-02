package tasks;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

import grexClasses.Room;
import grexClasses.User;
import grexEnums.RET_STATUS;
import money.cache.grexActivities.CreateRoomActivity;

import static grexClasses.GrexSocket.getRoomsStatus;

/**
 * Created by boyice on 1/31/2017.
 *
 */

public class CreateRoomActivityTask extends SocketActivityTask {

    public CreateRoomActivityTask(CreateRoomActivity socketActivity) {
        super(socketActivity, getRoomsStatus);
    }

    @Override
    protected RET_STATUS doInBackground(String... params) {
        if(params.length == 5) {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            socketActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = ((BitmapDrawable)((CreateRoomActivity)socketActivity).getImageView().getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    //bitmap.recycle();
                }

            });
            byte[] b = baos.toByteArray();
            baos.reset();
            final Room newRoom = new Room(params[0], Boolean.valueOf(params[1]), User.getUser().name, params[2], params[3], params[4]);
            newRoom.setImage(Base64.encodeToString(b, Base64.DEFAULT));
            b = null;
            Runnable function = new Runnable() {
                @Override
                public void run() {
                    grexSocket.emitRoom(newRoom);
                }
            };
            return super.doInBackground("Sending new room to server", function);
        }
        return getRoomsStatus;
    }
}
