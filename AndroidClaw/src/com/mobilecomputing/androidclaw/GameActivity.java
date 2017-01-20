package com.mobilecomputing.androidclaw;

import java.io.IOException;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.KeyEvent;

// Willian Hua and Shotaro Takada
// CSC 470 - Mobile Computing
// Project: Catcheroid
// Filename: GameActivity.java
// Description: Base Activity as described in AndroidManifest.xml. Will initiate SceneManager, ResourcesManager, and load the Splash scene.

public class GameActivity extends BaseGameActivity
{
	// Setup ResourcesManager and Camera values to be referenced by the scenes
	private ResourcesManager resourcesManager;
    private Camera camera;
    
    // Used to determine the pixel sizes
    private int screenWidth;
	private int screenHeight;
	
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException
	{
		// Get reference to ResourcesManager
	    ResourcesManager.prepareManager(mEngine, this, (BoundCamera)camera, getVertexBufferObjectManager());
	    resourcesManager = ResourcesManager.getInstance();
	    pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws IOException
	{
		// Get reference to SceneManager and load Splash scene
	    SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);
	}

	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException
	{
		// When this scene, go to menu scene in 2 seconds
	    mEngine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() 
	    {
	        public void onTimePassed(final TimerHandler pTimerHandler) 
	        {
	            mEngine.unregisterUpdateHandler(pTimerHandler);
	            SceneManager.getInstance().createMenuScene();
	        }
	    }));
	    pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
    
    @Override
    public Engine onCreateEngine(EngineOptions pEngineOptions) 
    {
    	// Setup the GameEngine to update 60 fps
        return new LimitedFPSEngine(pEngineOptions, 60);
    }
    
    public EngineOptions onCreateEngineOptions()
    {
    	// Set screen dimensions
    	DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
    	screenHeight = metrics.heightPixels;
    	screenWidth = metrics.widthPixels;
    	
    	// Set camera bounds and game engine to cover entire screen
        camera = new BoundCamera(0, 0, screenWidth, screenHeight);
        EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(screenWidth, screenHeight), this.camera);
        engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
        engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
        return engineOptions;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    {  
    	// Whenever back key is pressed, call the current scene's onBackKeyPressed method...
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
        }
        return false; 
    }
    
}