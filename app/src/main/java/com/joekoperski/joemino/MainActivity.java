// TODO Sound
// TODO Release Version
// TODO adapt button extents to each screen resolution
// TODO Playfield animation



package com.joekoperski.joemino;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;


import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MainActivity extends Activity {

    private GameView gameView;
    private FrameLayout gameLayout;// Sort of "holder" for everything we are placing
    private RelativeLayout gameButtons;//Holder for the buttons
    private Playfield playfield;
    private GameRules gameRules;

	////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        playfield = new Playfield();
        gameRules = new GameRules( playfield );

        gameView = new GameView( this );
        gameView.setPlayfieldExtents( playfield.GetWidth(), playfield.GetHeight() );

        gameLayout = new FrameLayout(this);
        gameButtons = new RelativeLayout(this);

        //Define the layout parameter for the button to wrap the content for both width and height
        RelativeLayout.LayoutParams layoutButtonNew = new RelativeLayout.LayoutParams( WRAP_CONTENT, WRAP_CONTENT );
        RelativeLayout.LayoutParams layoutButtonScore = new RelativeLayout.LayoutParams( WRAP_CONTENT, WRAP_CONTENT );
        RelativeLayout.LayoutParams layoutButtonExit = new RelativeLayout.LayoutParams( WRAP_CONTENT, WRAP_CONTENT );

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.FILL_PARENT );
        gameButtons.setLayoutParams(params);



        // Button New
        ImageButton buttonNew = new ImageButton(this );
        buttonNew.setImageResource( R.drawable.button_new_images );
        buttonNew.setAdjustViewBounds( true );
        buttonNew.setBackgroundColor( 0 );  // transparent background
        gameButtons.addView(  buttonNew );
        layoutButtonNew.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE );
        layoutButtonNew.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE );

        layoutButtonNew.width = 336;
        layoutButtonNew.height = 189;

        buttonNew.setLayoutParams(layoutButtonNew);

        buttonNew.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gameRules = new GameRules( playfield );
                gameView.renderPlayfield( playfield );
            }
        } );


        // Button Score
        ImageButton buttonScore = new ImageButton(this );
        buttonScore.setImageResource( R.drawable.button_score_images );
        buttonScore.setAdjustViewBounds( true );
        buttonScore.setBackgroundColor( 0 );  // transparent background
        gameButtons.addView(  buttonScore );
        layoutButtonScore.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE );
        layoutButtonScore.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE );

        layoutButtonScore.width = 336;
        layoutButtonScore.height = 189;

        buttonScore.setLayoutParams(layoutButtonScore);

        buttonScore.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DlgScores dlgScores = new DlgScores( MainActivity.this );
                dlgScores.show();
            }
        } );


        // Button Exit
        ImageButton buttonExit = new ImageButton(this);
        buttonExit.setImageResource( R.drawable.button_exit_images );
        buttonExit.setAdjustViewBounds( true );
        buttonExit.setBackgroundColor( 0 );  // transparent background
        gameButtons.addView( buttonExit );
        layoutButtonExit.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        layoutButtonExit.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

        layoutButtonExit.width = 336;
        layoutButtonExit.height = 189;

        buttonExit.setLayoutParams(layoutButtonExit);

        buttonExit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Exit the app
                moveTaskToBack(true);
            }
        } );

        gameLayout.addView(gameView);
        gameLayout.addView(gameButtons);
        setContentView(gameLayout);

        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().getDecorView().setBackgroundColor( 0x000000 );
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onDestroy() {  
        super.onDestroy();  
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
 	public void DeleteScores() {
        Log.d("MainActivity", "DeleteScores");
        HighscoreEntry highscoreEntry = new HighscoreEntry();
        Scores scores = new Scores( this );

        for( int i = 0; i < scores.getNumScores(); i++ ){
            highscoreEntry.name = "";
            highscoreEntry.score = -1;
            scores.insertAt( i, highscoreEntry );
        }// for i
        scores.save();

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void TileTouched(int x, int y ) {
        Boolean isHighscore = false;
        Log.d("MainActivity", "TileTouched");

        if( gameRules.makeMove( playfield, x, y ) == gameRules.GAMEOVER ) {
            gameView.renderPlayfield( playfield );

            HighscoreEntry highscoreEntry = new HighscoreEntry();
            Scores scores = new Scores( this );

            for( int i = 0; i < scores.getNumScores(); i++ ){
                if( gameRules.compareScore( scores.getAt(i).score) ) {
                    DlgHighscore dlgHighscore = new DlgHighscore( MainActivity.this );
                    dlgHighscore.show();
                    isHighscore = true;
                    break;
                }// if
            }// for i
            if( !isHighscore ){
                DlgGameOver dialog = new DlgGameOver( this );
                dialog.show();
            }// if
        }// if
        else {
            gameView.renderPlayfield( playfield );
        }// else
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void GUINotification( Boolean ready ) {
        if( ready ) {
            gameView.renderPlayfield( playfield );
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public int GetScore() {
        return gameRules.getScore();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void SetHighscoreName( String name) {
        HighscoreEntry highscoreEntry = new HighscoreEntry();
        Scores scores = new Scores( this );

        for( int i = 0; i < scores.getNumScores(); i++ ) {
            if( gameRules.compareScore( scores.getAt(i).score) ) {
                highscoreEntry.name = name;
                highscoreEntry.score = gameRules.getScore();

                scores.insertAt( i, highscoreEntry );
                break;
            }// if
        }// for i
        scores.save();
        StartGame();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void StartGame() {
        playfield = new Playfield();
        gameRules = new GameRules( playfield );
        gameView.renderPlayfield( playfield );

    }


}
