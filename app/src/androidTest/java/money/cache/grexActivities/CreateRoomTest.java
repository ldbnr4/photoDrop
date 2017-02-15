package money.cache.grexActivities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Base64;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import grexClasses.GrexSocket;
import grexClasses.Room;
import grexClasses.User;

import static grexClasses.GrexSocket.CONNECTION_STATUS.CONNECTED;
import static grexClasses.GrexSocket.RET_STATUS.SUCCESS;
import static junit.framework.TestCase.fail;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by boyice on 1/24/2017.
 *
 */

@RunWith(AndroidJUnit4.class)
public class CreateRoomTest {

    @Test
    public void testCreateRoom() {
        Context applicationContext = InstrumentationRegistry.getTargetContext().getApplicationContext();
        GrexSocket grexSocket = GrexSocket.getGrexSocket(applicationContext);
        if (GrexSocket.connection_status == CONNECTED) {
            int before = 0;
            int after = 0;
            try {
                grexSocket.emitGetRooms();
                while (GrexSocket.getGetRooms() != SUCCESS) ;
                before = User.getUser().getRoomsIn().size();
                for (int i = 0; i < 100; i++) {
                    Calendar calendar = Calendar.getInstance();
                    Date now = new Date();
                    calendar.setTime(now);
                    calendar.add(Calendar.HOUR, new Random().nextInt());
                    Room room = new Room("Test event" + i, true, GrexSocket.DF.format(now), GrexSocket.DF.format(calendar.getTime()), "This is going to be the greatest celebration of all time!");
                    if (new Random().nextBoolean()) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ((BitmapDrawable) applicationContext.getDrawable(R.drawable._xlarge_icons_100)).getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        room.setImage(Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT));
                    }
                    grexSocket.emitRoom(room);
                    while (GrexSocket.getSendRoom() != SUCCESS) ;
                }

                grexSocket.emitGetRooms();
                while (GrexSocket.getGetRooms() != SUCCESS) ;
                after = User.getUser().getRoomsIn().size();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            assertThat(before < after, is(true));
        } else
            fail();

        grexSocket.disconnect();
    }
}
