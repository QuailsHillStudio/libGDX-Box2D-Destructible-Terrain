package com.quailshillstudio.articlepolygonclipping;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.quailshillstudio.polygonClippingUtils.GroundFixture;
import com.quailshillstudio.polygonClippingUtils.PolygonBox2DShape;
import com.quailshillstudio.polygonClippingUtils.UserData;
import com.quailshillstudio.polygonClippingUtils.WorldCollisions;

public class PolygonClipping extends ApplicationAdapter {
	SpriteBatch batch;
	World world;
	Box2DDebugRenderer renderer;
	OrthographicCamera camera;
	private List<GroundFixture> polyVerts = new ArrayList<GroundFixture>();
	private boolean mustCreate;
	private float accu;
	private static final float TIME_STEP = 1 / 60f;
	private static int speedIte = 6, posIte = 2;
	private int count = 0;
	//public List<GroundFixture> groundFixtures = new ArrayList<GroundFixture>();
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		world = new World(new Vector2(0, -9.81f), false);
		world.setContactListener(new WorldCollisions(this));
		renderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(Gdx.graphics.getWidth()/10,Gdx.graphics.getHeight()/10);
		
		List<float[]> verts = new ArrayList<float[]>();
		float[] points = {-60,-10,-60,-40f,60,-40f,60,-10};
		verts.add(points);
		GroundFixture grFix = new GroundFixture(verts);
		polyVerts.add(grFix);
		mustCreate = true;
		
		createBall(UserData.BALL, new Vector2(-10,0));
		createBall(UserData.BOMB, new Vector2(10,0));
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		renderer.render(world, camera.combined);
		
		if(Gdx.input.justTouched()){
			int type;
			count++;
			if(count %2 == 0){
				type = UserData.BALL;
			}else{
				type = UserData.BOMB;
			}
			Vector3 box2Dpos = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
			createBall(type, new Vector2(box2Dpos.x, box2Dpos.y));
		}
		
		for (int i = 0; i < world.getBodyCount(); i++) {
			Array<Body> bodies = new Array<Body>();
			world.getBodies(bodies );
			UserData data = ((UserData) bodies.get(i).getUserData());
			if (data != null && data.getType() == UserData.GROUND) {
				if ((data.mustDestroy || mustCreate) && !data.destroyed) {
					world.destroyBody(bodies.get(i));
					bodies.removeIndex(i);
				}
			}
		}
		
		if(mustCreate)
			createGround();
		
		box2dTimeStep(Gdx.graphics.getDeltaTime());
	}

	public void switchGround(List<PolygonBox2DShape> rs) {
		mustCreate = true;
		List<float[]> verts = new ArrayList<float[]>();
		for (int i = 0; i < rs.size(); i++) {
			verts.add(rs.get(i).verticesToLoop());
		}
		GroundFixture grFix = new GroundFixture(verts);
		polyVerts.add(grFix);
	}
	
	protected void createGround() {
		BodyDef groundDef = new BodyDef();
		groundDef.type = BodyDef.BodyType.StaticBody;
		groundDef.position.set(0, 0);

		for (int i = 0; i < polyVerts.size(); i++) {
			Body nground = world.createBody(groundDef);
			UserData usrData = new UserData(UserData.GROUND);
			nground.setUserData(usrData);

			List<Fixture> fixtures = new ArrayList<Fixture>();
			for (int y = 0; y < this.polyVerts.get(i).getVerts().size(); y++) {
				if (this.polyVerts.get(i).getVerts().get(y).length >= 6) {
					ChainShape shape = new ChainShape();
					shape.createLoop(this.polyVerts.get(i).getVerts()
							.get(y));
					FixtureDef fixtureDef = new FixtureDef();
					fixtureDef.shape = shape;
					fixtureDef.density = 1;
					fixtureDef.friction = .8f;
					fixtures.add(nground.createFixture(fixtureDef));
				}
			}
			polyVerts.get(i).setFixtures(fixtures);
		}
		this.mustCreate = false;
		polyVerts.clear();
	}
	
	public Body createBall(int type, Vector2 position) {
		BodyDef defBall = new BodyDef();
		defBall.type = BodyDef.BodyType.DynamicBody;
		defBall.position.set(position);
		Body ball = world.createBody(defBall);
		ball.setUserData(new UserData(type));

		FixtureDef fixDefBall = new FixtureDef();
		fixDefBall.density = .25f;
		fixDefBall.restitution = .75f;
		CircleShape rond = new CircleShape();
		rond.setRadius(1);

		fixDefBall.shape = rond;
		ball.createFixture(fixDefBall);
		rond.dispose();
		
		return ball;
	}
	
	private void box2dTimeStep(float deltaTime) {
		float delta = Math.min(deltaTime, 0.25f);
		accu += delta;
		while (accu >= TIME_STEP) {
			world.step(TIME_STEP, speedIte, posIte);
			accu -= TIME_STEP;
		}
	}
}
