package fr.voxeet.sdk.sample.application;

import android.app.Application;

import java.util.Random;

import fr.voxeet.sdk.sample.R;
import voxeet.com.sdk.core.VoxeetSdk;
import voxeet.com.sdk.json.UserInfo;

/**
 * Created by RomainBenmansour on 06,April,2016
 */
public class SampleApplication extends Application {

    private static final String TAG = SampleApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        VoxeetSdk.sdkInitialize(this,
                getString(R.string.consumer_key),
                getString(R.string.consumer_secret),
                new UserInfo("michel", null, "http://img0.mxstatic.com/wallpapers/fd823da7f3e99936e0e3f4c8b5d69b65_large.jpeg"));
    }
}
