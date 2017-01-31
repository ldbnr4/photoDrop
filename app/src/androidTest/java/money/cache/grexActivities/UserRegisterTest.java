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
 * Created by boyice on 1/25/2017.
 *
 */

@RunWith(AndroidJUnit4.class)
public class UserRegisterTest {

    @Rule
    public ActivityTestRule<SignUpActivity> mActivityRule = new ActivityTestRule<>(SignUpActivity.class);

    @Test
    public void testRegister() {
        onView(withId(R.id.input_name)).perform(typeText("Fred Fedburger"));
        closeSoftKeyboard();
        onView(withId(R.id.input_email)).perform(typeText("fburger@gmail.com"));
        closeSoftKeyboard();
        onView(withId(R.id.input_mobile)).perform(typeText("8166992221"));
        closeSoftKeyboard();
        onView(withId(R.id.input_password)).perform(typeText("password"));
        closeSoftKeyboard();
        onView(withId(R.id.input_reEnterPassword)).perform(typeText("password"));
        closeSoftKeyboard();

        onView(withId(R.id.btn_signUp)).perform(click());
    }
}
