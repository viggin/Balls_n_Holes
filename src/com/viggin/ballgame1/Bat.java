package com.viggin.ballgame1;

import org.jbox2d.collision.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;

public class Bat {
	public float x1, y1, x2, y2; // in pixel
//	float thickness;
//	public int usefulTime = 1;
//	float restitution;
	public int colorFilter = 0;
	public Body body;
//	public boolean isCreating = true;
	public BatCurve parent = null;
	
	public final static float M2Px = BodyManager.M2Px;
	public final static float thickness = 4.0f / M2Px;

	public Bat(float x11, float y11, float x21, float y21, float dens, float fric, float restt) {
		x1 = x11; y1 = y11; x2 = x21; y2 = y21;
		Vec2 center = new Vec2((x1+x2)/2/M2Px, (y1+y2)/2/M2Px);
		float width = (float) Math.hypot(x1-x2, y1-y2) / M2Px;
		float angle = (float) Math.atan2(y2-y1, x2-x1);
		BodyDef bodydef = new BodyDef();
		bodydef.position.set(0,0); // set body's origin point
		body = BodyManager.world.createBody(bodydef);
		
		PolygonDef shape = new PolygonDef();
		
		shape.density = dens;
		shape.friction = fric;
		shape.restitution = restt;
		shape.setAsBox(width/2, thickness/2, center, angle);
		
		body.createShape(shape);
		body.setMassFromShapes();
		body.setUserData(this);
	}
}
