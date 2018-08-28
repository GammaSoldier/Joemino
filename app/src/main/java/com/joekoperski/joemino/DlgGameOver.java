package com.joekoperski.joemino;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DlgGameOver extends Dialog {

    private MainActivity mContext;

    public DlgGameOver(Context context) {
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
