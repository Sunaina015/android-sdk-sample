package fr.voxeet.sdk.sample.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.squareup.picasso.Picasso;
import com.voxeet.android.media.MediaStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.voxeet.sdk.sample.R;
import fr.voxeet.sdk.sample.Recording;
import voxeet.com.sdk.core.VoxeetSdk;
import voxeet.com.sdk.json.UserInfo;
import voxeet.com.sdk.models.abs.ConferenceUser;
import voxeet.com.sdk.models.impl.DefaultConferenceUser;
import voxeet.com.sdk.views.RoundedImageView;
import voxeet.com.sdk.views.VideoView;

/**
 * Created by RomainB on 4/21/16.
 */
public class RecordedConferencesAdapter extends BaseAdapter {
    private static final String TAG = RecordedConferencesAdapter.class.getSimpleName();

    private Context context;

    private List<Recording> recordings;

    private LayoutInflater inflater;

    public RecordedConferencesAdapter(Context context, List<Recording> recordings) {
        this.context = context;

        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.recordings = recordings;
    }

    @Override
    public int getCount() {
        return recordings.size();
    }

    @Override
    public Object getItem(int position) {
        return recordings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.recorded_conf_cell, parent, false);

            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Recording recording = (Recording) getItem(position);

        holder.date.setText(new Date(recording.timestamp).toString());

        holder.title.setText("Conference #" + (position + 1));

        return convertView;
    }

    private class ViewHolder {
        TextView title;

        TextView date;

        ViewHolder(View convertView) {
            title = (TextView) convertView.findViewById(R.id.conf_title);

            date = (TextView) convertView.findViewById(R.id.conf_date);
        }
    }
}
