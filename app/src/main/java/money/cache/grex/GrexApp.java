package money.cache.grex;

import android.app.Application;
import android.content.Context;

/**
 * Created by boyice on 2/28/2017.
 */
public class GrexApp extends Application {

    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
}
