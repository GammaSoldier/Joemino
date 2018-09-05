package com.joekoperski.joemino;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class DlgHighscore extends Dialog {

    private MainActivity mContext;
    private String name;
    private String mFontLocation;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    DlgHighscore(Context context) {
        super(context);
        mContext = (MainActivity)context;
        mFontLocation = null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void setFont( String fontLocation ) {
        mFontLocation = fontLocation;
    }// setFont


    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCancelable(false);

        setTitle( R.string.str_highscore );
        setContentView(R.layout.enter_score);

        Button buttonOK = (Button) findViewById(R.id.buttonOK);
        TextView text = (TextView)findViewById( R.id.textViewScore );

        text.setText( mContext.getString( R.string.str_yourscore, mContext.GetScore() ));

        // automatically show keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                EditText edit=(EditText)findViewById(R.id.editName);
                name=edit.getText().toString();

                mContext.SetHighscoreName( name );
           }// onClick
        });

        GuiFontDecoration fontDecoration = new GuiFontDecoration( mContext, mFontLocation );
        fontDecoration.overrideFonts( mContext, getWindow().getDecorView());

    }// onCreate

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public String getName() {
        return name;
    }// getName



}
