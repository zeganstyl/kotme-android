package com.kotme;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * {@link android.widget.ImageView} that supports directional cropping in both vertical and
 * horizontal directions instead of being restricted to center-crop. Automatically sets {@link
 * android.widget.ImageView.ScaleType} to MATRIX and defaults to center-crop.
 */
public class CropImageView extends AppCompatImageView {
    private static final float DEFAULT_HORIZONTAL_OFFSET = 0f;
    private static final float DEFAULT_VERTICAL_OFFSET = 0f;

    private float mHorizontalOffsetPercent = DEFAULT_HORIZONTAL_OFFSET;
    private float mVerticalOffsetPercent = DEFAULT_VERTICAL_OFFSET;

    public CropImageView(Context context) {
        this(context, null);
    }

    public CropImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        applyCropOffset();
    }

    /**
     * Sets the crop box offset by the specified percentage values. For example, a center-crop would
     * be (0.5, 0.5), a top-left crop would be (0, 0), and a bottom-center crop would be (0.5, 1)
     */
    public void setCropOffset(float horizontalOffsetPercent, float verticalOffsetPercent) {
        if (mHorizontalOffsetPercent < 0
                || mVerticalOffsetPercent < 0
                || mHorizontalOffsetPercent > 1
                || mVerticalOffsetPercent > 1) {
            throw new IllegalArgumentException("Offset values must be a float between 0.0 and 1.0");
        }

        mHorizontalOffsetPercent = horizontalOffsetPercent;
        mVerticalOffsetPercent = verticalOffsetPercent;
        applyCropOffset();
    }

    private void applyCropOffset() {
        Matrix matrix = getImageMatrix();

        float scale;
        int viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        int drawableWidth = 0, drawableHeight = 0;
        // Allow for setting the drawable later in code by guarding ourselves here.
        if (getDrawable() != null) {
            drawableWidth = getDrawable().getIntrinsicWidth();
            drawableHeight = getDrawable().getIntrinsicHeight();
        }

        // Get the scale.
        if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
            // Drawable is flatter than view. Scale it to fill the view height.
            // A Top/Bottom crop here should be identical in this case.
            scale = (float) viewHeight / (float) drawableHeight;
        } else {
            // Drawable is taller than view. Scale it to fill the view width.
            // Left/Right crop here should be identical in this case.
            scale = (float) viewWidth / (float) drawableWidth;
        }

        float viewToDrawableWidth = viewWidth / scale;
        float viewToDrawableHeight = viewHeight / scale;
        float xOffset = mHorizontalOffsetPercent * (drawableWidth - viewToDrawableWidth);
        float yOffset = mVerticalOffsetPercent * (drawableHeight - viewToDrawableHeight);

        // Define the rect from which to take the image portion.
        RectF drawableRect =
                new RectF(
                        xOffset,
                        yOffset,
                        xOffset + viewToDrawableWidth,
                        yOffset + viewToDrawableHeight);
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.FILL);

        setImageMatrix(matrix);
    }
}