package com.viggin.ballgame1;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

// Author: Ke Yan. Blog: yanke23.tk

// TODO: add scores; add attraction source
// TODO: what if draw bats before tilting don't count time?
// TODO: bug: click outside the dialog, can't go on
// TODO: string table; add more moving/static elements/holes
// TODO: make the players can define levels

public class GameActivity extends Activity {

	private final static long delayTime = 17;
	private GameView myView;
	private Handler mHandler;
	BodyManager bm;
	HoleManager hm;
	LevelManager lm; 
	protected float gx = 0f, gy = 0f;
	private boolean isInit = true;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		isInit = true;

		bm = BodyManager.getInstance();
		bm.init(dm);
		hm = HoleManager.getInstance();
		hm.init(dm);
		lm = LevelManager.getInstance();
		lm.init();
//		lm.hintStr = getString(R.string.wallHint);

		setGavityChange();
		myView = new GameView(this);
		setContentView(myView);
		mHandler = new Handler();
		mHandler.post(update);
	}

	public void onStop() {
		super.onStop();
		finish();
	}

	private void setGavityChange() {
		SensorManager sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		Sensor sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		SensorEventListener lsn = new SensorEventListener() {    
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}

			@Override
			public void onSensorChanged(SensorEvent e) {
				// TODO Auto-generated method stub
				gx = -e.values[SensorManager.DATA_X];  
				gy = e.values[SensorManager.DATA_Y];  
			}
		};
		sensorMgr.registerListener(lsn, sensor, SensorManager.SENSOR_DELAY_GAME);
	}

	private Runnable update = new Runnable() {
		public void run() {
			if (isInit) {
				isInit = false;
				showInfoDialog(1);
			} else {
				switch (lm.checkLevel()) {
				case 0:
					bm.setGravity(gx, gy);
					bm.update();
					myView.update();
					mHandler.postDelayed(update, delayTime);
					break;

				case 1:
					if (lm.curLevel < LevelManager.maxLevelNum) {
						showInfoDialog(2);					
					} else {
						showInfoDialog(3);
					}
					break;

				case -1:
					showInfoDialog(4);
					break;
				}
			}
		}
	};

	public void showInfoDialog(int idx) {
		switch (idx) {
		case 1:
			new AlertDialog.Builder(this)
			.setTitle("Hints").setMessage(R.string.hints)
			.setNeutralButton("OK", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mHandler.post(update);
					dialog.dismiss();
				}
			}).setNegativeButton("Quit", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					finish();
				}
			}).show();
			break;

		case 2:
			String name = lm.nextLevelName();
			if (lm.curLevel == 5) {
				name += getString(R.string.wallHint);
			}
			new AlertDialog.Builder(this)
			.setTitle("Next Level").setMessage(name)
			.setPositiveButton("Go", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					lm.makeNextLevel();
					mHandler.post(update);
					dialog.dismiss();
				}
			}).setNegativeButton("Quit", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					finish();
				}
			}).show();
			break;

		case 3:
			new AlertDialog.Builder(this)
			.setTitle("Congratulations").setMessage("You Win!")
			.setNeutralButton("OK", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			}).show();
			break;

		case 4:
			lm.curLevel --;
			new AlertDialog.Builder(this)
			.setTitle(lm.nextLevelName()).setMessage("Your Ball is Lost!")
			.setPositiveButton("Retry", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					lm.makeNextLevel();
					mHandler.post(update);
					dialog.dismiss();
				}
			}).setNegativeButton("Quit", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			}).show();
			break;
		}

	}

}