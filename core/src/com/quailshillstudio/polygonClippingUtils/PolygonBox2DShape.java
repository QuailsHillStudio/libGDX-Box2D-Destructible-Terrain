package com.quailshillstudio.polygonClippingUtils;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

public class PolygonBox2DShape extends Polygon{
		Vector2 circPos = new Vector2();
		float circRadius = 0;
	
	 public PolygonBox2DShape(float[] verts) {
	        if((verts.length%2) == 1){System.out.println("Verts Impairs"); return;}
	        //System.out.println(verts.length);
	        for (int i = 0; i < verts.length; i+=0) {
	            add(new Vertex(verts[i++], verts[i++]));
	        }
	    }
	    
	 	public PolygonBox2DShape(Shape shape){
	 		if(shape instanceof ChainShape) this.ConstPolygonBox2DShape((ChainShape)shape);
	 		else if (shape instanceof PolygonShape) this.ConstPolygonBox2DShape((PolygonShape) shape);
	 	}
	 	
	    /** Breaking the genetricity of the class by Adding this constructor Libgdx/Box2d specific**/
	    public void ConstPolygonBox2DShape(ChainShape shape){
	        //System.out.println("Entering highly bugged grounds : ChainShape constructor is meant to be used as an inspiration !");
	        float[] verts = null;
	    	if(shape.isLooped()){
		    	verts = new float[shape.getVertexCount()*2 - 2];
		        for(int i = 0, j = 0; i < shape.getVertexCount() - 1; i++){
		            Vector2 vect = new Vector2();
		            shape.getVertex(i, vect);
		            //System.out.println(vect);
		            verts[j++] = vect.x;
		            verts[j++] = vect.y;
		        }
	        }else{
	        	verts = new float[shape.getVertexCount()*2];
		        for(int i = 0, j = 0; i < shape.getVertexCount(); i++){
		            Vector2 vect = new Vector2();
		            shape.getVertex(i, vect);
		            //System.out.println(vect);
		            verts[j++] = vect.x;
		            verts[j++] = vect.y;
		        }
	        }
	        if((verts.length%2) == 1){System.out.println("Verts Impairs"); return;}
	        //System.out.println(verts.length);
	        for (int i = 0; i < verts.length; i+=0) {
	            add(new Vertex(verts[i++], verts[i++]));
	        }
	        	
	    }
	    
	    public void ConstPolygonBox2DShape(PolygonShape shape){
	    	float[] verts = new float[shape.getVertexCount()*2];
	        for(int i = 0, j = 0; i < shape.getVertexCount(); i++){
	            Vector2 vect = new Vector2();
	            shape.getVertex(i, vect);
	            //System.out.println(vect);
	            verts[j++] = vect.x;
	            verts[j++] = vect.y;
	        }
	        if((verts.length%2) == 1){System.out.println("Verts Impairs"); return;}
	        //System.out.println(verts.length);
	        for (int i = 0; i < verts.length; i+=0) {
	        	super.add(new Vertex(verts[i++], verts[i++]));
	        }
	    }
	    
	    public PolygonBox2DShape(float[][] points) {
			super(points);
		}

		/** Return a simple array with the vertices*/
	    public float[] vertices(){
	        float[] verts = new float[vertices*2];
	        Vertex v = first;
	        for (int i = 0, j = 0; i < vertices; i++) {
	            verts[j++] = v.x;
	            verts[j++] = v.y;
	            v = v.next;
	        }
	        return verts;
	    }
	    
	    /** Return a simple array with the vertices*/
	    public float[] verticesToLoop(){
	        //float[] verts = new float[vertices*2 - 1];
	    	int NbIn = 0; 
	    	List<Float> verts = new ArrayList<Float>();
	        Vertex v = first;
	        for (int i = 0, j = 0; i < vertices - 1; i++) {
	        	// Here escape the verts that have a square distance > 0.005f * 0.005f to avoid the b2DistanceSquared(v1,v2) > 0.005f * 0.005f expression
	        	if(v.equals(first) || (b2SquaredDistance(verts.get(j-2), verts.get(j-1), v.x, v.y) > (0.35f))){
	            	verts.add(v.x);
	            	j++;
	            	verts.add(v.y);
	            	j++;
	            }
	        	if(circRadius != 0 && this.isInCircle(circPos.x, circPos.y, circRadius, v.x, v.y)){NbIn ++;}
	            v = v.next;
	        }
	        
	        float[] vertsTab = null;
	        
	        if((NbIn == 0 || circRadius == 0) || (NbIn != (vertices -1))){
		        vertsTab = new float[verts.size()];
		        for(int i = 0; i < verts.size(); i ++){
		        	vertsTab[i] = verts.get(i).floatValue();
		        }
	        }else{
	        	vertsTab = new float[0];
	        }
	        return vertsTab;
	    }
	    
	    /** Calculate the union between two polygons */
	    public List<PolygonBox2DShape> unionCS(Polygon poly) {
	        return this.clipCS(poly, false, false);
	    }

	    /** Calculate the intersection between two polygons */
	    public List<PolygonBox2DShape> intersectionCS(Polygon poly) {
	        return this.clipCS(poly, true, true);
	    }

	    /** Calculate the difference between two polygons */
	    public List<PolygonBox2DShape> differenceCS(Polygon poly) {
	        return this.clipCS(poly, false, true);
	    }
	    
	    
	    public List<PolygonBox2DShape> clipCS(Polygon poly, boolean b, boolean b2){
	    	List<Polygon> rs = super.clip(poly, b, b2);
			List<PolygonBox2DShape> rsCS = new ArrayList<PolygonBox2DShape>();
			for(int i = 0; i < rs.size(); i++){
				rsCS.add(new PolygonBox2DShape(rs.get(i).points()));
	    	}
			return rsCS;
	    }
	    
	    public void circleContact(Vector2 vec, float radius){
	    	this.circPos = vec;
	    	this.circRadius = radius;
	    }
	    
	    private float b2SquaredDistance(float x1,float y1,float x2,float y2){
	    	Vector2 vec = new Vector2(x1, y1);
	    	return vec.dst2(x2, y2);
	    }
	    public boolean isInCircle(float circleX, float circleY, float r, float x, float y){
			double d = Math.sqrt((circleX - x)*(circleX - x) + (circleY- y)*(circleY- y));
		    return d <= r;
		  
		}
}
