// TODO display scores
// TODO use custom font for dialogs (https://stackoverflow.com/questions/27588965/how-to-use-custom-font-in-a-project-written-in-android-studio)
// TODO explode tiles
// TODO Release Version

package com.joekoperski.joemino;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static android.util.TypedValue.COMPLEX_UNIT_FRACTION;
import static android.util.TypedValue.COMPLEX_UNIT_FRACTION_PARENT;
import static android.util.TypedValue.COMPLEX_UNIT_MM;
import static android.util.TypedValue.COMPLEX_UNIT_SP;


public class MainActivity extends Activity {

    private static final int SOUND_CLICK = 0;
    private static final int SOUND_VICTORY = 1;
    private static final int SOUND_GAMEOVER = 2;

    private GameView gameView;
    private Playfield playfield;
    private GameRules gameRules;

    private BitmapTextView scoreView;
    private BitmapTextView scoreTextView;

    static SoundPool soundPool;
    static int[] sm;

	////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FrameLayout gameLayout;         // Sort of "holder" for everything we are placing
        RelativeLayout layoutGameButtons;     //Holder for the buttons
        RelativeLayout layoutScoreView;

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Display theDisplay = getWindowManager().getDefaultDisplay();
        Point displaySize = new Point();
        theDisplay.getSize(displaySize);

        playfield = new Playfield();
        gameRules = new GameRules( playfield );

        gameView = new GameView( this );
        gameView.setPlayfieldExtents( playfield.GetWidth(), playfield.GetHeight() );

        gameLayout = new FrameLayout(this);

        // score
        layoutScoreView = new RelativeLayout(this);

        scoreView = new BitmapTextView(this);
        scoreView.init(displaySize.x / 5, displaySize.y / 15, R.drawable.score_display_right);
        scoreView.setTextColor( Color.WHITE );
        scoreView.setText( "Score" );
        scoreView.setTextSize( COMPLEX_UNIT_FRACTION, 76 );
        scoreView.setX( displaySize.x / 2 );
        scoreView.setY( displaySize.y / 8 );

        scoreTextView = new BitmapTextView(this);
        scoreTextView.init(displaySize.x / 5, displaySize.y / 15, R.drawable.score_display_left);
        scoreTextView.setTextColor( Color.WHITE );
        scoreTextView.setText( "Tiles:" );
        scoreTextView.setTextSize( COMPLEX_UNIT_FRACTION, 76 );
        scoreTextView.setX(  displaySize.x / 2 - displaySize.x / 5 );
        scoreTextView.setY( displaySize.y / 8 );

        layoutScoreView.addView(scoreView);
        layoutScoreView.addView(scoreTextView);



        // Buttons
        layoutGameButtons = new RelativeLayout(this);

        // TODO replace constants by variables
        float ratioX = 336f / displaySize.x;
        float ratioY = 264f / displaySize.y;
        Point buttonSize = new Point( (int) ((float) (displaySize.x) * ratioX), (int) ((float) (displaySize.y) * ratioY));
        Point position;

        // Button New
        position = new Point( 0,displaySize.y - buttonSize.y );
        SizedImageButton buttonNew = new SizedImageButton( this, R.drawable.button_new_images, buttonSize, position );
        buttonNew.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartGame();
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

        layoutGameButtons.addView(  buttonNew );
        layoutGameButtons.addView(  buttonScore );
        layoutGameButtons.addView(  buttonExit );

        gameLayout.addView(gameView);
        gameLayout.addView(layoutGameButtons);
        gameLayout.addView(layoutScoreView);

        setContentView(gameLayout);

        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().getDecorView().setBackgroundColor( 0x000000 );



        // Load sounds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .build();
        }// if
        else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }// else
        // set volume control to "media" when changing volume
        setVolumeControlStream( AudioManager.STREAM_MUSIC );
        // initialize sounds
        sm = new int[3];
        sm[SOUND_CLICK] = soundPool.load(this, R.raw.click, 1);
        sm[SOUND_VICTORY] = soundPool.load(this, R.raw.victory, 1);
        sm[SOUND_GAMEOVER] = soundPool.load(this, R.raw.gameover, 1);


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
        int moveResult;

        moveResult = gameRules.makeMove( playfield, x, y );
        if( moveResult == GameRules.CONTINUE_MOVE) {
            PlaySound( SOUND_CLICK );
        }

        while(  moveResult == GameRules.CONTINUE_MOVE ) {
            // aninmate
            gameView.renderPlayfield( playfield );
            moveResult = gameRules.makeMove( playfield, x, y );
        }// while

        gameView.renderPlayfield( playfield );
        scoreView.setText( String.format( "%1$d", gameRules.getScore()) );

        switch (moveResult) {
            case GameRules.CONTINUE:
                // next move possible
                break;
            case GameRules.GAMEOVER:
                Scores scores = new Scores( this );

                for( int i = 0; i < scores.getNumScores(); i++ ){
                    if( gameRules.compareScore( scores.getAt(i).score) ) {
                        PlaySound( SOUND_VICTORY );
                        DlgHighscore dlgHighscore = new DlgHighscore( MainActivity.this );
                        dlgHighscore.show();
                        isHighscore = true;
                        break;
                    }// if
                }// for i
                if( !isHighscore ){
                    PlaySound( SOUND_GAMEOVER );
                    DlgGameOver dialog = new DlgGameOver( this );
                    dialog.show();
                }// if
                break;
            default:    // FORBIDDEN
                // do nothing
        }// switch
    }// TileTouched



    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void GUINotification( Boolean ready ) {
        if( ready ) {
            gameView.renderPlayfieldFirstTime( playfield );
            scoreView.setText( String.format( "%1$d", gameRules.getScore()) );

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
        gameView.renderPlayfieldFirstTime( playfield );
        scoreView.setText( String.format( "%1$d", gameRules.getScore()) );

    }// StartGame



    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void PlaySound( int id ) {
        soundPool.play(sm[id], 1, 1, 1, 0, 1f);
    }
}// MainActivity
