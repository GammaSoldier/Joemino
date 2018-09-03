package com.joekoperski.joemino;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;


public class DlgScores extends Dialog {

    private MainActivity parent;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    DlgScores(MainActivity context) {
        super(context);
        parent = context;
    }// DlgScores

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setTitle( R.string.str_highscores );
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.score_board);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.score_board_title);
        initList();

        //On Click listeners for the buttons present in the Dialog
        Button buttonOK = (Button) findViewById(R.id.buttonOK);
        Button buttonDel = (Button) findViewById(R.id.buttonDel);

        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); //to dismiss the Dialog
            }// onClick
        });

        buttonDel.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v) {
                //Fire an intent on click of this button
               parent.DeleteScores();
               initList();
            }// onClick
        });
    }// onCreate


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void initList() {
        Scores scores = new Scores(parent);

        TextView textView;
        textView = (TextView) findViewById( R.id.textView12 );
        textView.setText( scores.getAt( 0 ).name );
        textView = (TextView) findViewById( R.id.textView13 );
        textView.setText( Integer.toString(scores.getAt( 0 ).score) );

        textView = (TextView) findViewById( R.id.textView22 );
        textView.setText( scores.getAt( 1 ).name );
        textView = (TextView) findViewById( R.id.textView23 );
        textView.setText( Integer.toString(scores.getAt( 1 ).score) );

        textView = (TextView) findViewById( R.id.textView32 );
        textView.setText( scores.getAt( 2 ).name );
        textView = (TextView) findViewById( R.id.textView33 );
        textView.setText( Integer.toString(scores.getAt( 2 ).score) );

        textView = (TextView) findViewById( R.id.textView42 );
        textView.setText( scores.getAt( 3 ).name );
        textView = (TextView) findViewById( R.id.textView43 );
        textView.setText( Integer.toString(scores.getAt( 3 ).score) );

        textView = (TextView) findViewById( R.id.textView52 );
        textView.setText( scores.getAt( 4 ).name );
        textView = (TextView) findViewById( R.id.textView53 );
        textView.setText( Integer.toString(scores.getAt( 4 ).score) );

    }
}// DlgScores
