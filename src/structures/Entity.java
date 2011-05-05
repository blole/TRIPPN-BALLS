package structures;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import collision.CollisionData;

import engine.Engine;
import engine.ModelLoader;
import engine.Settings;

public class Entity {
	public static final List<Entity> all = new ArrayList<Entity>();
	public static final Set<Entity> hasGravity = new HashSet<Entity>();
	
	public Doodad doodad;
	private List<Entity> children = new ArrayList<Entity>();
	
	private boolean hasOwnGravity = false;
	private boolean isFrozen = false;
	private boolean isAnon = false;
	private boolean isSphere = false;
	
	private String modelName;
	public final Vector speed = new Vector(0,0,0);
	public final Vector pos = new Vector(0,0,0);
	
	private float boundingRadius = 1;
	private float mass = 1;
	private VBOinfo vbo;
	private Entity father;
	public float[] tint = new float[]{1,1,1};
	
	public Entity (String doodadName) {
		doodad = new Doodad(doodadName, this);
		tint[0] = Engine.random.nextInt()%250;
		tint[1] = Engine.random.nextInt()%250;
		tint[2] = Engine.random.nextInt()%250;
		doodad.call("onBirth");
		all.add(this);
	}
	
//	private void loadEverything(String doodadName) {
//		LuaLoader.loadInfo(this, doodadName);
//		doodad = LuaLoader.getDoodad(doodadName);
//		vbo = ModelLoader.loadStatic(modelName);
//		doodad.call("onBirth");
//	}
	
	public void render() {
		Engine.gl.glPushMatrix();
		Engine.gl.glColor3fv(tint, 0);
		Engine.gl.glTranslatef(pos.x, pos.y, pos.z);
		if (vbo != null)
			Engine.drawVBO(vbo);
		for (Entity child : children)
			child.render();
		Engine.gl.glPopMatrix();
	}
	
	public void move() {
		pos.addSelf(speed);
	}
	public void collide(CollisionData d){
		Entity other = d.other;
		
		pos.subtractSelf(speed.multiply(d.t));
		other.pos.subtractSelf(other.speed.multiply(d.t));
		
		Vector vectorBetween = pos.vectorTo(other.pos);
		
		if (vectorBetween.isZero())
			vectorBetween.x=0.00001f;
		
		float distanceBetween = (vectorBetween.abs() - (boundingRadius+other.getBoundingRadius()))/2;
		if (distanceBetween < 0) {
			pos.addSelf(vectorBetween.setLength(distanceBetween));
			other.pos.addSelf(vectorBetween.setLength(-distanceBetween));
			vectorBetween = pos.vectorTo(other.pos);
		}
		Vector toOther = speed.multiply(mass).proj(vectorBetween);
		Vector toMe = other.speed.multiply(mass).proj(vectorBetween);
		
		speed.addSelf(toMe);
		other.speed.subtractSelf(toMe);
		other.speed.addSelf(toOther);
		speed.subtractSelf(toOther);
		doodad.pushCall("onCollide", this.doodad.getUserdata(), other.doodad.getUserdata());
	}
	public void attract(Entity other) {
		Vector vectorBetween = pos.vectorTo(other.pos);
		
		if (vectorBetween.abs() >= (boundingRadius+other.getBoundingRadius())) {
			float xDiff = other.pos.x-pos.x;
			float yDiff = other.pos.y-pos.y;
			float zDiff = other.pos.z-pos.z;
			float distance = vectorBetween.abs();
			float attraction = Settings.gravitationalConstant/(distance*distance);
			
			speed.x += xDiff*attraction;
			speed.y += yDiff*attraction;
			speed.z += zDiff*attraction;
			other.speed.x -= xDiff*attraction;
			other.speed.y -= yDiff*attraction;
			other.speed.z -= zDiff*attraction;
		}
	}
	
	public void attach(Entity anonEntity) {
		if (!anonEntity.isAnon())
			anonEntity.setAnon(true);
		children.add(anonEntity);
		anonEntity.setFather(this);
	}
	
	/*
	 * Sets
	 */
	private void setFather(Entity father) {
		this.father = father;
	}
	public void setMass(float mass) {
		this.mass = mass;
	}
	public void setBoundingRadius(float radius) {
		this.boundingRadius = radius;
	}
	public void setKill() {
		doodad.pushCall("onDestroy");
	}
	public void setVbo(VBOinfo vbo) {
		this.vbo = vbo;
	}
	public void setModel(String modelName) {
		this.modelName = modelName;
		vbo = ModelLoader.loadStatic(modelName);
	}
	/*
	 * Gets
	 */
	public Entity getFather() {
		return father;
	}
	public float getMass() {
		return mass;
	}
	public float getBoundingRadius() {
		return boundingRadius;
	}
	public String getModel() {
		return modelName;
	}
	
	/*
	 * Set information booleans
	 */
	public Entity setFrozen(boolean frozen) {
		isFrozen = frozen;
		return this;
	}
	public Entity setAnon(boolean isAnon) {
		this.isAnon = isAnon;
		if (isAnon)
			Entity.all.remove(this);
		else
			Entity.all.add(this);
		return this;
	}
	public Entity setSphere(boolean isSphere) {
		this.isSphere = isSphere;
		return this;
	}
	public Entity setOwnGravity(boolean hasOwnGravity) {
		if (hasOwnGravity) {
			if (!Entity.hasGravity.contains(this)) {
				Entity.hasGravity.add(this);
				this.hasOwnGravity = true;
			}
		}
		else {
			Entity.hasGravity.remove(this);
			this.hasOwnGravity = false;
		}
		return this;
	}
	/*
	 * Information booleans
	 */
	public boolean isFrozen() {
		return isFrozen;
	}
	public boolean isAnon() {
		return isAnon;
	}
	public boolean isSphere() {
		return isSphere;
	}
	public boolean hasOwnGravity() {
		return hasOwnGravity;
	}
	@Override
	public String toString() {
		return "Entity: "+"<doodad should be here>"+"   model: "+modelName;
	}
}