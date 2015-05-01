package com.viggin.ballgame1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jbox2d.common.Vec2;

// a list of bats forming a curve. If the ball touches one bat, the whole curve disappears
public class BatCurve {

	public List<Bat> elements;
	public int usefulTime = 1; // if set to 1, the BatCurve disappears after touch the ball once. Or you can set it to a large value
//	public boolean isCreating = true;
	public List<Vec2> vertices;
	private float lastX, lastY;
	public int colorFilter = 0;
	public final static float M2Px = BodyManager.M2Px;
	public final static float minBatWidth = 5f / M2Px;

	public BatCurve() {
		elements = new ArrayList<Bat>();
		vertices = new ArrayList<Vec2>();
	}
	
	// parameters in pixels
	public void addBatStart(float x, float y) {
		vertices.add(new Vec2(x,y));
		lastX = x;
		lastY = y;
	}
	
	public void addBatMove(float x, float y) {
		if (vertices.isEmpty()) {
			addBatStart(x, y);
			return;
		}
		float width = (float) Math.hypot(x-lastX, y-lastY) / M2Px;
		if (width < minBatWidth) {
			return;
		}
		
		Bat bat = new Bat(lastX, lastY, x, y, 0,BodyManager.maxFriction/2,BodyManager.maxRestitution);
		bat.parent = this;
		elements.add(bat);
		addBatStart(x, y);
	}
	
//	public void addBatEnd() {
//		isCreating = false;
//	}
	
	public void destroy() {
		Iterator<Bat> iter = elements.iterator();
		while (iter.hasNext()) {
			Bat bat = (Bat) iter.next();
			BodyManager.world.destroyBody(bat.body);
		}
		elements.clear();
		vertices.clear();
		
	}
}
