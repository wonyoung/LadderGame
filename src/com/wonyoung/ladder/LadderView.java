package com.wonyoung.ladder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
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
	public Rect mScratchRect;

	class LadderGoThread extends Thread {
		private SurfaceHolder mSurfaceHolder;

		private int [] color =  {
				Color.GREEN,
				Color.MAGENTA,
				Color.YELLOW,
				Color.CYAN,
				Color.BLUE,
				Color.RED
		};
		
		private Iterator<LadderPosition> mIterator;
		private LadderPosition mPrevPosition;
		private LadderPosition mCurrentPosition;
		private boolean mDrawDone;
		private boolean mRun = false;
		public Paint mLinePaint;
		
		private List<Rect> lines;

		private int mCanvasWidth;
		private int mCanvasHeight;
		
		public LadderGoThread(SurfaceHolder holder, int ladder) {
			mSurfaceHolder = holder;
			mIterator = mLadder.iterator(ladder);
			mPrevPosition = new LadderPosition(ladder, 0);
			mCurrentPosition = mPrevPosition;
			mDrawDone = true;
			
			mLinePaint = new Paint();
			mLinePaint.setAntiAlias(true);
			mLinePaint.setColor(color[ladder % color.length]);
			
			getCanvasWidth();
			lines = new ArrayList<Rect>();			
			lines.add(getHeaderRect(ladder));
		}


		private void getCanvasWidth() {
			Canvas c = null;
			try {
				c = mSurfaceHolder.lockCanvas(null);
				mCanvasWidth = c.getWidth();
				mCanvasHeight = c.getHeight();
			} finally {
				if (c!=null)
					mSurfaceHolder.unlockCanvasAndPost(c);
			}			
		}

		@Override
		public void run() {
			while(mRun ) {
				Canvas c = null;

				try {
					c = mSurfaceHolder.lockCanvas(null);
					getNext();
					synchronized (mSurfaceHolder) {
						doDraw(c);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				} finally {
					if (c!=null)
						mSurfaceHolder.unlockCanvasAndPost(c);
				}
			}
		}

		private void getNext() throws InterruptedException {
			if (mDrawDone) {
				if (mIterator.hasNext()) {
					mPrevPosition = mCurrentPosition;
					mCurrentPosition = mIterator.next();
					lines.add(getRect(mPrevPosition, mCurrentPosition));
				} else {
					if (mCurrentPosition == null)
						throw new InterruptedException();
					else {
						lines.add(getFooterRect(mCurrentPosition.ladder));
						mCurrentPosition = null;
					}
				}
			}
			
		}

		private Rect getHeaderRect(int ladder) {
			int distanceX = (mCanvasWidth - LEFT_MARGIN - RIGHT_MARGIN) / mLadder.size();
			
			int x1 = LEFT_MARGIN+ladder*distanceX+distanceX/2;
			int x2 = LEFT_MARGIN+ladder*distanceX+distanceX/2+LINE_WIDTH;
			int distanceY = (mCanvasHeight - TOP_MARGIN-BOTTOM_MARGIN) / (mLadder.height()+2);
			int y1 = TOP_MARGIN+(0)*distanceY;
			int y2 = TOP_MARGIN+(1)*distanceY+LINE_WIDTH;

			return new Rect(x1,y1,x2,y2);
		}

		private Rect getFooterRect(int ladder) {
			int distanceX = (mCanvasWidth - LEFT_MARGIN - RIGHT_MARGIN) / mLadder.size();
			
			int x1 = LEFT_MARGIN+ladder*distanceX+distanceX/2;
			int x2 = LEFT_MARGIN+ladder*distanceX+distanceX/2+LINE_WIDTH;
			int distanceY = (mCanvasHeight - TOP_MARGIN-BOTTOM_MARGIN) / (mLadder.height()+2);
			int y1 = TOP_MARGIN+(mLadder.height())*distanceY;
			int y2 = TOP_MARGIN+(mLadder.height()+1)*distanceY+LINE_WIDTH;

			return new Rect(x1,y1,x2,y2);
		}
		
		private Rect getRect(LadderPosition lp1,
				LadderPosition lp2) {
			int distanceX = (mCanvasWidth - LEFT_MARGIN - RIGHT_MARGIN) / mLadder.size();
			
			int x1 = LEFT_MARGIN+lp1.ladder*distanceX+distanceX/2;
			int x2 = LEFT_MARGIN+lp2.ladder*distanceX+distanceX/2+LINE_WIDTH;
			int distanceY = (mCanvasHeight - TOP_MARGIN-BOTTOM_MARGIN) / (mLadder.height()+2);
			int y1 = TOP_MARGIN+(lp1.position+1)*distanceY;
			int y2 = TOP_MARGIN+(lp2.position+1)*distanceY+LINE_WIDTH;

			return new Rect(x1,y1,x2,y2);
		}

		private void doDraw(Canvas canvas) {
			for (Rect r : lines) {
				canvas.drawRect(r, mLinePaint);
			}
			canvas.save();
			canvas.restore();
		}
		
		public void setRunning(boolean b) {
			mRun = b;
		}
	}
	
	class LadderThread extends Thread {
		private SurfaceHolder mSurfaceHolder;
		private boolean mRun;
		public LadderThread(SurfaceHolder holder, Context context) {
			mSurfaceHolder = holder;
			mLinePaint = new Paint();
			mLinePaint.setAntiAlias(true);
			mLinePaint.setARGB(255, 127, 127, 127);
			
			mScratchRect = new Rect(0,0,0,0);
		}
		
		@Override
		public void run() {
			if (mRun) {
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
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {			
			if (ladder >= mLadder.size())
				return false;

			LadderGoThread thread = new LadderGoThread(getHolder(), ladder++);

			thread.start();
			thread.setRunning(true);
			
			return true;
		}
		return false;
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
