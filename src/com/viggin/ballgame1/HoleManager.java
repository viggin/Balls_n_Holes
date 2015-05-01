package com.viggin.ballgame1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.util.DisplayMetrics;

public class HoleManager {
	private static float radius;
	private static float distTh;
	private Random rand = new Random();

	class Hole {
		float x, y;
		float radius = HoleManager.radius;
		int colorIdx = 0;
		int newFallColorIdx = -1;
		public Hole (float x1, float y1, int c1) {
			x = x1; y = y1; colorIdx = c1;
		}
	}
	private static HoleManager instance;
	public List<Hole> holes;
	private DisplayMetrics dm;
	public boolean isFalling = false;
	
	private HoleManager(){
		holes = new ArrayList<Hole>();
	}
	
	public static HoleManager getInstance() {
		if (instance == null) {
			instance = new HoleManager();
		}
		return instance;
	}
	
	public void init(DisplayMetrics dm1) {
		dm = dm1;
		isFalling = false;
		radius = BodyManager.maxBallRadius;
		distTh = radius*1.5f;
	}
	
	public void createHoles(int[] holeNum) {
		isFalling = false;
		holes.clear();
		float x,y;
		for (int i = 0; i < holeNum.length; i++) {
			for (int j = 0; j < holeNum[i]; j++) {
				do {
					x = radius + rand.nextFloat() * (dm.widthPixels - radius*2);
					y = radius + rand.nextFloat() * (dm.heightPixels - radius*2);
				} while (checkFalling(x,y,-1,2) != -1);
				holes.add(new Hole(x, y, i));
			}
		}
	}
	
	public void createHoles(float xr[], float yr[], int cr[]) {
		isFalling = false;
		holes.clear();
		for (int i = 0; i < xr.length; i++) {
			holes.add(new Hole(xr[i]*dm.widthPixels, yr[i]*dm.heightPixels, cr[i]));
		}
	}
	
	public int checkFalling(float ballX, float ballY, int colorIdx, float distMul) {
		Hole h;
		for (int i = 0; i < holes.size(); i++) {
			h = holes.get(i);
			if (Math.hypot(h.x - ballX, h.y - ballY) < distTh*distMul) {
				h.newFallColorIdx = colorIdx;
				if (colorIdx != h.colorIdx) {
					isFalling = true;
				}
				return i;
			}
		}
		if (ballX < 0 || ballX > dm.widthPixels
			|| ballY < 0 || ballY > dm.heightPixels ) {
			isFalling = true;
			return -2;
		}
		return -1;
	}
	
}
