package fr.voxeet.sdk.sample.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import fr.voxeet.sdk.sample.R;
import voxeet.com.sdk.events.success.SocketConnectEvent;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int RECORD_AUDIO_RESULT = 0x20;
    private static final int REQUEST_EXTERNAL_STORAGE = 0x21;

    private static final int JOIN = 0x1000;
    private static final int CREATE = 0x1010;
    private static final int DEMO = 0x1020;

    private int lastAction;

    private Button demoCall;

    private Button createConf;

    private Button joinConf;

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

        demoCall = (Button) findViewById(R.id.demo);
        demoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDemoCall();
            }
        });

        createConf = (Button) findViewById(R.id.create_conf);
        createConf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createConf();
            }
        });

        joinConf = (Button) findViewById(R.id.join_conf);
        joinConf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinCall();
            }
        });

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

    private void startDemoCall() {
        lastAction = DEMO;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_RESULT);
        else {
            Intent intent = new Intent(MainActivity.this, CreateConfActivity.class);
            intent.putExtra("demo", true);
            MainActivity.this.startActivity(intent);
        }
    }

    private void joinCall() {
        lastAction = JOIN;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_RESULT);
        else {
            Intent intent = new Intent(MainActivity.this, CreateConfActivity.class);
            intent.putExtra("joinConf", true);
            MainActivity.this.startActivity(intent);
        }
    }

    private void createConf() {
        lastAction = CREATE;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_RESULT);
        else {
            Intent intent = new Intent(MainActivity.this, CreateConfActivity.class);
            intent.putExtra("demo", false);
            MainActivity.this.startActivity(intent);
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
