package money.cache.grexActivities;

import android.content.Context;
import android.content.res.TypedArray;
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

import grexClasses.Room;
import grexClasses.SocketCluster;
import grexEnums.RET_STATUS;

/**
 * Created by Lorenzo on 2/27/2017.
 *
 */

@RunWith(AndroidJUnit4.class)
public class SocketcluserTest {
    @Test
    public void testAddUser() {
        SocketCluster.emitUser();
        while (SocketCluster.getSendUser() == RET_STATUS.NONE) ;
        System.out.println(SocketCluster.getSendUser());
    }

    @Test
    public void testAddRoom() {
        SocketCluster.emitRoom(new Room("Everything is Awesome", false, "today", "tomorrow", "The best time of your life"));
        while (SocketCluster.getSendRoom() == RET_STATUS.NONE) ;
        System.out.println(SocketCluster.getSendRoom());
    }

    @Test
    public void testAddRoomsBulk() {
        Context applicationContext = InstrumentationRegistry.getTargetContext().getApplicationContext();
        for (int i = 0; i < 100; i++) {
            Calendar calendar = Calendar.getInstance();
            Date now = new Date();
            calendar.setTime(now);
            calendar.add(Calendar.HOUR, new Random().nextInt(72));
            Room room = new Room("Test event" + i, true, SocketCluster.DF.format(now), SocketCluster.DF.format(calendar.getTime()), "This is going to be the greatest celebration of all time!");
            if (new Random().nextBoolean()) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                TypedArray imgs = applicationContext.getResources().obtainTypedArray(R.array.apptour);
                Random rand = new Random();
                int rndInt = rand.nextInt(imgs.length());
                int resID = imgs.getResourceId(rndInt, 0);
                ((BitmapDrawable) applicationContext.getDrawable(resID)).getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, baos);
                room.setImage(Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT));
            }
            SocketCluster.emitRoom(room);
            while (SocketCluster.getSendRoom() == RET_STATUS.NONE) ;
        }
    }

    @Test
    public void testGetRooms() {
        SocketCluster.emitGetRooms();
        while (SocketCluster.getGetRoomsStat() == RET_STATUS.NONE) ;
        System.out.println(SocketCluster.getGetRoomsStat());
    }
}
