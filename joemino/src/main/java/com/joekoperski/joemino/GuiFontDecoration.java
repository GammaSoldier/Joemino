package com.joekoperski.joemino;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class GuiFontDecoration {

    private Typeface mFont;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    GuiFontDecoration(Context context, String fontLocation) {
        if (fontLocation != null) {
            mFont = Typeface.createFromAsset(context.getAssets(), fontLocation);
        }// if
        else {
            mFont = Typeface.DEFAULT;
        }// else

    }// GuiFontDecoration


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void overrideFonts(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child);
                }// if
            }// if
            else if (v instanceof TextView) {
                ((TextView) v).setTypeface(mFont);
            }// else
        }// try
        catch (Exception e) {
        }// catch
    }// overrideFonts

}// GuiFontDecoration
