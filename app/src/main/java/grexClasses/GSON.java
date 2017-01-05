package grexClasses;

import com.google.gson.Gson;

/**
 * Created by boyice on 1/5/2017.
 *
 */
public class GSON {
    private static Gson ourInstance = new Gson();

    public static Gson getInstance() {
        return ourInstance;
    }

    private GSON() {
    }
}
