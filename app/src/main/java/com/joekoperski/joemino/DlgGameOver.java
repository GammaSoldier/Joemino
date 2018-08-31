package com.joekoperski.joemino;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DlgGameOver extends Dialog {

    private MainActivity mContext;

    DlgGameOver(Context context) {
        super(context);
        mContext = (MainActivity)context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use the Builder class for convenient dialog construction
        setCancelable(false);
        setTitle( R.string.app_name );

        setContentView(R.layout.game_over);

        Button buttonOK = (Button) findViewById(R.id.buttonOK);
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mContext.StartGame();
             }// onClick
        });

    }// OnCreate
}
