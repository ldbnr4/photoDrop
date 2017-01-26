package grexClasses;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Lorenzo on 1/4/2017.
 *
 */
public final class User {
    private static final User user = new User();
    public String name;
    private Set<Room> roomsIn = Collections.synchronizedSet(new HashSet<Room>());
    private Set<User> friends = Collections.synchronizedSet(new HashSet<User>());

    private User() {}

    public static synchronized User getUser() {
        return user;
    }

    void addToRoomsIn(Room room){
        roomsIn.add(room);
    }

    public Set<Room> getRoomsIn() {
        return roomsIn;
    }

    public Set<User> getFriends() {
        return friends;
    }

    void addToFriends(User user){
        friends.add(user);
    }
}
