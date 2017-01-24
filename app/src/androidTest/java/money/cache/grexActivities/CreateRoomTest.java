package money.cache.grexActivities;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by boyice on 1/24/2017.
 *
 */

@RunWith(AndroidJUnit4.class)
public class CreateRoomTest {

    @Rule
    public ActivityTestRule<CreateRoomActivity> mActivityRule = new ActivityTestRule<>(CreateRoomActivity.class);

    @Test
    public void listGoesOverTheFold() {
        onView(withId(R.id.txt_mom_name)).perform(typeText("My New Moment"));
        closeSoftKeyboard();
        onView(withId(R.id.btn_mom_done)).perform(click());
    }
}
