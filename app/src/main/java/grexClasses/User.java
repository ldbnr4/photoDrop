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
    private SortedMap<String, Room> roomsIn = Collections.synchronizedSortedMap(new TreeMap<String, Room>());
    private Set<User> friends = Collections.synchronizedSet(new HashSet<User>());

    private User() {}

    public static synchronized User getUser() {
        return user;
    }

    public void addToRoomsIn(Room room) {
        if (!roomsIn.containsKey(room.getId()))
            roomsIn.put(room.getId(), room);
    }

    public SortedMap<String, Room> getRoomsIn(int num) {
        SortedMap<String, Room> ret = new TreeMap<>();
        Room[] roomEntries = (Room[]) roomsIn.entrySet().toArray();
        for (int i = 0; i < num; i++) {
            ret.put(roomEntries[i].getId(), roomEntries[i]);
        }
        return ret;
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
