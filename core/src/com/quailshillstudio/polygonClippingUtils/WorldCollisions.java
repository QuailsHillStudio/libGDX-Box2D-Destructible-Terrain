package com.quailshillstudio.polygonClippingUtils;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Array;
import com.quailshillstudio.articlepolygonclipping.PolygonClipping;

public class WorldCollisions implements ContactListener{
public float circRadius = 4f;
public int segments = (int) (circRadius*2 + circRadius/2);
//private boolean clipped;
private PolygonClipping game;
private boolean clipped;

public WorldCollisions(PolygonClipping game){
	this.game = game;
}

@Override
public void beginContact(Contact contact) {
	   
}

@Override
public void endContact(Contact contact) {
   
}

@Override
public void preSolve(Contact contact, Manifold oldManifold) {

}

@Override
public void postSolve(Contact contact, ContactImpulse impulse) {
	 Body a= contact.getFixtureA().getBody();
	   Body b= contact.getFixtureB().getBody();
	   UserData dataA = (UserData)a.getUserData();
	   UserData dataB = (UserData)b.getUserData();
	   
	if(dataA instanceof UserData && dataA.getType() == UserData.GROUND && dataB instanceof UserData && dataB.getType() == UserData.BOMB){
		clippingGround(a, b, dataA);
	}else if(dataB instanceof UserData && dataB.getType() == UserData.GROUND && dataA instanceof UserData && dataA.getType() == UserData.BOMB){
		clippingGround(b, a, dataB);
	   }
	clipped = false;
	}

	private float[] getVerts(Shape shape) {
		float [] verts = new float[0];
		if(shape instanceof PolygonShape){
			PolygonShape polyShape = (PolygonShape) shape;
			verts = new float[polyShape.getVertexCount()*2];
	        for(int i = 0, j = 0; i < polyShape.getVertexCount(); i++){
	            Vector2 vect = new Vector2();
	            polyShape.getVertex(i, vect);
	            verts[j++] = vect.x;
	            verts[j++] = vect.y;
	        }
		}
		if(shape instanceof ChainShape){
			ChainShape cshape = (ChainShape) shape;
	        verts = null;
	    	if(cshape.isLooped()){
		    	verts = new float[cshape.getVertexCount()*2 - 2];
		        for(int i = 0, j = 0; i < cshape.getVertexCount() - 1; i++){
		            Vector2 vect = new Vector2();
		            cshape.getVertex(i, vect);
		            verts[j++] = vect.x;
		            verts[j++] = vect.y;
		        }
	        }else{
	        	verts = new float[cshape.getVertexCount()*2];
		        for(int i = 0, j = 0; i < cshape.getVertexCount(); i++){
		            Vector2 vect = new Vector2();
		            cshape.getVertex(i, vect);
		            verts[j++] = vect.x;
		            verts[j++] = vect.y;
		        }
	        }
		}
		return verts;
	}

	private void clippingGround(Body a, Body b, UserData dataA) {
		/*if(!clipped) clipped = true;
		else return;*/
		
		List<PolygonBox2DShape> totalRS = new ArrayList<PolygonBox2DShape>();
		
		float[] circVerts = CollisionGeometry.approxCircle(b.getPosition().x, b.getPosition().y, circRadius, segments );
		ChainShape shape = new ChainShape();
		shape.createLoop(circVerts);
		   
		PolygonBox2DShape circlePoly = new PolygonBox2DShape(shape);
		Body body = a;  
		
		Array<Fixture> fixtureList = body.getFixtureList();
		int fixCount = fixtureList.size;
		for(int i = 0; i < fixCount; i++){
			
			PolygonBox2DShape polyClip = null;
			   if(fixtureList.get(i).getShape() instanceof PolygonShape){
				  polyClip = new PolygonBox2DShape((PolygonShape)fixtureList.get(i).getShape());
			   }
			   else if(fixtureList.get(i).getShape() instanceof ChainShape){
				  polyClip = new PolygonBox2DShape((ChainShape)fixtureList.get(i).getShape());
			   }
			   List<PolygonBox2DShape> rs = polyClip.differenceCS(circlePoly);
			   for(int y = 0; y < rs.size(); y++){
				   rs.get(y).circleContact(b.getPosition(), circRadius);
				   totalRS.add(rs.get(y));
			   }  
		}
		
		//Here we paste all the computed polys to the game screen
		   game.switchGround(totalRS);
		   ((UserData)body.getUserData()).mustDestroy = true;
	}
}