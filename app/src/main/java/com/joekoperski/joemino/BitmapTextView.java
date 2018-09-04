package com.joekoperski.joemino;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

class BitmapTextView extends TextView {

    BitmapTextView (Context context) {
        super( context );
    }// BitmapTextView

    public void init( int width, int height, int resourceId ) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        setLayoutParams(params);

        setBackgroundColor(Color.TRANSPARENT);
        setBackgroundResource(resourceId);

        setPadding(0, 0, 0, 0);
        setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
    }
}
