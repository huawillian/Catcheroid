package com.mobilecomputing.androidclaw;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

//Willian Hua and Shotaro Takada
//CSC 470 - Mobile Computing
//Project: Catcheroid
//Filename: SceneManager.java
//Description: Helper Class, to manage the scenes for the game engine. Referenced during different scenes to change, load, exit scenes.

public class SceneManager
{
    //---------------------------------------------
    // SCENES
    //---------------------------------------------
    
    private BaseScene splashScene;
    private BaseScene menuScene;
    private BaseScene gameScene;
    private BaseScene loadingScene;
    
    //---------------------------------------------
    // VARIABLES
    //---------------------------------------------
    
    private static final SceneManager INSTANCE = new SceneManager();
    private SceneType currentSceneType = SceneType.SCENE_SPLASH;  
    private BaseScene currentScene;
    private Engine engine = ResourcesManager.getInstance().engine;
    
    public enum SceneType
    {
        SCENE_SPLASH,
        SCENE_MENU,
        SCENE_GAME,
        SCENE_LOADING,
    }
    
    //---------------------------------------------
    // CLASS LOGIC
    //---------------------------------------------
    
    // Scene Setter methods
    public void setScene(BaseScene scene)
    {
        engine.setScene(scene);
        currentScene = scene;
        currentSceneType = scene.getSceneType();
    }
    
    public void setScene(SceneType sceneType)
    {
        switch (sceneType)
        {
            case SCENE_MENU:
                setScene(menuScene);
                break;
            case SCENE_GAME:
                setScene(gameScene);
                break;
            case SCENE_SPLASH:
                setScene(splashScene);
                break;
            case SCENE_LOADING:
                setScene(loadingScene);
                break;
            default:
                break;
        }
    }
    
    // Scene Creater methods
    public void createSplashScene(OnCreateSceneCallback pOnCreateSceneCallback)
    {
    	int x = 0;
        ResourcesManager.getInstance().loadSplashScreen();
        splashScene = new SplashScene(x);
        currentScene = splashScene;
        pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
    }
    
    public void createMenuScene()
    {
    	int x = 0;
        ResourcesManager.getInstance().loadMenuResources();
        menuScene = new MainMenuScene(x);
        loadingScene = new LoadingScene(x);
        SceneManager.getInstance().setScene(menuScene);

        // Dispose splashScene after creating and loading menu scene
        ResourcesManager.getInstance().unloadSplashScreen();
        splashScene.disposeScene();
        splashScene = null;
    }
    
    // Scene Loader methods
    public void loadGameScene(final Engine mEngine, final int x)
    {
        setScene(loadingScene);
        ResourcesManager.getInstance().unloadMenuTextures();
                
        // Load game scene after 0.5f of loading resources for the game
        mEngine.registerUpdateHandler(new TimerHandler(0.5f, new ITimerCallback() 
        {
            public void onTimePassed(final TimerHandler pTimerHandler) 
            {            	
                mEngine.unregisterUpdateHandler(pTimerHandler);
                ResourcesManager.getInstance().loadGameResources();
                gameScene = new GameScene(x);
                setScene(gameScene);
                
            }
        }));
    }
    
    // To be called when back button is pressed during Game Scene
    public void loadMenuScene(final Engine mEngine)
    {
        setScene(loadingScene);
        gameScene.disposeScene();
        ResourcesManager.getInstance().unloadGameTextures();
        
        // Load menu scene after half a second.
        mEngine.registerUpdateHandler(new TimerHandler(0.5f, new ITimerCallback() 
        {
            public void onTimePassed(final TimerHandler pTimerHandler) 
            {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                ResourcesManager.getInstance().loadMenuTextures();
                setScene(menuScene);
            }
        }));
    }
    
    
    //---------------------------------------------
    // GETTERS AND SETTERS
    //---------------------------------------------
    
    public static SceneManager getInstance()
    {
        return INSTANCE;
    }
    
    public SceneType getCurrentSceneType()
    {
        return currentSceneType;
    }
    
    public BaseScene getCurrentScene()
    {
        return currentScene;
    }

}