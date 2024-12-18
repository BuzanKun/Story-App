package com.dicoding.picodiploma.storyapp.view.add

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasType
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.dicoding.picodiploma.storyapp.R
import com.dicoding.picodiploma.storyapp.utils.EspressoIdlingResource
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AddStoryActivityTest {
    @get:Rule
    val activity = ActivityScenarioRule(AddStoryActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        Intents.release()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun testUploadStory_Error() {
        //Should be error because no token is detected to properly Upload the Story
        onView(withId(R.id.ed_add_description)).perform(
            typeText("Description for Upload Story Test"),
            closeSoftKeyboard()
        )

        val context =
            androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val uri = Uri.parse("android.resource://${context.packageName}/${R.drawable.image_login}")

        val storyData = Intent().apply { data = uri }
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, storyData)

        intending(
            allOf(
                hasAction("androidx.activity.result.contract.action.PICK_IMAGES"),
                hasType("image/*")
            )
        ).respondWith(result)

        onView(withId(R.id.btn_gallery)).perform(click())

        Intents.intended(
            allOf(
                hasAction("androidx.activity.result.contract.action.PICK_IMAGES"),
                hasType("image/*")
            )
        )

        onView(withId(R.id.iv_story_image)).check(matches(isDisplayed()))

        onView(withId(R.id.button_add)).perform(click())

        onView(withText("Bad HTTP authentication header format")).check(matches(isDisplayed()))
    }
}