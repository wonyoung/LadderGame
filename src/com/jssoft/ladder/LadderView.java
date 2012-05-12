package com.jssoft.ladder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class LadderView extends SurfaceView implements Callback {

	public Paint mLinePaint;
	public RectF mScratchRect;

	class LadderThread extends Thread {

		private SurfaceHolder mSurfaceHolder;
		private Context mContext;
		private boolean mRun;
		private int tempR = 0;

		public LadderThread(SurfaceHolder holder, Context context) {
			mSurfaceHolder = holder;
			mContext = context;
			
			mLinePaint = new Paint();
			mLinePaint.setAntiAlias(true);
			mLinePaint.setARGB(255, 0, 255, 0);
			
			mScratchRect = new RectF(0,0,0,0);
		}
		
		@Override
		public void run() {
			while (mRun) {
				Canvas c = null;
				
				try {
					c = mSurfaceHolder.lockCanvas(null);
					doDraw(c);
				} finally {
					if (c != null) {
						mSurfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}

		private void doDraw(Canvas canvas) {
			tempR  += 10;
			if (tempR > 400) tempR = 10;
			mScratchRect.set(4,4,4+tempR, 4+tempR);
			canvas.drawRect(mScratchRect, mLinePaint);
			
			canvas.save();
			canvas.restore();
		}

		public void setRunning(boolean b) {
			mRun = b;
		}
		
	}

	private LadderThread mThread;
	private Ladder mLadder;
	
	public LadderView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mLadder = Ladder.create(8, 10);
		
		
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		
		mThread = new LadderThread(holder, context);
		
		setFocusable(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mThread.setRunning(true);
		mThread.start();
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mThread.setRunning(false);
		
	}

	public LadderThread getThread() {
		return mThread;
	}

}
