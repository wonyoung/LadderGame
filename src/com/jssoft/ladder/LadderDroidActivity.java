package com.jssoft.ladder;

import android.app.Activity;
import android.os.Bundle;

import com.jssoft.ladder.LadderView.LadderThread;

public class LadderDroidActivity extends Activity {
    private LadderView mLadderView;
	private LadderThread mLadderThread;

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
}