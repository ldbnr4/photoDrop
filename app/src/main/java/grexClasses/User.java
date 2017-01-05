package grexClasses;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Lorenzo on 1/4/2017.
 */
public final class User {
    private static final User user = new User();
    String name;
    Set<String> roomSet;

    private User() {
        roomSet = new HashSet<>();
    }

    public static User getUser() {
        return user;
    }
}
