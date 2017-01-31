package money.cache.grexActivities;

import android.support.v7.app.AppCompatActivity;

import grexClasses.GrexSocket;
import grexEnums.RET_STATUS;

/**
 * Created by Lorenzo on 1/29/2017.
 *
 */
public abstract class SocketActivity extends AppCompatActivity {

    GrexSocket grexSocket = GrexSocket.getGrexSocket();

    public abstract void onFail();

    public abstract void onSuccess();

    public abstract void onPostExecute(RET_STATUS success);
}
