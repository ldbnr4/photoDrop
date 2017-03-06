package grexClasses;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import java.net.UnknownHostException;
import java.util.Collections;

/**
 * Created by Lorenzo on 3/4/2017.
 */

public class RemoteMongoConnection {
    private static final RemoteMongoConnection ourInstance = new RemoteMongoConnection();

    private MongoClient mongoClient = null;
    private DB db = openConnection();

    private RemoteMongoConnection() {
    }

    public static RemoteMongoConnection getInstance() {
        return ourInstance;
    }

    private DB openConnection() {

        String dbuname = "admin", database = "test", password = "password";
        //String dbuname = "mizzouUser", database = "MizzouAMUMC", password = "password";
        if (mongoClient == null) {
            MongoCredential credential = MongoCredential.createCredential(dbuname, database, password.toCharArray());
            try {
                //this.mongoClient = new MongoClient(new ServerAddress("ds051543.mongolab.com", 51543), Collections.singletonList(credential));
                this.mongoClient = new MongoClient(new ServerAddress("ldbnr4.ddns.net", 27017), Collections.singletonList(credential));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return this.mongoClient.getDB(database);
    }
}
