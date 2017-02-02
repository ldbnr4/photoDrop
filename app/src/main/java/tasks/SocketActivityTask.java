package tasks;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import grexClasses.GrexSocket;
import grexEnums.RET_STATUS;
import money.cache.grexActivities.R;
import money.cache.grexActivities.SocketActivity;

import static grexClasses.CONNECTION_STATUS.CONNECTED;
import static grexEnums.RET_STATUS.NONE;

/**
 * Created by Lorenzo on 1/29/2017.
 *
 */
abstract class SocketActivityTask extends AsyncTask<String, Void, RET_STATUS> {
    SocketActivity socketActivity;
    private ProgressDialog progressDialog;
    private RET_STATUS ret_status;
    GrexSocket grexSocket = GrexSocket.getGrexSocket();

    SocketActivityTask(SocketActivity socketActivity, RET_STATUS ret_status) {
        this.socketActivity = socketActivity;
        this.ret_status = ret_status;
    }

    RET_STATUS doInBackground(final String msg, Runnable function) {
        socketActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(socketActivity,
                        R.style.AppTheme_Dark_Dialog);

                progressDialog.setIndeterminate(true);
                progressDialog.setMessage(msg);
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        progressDialog.dismiss();
                        socketActivity.onFail();
                        ret_status = NONE;
                        SocketActivityTask.this.cancel(true);
                    }
                });
                progressDialog.show();
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
        progressDialog.dismiss();

        if(GrexSocket.connection_status == CONNECTED){
            socketActivity.onPostExecute(retStatResult);
        }
        ret_status = NONE;
    }

    @Override
    protected void onCancelled() {
        progressDialog.dismiss();
        socketActivity.onFail();
        ret_status = NONE;
    }
}
