package com.lessask;

import java.util.Iterator;
import java.util.LinkedList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.lessask.test.TestSocket;

public class ProgressView extends View {
	private final String TAG = getClass().getSimpleName();
    public final static float MAX_RECORD_TIME = 10000f;
	public final static float MIN_RECORD_TIME = 2000f;

	public ProgressView(Context context) {
		super(context);
		init(context);
	}

	public ProgressView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init(paramContext);

	}

	public ProgressView(Context paramContext, AttributeSet paramAttributeSet,
			int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		init(paramContext);
	}

	private DisplayMetrics displayMetrics;
	private int screenWidth, progressHeight;
	private Paint progressPaint, flashPaint, minTimePaint, breakPaint,
			rollbackPaint;
	private float perWidth, flashWidth = 20f, minTimeWidth = 5f,
			breakWidth = 2f;
	private LinkedList<Integer> timeList = new LinkedList<Integer>();// 每次暂停录制时，将录制时长记录到队列中

	private void init(Context paramContext) {
		displayMetrics = getResources().getDisplayMetrics();
		screenWidth = displayMetrics.widthPixels;
		perWidth = screenWidth / MAX_RECORD_TIME;
//		Log.d("wzy.size", TAG + ".perWidth=" + perWidth);

		progressPaint = new Paint();
		flashPaint = new Paint();
		minTimePaint = new Paint();
		breakPaint = new Paint();
		rollbackPaint = new Paint();

		//setBackgroundColor(Color.parseColor("#222222"));

		progressPaint.setStyle(Paint.Style.FILL);
		progressPaint.setColor(Color.parseColor("#19E3CF"));

		flashPaint.setStyle(Paint.Style.FILL);
		flashPaint.setColor(Color.parseColor("#FFFFFF"));

		minTimePaint.setStyle(Paint.Style.FILL);
		minTimePaint.setColor(Color.parseColor("#FF0000"));

		breakPaint.setStyle(Paint.Style.FILL);
		breakPaint.setColor(Color.parseColor("#000000"));

		rollbackPaint.setStyle(Paint.Style.FILL);
		rollbackPaint.setColor(Color.rgb(255, 98, 89));
	}

	private volatile State currentState = State.PAUSE;
	private boolean isVisible = true;// 一闪一闪的黄色区域是否可见
	private float countWidth = 0;// 每次绘制完成后，进度条的长度
	private float perProgress = 0;// 手指按下时，进度条每次增长的长度
	private long initTime;// 绘制完成时的时间戳
	private long drawFlashTime = 0;// 闪动的黄色区域时间戳

	private long lastStartTime = 0; // 最近视频片段的开始时间
	private long lastEndTime = 0; // 最近视频片段的结束时间

	public static enum State {
		START(0x1), PAUSE(0x2), ROLLBACK(0x3), DELETE(0x4);

		static State mapIntToValue(final int stateInt) {
			for (State value : State.values()) {
                if (stateInt == value.getIntValue()) {
                    return value;
                }
            }
			return PAUSE;
		}

		private int mIntValue;

		State(int intValue) {
			mIntValue = intValue;
		}

		int getIntValue() {
			return mIntValue;
		}
	}

	public void setCurrentState(State state) {
		currentState = state;
		if (state != State.START)
			perProgress = perWidth;

        if (state == State.START)
            Log.e(TAG, "START");
		if (state == State.PAUSE) {
            Log.e(TAG, "PAUSE");
        }
	}

	protected void onDraw(Canvas canvas) {
//		Log.d("wzy.lifecycle", TAG + ".onDraw() called!");
		super.onDraw(canvas);
		progressHeight = getMeasuredHeight();
		//Log.d("wzy.size", TAG + ".progressHeight=" + progressHeight);
		long curSystemTime = System.currentTimeMillis();
		countWidth = 0;
//		Log.d("wzy.logic", TAG + ".timeList.isEmpty()=" + timeList.isEmpty());
		if (currentState == State.START) {
            perProgress += perWidth * (curSystemTime - initTime);
            if (countWidth + perProgress <= getMeasuredWidth())
                canvas.drawRect(countWidth, 0, countWidth + perProgress,
                        getMeasuredHeight(), progressPaint);
            else
                canvas.drawRect(countWidth, 0, getMeasuredWidth(),
                        getMeasuredHeight(), progressPaint);
        }
		initTime = System.currentTimeMillis();
		invalidate();
	}
}
