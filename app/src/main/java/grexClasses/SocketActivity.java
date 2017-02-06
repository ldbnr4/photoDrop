package grexClasses;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import grexEnums.RET_STATUS;

/**
 * Created by Lorenzo on 1/29/2017.
 *
 */
public abstract class SocketActivity extends FragmentActivity {

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

    public void noInternet() {
        showErrorToast("Unable to connect to the Internet");
    }

    public void noServer() {
        //"Our servers are down :(";
    }

    private void showErrorToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void retryConnection(){
        GrexSocket.getGrexSocket().hasConnection();
    }

}
