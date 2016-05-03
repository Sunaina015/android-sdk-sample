package fr.voxeet.sdk.sample.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import fr.voxeet.sdk.sample.R;
import fr.voxeet.sdk.sample.adapters.ParticipantAdapter;
import voxeet.com.sdk.events.success.ConferenceLeft;
import voxeet.com.sdk.events.success.MessageReceived;
import voxeet.com.sdk.events.success.ConferenceJoined;
import voxeet.com.sdk.events.success.ParticipantAdded;
import voxeet.com.sdk.events.success.ParticipantUpdated;
import voxeet.com.sdk.core.VoxeetSdk;
import voxeet.com.sdk.exception.VoxeetSdkException;

import static fr.voxeet.sdk.sample.adapters.ParticipantAdapter.*;

/**
 * Created by RomainBenmansour on 4/21/16.
 */
public class CreateConfActivity extends AppCompatActivity {

    private static final String TAG = CreateConfActivity.class.getSimpleName();

    private Button leave;

    private ListView participants;

    private ViewGroup joinLayout;

    private ParticipantAdapter adapter;

    private Button join;

    private Button sendBroadcast;

    private EditText editextConference;

    private EditText broadcastConference;

    private TextView aliasId;

    private Handler handler;

    private boolean isDemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_conf_activity);

        this.adapter = new ParticipantAdapter(this);

        this.joinLayout = (ViewGroup) findViewById(R.id.join_conf_layout);

        this.aliasId = (TextView) findViewById(R.id.conference_alias);

        this.join = (Button) findViewById(R.id.join);
        this.join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoxeetSdk.joinConference(editextConference.getText().toString());

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
                VoxeetSdk.sendBroadcast(broadcastConference.getText().toString());
            }
        });

        this.leave = (Button) findViewById(R.id.leaveConf);
        this.leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoxeetSdk.leaveConference();
            }
        });

        this.handler = new Handler(Looper.getMainLooper());

        if (getIntent().hasExtra("joinConf") && getIntent().getBooleanExtra("joinConf", false))
            displayJoin();
        else {
            if (getIntent().hasExtra("demo") && getIntent().getBooleanExtra("demo", false)) {
                isDemo = true;
                VoxeetSdk.createDemoConference();
            } else {
                isDemo = false;
                VoxeetSdk.createConference();
            }
        }

        VoxeetSdk.register(this);
    }

    private void displayJoin() {
        joinLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        VoxeetSdk.unregister(this);
    }

    @Subscribe
    public void onEvent(final ConferenceJoined event) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!isDemo) {
                    aliasId.setVisibility(View.VISIBLE);
                    aliasId.setText(event.getAliasId());
                }

                leave.setVisibility(View.VISIBLE);
            }
        });
    }

    @Subscribe
    public void onEvent(final ConferenceLeft event) {
        finish();
    }

    @Subscribe
    public void onEvent(final ParticipantUpdated event) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                adapter.updateParticipant(event.getConferenceUser());
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Subscribe
    public void onEvent(MessageReceived event) {

        ParticipantAdapter.RoomPosition position = adapter.getUserPosition(event.getUserId());

        VoxeetSdk.playSound("elephant_mono.mp3", position.angle, position.distance);

        Log.e(TAG, event.getMessage());
    }

    @Subscribe
    public void onEvent(final ParticipantAdded event) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                adapter.addParticipant(event.getConferenceUser());
                adapter.notifyDataSetChanged();
            }
        });
    }
}