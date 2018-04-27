package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import com.sun.prism.paint.Color;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication implements ActionListener{

    private BulletAppState bulletAppState;
    private Spatial walls;
    private RigidBodyControl landscapeWall;
    private CharacterControl player;
    private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false, up = false, down = false, run = false;
    
    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();
    private PointLight flashlight;
    private float speed = 0.2f;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        keySet();
        
        walls = assetManager.loadModel("Models/Walls/maze walls.j3o");
        CollisionShape sceneWall = CollisionShapeFactory.createMeshShape(walls);
        landscapeWall = new RigidBodyControl(sceneWall, 0);
        walls.addControl(landscapeWall);
        
        
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setJumpSpeed(20);
        player.setFallSpeed(30);
        player.setGravity(new Vector3f(0,-30f,0));
        player.setPhysicsLocation(new Vector3f(0, 10, 0));
        
        flashlight = new PointLight(camDir, ColorRGBA.White, 20);
        
       
        
//        Spatial monster = assetManager.loadModel("Models/Monster/MrHumpty.obj");
//        DirectionalLight sun = new DirectionalLight();
//        sun.setDirection(new Vector3f(-2f,-2f,-2f).normalizeLocal());
//        rootNode.addLight(sun);
       
//        rootNode.attachChild(monster);

        
        rootNode.attachChild(walls);
        rootNode.addLight(flashlight);
        bulletAppState.getPhysicsSpace().add(landscapeWall);
        bulletAppState.getPhysicsSpace().add(player);
    }

    @Override
    public void simpleUpdate(float tpf) {
        camDir.set(cam.getDirection()).multLocal(speed);
        camLeft.set(cam.getLeft()).multLocal(speed/2);
        walkDirection.set(0, 0, 0);
        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            walkDirection.addLocal(camDir);
        }
        if (down) {
            walkDirection.addLocal(camDir.negate());
        }
       
        
        player.setWalkDirection(walkDirection);
        cam.setLocation(player.getPhysicsLocation());
        flashlight.setPosition(new Vector3f(cam.getLocation()));
        
        
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    public void keySet(){
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Run", new KeyTrigger(KeyInput.KEY_LSHIFT));
        
        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        inputManager.addListener(this, "Jump");
        inputManager.addListener(this, "Run");
        
    }
    
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        switch(name){
            case "Left": left = isPressed; break;
            
            case "Right": right = isPressed; break;
            
            case "Up": up = isPressed; break;
            
            case "Down": down = isPressed; break;
            
            case "Jump": player.jump(new Vector3f(0,15f,0)); break;
            
            default: break;
            
        }
    }
}
