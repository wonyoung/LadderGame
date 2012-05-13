package com.jssoft.ladder;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.jssoft.ladder.LadderView.LadderGoThread;
import com.jssoft.ladder.LadderView.LadderThread;

public class LadderDroidActivity extends Activity {
    private LadderView mLadderView;
	private LadderThread mLadderThread;
	private Ladder mLadder;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ladder_layout);


        
        mLadderView = (LadderView) findViewById(R.id.ladder);
        mLadderThread = mLadderView.getThread();
        
        if (savedInstanceState == null) {
        	;
        } else {
        	;
        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	
    	return super.onKeyDown(keyCode, event);
    }
}