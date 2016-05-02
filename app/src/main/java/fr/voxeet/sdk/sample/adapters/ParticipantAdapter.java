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
import java.util.List;

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

    public ParticipantAdapter(Context context) {
        this.context = context;

        this.participants = new ArrayList<>();

        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateParticipant(ConferenceUser conferenceUser) {

        if (conferenceUser.getStatus().equalsIgnoreCase(ConferenceUser.LEFT)) {
            ConferenceUser user = doesContain(conferenceUser);
            if (user != null)
                participants.remove(user);
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

        if (doesContain(conferenceUser) == null)
            this.participants.add(conferenceUser);
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

        double xValue = ((double)x / 100.0) - 1.0;
        double yValue = (double)y / 100.0;

        double angle2 = ((Math.atan2(xValue, yValue) * 180.0) / Math.PI);
        double distance = Math.sqrt(xValue * xValue + yValue * yValue) * 10;

        Log.e(TAG, "xValue : " + xValue + " - YValue : " + yValue + " - angle : " + angle2 + " - distance : " + distance);

        VoxeetSdk.changePeerPosition(userId, angle2, distance);
    }

    private class ViewHolder {
        TextView userId;
        TextView device;
        TextView position;
        SeekBar angle;
        SeekBar distance;
    }
}
