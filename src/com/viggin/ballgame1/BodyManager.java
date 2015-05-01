package com.viggin.ballgame1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.ContactListener;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.ContactPoint;
import org.jbox2d.dynamics.contacts.ContactResult;

import com.viggin.ballgame1.Bat;

import android.util.DisplayMetrics;


public class BodyManager {
	
	private AABB worldAABB;
	public static World world;
	private DisplayMetrics dm;
	protected Vec2 g = new Vec2(0f,0f);
	private Random rand = new Random();

	public final static float M2Px = 30;// the ratio between screen and world, M2Px pixels = 1m;
	private final static float timeStep = 5.0f / 60.0f;
	private final static int iterations = 10;// the larger, the more accurate and slower
	
	public final static float maxDensity = 1f;
	public final static float maxFriction = .1f;
	public final static float maxRestitution = .5f; // can't stop bouncing if >1
	public static float maxBallRadius = 30;
	public final static float initBallHoleDist = 2;
	
	private static BodyManager instance;
	private static HoleManager hm;
	int hasWall[];
	
	public List<Ball> balls;
	public List<BatCurve> bats;
	public List<Bat> walls;
//	private BatCurve tempBat;
	
	private BodyManager(){
		hm = HoleManager.getInstance();
	}

	public static BodyManager getInstance() {
		if (instance == null) {
			instance = new BodyManager();
		}
		return instance;
	}
	
	public void init(DisplayMetrics dm1) {
		balls = new ArrayList<Ball>();
		bats = new ArrayList<BatCurve>();
		walls = new ArrayList<Bat>();
		dm = dm1;
		createWorld();
//		createWalls();
		maxBallRadius = Math.min(dm.heightPixels, dm.widthPixels) / 18;
	}
	
	private void createWorld() {
		worldAABB = new AABB();
		worldAABB.lowerBound.set(-20.0f, -20.0f);
		worldAABB.upperBound.set(120.0f, 120.0f);
		boolean doSleep = false;
		world = new World(worldAABB, g, doSleep);
		world.setContactListener(new ContactListener() {
			@Override
			public void result(ContactResult point) {
				Object ud1 = point.shape1.getBody().getUserData(),
						ud2 = point.shape2.getBody().getUserData();
				if (ud1 instanceof Bat && ((Bat)ud1).parent != null) { // is part of a batCurve
					((Bat)ud1).parent.usefulTime --;
				} else if (ud2 instanceof Bat && ((Bat)ud2).parent != null){
					((Bat)ud2).parent.usefulTime --;
				}
			}
			
			@Override
			public void remove(ContactPoint point) {}
			@Override
			public void persist(ContactPoint point) {}
			@Override
			public void add(ContactPoint point) {}
		});
	}
	
	public void createWalls(int hasWall[]) {
		this.hasWall = hasWall;
		float th = Bat.thickness*M2Px*2;
		clearWalls();
		float dw = dm.widthPixels, dh = dm.heightPixels;
		final float x1[] = {0,	0,	0,		dw-th};
		final float y1[] = {0,	0,	dh-th,	0};
		final float x2[] = {dw,	th,	dw,		dw};
		final float y2[] = {th,	dh,	dh,		dh};
		for (int i = 0; i < 4; i++) {
			if (hasWall[i] == 1) {
				walls.add(new Bat(x1[i],y1[i],x2[i],y2[i], 0,0,maxRestitution/2));
			}
		}
	}
	
	
//	private void createWall(float x1, float y1, float x2, float y2) {
//		BodyDef bodydef = new BodyDef();
//		bodydef.position.setZero();
//		Body body = world.createBody(bodydef);
//		PolygonDef shape = new PolygonDef();
//		Vec2 center = new Vec2((x1+x2)/2/M2Px, (y1+y2)/2/M2Px);		
//		shape.density = 0;
//		shape.friction = 0;
//		shape.restitution = maxRestitution/2;
//
//		shape.setAsBox((x2-x1)/2/M2Px, (y2-y1)/2/M2Px, center, 0);
//		body.createShape(shape);
//		body.setMassFromShapes();
//		walls.add(new Bat(x1,y1,x2,y2,body));
//	}
	
	public void createBalls(int[] ballNum) {
		clearBalls();
		for (int i = 0; i < ballNum.length; i++) {
			for (int j = 0; j < ballNum[i]; j++) {
				float r = maxBallRadius;
//				float r = (maxBallRadius+maxBallRadius*rand.nextFloat())/2;
				float x,y;
				do {
					x = r+rand.nextFloat()*(dm.widthPixels-r*2);
					y = r+rand.nextFloat()*(dm.heightPixels-r*2);
				} while (hm.checkFalling(x,y,-1,initBallHoleDist) != -1);
				
				balls.add(new Ball(x, y, r, i+1, maxDensity, maxFriction, maxRestitution));
			}
		}
		hm.isFalling = false;
	}
	
	public void createBalls(float xr[], float yr[], int cr[]) {
		clearBalls();
		for (int i = 0; i < xr.length; i++) {
			balls.add(new Ball(xr[i]*dm.widthPixels, yr[i]*dm.heightPixels, maxBallRadius, cr[i], 
					maxDensity, maxFriction, maxRestitution));
		}
	}
	
//	private void createBall(float x, float y, float radius, int colorIdx) {
//		CircleDef shape = new CircleDef();
//		shape.density = maxDensity;//*rand.nextFloat();
//		shape.friction = maxFriction;//*rand.nextFloat();
//		shape.restitution = maxRestitution;//*rand.nextFloat();
//		
//		shape.radius = radius / M2Px;
//		BodyDef bodyDef = new BodyDef();
//		bodyDef.position.set(x / M2Px, y / M2Px);
//		
//		Body body = world.createBody(bodyDef);
//		body.createShape(shape);
//		body.setMassFromShapes();
//		balls.add(new Ball(colorIdx, radius, body));
//
//	}
	
	public void setGravity(float gx1, float gy1) {
		g.set(gx1, gy1);
	}
	
	public void update() {
		world.setGravity(g);
		world.step(timeStep, iterations);
		
		Iterator<Ball> iter1 = balls.iterator();
		while (iter1.hasNext()) {
			Ball ballData = iter1.next();
			ballData.setPos( ballData.body.getPosition() );
			int holeIdx = hm.checkFalling(ballData.x, ballData.y, ballData.colorIdx ,1f);
			if (holeIdx >= 0) {
				world.destroyBody(ballData.body);
				iter1.remove();
			}
		}
		
		Iterator<BatCurve> iter2 = bats.iterator();
		while (iter2.hasNext()) {
			BatCurve batData = iter2.next();
			if (batData.usefulTime <= 0) {
				batData.destroy();
				iter2.remove();
			}
		}
	}
	
	public void clearBalls() {
		for (Ball ballData : balls) {
			world.destroyBody(ballData.body);
		}
		balls.clear();
	}
	
	public void clearBats() {
		for (BatCurve batData : bats) {
			batData.destroy();
		}
		bats.clear();
	}

	public void clearWalls() {
		for (Bat wallData : walls) {
			world.destroyBody(wallData.body);
		}
		walls.clear();
	}
	
//	public void addBatStart(float x, float y) {
//		tempBat = new BatCurve();
//		tempBat.addBatStart(x, y);
//	}
//
//	public void addBatMoving(float x, float y) {
//		tempBat.addBatMove(x, y);
//	}
//	
//	public void addBatEnd() {
//		tempBat.addBatEnd();
//		bats.add(tempBat);
//	}
	
	public void addAttraction(float x, float y, float forceMul) {
		Vec2 delta = new Vec2();
		for (Ball ballData : balls) {
			delta.set(x-ballData.x, y-ballData.y);
			ballData.externForce = delta.mul((float) Math.pow(delta.length()/M2Px,-3) * forceMul);
			ballData.body.applyForce(ballData.externForce, ballData.pos());
		}
	}

	public void removeAttraction() {
		for (Ball ballData : balls) {
			ballData.body.applyForce(ballData.externForce.negateLocal(), ballData.pos());
		}
	}
}
