package grexClasses;

/**
 * Created by boyice on 2/2/2017.
 *
 */
public class LocalDatabase {
    private static LocalDatabase ourInstance = new LocalDatabase();

    public static LocalDatabase getInstance() {
        return ourInstance;
    }

    private LocalDatabase() {
    }
}
