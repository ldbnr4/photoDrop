package grexClasses;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

/**
 * Created by Lorenzo on 1/29/2017.
 *
 */
public abstract class SocketActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        //Runtime.getRuntime().gc();
        if(GrexSocket.getGrexSocket().getConnectivityManager() == null){
            GrexSocket.initConnection(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Runtime.getRuntime().gc();
    }

    private void showErrorToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

}
