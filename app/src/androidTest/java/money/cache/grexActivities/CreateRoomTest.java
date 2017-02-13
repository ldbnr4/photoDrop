package money.cache.grexActivities;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;

/**
 * Created by boyice on 1/24/2017.
 *
 */

@RunWith(AndroidJUnit4.class)
public class CreateRoomTest {

    @Rule
    public ActivityTestRule<CreateRoomActivity> mActivityRule = new ActivityTestRule<>(CreateRoomActivity.class);

    @Test
    public void testCreateRoom() {
        onView(withId(R.id.txt_mom_name)).perform(typeText("My New Moment"));
        closeSoftKeyboard();
        //onView(withId(R.id.btn_mom_done)).perform(click());
        onView(withId(R.id.table_row_createRm_dates)).perform(click());
        //TODO: fix this test
        onView(withSpinnerText("today")).perform(swipeDown());
        onView(withSpinnerText("today")).perform(swipeDown());
    }


}
