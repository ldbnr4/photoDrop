package grexClasses;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by Lorenzo on 1/4/2017.
 *
 */

//TODO: Add image field
public final class User {
    private static final User user = new User();
    private static String name = "GREX_ORPHAN";
    private SortedMap<Integer, TreeMap<String, Room>> pagedRoomsIn = Collections.synchronizedSortedMap(new TreeMap<Integer, TreeMap<String, Room>>());
    private SortedMap<String, Room> roomsIn = Collections.synchronizedSortedMap(new TreeMap<String, Room>());
    private Set<User> friends = Collections.synchronizedSet(new HashSet<User>());
    private TreeSet<String> pagedRooms = new TreeSet<>();
    private boolean dirtyRooms = false;

    private User() {}

    public static synchronized User getUser() {
        return user;
    }

    public static String getName() {
        return name;
    }

    public void setName(String name) {
        User.name = name;
    }

    public void addToRoomsIn(Room room) {
        if (!roomsIn.containsKey(room.getId())) {
            if (!dirtyRooms) dirtyRooms = true;
            roomsIn.put(room.getId(), room);
        }
    }

    public int getRoomsInSize() {
        return roomsIn.size();
    }

    public int getRoomsInPageCount() {
        return pagedRoomsIn.size();
    }

    private void paginateRooms() {
        if (dirtyRooms) {
            int numRoomsIn = roomsIn.size();
            int currentPage = pagedRoomsIn.size();
            TreeMap<String, Room> stringRoomTreeMap = pagedRoomsIn.get(currentPage);
            int count;
            if (stringRoomTreeMap == null) {
                count = 0;
            } else {
                count = stringRoomTreeMap.size();
            }
            Room cRoom;
            TreeMap<String, Room> cPageMap = null;
            Room[] roomEntrySet = roomsIn.values().toArray(new Room[]{});
            for (int i = 0; i < numRoomsIn; i++) {
                cRoom = roomEntrySet[i];
                if (count % 10 == 0 || cPageMap == null) {
                    cPageMap = new TreeMap<>();
                    pagedRoomsIn.put(currentPage++, cPageMap);
                }
                if (!pagedRooms.contains(cRoom.getId())) {
                    cPageMap.put(cRoom.getId(), cRoom);
                    pagedRooms.add(cRoom.getId());
                    count++;
                }
            }
            dirtyRooms = false;
        }
    }

    public TreeMap<String, Room> getRoomsIn(int num) {
        paginateRooms();
        return pagedRoomsIn.get(num);
    }

    public Set<User> getFriends() {
        return friends;
    }

    void addToFriends(User user){
        friends.add(user);
    }
}
