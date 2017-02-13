package GrexInterfaces;

import android.os.AsyncTask;

/**
 * Created by Lorenzo on 2/12/2017.
 */

public abstract class SocketTask<Params, Progress, Result> extends AsyncTask<Void, Void, Void> {

    @Override
    protected abstract void onPreExecute();

    @Override
    protected abstract Void doInBackground(Void... params);

    @Override
    protected abstract void onPostExecute(Void param);
}
