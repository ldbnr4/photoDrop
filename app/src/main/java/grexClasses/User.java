package grexClasses;

import com.google.android.gms.maps.model.LatLng;

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
    public String name = "GREX_ORPHAN";
    public LatLng location;
    public Set<Room> roomsHosting = new HashSet<>();
    public Set<Room> rommsGeo = new HashSet<>();
    private SortedMap<Integer, TreeMap<String, Room>> pagedRoomsIn = new TreeMap<>();
    private SortedMap<String, Room> roomsIn = new TreeMap<>();
    private Set<User> friends = new HashSet<>();
    private TreeSet<String> pagedRooms = new TreeSet<>();
    private boolean dirtyRooms = false;

    private User() {}

    public User(String name) {
        this.name = name;
    }

    public static synchronized User getUser() {
        return user;
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
                    pagedRoomsIn.put(currentPage, cPageMap);
                    currentPage++;
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
