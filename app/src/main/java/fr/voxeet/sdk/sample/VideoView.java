package fr.voxeet.sdk.sample;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.voxeet.android.media.video.RendererCommon;
import com.voxeet.android.media.video.SurfaceViewRenderer;

import butterknife.Bind;
import butterknife.ButterKnife;
import voxeet.com.sdk.core.VoxeetSdk;

/**
 * Created by romainbenmansour on 11/08/16.
 */
public class VideoView extends FrameLayout {
    private final String TAG = VideoView.class.getSimpleName();

    private boolean isAttached = false;
    
    @Bind(R.id.renderer)
    protected SurfaceViewRenderer renderer;

    public VideoView(Context context) {
        super(context);

        init();
    }

    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        View v = inflate(getContext(), R.layout.preview_video_streaming, this);

        ButterKnife.bind(this, v);

        setSurfaceViewRenderer();
    }

    public void setSurfaceViewRenderer() {
        this.renderer.init(VoxeetSdk.getSdkEglContext(), null);

        this.renderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
    }

    public void release() {
        this.renderer.release();
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }

    public SurfaceViewRenderer getRenderer() {
        return renderer;
    }

    public void setAttached(boolean attached) {
        isAttached = attached;
    }

    public boolean isAttached() {
        return isAttached;
    }
}