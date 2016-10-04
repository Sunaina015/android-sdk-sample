package fr.voxeet.sdk.sample;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.voxeet.android.media.video.EglBase;
import com.voxeet.android.media.video.RendererCommon;
import com.voxeet.android.media.video.SurfaceViewRenderer;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by romainbenmansour on 11/08/16.
 */
public class ScreenShareView extends FrameLayout {
    private final String TAG = ScreenShareView.class.getSimpleName();

    private final ScreenShareStateListener listener;

    private boolean isMaxedOut = false;

    public final int defaultWith = getResources().getDimensionPixelSize(R.dimen.screen_preview_width);

    public final int defaultHeight = getResources().getDimensionPixelSize(R.dimen.screen_preview_height);

    public final int defaultWithRenderer = getResources().getDimensionPixelSize(R.dimen.screen_preview_width_padding);

    public final int defaultHeightRenderer = getResources().getDimensionPixelSize(R.dimen.screen_preview_height_padding);

    private LayoutParams defaultParams;

    private LayoutParams maxSizeParams;

    private LayoutParams renderParams;

    private EglBase eglBase;

    @Bind(R.id.container)
    protected FrameLayout container;

    @Bind(R.id.renderer)
    protected SurfaceViewRenderer renderer;

    public ScreenShareView(Context context, ScreenShareStateListener listener) {
        super(context);

        this.listener = listener;

        init();
    }

    public ScreenShareView(Context context) {
        super(context);

        this.listener = null;

        init();
    }

    public ScreenShareView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.listener = null;

        init();
    }

    public ScreenShareView(Context context, AttributeSet attrs, ScreenShareStateListener listener) {
        super(context, attrs);

        this.listener = listener;

        init();
    }

    private void init() {
        View v = inflate(getContext(), R.layout.preview_screensharing, this);

        ButterKnife.bind(this, v);

        setSurfaceViewRenderer();

        defaultParams = new LayoutParams(
                defaultWith,
                defaultHeight);
        defaultParams.gravity = Gravity.TOP | Gravity.RIGHT;

        renderParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        renderParams.gravity = Gravity.CENTER;

        maxSizeParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        maxSizeParams.gravity = Gravity.CENTER;
    }

    public void setSurfaceViewRenderer() {
        this.eglBase = EglBase.create();

        this.renderer.init(eglBase.getEglBaseContext(), null);

        this.renderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }

    public SurfaceViewRenderer getRenderer() {
        return renderer;
    }

    public interface ScreenShareStateListener {
        void onDefaultSize();

        void onMaxSize();
    }
}