package grexClasses;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Lorenzo on 1/4/2017.
 *
 */

//TODO: Add image field
public final class User {
    private static final User user = new User();
    private String name = "GREX_ORPHAN";
    private SortedMap<Object, Object> roomsIn = Collections.synchronizedSortedMap(new TreeMap<>());
    private Set<User> friends = Collections.synchronizedSet(new HashSet<User>());

    private User() {}

    public static synchronized User getUser() {
        return user;
    }

    public void addToRoomsIn(Room room) {
        if (!roomsIn.containsKey(room.getId()))
            roomsIn.put(room.getId(), room);
    }

    public SortedMap<Object, Object> getRoomsIn() {
        return roomsIn;
    }

    public Set<User> getFriends() {
        return friends;
    }

    void addToFriends(User user){
        friends.add(user);
    }

    String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
