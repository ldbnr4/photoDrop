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
 * TODO: Make combined fields lat and lon
 */

public class Room {
    public double lat;
    public double lon;
    private String host;
    private String name;
    private boolean isPubic;
    private String image;
    private String description;
    private Set<User> guests;
    private Set<String> photoRoll;
    private Set<String> videoRoll;
    private String id;

    public Room(String name, boolean isPub, String description, double lat, double lon) {
        id = new SimpleDateFormat("MMddyyyy_", Locale.US).format(new Date()) + Calendar.getInstance().getTimeInMillis() + "_" + User.getUser().name;
        guests = new HashSet<>();
        photoRoll = new HashSet<>();
        videoRoll = new HashSet<>();
        this.host = User.getUser().name;
        this.name = name;
        this.isPubic = isPub;
        this.description = description;
        this.lat = lat;
        this.lon = lon;
    }

    public Room(String roomName) {
        this.name = roomName;
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

    public String getId() {
        return id;
    }

    /*@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Room other = (Room) obj;
        return other._id.equals(this._id);
    }

    public String getId() {
        return _id;
    }*/
}
