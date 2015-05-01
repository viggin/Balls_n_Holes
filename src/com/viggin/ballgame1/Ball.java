package com.viggin.ballgame1;

import org.jbox2d.collision.CircleDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;

public class Ball {
	public int colorIdx;
	public float x, y; // in pixel
	public float radius; // in pixel
	public Body body;
	public Vec2 externForce = new Vec2();
	public final static float M2Px = BodyManager.M2Px;

	public Ball(float x, float y, float radius, int colorIdx, float dens, float fric, float restt) {
		this.colorIdx = colorIdx;
		this.radius = radius;

		CircleDef shape = new CircleDef();
		shape.density = dens;//*rand.nextFloat();
		shape.friction = fric;//*rand.nextFloat();
		shape.restitution = restt;//*rand.nextFloat();

		shape.radius = radius / M2Px;
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(x / M2Px, y / M2Px);

		body = BodyManager.world.createBody(bodyDef);
		body.createShape(shape);
		body.setMassFromShapes();
	}
	
	public void setPos(Vec2 pos) {
		x = pos.x * BodyManager.M2Px;
		y = pos.y * BodyManager.M2Px;
	}
	
	public Vec2 pos() {
		return new Vec2(x,y);
	}
}
