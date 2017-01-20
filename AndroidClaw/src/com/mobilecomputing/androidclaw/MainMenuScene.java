package com.mobilecomputing.androidclaw;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;
import org.andengine.engine.camera.Camera;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.mobilecomputing.androidclaw.BaseScene;
import com.mobilecomputing.androidclaw.SceneManager.SceneType;

//Willian Hua and Shotaro Takada
//CSC 470 - Mobile Computing
//Project: Catcheroid
//Filename: MainMenuScene.java
//Description: Main Menu Scene controller

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener
{
	public MainMenuScene(int x) {
		super(x);
	}

	private int screenWidth;
	private int screenHeight;

	// BUTTON ELEMENTS ****************************************
	//
	// ADD/CHANGE MENU ELEMENTS HERE
	private MenuScene menuChildScene;
	private final int MENU_PLAY = 0;
	private final int MENU_TIME = 1;
	
	// Music Resources
	private Music music;
	private Music coin;
	
	@Override
	public void createScene(int x)
	{
		// Load Menu and background upon scene creation
	     createBackground();
	     createMenuChildScene();
	}

	@Override
	public void onBackKeyPressed()
	{
	    System.exit(0);
	}

	@Override
	public SceneType getSceneType()
	{
	    return SceneType.SCENE_MENU;
	}

	@Override
	public void disposeScene()
	{
		
	}
	
	private void createBackground()
	{
    	DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
    	screenHeight = metrics.heightPixels;
    	screenWidth = metrics.widthPixels;
		
    	// Set background
    	Sprite sprite = new Sprite(0, 0, resourcesManager.menu_background_region, vbom)	    {
	        @Override
	        protected void preDraw(GLState pGLState, Camera pCamera) 
	        {
	            super.preDraw(pGLState, pCamera);
	            pGLState.enableDither();
	        }
	    };

    	sprite.setSize(screenWidth, screenHeight);

	    attachChild(sprite);
	    
	    // Attempt to play music
	    try
	    {
	        music = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity,"mfx/MainMen(Baby_Elephant_Walk).ogg");
	        music.play();
	        
	        coin = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity,"mfx/smw_coin.ogg");   
	    }
	    catch (IOException e)
	    {
	        e.printStackTrace();
	    }
	}

    
	// BUTTON ELEMENTS ****************************************
	//
	// DESCRIBE HOW THEY LOOK HERE
	private void createMenuChildScene()
	{  
	    menuChildScene = new MenuScene(camera);
	    menuChildScene.setPosition(0, 0);
	    
	    // Create classic button
	    final IMenuItem playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY, resourcesManager.play_region, vbom), 1.2f, 1);	
	    playMenuItem.setPosition(playMenuItem.getX(), playMenuItem.getY() + 230);
	    
	    //Create Time Trial Button
	    final IMenuItem timeMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_TIME, resourcesManager.time_region, vbom), 1.2f, 1);	
	    timeMenuItem.setPosition(timeMenuItem.getX(), timeMenuItem.getY() + 1000); //doesnt affect?
	    
	    // Set Button on click listener to handle play button request
	    menuChildScene.addMenuItem(playMenuItem);
	    menuChildScene.addMenuItem(timeMenuItem);
	    menuChildScene.buildAnimations();
	    menuChildScene.setBackgroundEnabled(false);
	    menuChildScene.setOnMenuItemClickListener(this);
	    
	    // Display Menu elements
	    setChildScene(menuChildScene);
	}

	// BUTTON ELEMENTS ****************************************
	//
	// DESCRIBE HOW THEY WORK HERE
	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY)
	{
	    switch(pMenuItem.getID())
	    {
	        case MENU_PLAY:
	            //Load Game Scene when play button is pressed
	            SceneManager.getInstance().loadGameScene(engine, 1);
	            music.stop();
	            coin.play();
	            return true;
	        case MENU_TIME:
	        	//LOad game scene when time trial button is pressed
	        	SceneManager.getInstance().loadGameScene(engine, 999);
	        	music.stop();
	        	coin.play();
	        	return true;
	        	
	        default:
	            return false;
	    }
	}

}
