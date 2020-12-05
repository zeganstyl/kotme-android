package org.thelemistix.kotme;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;


public class OutlineTextView extends AppCompatTextView {
    private int textColor;
    private int outlineColor;

    public OutlineTextView(Context context) {
        this(context, null);
    }

    public OutlineTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public OutlineTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // If the reflection fails (which really shouldn't happen), we
        // won't need the rest of this stuff, so we keep it in the try-catch

        textColor = getTextColors().getDefaultColor();

        // These can be changed to hard-coded default
        // values if you don't need to use XML attributes

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.OutlineTextView);
        outlineColor = a.getColor(R.styleable.OutlineTextView_outlineColor, Color.TRANSPARENT);
        setOutlineStrokeWidth(a.getDimensionPixelSize(R.styleable.OutlineTextView_outlineWidth, 0));
        a.recycle();
    }

    @Override
    public void setTextColor(int color) {
        textColor = color;
        super.setTextColor(color);
    }

    public void setOutlineColor(int color) {
        outlineColor = color;
        invalidate();
    }

    public void setOutlineWidth(float width) {
        setOutlineStrokeWidth(width);
        invalidate();
    }

    private void setOutlineStrokeWidth(float width) {
        getPaint().setStrokeWidth(2 * width + 1);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.setTextColor(outlineColor);
        getPaint().setStyle(Paint.Style.STROKE);
        super.onDraw(canvas);
        super.setTextColor(textColor);
        getPaint().setStyle(Paint.Style.FILL);
        super.onDraw(canvas);
    }
}