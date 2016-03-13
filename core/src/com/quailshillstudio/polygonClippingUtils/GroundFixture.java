package com.quailshillstudio.polygonClippingUtils;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;

public class GroundFixture{
	private List<float[]> verts = new ArrayList<float[]>();
	private List<Fixture> fixtureRefs;
	private Body body;
	
	public GroundFixture(List<float[]> verts){
		this.setVerts(verts);
	}
	
	public Body getBody() {
		return this.body;
	}
	
	public void setBody(Body nground) {
		this.body = nground;
	}
	
	public List<Fixture> getFixtures() {
		return this.fixtureRefs;
	}
	
	public void setFixtures(List<Fixture> fixtures) {
		this.fixtureRefs = fixtures;
	}

	public List<float[]> getVerts() {
		return verts;
	}

	public void setVerts(List<float[]> verts) {
		this.verts = verts;
	}
}
