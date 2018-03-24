package vi.filepicker;

import android.app.Instrumentation;
import android.os.Build;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import droidninja.filepicker.FilePickerActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertNotNull;
import static vi.filepicker.RecyclerViewItemCountAssertion.withItemCount;

/**
 * Created by droidNinja on 23/02/18.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

  @Rule public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

  private MainActivity mainActivity = null;

  public Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(FilePickerActivity.class.getName(), null,false);

  @Before
  public void setUp() throws Exception {
    mainActivity = activityTestRule.getActivity();

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      getInstrumentation().getUiAutomation().executeShellCommand(
          "pm grant " + getTargetContext().getPackageName()
              + " android.permission.WRITE_EXTERNAL_STORAGE");
    }
  }

  @Test
  public void testFilePicker(){
    assertNotNull(mainActivity.findViewById(R.id.pick_photo));

    onView(withId(R.id.pick_photo)).perform(click());

    FilePickerActivity filePickerActivity =
        (FilePickerActivity) getInstrumentation().waitForMonitorWithTimeout(monitor, 5000);

    assertNotNull(filePickerActivity);

    onView(allOf(isDisplayed(), withId(R.id.recyclerview))).perform(
        RecyclerViewActions.actionOnItemAtPosition(1, click()));

    onView(allOf(isDisplayed(), withId(R.id.recyclerview))).perform(
        RecyclerViewActions.actionOnItemAtPosition(2, click()));

    onView(withId(R.id.action_done)).perform(click());

    onView(allOf(isDisplayed(), withId(R.id.recyclerview))).check(withItemCount(2));

  }

  @After
  public void tearDown(){
    mainActivity = null;
  }

}