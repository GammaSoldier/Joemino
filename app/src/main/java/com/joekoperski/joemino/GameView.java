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


public class GameView extends SurfaceView implements Callback {

	private SurfaceHolder surfaceHolder;
	private Bitmap bmpTiles[];
    private Bitmap bmpBackground;
    private Bitmap bmpPlayfieldBackground;
    private Bitmap bmpPlayfieldScreen[];
    private int activePlayfieldScreen;

    private Point posPlayfieldScreen;

	private int touchX, touchY;
	private int mWidth, mHeight;

	private GfxLoopThread gfxLoopThread;

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
	}// GameView


	////////////////////////////////////////////////////////////////////////////////////////////////
	public void setExtents( int x, int y ) {
		mWidth = x;
		mHeight = y;
	}// setExtents


	////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		int x = getWidth();
		int y = getHeight();

		// Prapare graphics
		Bitmap bmpTemp;
		bmpTemp = BitmapFactory.decodeResource(getResources(), R.drawable.background);
		bmpBackground = Bitmap.createScaledBitmap( bmpTemp, getWidth(), getHeight(), true );
        bmpPlayfieldBackground = BitmapFactory.decodeResource(getResources(), R.drawable.playfield);

		bmpTiles = new Bitmap[5];
        bmpTiles[0] = BitmapFactory.decodeResource(getResources(), R.drawable.tile1);
        bmpTiles[1] = BitmapFactory.decodeResource(getResources(), R.drawable.tile2);
        bmpTiles[2] = BitmapFactory.decodeResource(getResources(), R.drawable.tile3);
        bmpTiles[3] = BitmapFactory.decodeResource(getResources(), R.drawable.tile4);
        bmpTiles[4] = BitmapFactory.decodeResource(getResources(), R.drawable.tile5);


        posPlayfieldScreen = new Point( 0, (y - x) / 2 );

		bmpPlayfieldScreen[ activePlayfieldScreen ] = Bitmap.createBitmap( getWidth(), getWidth(), bmpPlayfieldBackground.getConfig() );
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
		if( canvas != null ) {
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

			int width = getWidth();
			int height = getHeight();

			int x = (touchX -  posPlayfieldScreen.x)  / ( (width - 2 * posPlayfieldScreen.x) / mWidth ) ;
			int y = (touchY -  posPlayfieldScreen.y)  / ( (height - 2 * posPlayfieldScreen.y) / mHeight ) ;
			Log.d( "GameView", "touchX: " + touchX );
			Log.d( "GameView", "touchY: " + touchY );
			Log.d( "GameView", "x: " + x );
			Log.d( "GameView", "y: " + y );

			mContext.TileTouched( x, y );
		}// if
		return true;
	}// onTouchEvent


    ////////////////////////////////////////////////////////////////////////////////////////////////
	public void renderPlayfield(Playfield playfield ) {
        int width = getWidth();
		int size = (width - 2 * posPlayfieldScreen.x) / mWidth;

		bmpPlayfieldScreen[1 - activePlayfieldScreen] = Bitmap.createBitmap( width, width, bmpPlayfieldBackground.getConfig() );
		Canvas canvas = new Canvas( bmpPlayfieldScreen[1 - activePlayfieldScreen] );
		canvas.drawBitmap(bmpPlayfieldBackground, null, new Rect(0,0, width, width ), null);

		for( int j = 0; j < playfield.GetHeight(); j++ ) {
			for (int i = 0; i < playfield.GetWidth(); i++) {
				if( playfield.Get( i, j ) >= 0 ) {
					canvas.drawBitmap(bmpTiles[ playfield.Get( i, j ) ], null, new Rect(i * size, j * size, (i + 1) * size - 1, (j + 1) * size - 1), null);
				}// if
			}// for i
		}// for j
		activePlayfieldScreen = 1 - activePlayfieldScreen;
    }// renderPlayfield
}// BitmapView
