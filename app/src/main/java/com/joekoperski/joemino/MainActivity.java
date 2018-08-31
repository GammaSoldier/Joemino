// TODO Sound
// TODO Release Version
// TODO adapt button extents to each screen resolution
// TODO Playfield animation



package com.joekoperski.joemino;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;


public class MainActivity extends Activity {

    private GameView gameView;
    private Playfield playfield;
    private GameRules gameRules;

	////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FrameLayout gameLayout;// Sort of "holder" for everything we are placing
        RelativeLayout gameButtons;//Holder for the buttons
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        playfield = new Playfield();
        gameRules = new GameRules( playfield );

        gameView = new GameView( this );
        gameView.setPlayfieldExtents( playfield.GetWidth(), playfield.GetHeight() );

        gameLayout = new FrameLayout(this);
        gameButtons = new RelativeLayout(this);

        Display theDisplay = getWindowManager().getDefaultDisplay();
        Point displaySize = new Point();
        theDisplay.getSize(displaySize);
        float ratioX = 336f / 1080f;
        float ratioY = 189f / 1920f;
        Point buttonSize = new Point( (int) ((float) (displaySize.x) * ratioX), (int) ((float) (displaySize.y) * ratioY));
        Point position;

        // Button New
        position = new Point( 0,displaySize.y - buttonSize.y );
        SizedImageButton buttonNew = new SizedImageButton( this, R.drawable.button_new_images, buttonSize, position );
        buttonNew.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gameRules = new GameRules( playfield );
                gameView.renderPlayfield( playfield );
            }
        } );

        // Button Score
        position = new Point( (displaySize.x - buttonSize.x) / 2,displaySize.y - buttonSize.y );
        SizedImageButton buttonScore = new SizedImageButton( this, R.drawable.button_score_images, buttonSize, position );
        buttonScore.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DlgScores dlgScores = new DlgScores( MainActivity.this );
                dlgScores.show();
            }
        } );

        // Button Exit
        position = new Point( displaySize.x - buttonSize.x,displaySize.y - buttonSize.y );
        SizedImageButton buttonExit = new SizedImageButton( this, R.drawable.button_exit_images, buttonSize, position );
        buttonExit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Exit the app
                moveTaskToBack(true);
            }
        } );


        gameButtons.addView(  buttonNew );
        gameButtons.addView(  buttonScore );
        gameButtons.addView(  buttonExit );


        gameLayout.addView(gameView);
        gameLayout.addView(gameButtons);
        setContentView(gameLayout);

        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().getDecorView().setBackgroundColor( 0x000000 );
    }// onCreate


    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }// onCreateOptionsMenu

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

    }// DeleteScores


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void TileTouched(int x, int y ) {
        Boolean isHighscore = false;
        Log.d("MainActivity", "TileTouched");

        if( gameRules.makeMove( playfield, x, y ) == gameRules.GAMEOVER ) {
            gameView.renderPlayfield( playfield );

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
    }// TileTouched

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void GUINotification( Boolean ready ) {
        if( ready ) {
            gameView.renderPlayfield( playfield );
        }// if
    }// GUINotification

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
    }// SetHighscoreName


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void StartGame() {
        playfield = new Playfield();
        gameRules = new GameRules( playfield );
        gameView.renderPlayfield( playfield );
    }// StartGame

}// MainActivity
