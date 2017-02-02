package money.cache.grexActivities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import grexClasses.GrexSocket;
import grexEnums.RET_STATUS;

/**
 * Created by Lorenzo on 1/29/2017.
 *
 */
public abstract class SocketActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        if(GrexSocket.getGrexSocket().getConnectivityManager() == null){
            GrexSocket.initConnection(this);
        }
    }

    public abstract void onFail();

    public abstract void onSuccess();

    public abstract void onPostExecute(RET_STATUS success);

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
