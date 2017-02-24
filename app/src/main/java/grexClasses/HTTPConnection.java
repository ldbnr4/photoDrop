package grexClasses;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by boyice on 2/24/2017.
 */
public class HTTPConnection {
    private static HTTPConnection ourInstance = new HTTPConnection();

    private HTTPConnection() {
        // Create URL
        URL githubEndpoint = null;
        try {
            githubEndpoint = new URL("https://zotime.ddns.net:3000/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (githubEndpoint != null) {
            // Create connection
            HttpsURLConnection myConnection = null;
            try {
                myConnection = (HttpsURLConnection) githubEndpoint.openConnection();
                if (myConnection != null) {
                    myConnection.setRequestProperty("User-Agent", "my-rest-app-v0.1");
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader =
                            new InputStreamReader(responseBody, "UTF-8");
                    JsonReader jsonReader = new JsonReader(responseBodyReader);
                } else {
                    // TODO: 2/24/2017 ERROR MESSAGE
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // TODO: 2/24/2017 ERROR MESSAGE
        }
    }

    public static HTTPConnection getInstance() {
        return ourInstance;
    }


}
