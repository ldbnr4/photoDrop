package money.cache.grex;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by boyice on 1/25/2017.
 *
 */

@RunWith(AndroidJUnit4.class)
public class ViewRoomsTest {
    @Rule
    public ActivityTestRule<HomeActivity> mActivityRule = new ActivityTestRule<>(HomeActivity.class);

    @Test
    public void testViewRooms(){
        mActivityRule.launchActivity(new Intent());
        System.out.println("DONE!");
    }
}
