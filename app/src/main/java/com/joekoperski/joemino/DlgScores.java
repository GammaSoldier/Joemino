package com.joekoperski.joemino;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;


public class DlgScores extends Dialog {

    private MainActivity parent;
    private String mFontLocation;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    DlgScores(MainActivity context) {
        super(context);
        parent = context;
        mFontLocation = null;
    }// DlgScores


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void setFont(String fontLocation) {
        mFontLocation = fontLocation;
    }// setFont


    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setTitle(R.string.str_highscores);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.score_board);
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
        GuiFontDecoration fontDecoration = new GuiFontDecoration(parent, mFontLocation);
        fontDecoration.overrideFonts(parent, getWindow().getDecorView());
    }// onCreate


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void initList() {
        Scores scores = new Scores(parent);
        int score;

        TextView textView;

        int viewId[][] = {
                  {R.id.textView11, R.id.textView12, R.id.textView13}
                , {R.id.textView21, R.id.textView22, R.id.textView23}
                , {R.id.textView31, R.id.textView32, R.id.textView33}
                , {R.id.textView41, R.id.textView42, R.id.textView43}
                , {R.id.textView51, R.id.textView52, R.id.textView53}
        };

        for (int i = 0; i < 5; i++) {
            score = scores.getAt(i).score;
            textView = (TextView) findViewById(viewId[i][1]);
            textView.setText(scores.getAt(i).name);
            textView = (TextView) findViewById(viewId[i][2]);
            textView.setText(Integer.toString(score));
            for (int j = 0; j < 3; j++) {
                setThresholdEntryColor(viewId[i][j], score);
            }// for j
        }// for i

    }// initList


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void setThresholdEntryColor(int id, int score) {
        if (score < parent.getResources().getInteger(R.integer.value_highscore_theshold)) {
            ((TextView) findViewById(id)).setTextColor(Color.rgb(255, 120, 255));
        }// if
    }// setThresholdEntryColor
}// DlgScores

