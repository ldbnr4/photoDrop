package grexClasses;

import android.app.ProgressDialog;

import money.cache.grexActivities.SocketActivity;

/**
 * Created by Lorenzo on 1/29/2017.
 */

public class SockAct_ProgDial {
    public SocketActivity socketActivity;
    public ProgressDialog progressDialog;

    public SockAct_ProgDial(SocketActivity socketActivity, ProgressDialog progressDialog) {
        this.socketActivity = socketActivity;
        this.progressDialog = progressDialog;
    }
}
