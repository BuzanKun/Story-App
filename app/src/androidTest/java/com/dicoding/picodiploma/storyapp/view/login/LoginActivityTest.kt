package com.dicoding.picodiploma.storyapp.view.login

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.dicoding.picodiploma.storyapp.R
import com.dicoding.picodiploma.storyapp.utils.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

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
    fun testUserLogin_Success() {
        onView(withId(R.id.ed_login_email)).perform(
            typeText("seseorang@gmail.com"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.ed_login_password)).perform(
            typeText("akunseseorang"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.loginButton)).perform(click())
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))
        onView(withText(R.string.login_success)).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withId(android.R.id.button1)).perform(click())
        onView(withId(R.id.rv_story_list)).check(matches(isDisplayed()))
    }

    @Test
    fun testUserLogin_Error() {
        onView(withId(R.id.ed_login_email)).perform(
            typeText("ngasal@gmail.com"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.ed_login_password)).perform(
            typeText("inipasswordngasal"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.loginButton)).perform(click())
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))
        onView(withText(R.string.failed)).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withId(android.R.id.button1)).perform(click())
    }
}