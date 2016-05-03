package fr.voxeet.sdk.sample.application;

import android.app.Application;

import fr.voxeet.sdk.sample.R;
import voxeet.com.sdk.core.VoxeetSdk;

/**
 * Created by RomainBenmansour on 06,April,2016
 */
public class SampleApplication extends Application {

    private static final String TAG = SampleApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        VoxeetSdk.sdkInitialize(this, getString(R.string.consumer_key), getString(R.string.consumer_secret));
    }
}
