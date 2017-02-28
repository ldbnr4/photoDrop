package money.cache.grexActivities;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import grexClasses.GrexSocket;
import grexClasses.SocketCluster;

/**
 * Created by Lorenzo on 2/27/2017.
 */

@RunWith(AndroidJUnit4.class)
public class SocketcluserTest {
    @Test
    public void testAddUser() {
        SocketCluster.emitUser();
        while (SocketCluster.getSendUser() == GrexSocket.RET_STATUS.NONE) ;
        System.out.println(SocketCluster.getSendUser());
    }
}
