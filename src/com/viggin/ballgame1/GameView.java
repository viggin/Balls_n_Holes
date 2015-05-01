package com.viggin.ballgame1;

import java.util.Iterator;

import org.jbox2d.common.Vec2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

class GameView extends View {

	public static float M2Px;
	private Paint paint;
	private Canvas canvas;
	private BodyManager bm;
	private HoleManager hm;
	//	float newBatStartX, newBatStartY;
	//	float newBatEndX, newBatEndY;
	boolean isDrawing = false;
	//	private static float minBatWidth;
	private BatCurve tempBat;

	private static final int colorLib[] = {Color.GRAY, Color.RED, Color.GREEN, Color.BLUE|0x0080a000};
	private static final int holeBorderW = 8;

	public GameView(Context context) {
		super(context);
		bm = BodyManager.getInstance();
		M2Px = BodyManager.M2Px;
		//		minBatWidth = 5f / M2Px;
		hm = HoleManager.getInstance();
		paint = new Paint();
		paint.setAntiAlias(true);
	}

	public void update() {
		postInvalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN :
			tempBat = new BatCurve();
			tempBat.addBatStart(x, y);
			//			bm.addBatStart(x,y);
			//			newBatStartX = x;
			//			newBatStartY = y;
			//			bm.addAttraction(x,y,100f); // has bug
			break;

		case MotionEvent.ACTION_MOVE :
			isDrawing = true;
			//			float width = (float) Math.hypot(newBatStartX-x, newBatStartY-y) / M2Px;
			//			if (width >= minBatWidth) {
			tempBat.addBatMove(x, y);
			//				bm.addBatMoving(x, y);
			//				newBatStartX = x;
			//				newBatStartY = y;
			//			}

			break;

		case MotionEvent.ACTION_UP :
			isDrawing = false;
			//			tempBat.addBatEnd();
			bm.bats.add(tempBat);
			//			bm.addBatEnd();
			//			bm.removeAttraction(); // has bug
			break;

		}
		return true;
	}

	protected void onDraw(Canvas canvas) {
		this.canvas = canvas;
		drawWalls();
		drawHoles();
		drawBalls();
		drawBats();

		if (isDrawing) {
			paint.setColor(Color.YELLOW);
			paint.setStrokeWidth(Bat.thickness*M2Px*2);
			Iterator<Vec2> iter = tempBat.vertices.iterator();
			Vec2 p1 = iter.next();
			while (iter.hasNext()) {
				Vec2 p2 = iter.next();
				canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
				p1 = p2;
			}
		}
	}

	private void drawWalls() {
		paint.setColor(Color.GRAY);
		//		paint.setStrokeWidth(BodyManager.thickness*M2Px*2);
		paint.setStyle(Paint.Style.FILL);
		for (Bat wallData : bm.walls) {
			canvas.drawRect(wallData.x1, wallData.y1, wallData.x2, wallData.y2, paint);
		}
	}

	private void drawBats() {
		for (BatCurve batData : bm.bats) {
			if (batData.usefulTime > 0) {
				paint.setColor(colorLib[batData.colorFilter]);
				paint.setStrokeWidth(Bat.thickness*M2Px);
			} else {
				paint.setColor(Color.CYAN);
				paint.setStrokeWidth(Bat.thickness*M2Px*2);
			}
			
			Iterator<Vec2> iter = batData.vertices.iterator();
			Vec2 p1 = iter.next();
			while (iter.hasNext()) {
				Vec2 p2 = iter.next();
				canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
				p1 = p2;
			}

		}
	}

	private void drawBalls() {
		paint.setStyle(Paint.Style.FILL);
		for (Ball ballData : bm.balls) {
			paint.setColor(colorLib[ballData.colorIdx]);
			canvas.drawCircle(ballData.x, ballData.y, ballData.radius, paint);
		}		
	}

	private void drawHoles() {
		paint.setStyle(Paint.Style.STROKE);  
		paint.setStrokeWidth(holeBorderW);
		for (HoleManager.Hole hole : hm.holes) {
			paint.setColor(colorLib[hole.colorIdx] | 0x00808080);
			canvas.drawCircle(hole.x, hole.y, hole.radius, paint);
			if (hole.newFallColorIdx > 0) {
				paint.setColor(colorLib[hole.newFallColorIdx] & 0x80FFFFFF);  
				canvas.drawCircle(hole.x, hole.y, hole.radius + holeBorderW-1, paint);
				hole.newFallColorIdx = -1;
			}
		}
	}
}