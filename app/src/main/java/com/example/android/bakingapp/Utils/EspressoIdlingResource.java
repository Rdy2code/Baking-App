package com.example.android.bakingapp.Utils;

import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.idling.CountingIdlingResource;

/**
 * Utility class to for marking long-running processes in an Espresso UI test
 * Reference: https://www.pluralsight.com/guides/testing-in-android-with-espresso-part-2
 * Note: By default, Espresso synchronizes view operations with the UI thread and AsyncTasks
 * However, this class is included for demonstration purposes
 */

public class EspressoIdlingResource {

    private static final String RESOURCE = "global";

    private static CountingIdlingResource mCountingIdlingResource =
            new CountingIdlingResource (RESOURCE);

    public static void increment() {
        mCountingIdlingResource.increment();
    }

    public static void decrement() {
        mCountingIdlingResource.decrement();
    }

    public static IdlingResource getIdlingResource() {
        return mCountingIdlingResource;
    }
}
