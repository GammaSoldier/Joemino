package com.joekoperski.joemino;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class DlgGameOver extends Dialog {

    private MainActivity mContext;
    private String mFontLocation;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    DlgGameOver(Context context) {
        super(context);
        mContext = (MainActivity) context;
        mFontLocation = null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void setFont(String fontLocation) {
        mFontLocation = fontLocation;
    }// setFont


    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use the Builder class for convenient dialog construction
        setCancelable(false);
//        setTitle(R.string.app_name);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.game_over);

        Button buttonOK = (Button) findViewById(R.id.buttonOK);
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mContext.StartGame();
            }// onClick
        });
        GuiFontDecoration fontDecoration = new GuiFontDecoration(mContext, mFontLocation);
        fontDecoration.overrideFonts(mContext, getWindow().getDecorView());

    }// OnCreate
}
