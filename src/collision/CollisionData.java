package collision;

import structures.Entity;
import structures.Vector;


/**
 * A wrapper class for all the necessary data that is
 * passed aroung in the collision system. I use as a 
 * way to mimic the use of pointers. 
 * @author Jacob Norlin Andersson
 *
 */
public class CollisionData {
	
	public float t; //At which point in time the object intersect.
	public Vector intersectionPoint;
	public boolean collision; //Used to find if a collision took place.
	public Entity other;
	
	/**
	 * Constructs a CollisionData object with the given values.
	 * @param b Did i find a collision?
	 * @param t Time of the collision along the speed vector
	 * @param v Point of collision
	 */
	public CollisionData(boolean b, float t, Vector v){
		this.t = t;
		intersectionPoint = v;
		collision = b;
	}
	/**
	 * Constructs a CollisionData object with collision = false, t=-1 and 
	 * intersectionPoint = null;
	 */
	public CollisionData(){
		t = -1;
		intersectionPoint = null;
		collision = false;
	}
	
	public String toString(){
		String s = "CollisinData, collision: "+collision+" t: "+t+" point: "+intersectionPoint+" other: "+other;
		return s;
	}

}
