package tasks;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import grexClasses.GrexSocket;
import grexEnums.RET_STATUS;
import money.cache.grexActivities.R;
import money.cache.grexActivities.SocketActivity;

import static grexEnums.RET_STATUS.NONE;

/**
 * Created by Lorenzo on 1/29/2017.
 *
 */
public abstract class SocketActivityTask extends AsyncTask<String, Void, RET_STATUS> {
    private SocketActivity socketActivity;
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
        function.run();

        long totalTime = 3000;
        long startTime = System.currentTimeMillis();
        boolean toFinish = false;

        while (!toFinish && ret_status == NONE) {
            toFinish = (System.currentTimeMillis() - startTime >= totalTime);
        }

        return ret_status;
    }

    @Override
    protected void onPostExecute(RET_STATUS retStatResult) {
        progressDialog.dismiss();
        socketActivity.onPostExecute(retStatResult);
        ret_status = NONE;
    }

    @Override
    protected void onCancelled() {
        progressDialog.dismiss();
        socketActivity.onFail();
        ret_status = NONE;
    }

}
