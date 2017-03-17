package grexClasses;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Lorenzo on 1/29/2017.
 *
 */
public abstract class SocketActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        Runtime.getRuntime().gc();
        setUpNoNet();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onResume(){
        super.onResume();
        Runtime.getRuntime().gc();
    }

    protected abstract void setUpNoNet();
}
