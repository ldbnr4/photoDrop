package money.cache.grexActivities;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;

import grexClasses.GrexSocket;
import grexClasses.Room;
import grexClasses.User;
import grexEnums.CONNECTION_STATUS;
import grexEnums.RET_STATUS;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by boyice on 1/24/2017.
 *
 */

@RunWith(AndroidJUnit4.class)
public class CreateRoomTest {

    @Test
    public void testCreateRoom() {
        GrexSocket.initConnection();
        if (GrexSocket.connection_status == CONNECTION_STATUS.CONNECTED) {
            GrexSocket.getGrexSocket().tEmitGetRooms();
            while (GrexSocket.getRooms != RET_STATUS.SUCCESS) ;
            int before = User.getUser().getRoomsIn().size();
            GrexSocket.getRooms = RET_STATUS.NONE;

            Calendar calendar = Calendar.getInstance();
            Date now = new Date();
            calendar.setTime(now);
            calendar.add(Calendar.HOUR, 1);
            Room room = new Room("My Incredible Party", true, GrexSocket.DF.format(now), GrexSocket.DF.format(calendar.getTime()), "This is going to be the greatest celebration of all time!");
            GrexSocket.getGrexSocket().tEmitRoom(room);
            while (GrexSocket.sendRoom != RET_STATUS.SUCCESS) ;

            GrexSocket.getGrexSocket().tEmitGetRooms();
            while (GrexSocket.getRooms != RET_STATUS.SUCCESS) ;
            int after = User.getUser().getRoomsIn().size();

            assertThat(before < after, is(true));
        } else
            fail();
    }
}
