package fr.voxeet.sdk.sample.dialogs;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.voxeet.android.media.Media;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.voxeet.sdk.sample.R;
import voxeet.com.sdk.core.VoxeetSdk;

/**
 * Created by Thomas on 08/12/2015.
 */
public class ConferenceOutput extends DialogFragment {

    public static final String TAG = ConferenceOutput.class.getSimpleName();

    protected ListView outputListView;

    private List<Media.AudioRoute> currentRoutes;

    private List<String> routesDescription = Arrays.asList("Headset", "Phone", "Speaker", "Bluetooth");

    private Context context;

    public ConferenceOutput(Context context) {
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_conference_output, container, true);

        outputListView = (ListView) v.findViewById(R.id.output_list_view);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        outputListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                VoxeetSdk.setOutputRoute(currentRoutes.get(position));
                dismiss();
            }
        });
    }

    protected void setRoutesFromAudioSession() {
        currentRoutes = VoxeetSdk.getAvailableRoutes();

        List<String> desc = new ArrayList<>();
        for (Media.AudioRoute r : currentRoutes) {
            desc.add(routesDescription.get(r.value()));
        }

        outputListView.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_single_choice, desc.toArray(new String[desc.size()])));

        Media.AudioRoute selectedRoute = VoxeetSdk.currentRoute();

        outputListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        outputListView.setItemChecked(currentRoutes.indexOf(selectedRoute), true);
        outputListView.setSelection(currentRoutes.indexOf(selectedRoute));
    }

    @Override
    public void onResume() {
        super.onResume();

        setRoutesFromAudioSession();
    }
}
