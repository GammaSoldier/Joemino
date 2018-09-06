package com.joekoperski.joemino;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

class BitmapTextView extends TextView {
    private Context mContext;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    BitmapTextView(Context context) {
        super(context);
        mContext = context;
    }// BitmapTextView

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void init(int width, int height, int resourceId, String fontLocation) {
        Typeface font = Typeface.createFromAsset(mContext.getAssets(), fontLocation);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        setLayoutParams(params);

        setBackgroundColor(Color.TRANSPARENT);
        setBackgroundResource(resourceId);

        setPadding(0, 0, 0, 0);
        setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        setTypeface(font);
    }// init

}// BitmapTextView
