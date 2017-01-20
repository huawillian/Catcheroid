package com.mobilecomputing.androidclaw;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.mobilecomputing.androidclaw.SceneManager.SceneType;

//Willian Hua and Shotaro Takada
//CSC 470 - Mobile Computing
//Project: Catcheroid
//Filename: LoadingScene.java
//Description: Loading Scene controller

public class LoadingScene extends BaseScene
{
	public LoadingScene(int x) {
		super(x);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createScene(int x)
	{
    	DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
    	int screenHeight = metrics.heightPixels;
    	int screenWidth = metrics.widthPixels;
    	
		Text text = new Text(screenWidth/3, screenHeight/2-50, resourcesManager.font, "Loading...", new TextOptions(HorizontalAlign.CENTER) , vbom);
		text.setScale(2.0f);
		text.setColor(Color.WHITE);
		
	    setBackground(new Background(Color.BLACK));
	    attachChild(text);
	}

    @Override
    public void onBackKeyPressed()
    {
        return;
    }

    @Override
    public SceneType getSceneType()
    {
        return SceneType.SCENE_LOADING;
    }

    @Override
    public void disposeScene()
    {

    }
}
