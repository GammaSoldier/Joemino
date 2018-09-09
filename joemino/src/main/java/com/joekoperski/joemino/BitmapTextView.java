package com.joekoperski.joemino;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

class BitmapTextView extends TextView {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    BitmapTextView(Context context) {
        super(context);
    }// BitmapTextView

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void init(Context context,int width, int height, int resourceId, String fontLocation,int alingnHorizontal) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), fontLocation);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        setLayoutParams(params);

        setBackgroundColor(Color.TRANSPARENT);
        setBackgroundResource(resourceId);

        setPadding(10, 0, 10, 0);
        setGravity(alingnHorizontal | Gravity.CENTER_VERTICAL);

        setTypeface(font);
    }// init

}// BitmapTextView
