package money.cache.grexActivities;

import android.os.Bundle;

import grexClasses.ProgressBarActvity;

/**
 * Created by Lorenzo on 1/29/2017.
 */
public abstract class SocketActivity extends ProgressBarActvity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public abstract void onFail();

    @Override
    public abstract void onSuccess();
}
