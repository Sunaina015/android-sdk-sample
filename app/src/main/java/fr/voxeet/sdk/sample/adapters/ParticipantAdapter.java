package fr.voxeet.sdk.sample.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.voxeet.sdk.sample.R;
import voxeet.com.sdk.models.ConferenceUser;
import voxeet.com.sdk.core.VoxeetSdk;

/**
 * Created by RomainBenmansour on 4/21/16.
 */
public class ParticipantAdapter extends BaseAdapter {

    private static final String TAG = ParticipantAdapter.class.getSimpleName();

    private Context context;

    private List<ConferenceUser> participants;

    private LayoutInflater inflater;

    private Map<String, RoomPosition> positionMap;

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

        this.participants = new ArrayList<>();

        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.positionMap = new HashMap<>();
    }

    public void updateParticipant(ConferenceUser conferenceUser) {

        if (conferenceUser.getStatus().equalsIgnoreCase(ConferenceUser.LEFT)) {
            ConferenceUser user = doesContain(conferenceUser);
            if (user != null) {
                positionMap.remove(conferenceUser.getUserId());
                participants.remove(user);
            }
        }
    }

    public ConferenceUser doesContain(final ConferenceUser user) {
        final ConferenceUser model = Iterables.find(participants, new Predicate<ConferenceUser>() {
            @Override
            public boolean apply(ConferenceUser input) {
                return user.getUserId().equalsIgnoreCase(input.getUserId());
            }
        }, null);

        return model;
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
            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.participants_cell, parent, false);

            holder.userId = (TextView) convertView.findViewById(R.id.user_id);
            holder.device = (TextView) convertView.findViewById(R.id.device);
            holder.position = (TextView) convertView.findViewById(R.id.position);
            holder.angle = (SeekBar) convertView.findViewById(R.id.angle);
            holder.distance = (SeekBar) convertView.findViewById(R.id.distance);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        final ConferenceUser user = (ConferenceUser) getItem(position);

        holder.device.setText(user.getDeviceType());

        holder.position.setText(context.getResources().getString(R.string.participant_number, (position + 1)));

        holder.userId.setText(user.getUserId());

        holder.angle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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

        double angle = ((double)x / 100.0) - 1.0;

        double distance = (double)y / 100.0;

        positionMap.put(userId, new RoomPosition(angle, distance));

        VoxeetSdk.changePeerPosition(userId, angle, distance);
    }

    public RoomPosition getUserPosition(String userId) {
        return positionMap.get(userId);
    }

    private class ViewHolder {
        TextView userId;
        TextView device;
        TextView position;
        SeekBar angle;
        SeekBar distance;
    }
}
