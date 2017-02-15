package money.cache.grexActivities;

import android.support.test.runner.AndroidJUnit4;

import org.junit.runner.RunWith;

/**
 * Created by boyice on 1/24/2017.
 * TODO: THIS IS BROKEN :(
 */

@RunWith(AndroidJUnit4.class)
public class CreateRoomTest {

   /* @Test
    public void testCreateRoom() {
        //GrexSocket.getGrexSocket().initConnection(new CreateRoomActivity().getApplicationContext());
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
    }*/
}
