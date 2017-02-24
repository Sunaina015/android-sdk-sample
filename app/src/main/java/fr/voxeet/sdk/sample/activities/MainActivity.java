package fr.voxeet.sdk.sample.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.voxeet.sdk.sample.R;
import voxeet.com.sdk.events.success.SocketConnectEvent;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int RECORD_AUDIO_RESULT = 0x20;
    private static final int REQUEST_EXTERNAL_STORAGE = 0x21;

    public static final int JOIN = 0x1000;
    public static final int CREATE = 0x1010;
    public static final int DEMO = 0x1020;
    public static final int REPLAY = 0x1030;

    private int lastAction;

    @Bind(R.id.demo)
    protected Button demoCall;

    @Bind(R.id.create_conf)
    protected Button createConf;

    @Bind(R.id.join_conf)
    protected Button joinConf;

    @Bind(R.id.replay_conf)
    protected Button replayConf;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RECORD_AUDIO_RESULT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    switch (lastAction) {
                        case DEMO:
                            startDemoCall();
                            break;
                        case CREATE:
                            createConf();
                            break;
                        case JOIN:
                            joinCall();
                            break;
                        default:
                            throw new IllegalStateException("Invalid last option");
                    }
                }
                return;
            }
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Storage granted", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        EventBus.getDefault().register(this);

        ButterKnife.bind(this);

        verifyStoragePermissions(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }
    }

    @OnClick(R.id.demo)
    public void demoButton() {
        startDemoCall();
    }

    @OnClick(R.id.create_conf)
    public void createButton() {
        createConf();
    }

    @OnClick(R.id.join_conf)
    public void joinButton() {
        joinCall();
    }

    @OnClick(R.id.replay_conf)
    public void replayButton() {
        replayConf();
    }

    public void verifyStoragePermissions(Activity context) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    context,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void replayConf() {
        conferenceActivity(lastAction = REPLAY);
    }

    private void startDemoCall() {
        conferenceActivity(lastAction = DEMO);
    }

    private void joinCall() {
        conferenceActivity(lastAction = JOIN);
    }

    private void createConf() {
        conferenceActivity(lastAction = CREATE);
    }

    public void conferenceActivity(int lastAction) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_RESULT);
        else {
            Intent intent = new Intent(MainActivity.this, CreateConfActivity.class);

            switch (lastAction) {
                case DEMO:
                    intent.putExtra("demo", true);
                    break;
                case CREATE:
                    intent.putExtra("create", true);
                    break;
                case REPLAY:
                    intent.putExtra("replay", true);
                    break;
                case JOIN:
                default:
                    intent.putExtra("join", true);
                    break;
            }

            startActivity(intent);
        }
    }

    @Subscribe
    public void onEvent(SocketConnectEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                demoCall.setEnabled(true);

                createConf.setEnabled(true);

                joinConf.setEnabled(true);

                replayConf.setEnabled(true);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }
}
