package com.viggin.ballgame1;


public class LevelManager {
	// can be modified or add more levels
	public static final int maxLevelNum = 10;
	public static final int ballNum[][] = {
		{},{1,0,0}, {1,1,0}, {1,1,0}, {1,1,1}, {1,1,1},
		{1,1,1}, {1,1,1}, {1,1,1}, {2,2,2}, {2,2,2}};
	public static final int holeNum[][] = { 
		{},{0,1,0,0}, {0,1,1,0}, {2,1,1,0}, {0,1,1,1}, {5,1,1,1},
		{2,1,1,1}, {2,1,1,1}, {2,2,2,2}, {1,1,1,1}, {2,2,2,2}};
	public static final int wallNum[][] = { 
		{},{1,1,1,1},{1,1,1,1},{1,1,1,1},{1,1,1,1},{1,1,1,1},
		{1,1,1,0},{1,0,1,0},{1,0,1,0},{1,0,1,0},{0,0,0,0}};
	public static final String levelName[] = {
		"","Apple", "Banana", "Cherry", "Date", "Fig", "Grape", "Haw", "Juicy Peach", "Kiwi", "Lemon" };
	public int curLevel;
//	public String hintStr;
	
	private static LevelManager instance;
	private BodyManager bm;
	private HoleManager hm;
	
	private LevelManager(){
	}

	public static LevelManager getInstance() {
		if (instance == null) {
			instance = new LevelManager();
		}
		return instance;
	}
	
	public void init() {
		bm = BodyManager.getInstance();
		hm = HoleManager.getInstance();
		curLevel = 0;
	}
	
	public int checkLevel() {
		return hm.isFalling ? -1 : (bm.balls.isEmpty() ? 1 : 0);
	}
	
	public String nextLevelName() {
		String name = String.format("Level %d: %s", curLevel+1, levelName[curLevel+1]);
//		if (curLevel+1 == 6) {
//			name += hintStr;
//		}
		return name;
	}

	public boolean makeNextLevel() {
		curLevel++;
		if (curLevel > maxLevelNum) {
			return false;
		}
		bm.createWalls(wallNum[curLevel]);
		if (curLevel == 9) {
			hm.createHoles(new float[] {.1f,.5f,.9f,.5f}, new float[] {.3f,.3f,.3f,.7f}, new int[] {1,0,2,3});
			bm.createBalls(new float[] {.4f,.3f,.7f,.5f,.5f,.7f}, new float[] {.1f,.3f,.3f,.5f,.9f,.7f},
					new int[] {3,1,2,1,3,2});
		} else if (curLevel == 10) {
			hm.createHoles(	
					new float[] {.1f,.3f,.8f,.1f,.3f,.5f,.9f,.9f}, 
					new float[] {.8f, .8f, .85f,.5f,.5f,.5f,.7f,.25f}, 
					new int[] {0,1,0,2,1,3,2,3});
			bm.createBalls(
					new float[] {.3f,.6f,.3f,.3f,.7f,.7f}, 
					new float[] {.9f,.8f,.7f,.3f,.65f,.35f}, 
					new int[] {1,1,2,3,2,3});
		} else {
			hm.createHoles(holeNum[curLevel]);
			bm.createBalls(ballNum[curLevel]);
		}
		bm.clearBats();
		return true;
	}
}