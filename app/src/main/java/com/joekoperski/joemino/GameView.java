
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

	private final static int SMOOTH_ANIMATION_STEPS = 2;

	private final static int SMOOTH_DELETION_STEPS = 7;

	private SurfaceHolder surfaceHolder;
	private Bitmap bmpTiles[];
    private Bitmap bmpBackground;
    private Bitmap bmpPlayfieldBackground;
    private Bitmap bmpPlayfieldScreen[];
    private int activePlayfieldScreen;

    private Point posPlayfieldScreen;

	private int touchX, touchY;
	private int mPlayfieldExtentX, mPlayfieldExtentY;
	private Point mPlayfieldScreen;

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
		mPlayfieldScreen = new Point(0,0);
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
		mPlayfieldScreen.x = getPlayfieldScreenWidth();
		mPlayfieldScreen.y = getPlayfieldScreenHeight();
		posPlayfieldScreen = new Point( (getWidth() - mPlayfieldScreen.x) / 2, (getHeight() - mPlayfieldScreen.y) / 2 );

		// Prapare graphics
		Bitmap bmpTemp;
		bmpTemp = BitmapFactory.decodeResource(getResources(), R.drawable.background);
		bmpBackground = Bitmap.createScaledBitmap( bmpTemp, getWidth(), getHeight(), true );
		bmpTemp = BitmapFactory.decodeResource(getResources(), R.drawable.playfield);
		bmpPlayfieldBackground = Bitmap.createScaledBitmap( bmpTemp, mPlayfieldScreen.x, mPlayfieldScreen.y, true );

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

		bmpPlayfieldScreen[ activePlayfieldScreen ] = Bitmap.createBitmap( mPlayfieldScreen.x, mPlayfieldScreen.y, bmpPlayfieldBackground.getConfig() );

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
                Log.d( "GameView", "surfaceDestroyed InterruptedException");
			}// catch
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

			int x = (touchX -  posPlayfieldScreen.x) / ( mPlayfieldScreen.x / mPlayfieldExtentX) ;
			int y = (touchY -  posPlayfieldScreen.y) / ( mPlayfieldScreen.y / mPlayfieldExtentY) ;

			mContext.TileTouched( x, y );
		}// if
		return true;
	}// onTouchEvent


	////////////////////////////////////////////////////////////////////////////////////////////////
	public void renderPlayfieldFirstTime(Playfield playfield ) {
		int sizeX = mPlayfieldScreen.x / mPlayfieldExtentX;
		int sizeY = mPlayfieldScreen.y / mPlayfieldExtentY;

		bmpPlayfieldScreen[1 - activePlayfieldScreen] = Bitmap.createBitmap( mPlayfieldScreen.x, mPlayfieldScreen.y, bmpPlayfieldBackground.getConfig() );
		Canvas canvas = new Canvas( bmpPlayfieldScreen[1 - activePlayfieldScreen] );
		canvas.drawBitmap(bmpPlayfieldBackground, null, new Rect(0,0, mPlayfieldScreen.x, mPlayfieldScreen.y ), null);

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
		int sizeX = mPlayfieldScreen.x / mPlayfieldExtentX;
		int sizeY = mPlayfieldScreen.y / mPlayfieldExtentY;

		Boolean animationRunning;
		Boolean deletionRunning;

		int smoothAnimationStep = 1;
		int smoothDeletionStep = 1;

		//render tiles
		do{
            waitForDraw = true;
		    // render background
            bmpPlayfieldScreen[1 - activePlayfieldScreen] = Bitmap.createBitmap( mPlayfieldScreen.x, mPlayfieldScreen.y, bmpPlayfieldBackground.getConfig() );
            Canvas canvas = new Canvas( bmpPlayfieldScreen[1 - activePlayfieldScreen] );
            canvas.drawBitmap(bmpPlayfieldScreen[activePlayfieldScreen], null, new Rect(0,0, mPlayfieldScreen.x, mPlayfieldScreen.y ), null);

			animationRunning = false;
			deletionRunning = false;

            for (int j = mPlayfieldExtentY - 1; j >= 0; j--) {
                for (int i = mPlayfieldExtentX - 1; i >= 0; i--) {
                    Point moveTo = playfield.GetMovemap(i, j);
                    if (moveTo.x == GameRules.MOVE_DELETE && moveTo.y == GameRules.MOVE_DELETE) {
                        deletionRunning = animateTileErase(canvas, playfield, i, j, sizeX, sizeY, smoothDeletionStep);
                    } else if (moveTo.x > i) {
                        // move tile horizontally
                        animationRunning = animateTileMoveHorizontal(canvas, playfield, i, j, sizeX, sizeY, smoothAnimationStep);
                    }// if
                    else if (moveTo.y > j) {
                        // move tile vertically
                        animationRunning = animateTileMoveVertical(canvas, playfield, i, j, sizeX, sizeY, smoothAnimationStep);
                    }// if
                }// for i
            }// for j
            activePlayfieldScreen = 1 - activePlayfieldScreen;

			if( animationRunning ){
				smoothAnimationStep ++;
				if( smoothAnimationStep > SMOOTH_ANIMATION_STEPS){
					smoothAnimationStep = 1;
				}// if
			}// if

			if( deletionRunning ){
				smoothDeletionStep ++;
				if( smoothDeletionStep > SMOOTH_DELETION_STEPS){
					smoothDeletionStep = 1;
				}// if
			}// if

			while( waitForDraw ){
				try {
					TimeUnit.MILLISECONDS.sleep(1);
				} // try
				catch (InterruptedException exeption) {
					Log.d( "GameView", "renderPlayfield InterruptedException");
				}// catch
			}// while

		} while( animationRunning || deletionRunning );

	}// renderPlayfield


	////////////////////////////////////////////////////////////////////////////////////////////////
    private Boolean animateTileErase(Canvas canvas, Playfield playfield, int posX, int posY, int tileSizeX, int tileSizeY, int deletionStep) {
        Point moveTo = playfield.GetMovemap(posX, posY);
        double shrinkStep = ((double)deletionStep / (2 * SMOOTH_DELETION_STEPS));
        // erase tile
        Bitmap background = Bitmap.createBitmap(bmpPlayfieldBackground, posX * tileSizeX, posY * tileSizeY, tileSizeX, tileSizeY);
        canvas.drawBitmap(background, null, new Rect(posX * tileSizeX, posY * tileSizeY, (posX + 1) * tileSizeX, (posY + 1) * tileSizeY), null);

        Rect rect = new Rect(posX * tileSizeX + (int) (shrinkStep * tileSizeX),
                (posY * tileSizeY) + (int) (shrinkStep * tileSizeY),
                (posX + 1) * tileSizeX - (int) (shrinkStep * tileSizeX),
                (posY + 1) * tileSizeY - (int) (shrinkStep * tileSizeY));

        canvas.drawBitmap(bmpTiles[playfield.Get(posX,posY)], null, rect, null);
        if (deletionStep >= SMOOTH_DELETION_STEPS) {
            playfield.SetMovemap(posX, posY, new Point(posX, posY));
            playfield.Set(posX, posY, -1);
            return false;
        }// if

        return true;
    }


	////////////////////////////////////////////////////////////////////////////////////////////////
	private Boolean animateTileMoveVertical( Canvas canvas, Playfield playfield, int posX, int posY, int tileSizeX, int tileSizeY, int animationStep ) {
		int tileOffsetY = (int)(tileSizeY * (double)animationStep / SMOOTH_ANIMATION_STEPS);

		// erase source area only if no upper neighbour
		if(playfield.Get(posX, posY - 1) == -1 ) {
			Bitmap background = Bitmap.createBitmap( bmpPlayfieldBackground, posX * tileSizeX, posY * tileSizeY, tileSizeX, tileSizeY );
			canvas.drawBitmap(background, null, new Rect(posX * tileSizeX, posY * tileSizeY, (posX + 1) * tileSizeX, (posY + 1) * tileSizeY), null);
		}// if

		if( playfield.Get(posX, posY)  >= 0 ) {
			// only draw if there is a tile
			canvas.drawBitmap(bmpTiles[playfield.Get(posX, posY)], null, new Rect(posX * tileSizeX, posY * tileSizeY + tileOffsetY, (posX + 1) * tileSizeX, (posY + 1) * tileSizeY + tileOffsetY), null);
		}
		if( animationStep >= SMOOTH_ANIMATION_STEPS) {
			playfield.SetMovemap(posX, posY + 1, playfield.GetMovemap(posX, posY));
			playfield.SetMovemap(posX, posY, new Point(posX, posY));
			playfield.Set(posX, posY + 1, playfield.Get(posX, posY));
			playfield.Set(posX, posY, -1);
		}// if
		return true;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	private Boolean animateTileMoveHorizontal( Canvas canvas, Playfield playfield, int posX, int posY, int tileSizeX, int tileSizeY, int animationStep ) {
		int tileOffsetX = (int)(tileSizeX * (double)animationStep / SMOOTH_ANIMATION_STEPS);
        int tmp;
        // erase source area only if no left neighbour
		if( playfield.Get(posX - 1, posY) == -1 ) {
			Bitmap background = Bitmap.createBitmap(bmpPlayfieldBackground, posX * tileSizeX, posY * tileSizeY, tileSizeX, tileSizeY);
			canvas.drawBitmap(background, null, new Rect(posX * tileSizeX, posY * tileSizeY, (posX + 1) * tileSizeX, (posY + 1) * tileSizeY), null);
		}// if

        if( playfield.Get(posX, posY)  >= 0 ) {
			// only draw if there is a tile
			canvas.drawBitmap(bmpTiles[playfield.Get(posX, posY)], null, new Rect(posX * tileSizeX + tileOffsetX, posY * tileSizeY, (posX + 1) * tileSizeX + tileOffsetX, (posY + 1) * tileSizeY), null);
        }
		if( animationStep >= SMOOTH_ANIMATION_STEPS) {
			playfield.SetMovemap(posX + 1, posY, playfield.GetMovemap(posX, posY));
			playfield.SetMovemap(posX, posY, new Point(posX, posY));
			playfield.Set(posX + 1, posY, playfield.Get(posX, posY));
			playfield.Set(posX, posY, -1);
		}// if
		return true;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
    private int getPlayfieldScreenWidth() {
		int retVal;
		int height =(int)(getHeight() * 4.0 / 7);
        if( getWidth() < height ) {
            retVal =  getWidth();
        }// if
        else {
            retVal =  height;
        }// else
		// make size a multiple of tile widths
		retVal = (retVal / mPlayfieldExtentX) * mPlayfieldExtentX;
		return retVal;
    }// getPlayfieldScreenWidth


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private int getPlayfieldScreenHeight() {
        // make size a square
		return getPlayfieldScreenWidth();
    }// getPlayfieldScreenHeight
}// BitmapView
