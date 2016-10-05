package fr.voxeet.sdk.sample.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.voxeet.android.media.Media;
import com.voxeet.android.media.MediaStream;
import com.voxeet.android.media.video.CameraEnumerationAndroid;
import com.voxeet.android.media.video.VideoCapturerAndroid;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import fr.voxeet.sdk.sample.R;
import fr.voxeet.sdk.sample.ScreenShareView;
import fr.voxeet.sdk.sample.adapters.ParticipantAdapter;
import fr.voxeet.sdk.sample.dialogs.ConferenceOutput;
import voxeet.com.sdk.core.VoxeetPreferences;
import voxeet.com.sdk.core.VoxeetSdk;
import voxeet.com.sdk.events.success.ConferenceCreationSuccess;
import voxeet.com.sdk.events.success.ConferenceJoinedSuccessEvent;
import voxeet.com.sdk.events.success.ConferenceLeftSuccessEvent;
import voxeet.com.sdk.events.success.ConferenceUserJoinedEvent;
import voxeet.com.sdk.events.success.ConferenceUserUpdateEvent;
import voxeet.com.sdk.events.success.MessageReceived;
import voxeet.com.sdk.events.success.VideoStreamAddedEvent;
import voxeet.com.sdk.models.abs.ConferenceUser;

/**
 * Created by RomainBenmansour on 4/21/16.
 */
public class CreateConfActivity extends AppCompatActivity {

    private static final String TAG = CreateConfActivity.class.getSimpleName();

    private static final int CAMERA_REQUEST = 0x0010;

    private Button leave;

    private ListView participants;

    private ViewGroup joinLayout;

    private ParticipantAdapter adapter;

    private ViewGroup conferenceOptions;

    private ViewGroup sendText;

    private Button join;

    private Button audioRoutes;

    private Button mute;

    private Button sendBroadcast;

    private EditText editextConference;

    private EditText broadcastConference;

    private TextView aliasId;

    private Handler handler;

    private boolean isDemo;

    private boolean isInit;

    private ConferenceOutput conferenceOutput = null;

    private ScreenShareView screenShare;

    private ScreenShareView videoStream;

    private VideoCapturerAndroid capturer;

    private Media.MediaStreamListener mediaStreamListener = new Media.MediaStreamListener() {

        @Override
        public void onStreamAdded(String peer, MediaStream stream) {
            if (VoxeetPreferences.id() != null && VoxeetPreferences.id().equals(peer)) { // Own video stream
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        videoStream.setVisibility(View.VISIBLE);
                    }
                });
                VoxeetSdk.attachMediaSdkStream(peer, stream, videoStream.getRenderer());
            } else { // Participant video steam
                EventBus.getDefault().post(new VideoStreamAddedEvent(peer, stream));
            }
        }

        @Override
        public void onStreamRemoved(String peer) {
            if (VoxeetPreferences.id().equals(peer)) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        videoStream.setVisibility(View.GONE);
                    }
                });
            }
        }

        @Override
        public void onScreenStreamAdded(final String peer, final MediaStream stream) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    screenShare.setVisibility(View.VISIBLE);
                }
            });

            VoxeetSdk.attachMediaSdkStream(peer, stream, screenShare.getRenderer());
        }

        @Override
        public void onScreenStreamRemoved(String peer) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    screenShare.setVisibility(View.GONE);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_conf_activity);

        this.adapter = new ParticipantAdapter(this);

        this.screenShare = (ScreenShareView) findViewById(R.id.screen_share);

        this.videoStream = (ScreenShareView) findViewById(R.id.video_stream);

        this.joinLayout = (ViewGroup) findViewById(R.id.join_conf_layout);

        this.aliasId = (TextView) findViewById(R.id.conference_alias);

        this.conferenceOptions = (ViewGroup) findViewById(R.id.conference_options);

        this.sendText = (ViewGroup) findViewById(R.id.text_layout);

        this.capturer = VideoCapturerAndroid.create(CameraEnumerationAndroid.getNameOfFrontFacingDevice(), null);

        VoxeetSdk.setSdkVideoCapturer(capturer);

        this.join = (Button) findViewById(R.id.join);
        this.join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isInit) {
                    isInit = true;

                    VoxeetSdk.register(CreateConfActivity.this);
                }

                VoxeetSdk.joinSdkConference(editextConference.getText().toString());

                leave.setVisibility(View.VISIBLE);
            }
        });

        this.editextConference = (EditText) findViewById(R.id.conference_editext);

        this.broadcastConference = (EditText) findViewById(R.id.broadcast_editext);

        this.participants = (ListView) findViewById(R.id.participants);
        this.participants.setAdapter(adapter);

        this.sendBroadcast = (Button) findViewById(R.id.send_broadcast);
        this.sendBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoxeetSdk.sendSdkBroadcast(broadcastConference.getText().toString());
            }
        });

        this.leave = (Button) findViewById(R.id.leaveConf);
        this.leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoxeetSdk.leaveSdkConference();
            }
        });

        this.audioRoutes = (Button) findViewById(R.id.audio_routes);
        this.audioRoutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conferenceOutput != null) {
                    if (conferenceOutput.isVisible()) {
                        conferenceOutput.dismiss();
                    } else {
                        conferenceOutput.show(CreateConfActivity.this.getFragmentManager(), ConferenceOutput.TAG);
                    }
                }
            }
        });

        this.mute = (Button) findViewById(R.id.mute);
        this.mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean muted = !VoxeetSdk.isSdkMuted();
                if (muted)
                    mute.setText("Muted");
                else
                    mute.setText("Not Muted");

                VoxeetSdk.muteSdkConference(muted);
            }
        });

        this.conferenceOutput = new ConferenceOutput(this);

        this.handler = new Handler(Looper.getMainLooper());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
        } else
            initConf();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                initConf();
            else {
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();

                finish();
            }
        }
    }

    private void initConf() {
        if (getIntent().hasExtra("joinConf") && getIntent().getBooleanExtra("joinConf", false))
            displayJoin();
        else {
            if (getIntent().hasExtra("demo") && getIntent().getBooleanExtra("demo", false))
                VoxeetSdk.createSdkDemo();
            else
                VoxeetSdk.createSdkConference();

            isDemo = getIntent().getBooleanExtra("demo", false);

            isInit = true;

            VoxeetSdk.register(this);
        }
    }

    private void displayJoin() {
        joinLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (VoxeetSdk.isSdkConferenceLive())
            displayLeaveDialog();
        else
            super.onBackPressed();
    }

    private void displayLeaveDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getString(R.string.leave_conference_title));
        alertDialog.setMessage(getString(R.string.leave_conference_message));
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                VoxeetSdk.leaveSdkConference();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Subscribe
    public void onEvent(final ConferenceJoinedSuccessEvent event) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                joinLayout.setVisibility(View.GONE);

                conferenceOptions.setVisibility(View.VISIBLE);

                sendText.setVisibility(View.VISIBLE);

                if (!isDemo) {
                    aliasId.setVisibility(View.VISIBLE);
                    aliasId.setText(event.getAliasId());
                }
            }
        });

        VoxeetSdk.setMediaSdkStreamListener(mediaStreamListener);
    }

    @Subscribe
    public void onEvent(final ConferenceLeftSuccessEvent event) {
        VoxeetSdk.unregister(this);

        finish();
    }

    @Subscribe
    public void onEvent(final ConferenceUserUpdateEvent event) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (event.getUser().getStatus().equals(ConferenceUser.Status.LEFT.name())) {
                    adapter.updateParticipant(event.getUser());
                    adapter.notifyDataSetChanged();
                } else if (event.getUser().getStatus().equals(ConferenceUser.Status.ON_AIR.name())) {
                    adapter.addParticipant(event.getUser());
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Subscribe
    public void onEvent(MessageReceived event) {
        Log.e(TAG, event.getMessage());
    }

    @Subscribe
    public void onEvent(final ConferenceUserJoinedEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.addParticipant(event.getUser());

                adapter.notifyDataSetChanged();
            }
        });
    }
}