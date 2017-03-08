package money.cache.grex;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import grexClasses.Room;
import grexClasses.SocketCluster;
import grexClasses.User;
import grexEnums.RET_STATUS;
import grexEnums.ROOM_CATEGORY;

/**
 * Created by Lorenzo on 2/27/2017.
 *
 */

@RunWith(AndroidJUnit4.class)
public class SocketcluserTest {
    @Test
    public void testAddUser() {
        SocketCluster.emitUser();
        while (SocketCluster.getSendUser() == RET_STATUS.NONE) ;
        System.out.println(SocketCluster.getSendUser());
    }

    @Test
    public void testAddRoom() {
        final Context applicationContext = InstrumentationRegistry.getTargetContext().getApplicationContext();
        final LocationManager locationManager = (LocationManager) applicationContext.getSystemService(Context.LOCATION_SERVICE);
        final boolean[] DONE = {false};

        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                User user = User.getUser();
                user.location = new LatLng(location.getLatitude(), location.getLongitude());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int count = 0;
                        SocketCluster.getInstance().emitGPS();
                        User user1 = User.getUser();
                        double latitude = user1.location.latitude;
                        double longitude = user1.location.longitude;
                        if (new Random().nextBoolean()) {
                            latitude = user1.location.latitude + 5;
                            longitude = user1.location.longitude + 5;
                        } else {
                            count++;
                        }
                        SocketCluster.getInstance().emitRoom(new Room("Everything is Awesome", false, "The best time of your life", latitude, longitude));
                        System.out.println(count);
                        DONE[0] = true;
                    }
                }).start();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        //SocketCluster.emitGPS(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener);
                }
            }).join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (!DONE[0]) ;
        System.out.println("DONE");
    }

    @Test
    public void testAddRoomsBulk() {
        Context applicationContext = InstrumentationRegistry.getTargetContext().getApplicationContext();
        for (int i = 0; i < 100; i++) {
            Calendar calendar = Calendar.getInstance();
            Date now = new Date();
            calendar.setTime(now);
            calendar.add(Calendar.HOUR, new Random().nextInt(72));
            Room room = new Room("Test event" + i, true, "This is going to be the greatest celebration of all time!", 0, 0);
            if (new Random().nextBoolean()) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                TypedArray imgs = applicationContext.getResources().obtainTypedArray(R.array.apptour);
                Random rand = new Random();
                int rndInt = rand.nextInt(imgs.length());
                int resID = imgs.getResourceId(rndInt, 0);
                ((BitmapDrawable) applicationContext.getDrawable(resID)).getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, baos);
                room.setImage(Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT));
            }
            SocketCluster.getInstance().emitRoom(room);
            while (SocketCluster.getSendRoom() == RET_STATUS.NONE) ;
        }
    }

    @Test
    public void testGetRooms() {
        SocketCluster.getInstance().emitGetRooms(ROOM_CATEGORY.LIVE, 0);
    }
}
