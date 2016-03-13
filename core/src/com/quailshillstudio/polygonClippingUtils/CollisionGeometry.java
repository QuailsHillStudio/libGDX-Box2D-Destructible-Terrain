package com.quailshillstudio.polygonClippingUtils;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;


public class CollisionGeometry {
	public static boolean isInCircle(float circleX, float circleY, float r, float x, float y){
		double d = Math.sqrt((circleX - x)*(circleX - x) + (circleY- y)*(circleY- y));
	    return d <= r;
	  
	}
	
	public static double calculateDifferenceBetweenAngles(double firstAngle, double secondAngle)
	  {
	        double difference = secondAngle - firstAngle;
	        while (difference < -180) difference += 360;
	        while (difference > 180) difference -= 360;
	        return difference;
	 }
	
	public static double distanceBetween2Points(Vector2 p1, Vector2 p2){
		return distanceBetween2Points(p1.x, p1.y, p2.x, p2.y);
	}
	
	public static float distanceBetween2Points(float x1, float y1, float x2, float y2){
		return (float) Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
	}
	
	public static float distanceBetweenPointAndRectangle(float px, float py, float x, float y, float width, float height){
		float dx = Math.max(x - px, px - (x + width));
		float dy = Math.max(y - py, py - (y + height));
		return (float) Math.sqrt(dx*dx + dy*dy);
	}
	
	public static boolean CircleCircle(Vector2 vec, float r, Vector2 vec2, float r2){
		Circle c = new Circle(vec, r);
		Circle c2 = new Circle(vec2, r2);
		return Intersector.overlaps(c, c2) ;
	}
	
	public static boolean isPointInTriangle(float px, float py, float ax, float ay, float bx, float by, float cx, float cy){
		return Intersector.isPointInTriangle(px,py,ax,ay,bx,by,cx,cy);
	}
	
	public static float[] approxCircle(float x, float y, float r, int num_segments){
        float angle = 2 * MathUtils.PI / num_segments;
        float cos = MathUtils.cos(angle);
        float sin = MathUtils.sin(angle);
        float cx = r, cy = 0;
        float[] verts = new float[num_segments*2];
        int ii = 0;
            for (int i = 0; i < num_segments; i++) {
                float temp = cx;
                cx = cos * cx - sin * cy;
                cy = sin * temp + cos * cy;
                
                verts[ii] = x + cx;
                ii++;
                verts[ii] = y + cy;
                ii++;
                
            }
            return verts;
    }

	public static boolean isPolygonInCircle(
			float[] verts, Vector2 position,
			float circRadius) {
		for(int i = 0; i < verts.length; i+=2){
			if(!CollisionGeometry.isInCircle(position.x, position.y, circRadius, verts[i], verts[i+1])){
				return false;
				//we return early whenever we find the first vert that is outside the circle
				}
		}
		return true;
		//Here it's the case if every vert is in circle, so yeah, basically it's done.
	}
	
	public static boolean isPolygonPartiallyInCircle(
			float[] verts, Vector2 position,
			float circRadius) {
		for(int i = 0; i < verts.length; i+=2){
			if(CollisionGeometry.isInCircle(position.x, position.y, circRadius, verts[i], verts[i+1])){
				return true;
				}
		}
		return false;
		//Here it's the case if every vert is in circle, so yeah, basically it's done.
	}
/*
	const EPSILON:Number = 0.001;
	const EPSILON_SQUARE:Number = EPSILON*EPSILON;

	function side(x1, y1, x2, y2, x, y:Number):Number
	{
	 return (y2 - y1)*(x - x1) + (-x2 + x1)*(y - y1);
	}

	function naivePointInTriangle(x1, y1, x2, y2, x3, y3, x, y:Number):Boolean
	{
	 var checkSide1:Boolean = side(x1, y1, x2, y2, x, y) >= 0;
	 var checkSide2:Boolean = side(x2, y2, x3, y3, x, y) >= 0;
	 var checkSide3:Boolean = side(x3, y3, x1, y1, x, y) >= 0;
	 return checkSide1 && checkSide2 && checkSide3;
	}

	function pointInTriangleBoundingBox(x1, y1, x2, y2, x3, y3, x, y:Number):Boolean
	{
	 var xMin:Number = Math.min(x1, Math.min(x2, x3)) - EPSILON;
	 var xMax:Number = Math.max(x1, Math.max(x2, x3)) + EPSILON;
	 var yMin:Number = Math.min(y1, Math.min(y2, y3)) - EPSILON;
	 var yMax:Number = Math.max(y1, Math.max(y2, y3)) + EPSILON;
	 
	 if ( x < xMin || xMax < x || y < yMin || yMax < y )
	  return false;
	 else
	  return true;
	}

	function distanceSquarePointToSegment(x1, y1, x2, y2, x, y:Number):Number
	{
	 var p1_p2_squareLength:Number = (x2 - x1)*(x2 - x1) + (y2 - y1)*(y2 - y1);
	 var dotProduct:Number = ((x - x1)*(x2 - x1) + (y - y1)*(y2 - y1)) / p1_p2_squareLength;
	 if ( dotProduct < 0 )
	 {
	  return (x - x1)*(x - x1) + (y - y1)*(y - y1);
	 }
	 else if ( dotProduct <= 1 )
	 {
	  var p_p1_squareLength:Number = (x1 - x)*(x1 - x) + (y1 - y)*(y1 - y);
	  return p_p1_squareLength - dotProduct * dotProduct * p1_p2_squareLength;
	 }
	 else
	 {
	  return (x - x2)*(x - x2) + (y - y2)*(y - y2);
	 }
	}

	function accuratePointInTriangle(x1, y1, x2, y2, x3, y3, x, y:Number):Boolean
	{
	 if (! pointInTriangleBoundingBox(x1, y1, x2, y2, x3, y3, x, y))
	  return false;
	 
	 if (naivePointInTriangle(x1, y1, x2, y2, x3, y3, x, y))
	  return true;
	 
	 if (distanceSquarePointToSegment(x1, y1, x2, y2, x, y) <= EPSILON_SQUARE)
	  return true;
	 if (distanceSquarePointToSegment(x2, y2, x3, y3, x, y) <= EPSILON_SQUARE)
	  return true;
	 if (distanceSquarePointToSegment(x3, y3, x1, y1, x, y) <= EPSILON_SQUARE)
	  return true;
	 
	 return false;
	}*/
}
