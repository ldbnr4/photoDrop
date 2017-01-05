package grexClasses;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Lorenzo on 1/4/2017.
 */

class Room {
    public String id;
    String host;
    private Set<User> userSet;
    private Set<String> mediaSet;

    public Room(String host) {
        id = UUID.randomUUID().toString();
        userSet = new HashSet<>();
        mediaSet = new HashSet<>();
        this.host = host;
    }

    public boolean addPerson(User guest) {
        return userSet.add(guest);
    }

    public void addMedia(String media) {
        mediaSet.add(media);
    }

    public Set<String> getRoomMedia() {
        return mediaSet;
    }

    public Set<User> getUsers() {
        return userSet;
    }
}
