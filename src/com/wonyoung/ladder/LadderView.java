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
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class LadderView extends SurfaceView implements Callback {
	private static final int BOTTOM_MARGIN = 10;
	private static final int TOP_MARGIN = 10;
	private static final int LEFT_MARGIN = 10;		
	private static final int RIGHT_MARGIN = 10;
	
	private static final int LINE_WIDTH = 8;

	private List<RectPaint> lines;
	
	private int mCanvasWidth;
	private int mCanvasHeight;
	
	class RectPaint {
		public Rect r;
		public Paint p;
		
		public RectPaint(Rect rect, Paint paint) {
			this.r = rect;
			this.p = paint;
		}
	}
	
	class LadderGoThread extends Thread {
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
		public final Paint mLinePaint;
		
		public LadderGoThread(int ladder) {
			mIterator = mLadder.iterator(ladder);
			mPrevPosition = new LadderPosition(ladder, 0);
			mCurrentPosition = mPrevPosition;
			
			mLinePaint = new Paint();
			mLinePaint.setAntiAlias(true);
			mLinePaint.setColor(color[ladder % color.length]);
			mLinePaint.setAlpha(200);
			
			synchronized (lines) {
				lines.add(new RectPaint(getHeaderRect(ladder), mLinePaint));
			}
		}



		@Override
		public void run() {
			while(getNext()) {
				try {
					sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		private boolean getNext() {
			if (mIterator.hasNext()) {
				mPrevPosition = mCurrentPosition;
				mCurrentPosition = mIterator.next();
				synchronized (lines) {
					lines.add(new RectPaint(getRect(mPrevPosition, mCurrentPosition), mLinePaint));
				}
			} else {
				if (mCurrentPosition == null)
					return false;
				else {
					synchronized (lines) {
						lines.add(new RectPaint(getFooterRect(mCurrentPosition.ladder), mLinePaint));
					}
					mCurrentPosition = null;
				}
			}
			return true;
		}

		private Rect getHeaderRect(int ladder) {
			return getRectFromLadderPosition(ladder, -1, ladder, 0);
		}

		private Rect getFooterRect(int ladder) {
			return getRectFromLadderPosition(ladder, mLadder.height()-1, ladder, mLadder.height()+1);
		}
		
		private Rect getRect(LadderPosition lp1,
				LadderPosition lp2) {
			return getRectFromLadderPosition(lp1.ladder, lp1.position, lp2.ladder, lp2.position);
		}
	}
	
	
	private Rect getRectFromLadderPosition(int ladderA, int positionA, int ladderB, int positionB) {
		int distanceX = (mCanvasWidth - LEFT_MARGIN - RIGHT_MARGIN) / mLadder.size();
		int distanceY = (mCanvasHeight - TOP_MARGIN-BOTTOM_MARGIN) / (mLadder.height()+2);
		
		int lA = Math.min(ladderA, ladderB);
		int lB = Math.max(ladderA, ladderB);
		int pA = Math.min(positionA, positionB);
		int pB = Math.max(positionA, positionB);
		
		int x1 = LEFT_MARGIN+lA*distanceX+distanceX/2;
		int y1 = TOP_MARGIN+(pA+1)*distanceY;
		int x2 = LEFT_MARGIN+lB*distanceX+distanceX/2+LINE_WIDTH;
		int y2 = TOP_MARGIN+(pB+1)*distanceY+LINE_WIDTH;
		
		y1 = Math.min(y1, mCanvasHeight-TOP_MARGIN-BOTTOM_MARGIN);
		y2 = Math.min(y2, mCanvasHeight-TOP_MARGIN-BOTTOM_MARGIN);
		return new Rect(x1, y1, x2, y2);
	}
	
	class LadderDrawThread extends Thread {
		private SurfaceHolder mSurfaceHolder;
		private boolean mRun;
		public LadderDrawThread(SurfaceHolder holder, Context context) {
			mSurfaceHolder = holder;				
			lines = new ArrayList<RectPaint>();
		}

		@Override
		public synchronized void start() {
			getCanvasWidth();
			initializeLadderDraw();
			super.start();
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


		private void initializeLadderDraw() {
			Paint backgroundLine = new Paint();
			backgroundLine.setAntiAlias(true);
			backgroundLine.setARGB(255, 127, 127, 127);
			
			for (int i=0;i<mLadder.size(); i++) {
				lines.add(new RectPaint(getRectFromLadderPosition(i, -1, i, mLadder.height()+1), backgroundLine));
			}
			
			for (int i=0;i<mLadder.size(); i++) {
				Set<Entry<Integer, LadderPosition>> links = mLadder.getAllLink(i);
				Iterator<Entry<Integer, LadderPosition>> it = links.iterator();
				while(it.hasNext()) {
					Entry<Integer, LadderPosition> link = it.next();
					LadderPosition lp2 = link.getValue();
					
					lines.add(new RectPaint(getRectFromLadderPosition(i, link.getKey(), lp2.ladder, lp2.position), 
							backgroundLine));
				}
			}
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
			synchronized (lines) {
				for (RectPaint rp : lines) {
					canvas.drawRect(rp.r, rp.p);
				}			
			}
			canvas.save();
			canvas.restore();
		}

		public void setRunning(boolean b) {
			mRun = b;
		}
	}

	private LadderDrawThread mThread;
	private Ladder mLadder;
	
	public LadderView(Context context, AttributeSet attrs) {
		super(context, attrs);

		
		
		
		
		
        mLadder = Ladder.create(10,10);
        
        for(int i=0; i<25; i++)
        	mLadder.addLink();
        
        
        
        
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		
		mThread = new LadderDrawThread(holder, context);
		
		setFocusable(true);
	}
		
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			int x = (int) event.getX();
			int y = (int) event.getY();

			int distanceX = (mCanvasWidth - LEFT_MARGIN - RIGHT_MARGIN) / mLadder.size();
			int distanceY = (mCanvasHeight - TOP_MARGIN-BOTTOM_MARGIN) / (mLadder.height()+2);

			if (y > TOP_MARGIN+distanceY)
				return false;
			
			int ladder = (x - LEFT_MARGIN)/ distanceX;			
			
			ladder = Math.max(ladder, 0);
			ladder = Math.min(ladder, mLadder.size() - 1);
			
			new LadderGoThread(ladder).start();
			
			return true;
		}
		return false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		Log.i("SurfaceView", "surfaceChanged");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mThread.setRunning(true);
		mThread.start();
		Log.i("SurfaceView", "surfaceCreated");
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mThread.setRunning(false);
		Log.i("SurfaceView", "surfaceDestroyed");
		
	}

	public LadderDrawThread getThread() {
		return mThread;
	}

}
