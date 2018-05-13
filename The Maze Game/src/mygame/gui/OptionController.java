/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.gui;

import com.jme3.app.state.AbstractAppState;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import mygame.Main;

/**
 * Controller for option screen
 * @author Thanapoom Rattanathumawat
 * @author Poonnanun Poonnopathum
 */
public class OptionController extends AbstractAppState implements ScreenController {
    
    private Nifty nifty;
    private Screen screen;
    private Main main;
    private AppSettings setting;
    private CheckBox music, soundEffect, vSync, fullScreen ;
    private DropDown resolution, anti;
    private List<String> resolutionList = new ArrayList<>();
    private List<Integer> width = new ArrayList<>();
    private List<Integer> height = new ArrayList<>();
    private String[] antiList = {"1x", "2x", "4x", "8x", "16x"};
    private int screenWidth;
    private int screenHeight;
    private boolean isFirst = false;
    private boolean musicIsPlay ,soundEffectIsPlay; 
    
    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen; 
    }
    
    @Override
    public void onStartScreen() {
        
        if(!isFirst){
            setResolutionList();
            music = screen.findNiftyControl("music", CheckBox.class);
            soundEffect = screen.findNiftyControl("soundEffect", CheckBox.class);
            vSync = screen.findNiftyControl("vSync", CheckBox.class);
            fullScreen = screen.findNiftyControl("fullScreen", CheckBox.class);
        
            resolution = screen.findNiftyControl("resolution", DropDown.class);
            anti = screen.findNiftyControl("anti", DropDown.class);
       
            resolution.addAllItems(resolutionList);
            for (String a : antiList) {
                anti.addItem(a);
            }
            isFirst = true;
        }
        boxCheck();
        anti.selectItem(setting.getSamples()+"x");
        
    }
    
    @Override
    public void onEndScreen() {
        
    }
    
    public void setEnableRes(){
        if(fullScreen.isChecked()){
            resolution.setEnabled(false);
            fullScreenResolutionSet();
        }else{
            resolution.setEnabled(true);
        }
    }
    
    public void setMain(Main main){
        this.main = main;
    }
    
    public void setSetting(AppSettings setting){
        this.setting = setting;
        
    }
    
    public void setMute(boolean music, boolean soundEffect){
        this.musicIsPlay = music;
        this.soundEffectIsPlay = soundEffect;
    }
    
    public void applySetting(){
        AppSettings newSettings = (AppSettings) setting.clone();
        String[] temp = resolution.getSelection().toString().trim().split("x");
        newSettings.put("VSync", vSync.isChecked());
        newSettings.setFullscreen(fullScreen.isChecked());
        newSettings.put("Width", Integer.parseInt(temp[0].trim()));
        newSettings.put("Height", Integer.parseInt(temp[1].trim()));
        String[] antiA = anti.getSelection().toString().trim().split("x");
        newSettings.put("Samples", Integer.parseInt(antiA[0]));
        main.music = music.isChecked();
        main.soundEffect = soundEffect.isChecked();
        main.setSettings(newSettings);
        main.restart();
        main.mainMenu();
    }
    
    public void cancelSetting(){
        main.mainMenu();
    }
    
    private void setResolutionList(){
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = (int)screenSize.getWidth();
        screenHeight = (int)screenSize.getHeight();
        
        height.add(480);
        height.add(600);
        height.add(768);
        height.add(1024);
        height.add(768);
        height.add(900);
        height.add(1080);
        width.add(640);
        width.add(800);
        width.add(1024);
        width.add(1280);
        width.add(1366);
        width.add(1600);
        width.add(1920);
        
        for(int a = 0; a<width.size() && width.get(a) <= screenWidth && height.get(a) <= screenHeight ; a++){
            resolutionList.add(width.get(a) + " x " + height.get(a));
        }
    }
    
    private void fullScreenResolutionSet(){
        String res = screenWidth + " x " + screenHeight;
            boolean check = false;
            for(int a = 0; a < resolutionList.size(); a++){
                if(resolutionList.get(a).equalsIgnoreCase(res)){
                    
                    resolution.selectItem(res);
                    check = true;
                    break;
                }
            }
            if(check != true){
                
                resolution.addItem(res);
                resolution.selectItem(res);
            }
    }
    
    private void boxCheck(){
        if(musicIsPlay) music.check();
        if(soundEffectIsPlay) soundEffect.check();
        if(setting.isVSync()) vSync.check();
        if(setting.isFullscreen()){
            fullScreen.check();
            setEnableRes();
            fullScreenResolutionSet();
        }else{
            String res = "";
            for(int a = width.size()-1 ; a>=0 ; a--){
                if(setting.getWidth() == width.get(a)){
                    res += setting.getWidth();
                    res += " x ";
                    break;
                }
            }
            for(int a = height.size()-1 ; a>=0 ; a--){
                if(setting.getHeight() >= height.get(a)){
                    res += setting.getHeight();
                    break;
                }
            }
            
            resolution.selectItem(res);
        }
    }
}
