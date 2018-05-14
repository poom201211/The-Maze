package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
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
import mygame.gui.EndController;
import mygame.gui.GameScreenController;
import mygame.gui.MainMenuController;
import mygame.gui.MinimapController;
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
    private boolean left = false, right = false, up = false, down = false, run = false , tab = false;
    
    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();
    private PointLight flashlight;
    private float speed = 0.2f; 
    
    private GameScreenController gameScreenController;
    private MainMenuController mainMenuController;
    private OptionController optionController;
    private MinimapController miniMapController;
    private EndController endController;
    private Nifty nifty;
    private NiftyJmeDisplay niftyDisplay;
    
    private AudioNode bgMusic;
    private AudioNode clickSound;
    public boolean music = true, soundEffect = true;
    private String map;
    
    public boolean start = false;
    private boolean first = true;
    
    
    public static void main(String[] args) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int)screenSize.getWidth();
        int screenHeight = (int)screenSize.getHeight();
        Main app = new Main();
        
        
        
        app.setDisplayStatView(false);
        
        app.setShowSettings(false);
        AppSettings newSettings = new AppSettings(true);
        newSettings.put("Width", screenWidth);
        newSettings.put("Height", screenHeight);
//        newSettings.setFullscreen(true);
        newSettings.put("Title", "The Maze");
        newSettings.put("VSync", true);
        newSettings.put("Samples", 4); // Anti-Aliasing

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
        nifty.addXml("Interface/mapScreen.xml");
        nifty.addXml("Interface/EndScreen.xml");
        nifty.gotoScreen("StartScreen");
        
        
        guiViewPort.addProcessor(niftyDisplay);

        flyCam.setEnabled(false);
        
        audioInit();
        keySet();
        
        mainMenuController = (MainMenuController) nifty.getScreen("StartScreen").getScreenController();
        mainMenuController.setMain(this);
        gameScreenController = (GameScreenController) nifty.getScreen("GameScreen").getScreenController();
        gameScreenController.setMain(this);
        optionController = (OptionController) nifty.getScreen("OptionScreen").getScreenController();
        optionController.setMain(this);
        optionController.setSetting(this.settings);
        optionController.setMute(music , soundEffect);
        miniMapController = (MinimapController) nifty.getScreen("MiniMapScreen").getScreenController();
        miniMapController.setMain(this);
        endController = (EndController) nifty.getScreen("EndScreen").getScreenController();
        endController.setMain(this);
        
        
    }
    
    
    
    public void gameInit(){

        inputManager.setCursorVisible(false);
        flyCam.setEnabled(true);
        
        if(first){
            bulletAppState = new BulletAppState();
            stateManager.attach(bulletAppState);
        
        
            // Generate randomized maze maps
            String[] mapLocation = new String[3];
            mapLocation[0] = "Models/Walls/maze walls.j3o";
            mapLocation[1] = "Models/Walls/maze walls2.j3o";
            mapLocation[2] = "Models/Walls/maze walls3.j3o";
            String[] miniMap = new String[3];
            miniMap[0] = "Models/Walls/maze1.png";
            miniMap[1] = "Models/Walls/maze2.png";
            miniMap[2] = "Models/Walls/maze3.png";
            Random random = new Random();
            int randomMap = random.nextInt(2);
            walls = assetManager.loadModel(mapLocation[0]);
            map = miniMap[0];
       
       
        
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
            first = false;
        }
        
        player.setPhysicsLocation(new Vector3f(10, 10, 0));
        start = true;
        nifty.gotoScreen("GameScreen");
        
        

    }
    
    
    public void mainMenu(){
        if(!music)bgMusic.stop();
        else bgMusic.play();
        
        if(!soundEffect)clickSound.stop();
        else clickSound.play();
        nifty.gotoScreen("StartScreen");
        
    }
    
    public void optionInit(){
        optionController.setMute(music , soundEffect);
        nifty.gotoScreen("OptionScreen");
    }
    
    public void endGame(){
        start = false;
        flyCam.setEnabled(false);
        nifty.gotoScreen("EndScreen");
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
            if (run) {
                speed = 0.25f;  
            }else{
                speed = 0.2f;
            }
            player.setWalkDirection(walkDirection);
            cam.setLocation(player.getPhysicsLocation());
            if(cam.getLocation().getX() >= 120 || cam.getLocation().getX() <= -120) {
                endGame();
            }
            flashlight.setPosition(new Vector3f(cam.getLocation()));
            
            
        }
        if(!start){
            if(nifty.getCurrentScreen().getScreenId().equals("OptionScreen")){
                optionController.setEnableRes();
            }
        }
        
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    public void miniMapPop(){
        if(start){
           if(!tab) {
                nifty.gotoScreen("MiniMapScreen");
                inputManager.setCursorVisible(true);
                flyCam.setEnabled(false);
            }else{
                nifty.gotoScreen("GameScreen");
                inputManager.setCursorVisible(false);
                flyCam.setEnabled(true);
            } 
        }
        
        
    }
    
    public void keySet(){
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Run", new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addMapping("Click", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("Tab", new KeyTrigger(KeyInput.KEY_TAB));
        
        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        inputManager.addListener(this, "Run");
        inputManager.addListener(this, "Click");
        inputManager.addListener(this, "Tab");
    }
    
    public void audioInit(){
        bgMusic = new AudioNode(assetManager, "Sound/bgMusic.ogg", DataType.Stream);
        bgMusic.setLooping(true); 
        bgMusic.setPositional(false);
        bgMusic.setVolume(1);
        rootNode.attachChild(bgMusic);
        if(music) bgMusic.play();
        
        clickSound = new AudioNode(assetManager, "Sound/click.ogg", DataType.Stream);
        clickSound.setLooping(false); 
        clickSound.setPositional(false);
        clickSound.setVolume(3);
        rootNode.attachChild(clickSound);
    }
    
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        switch(name){
            case "Left": left = isPressed; break;
            
            case "Right": right = isPressed; break;
            
            case "Up": up = isPressed; break;
            
            case "Down": down = isPressed; break;
            
            case "Run" : run = isPressed; break;
            
            case "Click": if(soundEffect)clickSound.play(); break;
            
            case "Tab" : miniMapPop(); tab = isPressed; break;
            
            default: break;
            
        }
    }
}
