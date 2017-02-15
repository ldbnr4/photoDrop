package grexClasses;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Created by Lorenzo on 1/4/2017.
 *
 */

public class Room {
    private String host;
    private String name;
    private boolean isPubic;
    private String birth;
    private String death;
    private String image;
    private String description;
    private Set<User> guests;
    private Set<String> photoRoll;
    private Set<String> videoRoll;
    private String id;

    public Room(String name, boolean isPub, String birth, String death, String description) {
        id = new SimpleDateFormat("MMddyyyy_", Locale.US).format(new Date()) + Calendar.getInstance().getTimeInMillis() + "_" + User.getUser().getName();
        guests = new HashSet<>();
        photoRoll = new HashSet<>();
        videoRoll = new HashSet<>();
        this.host = User.getUser().getName();
        this.name = name;
        this.isPubic = isPub;
        this.birth = birth;
        this.death = death;
        this.description = description;
    }

    public String getImage() {
        return image;
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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getName() {
        return name;
    }

    public boolean isPubic() {
        return isPubic;
    }

    public String getBirth() {
        return birth;
    }

    public String getDeath() {
        return death;
    }

    public String getDescription() {
        return description;
    }

    public Set<User> getGuests() {
        return guests;
    }

    public Set<String> getPhotoRoll() {
        return photoRoll;
    }

    public Set<String> getVideoRoll() {
        return videoRoll;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Room other = (Room) obj;
        return other.id.equals(this.id);
    }

    public String getId() {
        return id;
    }
}
