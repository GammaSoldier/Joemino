// FIXME: 07.09.2018 memory usage3 (up to 200 MB)
// TODO splash screen
// TODO graphical redesign
// TODO define screen positions for all GUI elements in fractions of screen size
// TODO Release Version

package com.joekoperski.joemino;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import static android.util.TypedValue.COMPLEX_UNIT_FRACTION;


public class MainActivity extends Activity {

    private final static int SOUND_CLICK = 0;
    private final static int SOUND_VICTORY = 1;
    private final static int SOUND_GAMEOVER = 2;

    private GameView gameView;
    private Playfield playfield;
    private GameRules gameRules;

    private BitmapTextView scoreView;
    private BitmapTextView scoreTextView;

    private ImageView titleView;

    static SoundPool soundPool;
    static int[] sm;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FrameLayout gameLayout;         // Sort of "holder" for everything we are placing
        RelativeLayout layoutGameButtons;     //Holder for the buttons
        RelativeLayout layoutScoreView;
        RelativeLayout layoutTitleView;

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Display theDisplay = getWindowManager().getDefaultDisplay();
        Point displaySize = new Point();
        theDisplay.getSize(displaySize);

        playfield = new Playfield();
        gameRules = new GameRules(playfield);

        gameView = new GameView(this);
        gameView.setPlayfieldExtents(playfield.GetWidth(), playfield.GetHeight());

        gameLayout = new FrameLayout(this);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
//        options.inPreferredConfig = Bitmap.Config.RGB_565;
        // title

        BitmapFactory titleBitmapFactory = new BitmapFactory();
        titleBitmapFactory.decodeResource(getResources(), R.drawable.title, options);
        double zoomfactor = (displaySize.y * 0.1375d) / options.outHeight;

        int width = (int) (options.outWidth * zoomfactor);
        int height = (int) (displaySize.y * 0.1375d);
        layoutTitleView = new RelativeLayout(this);

        titleView = new ImageView(this);
        titleView.setImageResource(R.drawable.title);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        titleView.setLayoutParams(params);

        titleView.setX((displaySize.x - width) / 2);
        titleView.setY(0);

        layoutTitleView.addView(titleView);

        // score
        layoutScoreView = new RelativeLayout(this);

        width = (int) (displaySize.x * 5d / 20);
        height = (int)(displaySize.y / 22d);
        int x = displaySize.x / 2;
        scoreView = new BitmapTextView(this);
        scoreView.init(this, width, height, R.drawable.score_right, "fonts/SHOWG.TTF");
        scoreView.setTextColor(Color.WHITE);
        scoreView.setText("0");
        scoreView.setTextSize(COMPLEX_UNIT_FRACTION, 70);
        scoreView.setX(x);
        scoreView.setY((int) (displaySize.y * 0.1375d));

        scoreTextView = new BitmapTextView(this);
        scoreTextView.init(this, width, height, R.drawable.score_left, "fonts/SHOWG.TTF");
        scoreTextView.setTextColor(Color.WHITE);
        scoreTextView.setText(R.string.str_score_text);
        scoreTextView.setTextSize(COMPLEX_UNIT_FRACTION, 70);
        scoreTextView.setX(x - width);
        scoreTextView.setY((int) (displaySize.y * 0.1375d));

        layoutScoreView.addView(scoreView);
        layoutScoreView.addView(scoreTextView);

        // Buttons
        layoutGameButtons = new RelativeLayout(this);

        BitmapFactory buttonBitmapFactory = new BitmapFactory();
        Bitmap buttonBitmap = buttonBitmapFactory.decodeResource(getResources(), R.drawable.button_new_normal, options);
        zoomfactor = (displaySize.y * 0.1375d) / options.outHeight;
        Point buttonSize = new Point((int) (options.outWidth * zoomfactor), (int) (options.outHeight * zoomfactor));

        Point position;

        // Button New
        // FIXME: 07.09.2018 consumes 19 MB
        position = new Point(0, displaySize.y - buttonSize.y);
        SizedImageButton buttonNew = new SizedImageButton(this, R.drawable.button_new_images, buttonSize, position);
        buttonNew.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartGame();
            }// onClick
        });

        // Button Score
        // FIXME: 07.09.2018 consumes 19 MB
        position = new Point((displaySize.x - buttonSize.x) / 2, displaySize.y - buttonSize.y);
        SizedImageButton buttonScore = new SizedImageButton(this, R.drawable.button_score_images, buttonSize, position);
        buttonScore.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DlgScores dlgScores = new DlgScores(MainActivity.this);
                dlgScores.setFont("fonts/SHOWG.TTF");
                dlgScores.show();
                Scores scores = new Scores(getApplicationContext());
                for (int i = 0; i < scores.getNumScores(); i++) {
                    // if all highscores are < 10 display delete button
                    if (scores.getAt(i).score >= getResources().getInteger(R.integer.value_highscore_theshold)) {
                        dlgScores.findViewById(R.id.buttonDel).setVisibility(View.INVISIBLE);
                    }// if
                }// for i
            }// onClick
        });

        // Button Exit
        // FIXME: 07.09.2018 consumes 19 MB
        position = new Point(displaySize.x - buttonSize.x, displaySize.y - buttonSize.y);
        SizedImageButton buttonExit = new SizedImageButton(this, R.drawable.button_exit_images, buttonSize, position);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Exit the app
                moveTaskToBack(true);
            }// onClick
        });

        layoutGameButtons.addView(buttonNew);
        layoutGameButtons.addView(buttonScore);
        layoutGameButtons.addView(buttonExit);

        gameLayout.addView(gameView);
        gameLayout.addView(layoutTitleView);
        gameLayout.addView(layoutScoreView);
        gameLayout.addView(layoutGameButtons);

        setContentView(gameLayout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().getDecorView().setBackgroundColor(0x000000);

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
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
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
        Scores scores = new Scores(this);

        for (int i = 0; i < scores.getNumScores(); i++) {
            highscoreEntry.name = getResources().getString(R.string.str_highscore_entry_default_text);
            highscoreEntry.score = getResources().getInteger(R.integer.value_highscore_default);
            scores.insertAt(i, highscoreEntry);
        }// for i
        scores.save();

    }// DeleteScores


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void TileTouched(int x, int y) {
        Boolean isHighscore = false;
        Log.d("MainActivity", "TileTouched");

        int moveResult;

        moveResult = gameRules.makeMove(playfield, x, y);
        if (moveResult == GameRules.CONTINUE_MOVE) {
            PlaySound(SOUND_CLICK);
        }

        while (moveResult == GameRules.CONTINUE_MOVE) {
            // aninmate
            gameView.renderPlayfield(playfield);
            moveResult = gameRules.makeMove(playfield, x, y);
        }// while

        gameView.renderPlayfield(playfield);
        scoreView.setText(String.format("%1$d", gameRules.getScore()));

        switch (moveResult) {
            case GameRules.CONTINUE:
                // next move possible
                break;
            case GameRules.GAMEOVER:
                Scores scores = new Scores(this);

                for (int i = 0; i < scores.getNumScores(); i++) {
                    if (gameRules.compareScore(scores.getAt(i).score)) {
                        PlaySound(SOUND_VICTORY);
                        DlgHighscore dlgHighscore = new DlgHighscore(MainActivity.this);
                        dlgHighscore.setFont("fonts/SHOWG.TTF");
                        dlgHighscore.show();
                        isHighscore = true;
                        break;
                    }// if
                }// for i
                if (!isHighscore) {
                    PlaySound(SOUND_GAMEOVER);
                    DlgGameOver dialog = new DlgGameOver(this);
                    dialog.setFont("fonts/SHOWG.TTF");
                    dialog.show();
                }// if
                break;
            default:    // FORBIDDEN
                // do nothing
        }// switch
//        memInfo();
    }// TileTouched


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void GUINotification(Boolean ready) {
        if (ready) {
            gameView.renderPlayfieldFirstTime(playfield);
            scoreView.setText(String.format("%1$d", gameRules.getScore()));

        }// if
    }// GUINotification


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public int GetScore() {
        return gameRules.getScore();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void SetHighscoreName(String name) {
        HighscoreEntry highscoreEntry = new HighscoreEntry();
        Scores scores = new Scores(this);

        for (int i = 0; i < scores.getNumScores(); i++) {
            if (gameRules.compareScore(scores.getAt(i).score)) {
                highscoreEntry.name = name;
                highscoreEntry.score = gameRules.getScore();

                scores.insertAt(i, highscoreEntry);
                break;
            }// if
        }// for i
        scores.save();
        StartGame();
    }// SetHighscoreName


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void StartGame() {
        playfield = new Playfield();
        gameRules = new GameRules(playfield);
        gameView.renderPlayfieldFirstTime(playfield);
        scoreView.setText(String.format("%1$d", gameRules.getScore()));

    }// StartGame


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void PlaySound(int id) {
        soundPool.play(sm[id], 1, 1, 1, 0, 1f);
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void memInfo() {
        int pids[];
        int id = android.os.Process.myPid();

        pids = new int[1];
        pids[0] = id;

        Debug.MemoryInfo memInfo[];

        ActivityManager AM = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();

        AM.getMemoryInfo(memoryInfo);
        memInfo = AM.getProcessMemoryInfo (pids);

        int memory;
        memory = (int) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        int memoryClass;
        memoryClass = AM.getMemoryClass();
    }// memInfo

}// MainActivity
