package com.joekoperski.joemino;

import android.graphics.Canvas;
import android.util.Log;

public class GfxLoopThread extends Thread {

	static final long FPS = 60;
	private GameView theView;
	private boolean isRunning = false;
	
	public GfxLoopThread(GameView theView) {
		this.theView = theView;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void run() {
		long TPS = 1000 / FPS;
		long startTime, sleepTime;
		while ( isRunning ) {
			Canvas theCanvas = null;
			startTime = System.currentTimeMillis();
			
			try {
				theCanvas = theView.getHolder().lockCanvas();
				synchronized( theView.getHolder() ) {
					theView.draw( theCanvas );
				}// synchronized
			}// try 
			finally {
				if (theCanvas != null) {
					theView.getHolder().unlockCanvasAndPost(theCanvas);
				}// if
			}// finally
			
			sleepTime = TPS - (System.currentTimeMillis() - startTime);
			try {
				if (sleepTime > 0)
					sleep(sleepTime);
				else
					sleep(10);
			}// try 
			catch (InterruptedException e) {}
		}// while
		
		Log.d("GfxLoopThread", "exit run method");
	}// run	
}// GfxLoopThread
