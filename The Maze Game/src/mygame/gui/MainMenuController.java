/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.gui;

import com.jme3.app.state.AbstractAppState;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import mygame.Main;

/**
 * Controller for main menu screen
 * @author Thanapoom Rattanathumawat
 * @author Poonnanun Poonnopathum
 */
public class MainMenuController extends AbstractAppState implements ScreenController{

    private Nifty nifty;
    private Screen screen;
    private Main main;
    
    public MainMenuController(){
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
    
    public void setMain(Main main){
        this.main = main;
    }
    
    public void quitGame(){
        System.exit(1);
    }
    
    public void startGame(){
        main.gameInit();
    }
    
    public void option(){
        main.optionInit();
    }
}
