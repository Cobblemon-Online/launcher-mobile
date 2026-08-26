package net.kdt.pojavlaunch.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class CenterCropVideoView extends VideoView {

    private int videoWidth;
    private int videoHeight;

    public CenterCropVideoView(Context context) {
        super(context);
    }

    public CenterCropVideoView(
            Context context,
            AttributeSet attrs
    ) {
        super(context, attrs);
    }

    public CenterCropVideoView(
            Context context,
            AttributeSet attrs,
            int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
    }

    public void setVideoSize(
            int width,
            int height
    ) {
        videoWidth = width;
        videoHeight = height;

        requestLayout();
    }

    @Override
    protected void onMeasure(
            int widthMeasureSpec,
            int heightMeasureSpec
    ) {
        int containerWidth =
                MeasureSpec.getSize(widthMeasureSpec);

        int containerHeight =
                MeasureSpec.getSize(heightMeasureSpec);

        if (
                videoWidth == 0
                        || videoHeight == 0
                        || containerWidth == 0
                        || containerHeight == 0
        ) {
            super.onMeasure(
                    widthMeasureSpec,
                    heightMeasureSpec
            );
            return;
        }

        float videoRatio =
                (float) videoWidth / videoHeight;

        float containerRatio =
                (float) containerWidth / containerHeight;

        int finalWidth;
        int finalHeight;

        if (videoRatio > containerRatio) {
            // Vídeo é proporcionalmente mais largo.
            // Altura preenche a tela e sobra nas laterais.
            finalHeight = containerHeight;

            finalWidth =
                    Math.round(
                            containerHeight * videoRatio
                    );

        } else {
            // Vídeo é proporcionalmente mais alto.
            // Largura preenche a tela e sobra em cima/baixo.
            finalWidth = containerWidth;

            finalHeight =
                    Math.round(
                            containerWidth / videoRatio
                    );
        }

        setMeasuredDimension(
                finalWidth,
                finalHeight
        );
    }
}