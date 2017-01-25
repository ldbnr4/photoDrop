package grexClasses;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Lorenzo on 1/4/2017.
 *
 */

public class Room {
    String host;
    private String name;
    private boolean isPubic;
    private String birth;
    private String death;
    private String image;
    private String description;
    private Set<User> guests;
    private Set<String> photoRoll;
    private Set<String> videoRoll;

    public Room(String name, boolean isPub, String host, String birth, String death, String description) {
        guests = new HashSet<>();
        photoRoll = new HashSet<>();
        videoRoll = new HashSet<>();
        this.host = host = User.getUser().name;
        this.name = name;
        this.isPubic = isPub;
        this.birth = birth;
        this.death = death;
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean addPerson(User guest) {
        return this.guests.add(guest);
    }

    public void addMedia(String media) {
        photoRoll.add(media);
    }

    public Set<String> getRoomMedia() {
        return photoRoll;
    }

    public Set<User> getUsers() {
        return guests;
    }
}
