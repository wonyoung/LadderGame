package com.jssoft.ladder;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class LadderView extends SurfaceView implements Callback {
	private static final int BOTTOM_MARGIN = 40;
	private static final int TOP_MARGIN = 50;
	private static final int LINE_WIDTH = 4;
	private static final int LEFT_MARGIN = 10;		
	private static final int RIGHT_MARGIN = 10;
	
	
	public Paint mLinePaint;
	public RectF mScratchRect;

	class LadderGoThread extends Thread {
		private SurfaceHolder mSurfaceHolder;

		private Iterator<LadderPosition> mIterator;
		private LadderPosition mPrevPosition;
		private LadderPosition mCurrentPosition;
		private boolean mDrawDone;
		private int mDrawLine;
		private boolean mRun = false;
		public Paint mLinePaint;
		public LadderGoThread(SurfaceHolder holder, int ladder) {
			mSurfaceHolder = holder;
			mIterator = mLadder.iterator(ladder);
			mPrevPosition = new LadderPosition(ladder, 0);
			mCurrentPosition = mPrevPosition;
			mDrawDone = true;
			
			mLinePaint = new Paint();
			mLinePaint.setAntiAlias(true);
			mLinePaint.setARGB(255, 50*(ladder%6), 50*((ladder+3)%6), 50*((ladder+5)%6));
			
			mScratchRect = new RectF(0,0,0,0);			
		}

		@Override
		public void run() {
			while(mRun ) {
				Canvas c = null;

				try {
					c = mSurfaceHolder.lockCanvas(null);
					synchronized (mSurfaceHolder) {
						doDraw(c);
					}
				} finally {
					if (c!=null)
						mSurfaceHolder.unlockCanvasAndPost(c);
				}
			}
		}

		private void doDraw(Canvas canvas) {
			if (mDrawDone) {
				if (mIterator.hasNext()) {
					mPrevPosition = mCurrentPosition;
					mCurrentPosition = mIterator.next();
					mDrawLine = 0;
					mDrawDone = false;
				} else {
					mRun = false;
				}
			}
			int distanceX = (canvas.getWidth() - LEFT_MARGIN - RIGHT_MARGIN) / mLadder.size();
			
			int x1 = LEFT_MARGIN+mPrevPosition.ladder*distanceX+distanceX/2;
			int x2 = LEFT_MARGIN+mCurrentPosition.ladder*distanceX+distanceX/2+LINE_WIDTH;
			int distanceY = (canvas.getHeight() - TOP_MARGIN-BOTTOM_MARGIN) / (mLadder.height()+2);
			int y1 = TOP_MARGIN+(mPrevPosition.position+1)*distanceY;
			int y2 = TOP_MARGIN+(mCurrentPosition.position+1)*distanceY+LINE_WIDTH;
			mScratchRect.set(x1, y1, x2, y2);
			canvas.drawRect(mScratchRect, mLinePaint);
			Log.i("DRAW1", x1+" "+y1+" "+x2+" "+y2);
			if (mDrawLine++ > 1)
				mDrawDone = true;
			canvas.save();
			canvas.restore();
		}
		
		public void setRunning(boolean b) {
			mRun = b;
		}
	}
	
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
			mLinePaint.setARGB(255, 127, 127, 127);
			
			mScratchRect = new RectF(0,0,0,0);
		}
		
		@Override
		public void run() {
			while (mRun) {
				Canvas c = null;
				
				try {
					c = mSurfaceHolder.lockCanvas(null);
					synchronized (mSurfaceHolder) {
						doDraw(c);
					}
				} finally {
					if (c != null)
						mSurfaceHolder.unlockCanvasAndPost(c);
				}
			}
		}

		private void doDraw(Canvas canvas) {
			int distanceX = (canvas.getWidth() - LEFT_MARGIN - RIGHT_MARGIN) / mLadder.size();
			
			for (int i=0;i<mLadder.size(); i++) {
				mScratchRect.set(LEFT_MARGIN+i*distanceX+distanceX/2, TOP_MARGIN, 
						LEFT_MARGIN+i*distanceX+distanceX/2+LINE_WIDTH, canvas.getHeight()-TOP_MARGIN-BOTTOM_MARGIN);
				canvas.drawRect(mScratchRect, mLinePaint);
			}
			
			for (int i=0;i<mLadder.size(); i++) {
				Set<Entry<Integer, LadderPosition>> links = mLadder.getAllLink(i);
				Iterator<Entry<Integer, LadderPosition>> it = links.iterator();
				while(it.hasNext()) {
					Entry<Integer, LadderPosition> link = it.next();
					LadderPosition lp2 = link.getValue();
					int x1 = LEFT_MARGIN+i*distanceX+distanceX/2;
					int x2 = LEFT_MARGIN+lp2.ladder*distanceX+distanceX/2;
					int distanceY = (canvas.getHeight() - TOP_MARGIN-BOTTOM_MARGIN) / (mLadder.height()+2);
					int y1 = TOP_MARGIN+(link.getKey()+1)*distanceY;
					int y2 = TOP_MARGIN+(lp2.position+1)*distanceY+LINE_WIDTH;
					mScratchRect.set(x1, y1, x2, y2);
					canvas.drawRect(mScratchRect, mLinePaint);
				}
			}
			canvas.save();
			canvas.restore();
		}

		public void setRunning(boolean b) {
			mRun = b;
		}
		
	}

	private LadderThread mThread;
	private Ladder mLadder;
	private int ladder;
	
	public LadderView(Context context, AttributeSet attrs) {
		super(context, attrs);

		
		
		
		
		
        mLadder = Ladder.create(10,10);
        
        for(int i=0; i<25; i++)
        	mLadder.addLink();
        
        
        
        
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		
		mThread = new LadderThread(holder, context);
		
		setFocusable(true);
	}
	
	LadderGoThread thread;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		mThread.setRunning(false);
		
		thread = new LadderGoThread(getHolder(), ladder++);
		if (ladder > mLadder.size())
			return true;
		
		thread.start();
		thread.setRunning(true);
		
		return super.onKeyDown(keyCode, event);
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
