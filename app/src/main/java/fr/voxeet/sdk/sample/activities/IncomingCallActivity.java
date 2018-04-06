package fr.voxeet.sdk.sample.activities;

import android.support.annotation.NonNull;

import sdk.voxeet.com.toolkit.activities.notification.AbstractIncomingCallActivity;
import sdk.voxeet.com.toolkit.activities.notification.IncomingCallFactory;
import sdk.voxeet.com.toolkit.activities.workflow.VoxeetAppCompatActivity;

/**
 * In this snippet, `IncomingCallActivity` extends `AbstractIncomingCallActivity`. A method must be overriden :
 * ```
 *
 * @NonNull
 * @Override protected Class<? extends VoxeetAppCompatActivity> getActivityClassToCall() {
 * return MainActivity.class;
 * }
 * ```
 * <p>
 * Note that in the case of multiple Activities in the app, you can use the VoxeetAppCompatActivity to automatically register the activity to start when "accepting" a call.
 * Instead of returning `MainActivity.class` in the sample, use `IncomingCallFactory.getAcceptedIncomingActivityKlass()`:
 * <p>
 * ```
 * @NonNull
 * @Override protected Class<? extends VoxeetAppCompatActivity> getActivityClassToCall() {
 * Class<? extends VoxeetAppCompatActivity> temp = IncomingCallFactory.getAcceptedIncomingActivityKlass();
 * <p>
 * if(null != temp)
 * return temp;
 * <p>
 * return MainActivity.class;
 * }
 * <p>
 * ```
 */

public class IncomingCallActivity extends AbstractIncomingCallActivity {
    @NonNull
    @Override
    protected Class<? extends VoxeetAppCompatActivity> getActivityClassToCall() {
        Class<? extends VoxeetAppCompatActivity> temp = IncomingCallFactory.getAcceptedIncomingActivityKlass();

        if (null != temp)
            return temp;

        return MainActivity.class;
    }
}
