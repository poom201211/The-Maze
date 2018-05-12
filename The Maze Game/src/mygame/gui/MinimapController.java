/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.gui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Spatial;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import mygame.MinimapUtil;

/**
 *
 * @author poon_
 */
public class MinimapController extends AbstractAppState implements ScreenController{

    private Application app;
    private AppStateManager appStateManager;
    private Nifty nifty;
    private Screen screen;
    
    public MinimapController(){
        
    }
    
    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }

    @Override
    public void onStartScreen() {
        
    }

    @Override
    public void onEndScreen() {
        
    }
    
    @Override
    public void initialize(AppStateManager appStateManager, Application app) {
        this.app = app;
        this.appStateManager = appStateManager;
    }
        
    public void createMinimap(Spatial scene) {
        MinimapUtil.createMiniMap((SimpleApplication) app, scene, 0, 0);
        
        NiftyImage map = nifty.getRenderEngine().createImage(screen, "map", false);
        screen.findElementByName("minimap").getRenderer(ImageRenderer.class).setImage(map);
    }
        
}
