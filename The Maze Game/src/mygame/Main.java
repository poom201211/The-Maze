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
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Random;
import mygame.gui.GameScreenController;
import mygame.gui.MainMenuController;
import mygame.gui.OptionController;

/**
 * Main class for logical code.
 * @author Thanapoom Rattanathumawat
 * @author Poonnanun Poonnopathum
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
    
    private GameScreenController gameScreenController;
    private MainMenuController mainMenuController;
    private OptionController optionController;
    private Nifty nifty;
    private NiftyJmeDisplay niftyDisplay;
    
    private boolean start = false;
    
    public static void main(String[] args) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int)screenSize.getWidth();
        int screenHeight = (int)screenSize.getHeight();
        Main app = new Main();
        
        app.setShowSettings(false);
        AppSettings newSettings = new AppSettings(true);
        newSettings.put("Width", screenWidth);
        newSettings.put("Height", screenHeight);
        newSettings.setFullscreen(true);
        newSettings.put("Title", "The Maze");
        newSettings.put("VSync", true);
        newSettings.put("Samples", 8); // Anti-Aliasing

        app.setSettings(newSettings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        nifty = niftyDisplay.getNifty();
        nifty.addXml("Interface/MainMenu.xml");
        nifty.addXml("Interface/GameScreen.xml");
        nifty.addXml("Interface/OptionScreen.xml");
        nifty.gotoScreen("StartScreen");
        
        guiViewPort.addProcessor(niftyDisplay);

        flyCam.setEnabled(false);
        
        mainMenuController = (MainMenuController) nifty.getScreen("StartScreen").getScreenController();
        mainMenuController.setMain(this);
        gameScreenController = (GameScreenController) nifty.getScreen("GameScreen").getScreenController();
        gameScreenController.setMain(this);
        optionController = (OptionController) nifty.getScreen("OptionScreen").getScreenController();
        optionController.setMain(this);
        optionController.setSetting(this.settings);
    }
    
    public void gameInit(){

        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        inputManager.setCursorVisible(false);
        flyCam.setEnabled(true);
        
        keySet();
        
        // Generate randomized maze maps
        String[] mapLocation = new String[3];
        mapLocation[0] = "Models/Walls/maze walls.j3o";
        mapLocation[1] = "Models/Walls/maze walls2.j3o";
        mapLocation[2] = "Models/Walls/maze walls3.j3o";
        Random random = new Random();
        walls = assetManager.loadModel(mapLocation[random.nextInt(2)]);
       
        // Added class for object collision
        CollisionShape sceneWall = CollisionShapeFactory.createMeshShape(walls);
        landscapeWall = new RigidBodyControl(sceneWall, 0);
        walls.addControl(landscapeWall);
       
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setJumpSpeed(20);
        player.setFallSpeed(30);
        player.setGravity(new Vector3f(0,-30f,0));
        player.setPhysicsLocation(new Vector3f(10, 10, 0));
        
        flashlight = new PointLight(camDir, ColorRGBA.White, 20);
            
        // Initialize all nodes
        rootNode.attachChild(walls);
        rootNode.addLight(flashlight);
        bulletAppState.getPhysicsSpace().add(landscapeWall);
        bulletAppState.getPhysicsSpace().add(player);
        
        start = true;
        
        nifty.gotoScreen("GameScreen");
//        gameScreenController.createMinimap(this, walls);
    }
    
    public void optionInit(){
        nifty.gotoScreen("OptionScreen");
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        if(start){
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
