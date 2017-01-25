package grexClasses;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Lorenzo on 1/4/2017.
 *
 */
public final class User {
    private static final User user = new User();
    public String name;
    private static Set<Room> roomsIn;
    Set<User> friends;

    private User() {
        roomsIn = new HashSet<>();
        friends = new HashSet<>();
    }

    public static User getUser() {
        return user;
    }

    public static void addToRoomsIn(Room room){
        roomsIn.add(room);
    }
}
