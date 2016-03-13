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
	
	public void clippingFixture(Body b,float circRadius, int segments){
		List<PolygonBox2DShape> totalRS = new ArrayList<PolygonBox2DShape>();
		
		float[] circVerts = CollisionGeometry.approxCircle(b.getPosition().x, b.getPosition().y, circRadius, segments );
		ChainShape shape = new ChainShape();
		shape.createLoop(circVerts);
		   
		PolygonBox2DShape circlePoly = new PolygonBox2DShape(shape);

		int fixCount = this.getVerts().size();
		System.out.println("FixCount : " + fixCount);
		for(int i =0; i < fixCount; i++){
			
			PolygonBox2DShape polyClip = new PolygonBox2DShape(this.getVerts().get(i));
			   
			   List<PolygonBox2DShape> rs = polyClip.differenceCS(circlePoly);
			   System.out.println("Size : "+ rs.size());
			   for(int y = 0; y < rs.size(); y++){
				   if(!CollisionGeometry.isPolygonInCircle(rs.get(y).vertices(), b.getPosition(), circRadius))
				   totalRS.add(rs.get(y));
			   }  
		}
        getVerts().clear();
		for(int i = 0; i < totalRS.size(); i++){
			getVerts().add(totalRS.get(i).vertices());
		}
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
