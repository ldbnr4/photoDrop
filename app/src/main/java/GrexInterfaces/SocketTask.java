package grexInterfaces;

import android.os.AsyncTask;

/**
 * Created by Lorenzo on 2/12/2017.
 *
 */

public abstract class SocketTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    @Override
    protected abstract void onPreExecute();

    @Override
    protected abstract Result doInBackground(Params... params);

    @Override
    protected abstract void onPostExecute(Result param);
}
