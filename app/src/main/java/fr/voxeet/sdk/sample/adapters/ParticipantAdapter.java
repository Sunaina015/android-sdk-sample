package fr.voxeet.sdk.sample.adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.voxeet.android.media.MediaStream;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.voxeet.sdk.sample.R;
import fr.voxeet.sdk.sample.VideoView;
import voxeet.com.sdk.core.VoxeetSdk;
import voxeet.com.sdk.events.success.VideoStreamAddedEvent;
import voxeet.com.sdk.json.UserInfo;
import voxeet.com.sdk.models.abs.ConferenceUser;
import voxeet.com.sdk.models.impl.DefaultConferenceUser;

/**
 * Created by RomainB on 4/21/16.
 */
public class ParticipantAdapter extends BaseAdapter {
    private static final String TAG = ParticipantAdapter.class.getSimpleName();

    private Context context;

    private List<ConferenceUser> participants;

    private LayoutInflater inflater;

    private Map<String, RoomPosition> positionMap;

    private Map<String, MediaStream> mediaStreamMap;

    public class RoomPosition {
        public double angle;
        public double distance;

        public RoomPosition(double angle, double distance) {
            this.angle = angle;
            this.distance = distance;
        }
    }

    public ParticipantAdapter(Context context) {
        this.context = context;

        this.mediaStreamMap = new HashMap<>();

        this.participants = new ArrayList<>();

        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.positionMap = new HashMap<>();

        EventBus.getDefault().register(this);
    }

    public void updateParticipant(ConferenceUser conferenceUser) {
        if (conferenceUser.getStatus().equalsIgnoreCase(ConferenceUser.Status.LEFT.name())) {
            ConferenceUser user = doesContain(conferenceUser);
            if (user != null) {
                positionMap.remove(conferenceUser.getUserId());

                participants.remove(user);
            }
        }
    }

    public ConferenceUser doesContain(final ConferenceUser user) {
        return Iterables.find(participants, new Predicate<ConferenceUser>() {
            @Override
            public boolean apply(ConferenceUser input) {
                return user.getUserId().equalsIgnoreCase(input.getUserId());
            }
        }, null);
    }

    public void addParticipant(ConferenceUser conferenceUser) {
        if (doesContain(conferenceUser) == null) {
            participants.add(conferenceUser);

            positionMap.put(conferenceUser.getUserId(), new RoomPosition(0, 0.5));
        }
    }

    @Override
    public int getCount() {
        return participants.size();
    }

    @Override
    public Object getItem(int position) {
        return participants.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.participants_cell, parent, false);

            holder = new ViewHolder();
            holder.userId = (TextView) convertView.findViewById(R.id.user_id);
            holder.device = (TextView) convertView.findViewById(R.id.device);
            holder.position = (TextView) convertView.findViewById(R.id.position);
            holder.angle = (SeekBar) convertView.findViewById(R.id.angle);
            holder.distance = (SeekBar) convertView.findViewById(R.id.distance);
            holder.avatar = (VideoView) convertView.findViewById(R.id.avatar);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final DefaultConferenceUser user = (DefaultConferenceUser) getItem(position);

        holder.device.setText(user.getDeviceType());

        holder.position.setText(context.getResources().getString(R.string.participant_number, (position + 1)));

        UserInfo info = user.getUserInfo();

        if (!holder.avatar.isAttached() && mediaStreamMap.containsKey(user.getUserId())) {
            holder.avatar.setVisibility(View.VISIBLE);
            holder.avatar.setAttached(true);

            VoxeetSdk.attachMediaSdkStream(user.getUserId(), mediaStreamMap.get(user.getUserId()), holder.avatar.getRenderer());

            mediaStreamMap.remove(user.getUserId());
        }

        if (info != null && info.getName() != null && info.getName().length() > 0)
            holder.userId.setText(info.getName());
        else
            holder.userId.setText(user.getUserId());

        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoxeetSdk.muteSdkUser(user.getUserId(), !user.isMuted());

                Toast.makeText(context, "Mute set to: " + user.isMuted(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.angle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                updatePosition(user.getUserId(), progress, holder.distance.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        holder.distance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                updatePosition(user.getUserId(), holder.angle.getProgress(), progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return convertView;
    }

    private void updatePosition(String userId, int x, int y) {
        // angle has to be between -1 and 1
        double angle = ((double) x / 100.0) - 1.0;

        // distance has to be between 0 and 1
        double distance = (double) y / 100.0;

        positionMap.put(userId, new RoomPosition(angle, distance));

        VoxeetSdk.changePeerPosition(userId, angle, distance);
    }

    public int getItemAt(final String peerId) {
        for (int i = 0; i < participants.size(); i++) {
            if (participants.get(i).getUserId().equalsIgnoreCase(peerId))
                return i;
        }

        return -1;
    }

    @Subscribe
    public void onEvent(final VideoStreamAddedEvent event) {
        mediaStreamMap.put(event.getPeer(), event.getMediaStream());

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    private class ViewHolder {
        TextView userId;
        TextView device;
        TextView position;
        SeekBar angle;
        SeekBar distance;
        VideoView avatar;
    }
}
