package tasks;

import android.os.AsyncTask;
import android.widget.Toast;

import grexClasses.GrexSocket;
import grexClasses.SocketActivity;
import grexEnums.RET_STATUS;

import static grexEnums.CONNECTION_STATUS.CONNECTED;
import static grexEnums.RET_STATUS.NONE;

/**
 * Created by Lorenzo on 1/29/2017.
 *
 */
public abstract class SocketActivityTask extends AsyncTask<String, Void, RET_STATUS> {
    SocketActivity socketActivity;
    private RET_STATUS ret_status;
    GrexSocket grexSocket = GrexSocket.getGrexSocket();

    SocketActivityTask(SocketActivity socketActivity, RET_STATUS ret_status) {
        this.socketActivity = socketActivity;
        this.ret_status = ret_status;
    }

    RET_STATUS doInBackground(String msg, Runnable function) {
        //publishProgress(msg);
        socketActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });
        ret_status = NONE;
        Thread t = new Thread(function);
        t.start();
        try {
            t.join(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        switch (GrexSocket.connection_status){
            case CONNECTED:
                int count = 0;
                while (count < 2) {
                    if (attemptCommunication() == NONE) {
                        count++;
                    } else {
                        count = 2;
                    }
                }
                break;

            case INTERNET_DOWN:
                noInternet();
                break;
            case SERVER_DOWN:
                noServer();
                break;
        }
        return ret_status;
    }

    private RET_STATUS attemptCommunication(){
        long totalTime = 2500;
        long startTime = System.currentTimeMillis();
        boolean toFinish = false;
        while (!toFinish && ret_status == NONE) {
            toFinish = (System.currentTimeMillis() - startTime >= totalTime);
        }
        return ret_status;
    }

    private void showErrorToast(final String msg) {
        try {
            socketActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(socketActivity, msg, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void noInternet() {
        showErrorToast("Unable to connect to the Internet");
    }

    private void noServer() {
        showErrorToast("Our servers are down :(");
    }

    @Override
    protected void onPostExecute(RET_STATUS retStatResult) {
        //progressDialog.dismiss();

        if(GrexSocket.connection_status == CONNECTED){
            socketActivity.onPostExecute(retStatResult);
        }
        ret_status = NONE;
    }

    @Override
    protected void onCancelled() {
        //progressDialog.dismiss();
        socketActivity.onFail();
        ret_status = NONE;
    }
}
