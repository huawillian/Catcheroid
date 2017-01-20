package com.mobilecomputing.androidclaw;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.mobilecomputing.androidclaw.BaseScene;
import com.mobilecomputing.androidclaw.SceneManager.SceneType;

//Willian Hua and Shotaro Takada
//CSC 470 - Mobile Computing
//Project: AndroidCatcher
//Filename: SplashScene.java
//Description: Splash Scene controller

public class SplashScene extends BaseScene
{
	public SplashScene(int x) {
		super(x);
		// TODO Auto-generated constructor stub
	}

	private Sprite splash;

    @Override
    public void createScene(int x)
    {
    	DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
    	int screenHeight = metrics.heightPixels;
    	int screenWidth = metrics.widthPixels;
    	
    	// Set Sprite to the back of the renderer
    	splash = new Sprite(0, 0, resourcesManager.splash_region, vbom)
    	{
    	    @Override
    	    protected void preDraw(GLState pGLState, Camera pCamera) 
    	    {
    	       super.preDraw(pGLState, pCamera);
    	       pGLState.enableDither();
    	    }
    	};
    	        
    	// Set image properties
    	splash.setScale(1.0f);
    	splash.setPosition(0, 0);
    	splash.setSize(screenWidth, screenHeight);
    	attachChild(splash);
    }

    @Override
    public void onBackKeyPressed()
    {

    }

    @Override
    public void disposeScene()
    {
        splash.detachSelf();
        splash.dispose();
        this.detachSelf();
        this.dispose();
    }
    
    @Override
    public SceneType getSceneType()
    {
        return SceneType.SCENE_SPLASH;
    }
}