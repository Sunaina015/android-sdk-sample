package fr.voxeet.sdk.sample.application;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.voxeet.sdk.sample.R;
import fr.voxeet.sdk.sample.Recording;
import voxeet.com.sdk.core.VoxeetSdk;
import voxeet.com.sdk.json.UserInfo;

/**
 * Created by RomainBenmansour on 06,April,2016
 */
public class SampleApplication extends Application {

    private static final String TAG = SampleApplication.class.getSimpleName();

    @NonNull
    private List<Recording> recordedConference = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        VoxeetSdk.sdkInitialize(this,
                getString(R.string.consumer_key),
                getString(R.string.consumer_secret),
                new UserInfo("michel", null, "http://img0.mxstatic.com/wallpapers/fd823da7f3e99936e0e3f4c8b5d69b65_large.jpeg"));
        VoxeetSdk.enableOverlay(true);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);
    }

    public void saveRecordingConference(Recording newRecording) {
        for (Recording recording : recordedConference) {
            if (recording.conferenceId.equalsIgnoreCase(newRecording.conferenceId))
                return;
        }

        recordedConference.add(newRecording);
    }

    @NonNull
    public List<Recording> getRecordedConferences() {
        return recordedConference;
    }
}
