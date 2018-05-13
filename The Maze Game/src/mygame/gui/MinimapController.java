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
import mygame.Main;


/**
 * Controller for call map in game
 * @author Thanapoom Rattanathumawat
 * @author Poonnanun Poonnopathum
 */
public class MinimapController extends AbstractAppState implements ScreenController{

    private Nifty nifty;
    private Screen screen;
    private Main main;
    
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
    
    public void setMain(Main main) {
        this.main = main;
    }
    public void giveUp(){
        main.start = false;
        main.mainMenu(); 
    }
    
    
        
}
