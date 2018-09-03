
package com.joekoperski.joemino;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.util.concurrent.TimeUnit;


public class GameView extends SurfaceView implements Callback {

	private final static float SMOOTH_ANIMATION_STEP_SIZE = 0.5f;

	private SurfaceHolder surfaceHolder;
	private Bitmap bmpTiles[];
    private Bitmap bmpBackground;
    private Bitmap bmpPlayfieldBackground;
    private Bitmap bmpPlayfieldScreen[];
    private int activePlayfieldScreen;

    private Point posPlayfieldScreen;

	private int touchX, touchY;
	private int mPlayfieldExtentX, mPlayfieldExtentY;

	private GfxLoopThread gfxLoopThread;
    private Boolean waitForDraw;
	private MainActivity mContext;

	////////////////////////////////////////////////////////////////////////////////////////////////
	public GameView(Context context ) {
		super(context);
		mContext = (MainActivity)context;
		touchX = 0;
		touchY = 0;

		activePlayfieldScreen = 0;
		bmpPlayfieldScreen = new Bitmap[2];

		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);

        waitForDraw = false;

	}// GameView


	////////////////////////////////////////////////////////////////////////////////////////////////
	public void setPlayfieldExtents(int x, int y ) {
		mPlayfieldExtentX = x;
		mPlayfieldExtentY = y;
	}// setPlayfieldExtents


	////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// position playfield at center of screen
		posPlayfieldScreen = new Point( (getWidth() - getPlayfieldScreenWidth()) / 2, (getHeight() - getPlayfieldScreenHeight()) / 2 );

		// Prapare graphics
		Bitmap bmpTemp;
		bmpTemp = BitmapFactory.decodeResource(getResources(), R.drawable.background);
		bmpBackground = Bitmap.createScaledBitmap( bmpTemp, getWidth(), getHeight(), true );
		bmpTemp = BitmapFactory.decodeResource(getResources(), R.drawable.playfield);
		bmpPlayfieldBackground = Bitmap.createScaledBitmap( bmpTemp, getPlayfieldScreenWidth(), getPlayfieldScreenHeight(), true );

		bmpTiles = new Bitmap[5];
		bmpTiles[0] = BitmapFactory.decodeResource(getResources(), R.drawable.glass1);
		bmpTiles[1] = BitmapFactory.decodeResource(getResources(), R.drawable.glass2);
		bmpTiles[2] = BitmapFactory.decodeResource(getResources(), R.drawable.glass3);
		bmpTiles[3] = BitmapFactory.decodeResource(getResources(), R.drawable.glass4);
		bmpTiles[4] = BitmapFactory.decodeResource(getResources(), R.drawable.glass5);

/*
		bmpTiles[0] = BitmapFactory.decodeResource(getResources(), R.drawable.tile1);
		bmpTiles[1] = BitmapFactory.decodeResource(getResources(), R.drawable.tile2);
		bmpTiles[2] = BitmapFactory.decodeResource(getResources(), R.drawable.tile3);
		bmpTiles[3] = BitmapFactory.decodeResource(getResources(), R.drawable.tile4);
		bmpTiles[4] = BitmapFactory.decodeResource(getResources(), R.drawable.tile5);
*/

		bmpPlayfieldScreen[ activePlayfieldScreen ] = Bitmap.createBitmap( getPlayfieldScreenWidth(), getPlayfieldScreenHeight(), bmpPlayfieldBackground.getConfig() );

		gfxLoopThread = new GfxLoopThread(this);
		gfxLoopThread.setRunning(true);
		if( !gfxLoopThread.isAlive() ) {
            gfxLoopThread.start();
		}// if
		
		Log.d( "GameView", "GameView started, Id: "+ this.getId());
		Log.d( "GameView", "Thread Started, Id: " + gfxLoopThread.getId() );

		mContext.GUINotification( true );
	}// surfaceCreated

	////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {
	}// surfaceChanged


	////////////////////////////////////////////////////////////////////////////////////////////////
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		
		gfxLoopThread.setRunning(false);
		while(retry) {
			Log.d( "GameView", "Thread End");

			try {
				gfxLoopThread.join(1000 );
				retry=false;
			}// try
			catch(InterruptedException e) {
			}
		}// while
	}// surfaceDestroyed


    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
	public void draw( Canvas canvas ) {
        waitForDraw = false;
		if( canvas != null ) {
            super.draw(canvas);
            canvas.drawBitmap( bmpBackground, 0, 0, null );
			canvas.drawBitmap( bmpPlayfieldScreen[activePlayfieldScreen], posPlayfieldScreen.x, posPlayfieldScreen.y, null );
		}// if
	}// draw


	////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if( event.getAction() == MotionEvent.ACTION_UP ) {
			touchX = (int) event.getX();
			touchY = (int) event.getY();

			int x = (touchX -  posPlayfieldScreen.x) / ( getPlayfieldScreenWidth() / mPlayfieldExtentX) ;
			int y = (touchY -  posPlayfieldScreen.y) / ( getPlayfieldScreenHeight() / mPlayfieldExtentY) ;

			mContext.TileTouched( x, y );
		}// if
		return true;
	}// onTouchEvent


	////////////////////////////////////////////////////////////////////////////////////////////////
	public void renderPlayfieldFirstTime(Playfield playfield ) {
		int sizeX = getPlayfieldScreenWidth() / mPlayfieldExtentX;
		int sizeY = getPlayfieldScreenHeight() / mPlayfieldExtentY;

		bmpPlayfieldScreen[1 - activePlayfieldScreen] = Bitmap.createBitmap( getPlayfieldScreenWidth(), getPlayfieldScreenHeight(), bmpPlayfieldBackground.getConfig() );
		Canvas canvas = new Canvas( bmpPlayfieldScreen[1 - activePlayfieldScreen] );
		canvas.drawBitmap(bmpPlayfieldBackground, null, new Rect(0,0, getPlayfieldScreenWidth(), getPlayfieldScreenHeight() ), null);

		for( int j = 0; j < mPlayfieldExtentY; j++ ) {
			for (int i = 0; i < mPlayfieldExtentX; i++) {
				if( playfield.Get( i, j ) >= 0 ) {
					canvas.drawBitmap(bmpTiles[ playfield.Get( i, j ) ], null, new Rect(i * sizeX, j * sizeY, (i + 1) * sizeX, (j + 1) * sizeY), null);
				}// if
			}// for i
		}// for j
		activePlayfieldScreen = 1 - activePlayfieldScreen;
	}// renderPlayfieldFirstTime



	////////////////////////////////////////////////////////////////////////////////////////////////
	public void renderPlayfield(Playfield playfield ) {
		int sizeX = getPlayfieldScreenWidth() / mPlayfieldExtentX;
		int sizeY = getPlayfieldScreenHeight() / mPlayfieldExtentY;

		//render tiles
		Boolean animationRunning = false;

		float smoothAnimationStep = SMOOTH_ANIMATION_STEP_SIZE;
		do{
            waitForDraw = true;
		    // render background
            bmpPlayfieldScreen[1 - activePlayfieldScreen] = Bitmap.createBitmap( getPlayfieldScreenWidth(), getPlayfieldScreenHeight(), bmpPlayfieldBackground.getConfig() );
            Canvas canvas = new Canvas( bmpPlayfieldScreen[1 - activePlayfieldScreen] );
            canvas.drawBitmap(bmpPlayfieldScreen[activePlayfieldScreen], null, new Rect(0,0, getPlayfieldScreenWidth(), getPlayfieldScreenHeight() ), null);

			animationRunning = false;

			int tileOffsetX = (int)(sizeX * smoothAnimationStep);
			int tileOffsetY = (int)(sizeY * smoothAnimationStep);

			for (int j = mPlayfieldExtentY - 1; j >= 0; j--) {
				for (int i = mPlayfieldExtentX - 1; i >= 0; i--) {
					if (playfield.Get(i, j) >= 0) {
						Point moveTo = playfield.GetMovemap(i, j);

						if (moveTo.x > i) {
						    // move tile horizontally
                            // erase source area only if no left neighbour
                            if( playfield.Get(i - 1, j) == -1 ) {
                                Bitmap background = Bitmap.createBitmap(bmpPlayfieldBackground, i * sizeX, j * sizeY, sizeX, sizeY);
                                canvas.drawBitmap(background, null, new Rect(i * sizeX, j * sizeY, (i + 1) * sizeX, (j + 1) * sizeY), null);
                            }// if

							canvas.drawBitmap(bmpTiles[playfield.Get(i, j)], null, new Rect( i * sizeX + tileOffsetX, j * sizeY, (i + 1) * sizeX + tileOffsetX, (j + 1) * sizeY), null);

							if( smoothAnimationStep >= 1.0f) {
								playfield.SetMovemap(i + 1, j, moveTo);
								playfield.SetMovemap(i, j, new Point(i, j));
								playfield.Set(i + 1, j, playfield.Get(i, j));
								playfield.Set(i, j, -1);
							}// if
							animationRunning = true;
						}// if
						else if (moveTo.y > j) {
							// move tile vertically
                            // erase source area only if no upper neighbour
                            if(playfield.Get(i, j - 1) == -1 ) {
                                Bitmap background = Bitmap.createBitmap( bmpPlayfieldBackground, i * sizeX, j * sizeY, sizeX, sizeY );
                                canvas.drawBitmap(background, null, new Rect(i * sizeX, j * sizeY, (i + 1) * sizeX, (j + 1) * sizeY), null);
                            }// if

							canvas.drawBitmap(bmpTiles[playfield.Get(i, j)], null, new Rect(i * sizeX, j * sizeY + tileOffsetY, (i + 1) * sizeX, (j + 1) * sizeY + tileOffsetY), null);

							if( smoothAnimationStep >= 1.0f) {
								playfield.SetMovemap(i, j + 1, moveTo);
								playfield.SetMovemap(i, j, new Point(i, j));
								playfield.Set(i, j + 1, playfield.Get(i, j));
								playfield.Set(i, j, -1);
							}// if
							animationRunning = true;
						}// if
					}// if
                    else {
                        Point moveTo = playfield.GetMovemap(i, j);
                        // erase tile
                        if( moveTo.x == -1 && moveTo.y == -1) {
                            Bitmap background = Bitmap.createBitmap( bmpPlayfieldBackground, i * sizeX, j * sizeY, sizeX, sizeY );
                            canvas.drawBitmap(background, null, new Rect(i * sizeX, j * sizeY, (i + 1) * sizeX, (j + 1) * sizeY), null);
                        }// if
                    }// else
				}// for i
			}// for j
            activePlayfieldScreen = 1 - activePlayfieldScreen;

			if( animationRunning ){
				smoothAnimationStep += SMOOTH_ANIMATION_STEP_SIZE;
				if( smoothAnimationStep > 1f ){
					smoothAnimationStep = SMOOTH_ANIMATION_STEP_SIZE;
				}// if
			}// if

            while( waitForDraw ){
                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } // try
                catch (InterruptedException exeption) {
                    // TODO anything to do here?
                }// catch
            };
		} while( animationRunning );

	}// renderPlayfield


	////////////////////////////////////////////////////////////////////////////////////////////////
    private int getPlayfieldScreenWidth() {
		int height =(int)(getHeight() * 4 / 7);
        if( getWidth() < height ) {
            return getWidth();
        }// if
        else {
            return height;
        }// else
    }// getPlayfieldScreenWidth


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private int getPlayfieldScreenHeight() {
        // make size a square
		return getPlayfieldScreenWidth();
    }// getPlayfieldScreenHeight
}// BitmapView
