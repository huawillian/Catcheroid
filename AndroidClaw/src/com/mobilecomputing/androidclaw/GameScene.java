package com.mobilecomputing.androidclaw;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.SAXUtils;
import org.andengine.util.color.Color;
import org.andengine.util.level.EntityLoader;
import org.andengine.util.level.constants.LevelConstants;
import org.andengine.util.level.simple.*;
import org.xml.sax.Attributes;

import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.DisplayMetrics;

import com.badlogic.gdx.math.Vector2;
import com.mobilecomputing.androidclaw.SceneManager.SceneType;

//Willian Hua and Shotaro Takada
//CSC 470 - Mobile Computing
//Project: Catcheroid
//Filename: GameScene.java
//Description: 	Everything Game Scene
//				Load level from xml file e.g. 1.lvl

// Use Screen Touches and Sensors (Accelerometer)
public class GameScene extends BaseScene implements SensorEventListener
{
	public GameScene(int x) {
		super(x);
	}

	// TAGS to be used in game logic
	private static final String TAG_ENTITY = "entity";
	private static final String TAG_ENTITY_ATTRIBUTE_X = "x";
	private static final String TAG_ENTITY_ATTRIBUTE_Y = "y";
	private static final String TAG_ENTITY_ATTRIBUTE_TYPE = "type";
	    
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PRIZE1= "prize1";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PRIZE2 = "prize2";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PRIZE3 = "prize3";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PRIZE4 = "prize4";
	
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BAR = "bar";
	
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BACKGROUND = "background";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_CLAW = "claw";
	
	// Accelerometer variables
	private SensorManager sensorManager;
    double ax = 0.0; // X accel
    double ay = 0.0; // Y accel
	
    // HUD Elements
	private Text winText;
	private boolean winDisplayed = false;
    private HUD gameHUD;
    private Text scoreText;
    private Text timeText;
    private Text levelText;
    private int score = 0;
    private boolean classic;
    private int level;
    
    // Game Elements
	private Sprite player;
    private PhysicsWorld physicsWorld;
    
    // Game States
	boolean finding = true;
	boolean holding = false;
	boolean dropping = false;
	boolean winning = false;
	boolean freezeFlag = true;
	
	// Claw/player initializing values
	int clawOrigX = 0;
	int clawOrigY = 0;
	int indexPrize = 0;
    
	// Keep track of all prizes created
	LinkedList<Sprite> prizes;
	
	//keep track of all bars created
	LinkedList<Sprite> bars;
	
	// Music Resources
	Music tetris;
	Music victory;
	Music coin;
	Music tube;
	Music levelup;
	
	// Screen Dimensions
	int screenHeight;
	int screenWidth;
		
    //---------------------------------------------
    // LOAD LEVEL METHODS - GAME LOGIC DEFINED HERE
    //---------------------------------------------
	
	private void loadLevel(int levelID)
	{
		//if game mode is classic or time
		if(levelID == 999)
			classic = false;
		else
			classic = true;
		level = levelID;
		
		winDisplayed = false;
		
        // Use Simple Level Loader to load levels from xml file
	    final SimpleLevelLoader levelLoader = new SimpleLevelLoader(vbom);
	    PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
	    
	    // Find <level> xml element
	    levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(LevelConstants.TAG_LEVEL)
	    {
	        public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException 
	        {
		        return GameScene.this;
	        }
	    });
	    
	    // Find <entity ...> element
	    levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(TAG_ENTITY)
	    {
	    	// Set up game depending on the entity's attributes
	    	// X, Y, TYPE
	        public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException
	        {
	            final Sprite levelObject;
	        	
	            final int x = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_X);
	            final int y = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_Y);
	            final String type = SAXUtils.getAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_TYPE);
	            	            
	            if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PRIZE1) || type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PRIZE2) || type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PRIZE3) || type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PRIZE4))
	            {
	            	ITextureRegion region = null;
	            	
	            	// Set the type of prize picture to use
			        if(type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PRIZE1))
			        {
			            region = resourcesManager.prize1_region;
			        }
	            	else
		            if(type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PRIZE2))
		            {
		            	region = resourcesManager.prize2_region;
		            } 
		            else
	            	if(type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PRIZE3))
	            	{
	            		region = resourcesManager.prize3_region;
	            	}
	            	else region = resourcesManager.prize4_region;
	            	
			        // Set location depending on screen Dimensions
	            	final int spawnX = x * (screenWidth - 100) / 100;
	            	final int spawnY = y * (screenHeight - 100) / 100;
	            	
	                levelObject = new Sprite(spawnX, spawnY, 100, 100, region, vbom)
	                {

            	        int origY = spawnY;
	                
            	        // Called 60 times a second
	            	    @Override
	            	    protected void onManagedUpdate(float pSecondsElapsed) 
	            	    {
	            	        super.onManagedUpdate(pSecondsElapsed);

	            	        // If holding state is true and the prize being held is this entity
	            	        if(holding && (indexPrize == prizes.indexOf(this)))
	            	        {
	            	        	// Move this with claw
	            	        	this.mX = player.getX();
	            	        	this.mY = (int)(player.getY() + player.getHeight()) - 25;
	            	        	
	            	        	// Change state when this prize has moved up enough
	            	        	if(this.mY < 150)
	            	        	{
	            	        		holding = false;
	            	        		winning = true;
	            	        		freezeFlag = true;
	            	        	}
	            	        	
	            	        }
	            	        
	            	        // While dropping and prize being dropped is this entity
	            	        if(dropping  && (indexPrize == prizes.indexOf(this)))
	            	        {
	            	        	// Constantly move down until time elapsed or return to original y position
	            	        	if(this.mY < origY)
	            	        		this.mY += 5;
	            	        }
	            	        
	            	        // While winning...
	            	        if(winning && freezeFlag && (indexPrize == prizes.indexOf(this)))
	            	        {
	            	        	// Move prize to prize collection place
	            	        	this.mX = player.getX();
	            	        	this.mY = (int)(player.getY() + player.getHeight()) - 25;
	            	        	
            	        		MoveModifier mod2=new MoveModifier(3.0f,this.mX, screenWidth/2 - 50, this.mY, this.mY);
            	        		this.registerEntityModifier(mod2);
	            	        }

	            	    }
	            	    
	                };
	                
	                levelObject.registerEntityModifier(new LoopEntityModifier(new ScaleModifier(1, 1, 1.3f)));
	                prizes.add(levelObject);

	            }
	            else if(type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BAR))
	            {
	            	// Set location depending on screen Dimensions
	            	final int spawnX = x * (screenWidth - 100) / 100;
	            	final int spawnY = y * (screenHeight - 100) / 100;
	            	
	            	levelObject = new Sprite(spawnX, spawnY, 300, 50, resourcesManager.bar_region, vbom);
		            bars.add(levelObject);
	            }
	            else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_CLAW))
	            {
	            	// Set claw's original position
	            	clawOrigY = -screenHeight + 150;
	            	clawOrigX = screenWidth/2 - 50;
	            	
	            	player = new Sprite(clawOrigX, clawOrigY, resourcesManager.player_region, vbom)
	                {    
	            		// Used to calculate timed functions
	                	float timeStamp = 0.0f;
	                	float time = 0.0f;
	                	
	                	//value used for time trial, 60 seconds for now
	                	float timeRemain = 60.0f;
	                	
            	        // Called 60 times a second
	            	    @Override
	            	    protected void onManagedUpdate(float pSecondsElapsed) 
	            	    {
	            	        super.onManagedUpdate(pSecondsElapsed);
	            	        time += pSecondsElapsed;
	            	        
	            	        // Set Time and Level (for classic only) text
	            	        if(classic)
	            	        {
	            	        	timeText.setText("Time: " + Integer.toString((int)time) + " seconds");
	            	        	levelText.setText("Level: " + Integer.toString(level));
	            	        }
	            	        else if(classic == false)
	            	        {
	            	        	timeRemain = timeRemain - pSecondsElapsed;
	            	        	timeText.setText("Time Remaining: " + Integer.toString((int)timeRemain) + " seconds");
	            	        	levelText.setText("Level: Time Trial");
	            	        }
	            	        
	            	        // Set claw bounds
	            	        if(this.mX < 0) this.mX = 0;
	            	        if(this.mX > screenWidth - this.mWidth) this.mX = screenWidth - this.mWidth;
	            	        if(this.mY < -this.mHeight) this.mY = -this.mHeight;
	            	        if(this.mY > screenHeight - this.mHeight) this.mY = screenHeight - this.mHeight;
     
	            	        
	            	        if(finding)
	            	        {
	            	        	DecimalFormat df = new DecimalFormat("#.####");      
	            	        	final double accelX = Double.valueOf(df.format(ax));
	            	        	final double accelY = Double.valueOf(df.format(ay));
	            	        	
		            	        this.mX += -accelY;
		            	        this.mY += -accelX;	            	        
		            	        
		            	        // WIll change state based on collision by prize entity
		            	        for(Sprite prize : prizes)
		            	        {
		            	        	try
		            	        	{
										if(isCollides(this, prize) && Math.abs(accelY) <= 3)
										{
											indexPrize = prizes.indexOf(prize);
											finding = false;
											holding = true;
										}
									} 
		            	        	catch (Exception e) 
									{
										e.printStackTrace();
									}
		            	        }
		            	        
	            	        }
	            	        
	            	        // While holding
	            	        if(holding)
	            	        {
	            	        	// Claw will continue to move up
	            	        	this.mY += -2;

	            	        	DecimalFormat df = new DecimalFormat("#.####");      
	            	        	final double accelY = Double.valueOf(df.format(ay));
	            	        	final double accelX = Double.valueOf(df.format(ax));

	            	        	// Move up after if acceleration is going up
	            	        	if(-accelX < 0)
	            	        	{	
	            	        		this.mY += -accelX;
	            	        	}
	            	        	
	            	        	// Check for quick movements and change state if past a threshold
	            	        	if(Math.abs(accelY)*8 <= 10)
	            	        	{
			            	        this.mX += -accelY*2;
	            	        		//this.mX += -accelX;
	            	        	}
	            	        	else
	            	        	{
	            	        		holding = false;
	            	        		dropping = true;
	            	        		freezeFlag = true;
	            	        	}	    
	            	        	
	            	        	//check for hitting the bar
	            	        	for(Sprite bar : bars)
		            	        {
		            	        	try
		            	        	{
										if(isCollidesBar(this, bar))
										{
											holding = false;
			            	        		dropping = true;
			            	        		freezeFlag = true;
										}
									} 
		            	        	catch (Exception e) 
									{
										e.printStackTrace();
									}
		            	        }
	            	        	
	            	        }
	   
	            	        // While dropping
	            	        if(dropping)
	            	        {
	            	        	// Play dropping sound
	            	        	if(freezeFlag)
	            	        	{
	            	        		freezeFlag = false;
	            	        		timeStamp = time;
	            	        		tube.play();
	            	        	}
	            	        	
	            	        	// Drop for a second
	            	        	if((time - timeStamp) >= 1)
	            	        	{
	            	        		dropping = false;
	            	        		finding = true;
	            	        	}
	            	        }
	            	        
	            	        // While winning
	            	        if(winning)
	            	        {
	            	        	// Move claw to original position
	            	        	if(freezeFlag)
	            	        	{
	            	        		freezeFlag = false;
	            	        		timeStamp = time;

	            	        		MoveModifier mod1=new MoveModifier(3.0f,this.mX,screenWidth/2 - 50,this.mY, -screenHeight + 150);
	            	        		this.registerEntityModifier(mod1);
	            	        	}
	            	        	
	            	        	// After 3 seconds, get rid of the prize collected
	            	        	if((time - timeStamp) >= 3)
	            	        	{
	            	        		winning = false;
	            	        		finding = true;
	            	        		
	            	        		prizes.get(indexPrize).setVisible(false);
	            	        		prizes.get(indexPrize).setIgnoreUpdate(true);
	            	        		prizes.get(indexPrize).dispose();
	            	        		prizes.remove(indexPrize);
	            	        		
	            	        	    addToScore(15);;
	            	        	}
	            	        	
	            	        }
	            	        if(classic == false)
	            	        {
	            	        	if(winDisplayed == false && ((int)timeRemain == 0 || prizes.isEmpty()))
	            	        	{
	            	        		winDisplayed = true;
	            	            	winText = new Text(screenWidth/3, screenHeight/2.5f, resourcesManager.font, "Time's Up!\nYour score is " + score + "!", new TextOptions(HorizontalAlign.CENTER), vbom);
	            	            	winText.setScale(2.0f);
	            	            	gameHUD.attachChild(winText);
	            	            
	            		        	tetris.stop();
	            		        	victory.play();
	            		        
	            		        	// Freeze player entity
	            	            	this.setVisible(false);
	            	            	this.setIgnoreUpdate(true);
	            	            	this.dispose();
	            	        	}
	            	        }
	            	        
	            	        else if(classic == true)
	            	        {
	            	        // When prizes are empty for the first time, display win elements
		            	        if(winDisplayed == false && prizes.isEmpty())
		            	        {
		            	        	//number is max number of levels right now
		            	        	if(level < 3)
		            	        	{ 
		            	        		winDisplayed = true;
		            	        		levelup.play();
		            	        		for(Sprite bar:bars)
		            	        		{
		            	        			bar.setVisible(false);
		            	        			bar.dispose();
		            	        		}
		            	        		
		            	        		bars.clear();
		            	        		
		            	        		level++;
		            	        		loadLevel(level);
		            	        	}
		            	        	//if its the last level then display the winning message at the end
		            	        	else if(level == 3)
		            	        	{
		            	        		winDisplayed = true;
			            	        	
			            	            winText = new Text(screenWidth/3, screenHeight/2.5f, resourcesManager.font, "Congratulations!\nYou Win!", new TextOptions(HorizontalAlign.CENTER), vbom);
			            	            winText.setScale(2.5f);
			            	            gameHUD.attachChild(winText);
			            	            
			            		        tetris.stop();
			            		        victory.play();
			            		        
			            		        // Freeze player entity
			            	            this.setVisible(false);
			            	            this.setIgnoreUpdate(true);
			            	            this.dispose();	
		            	        	}
		            	        }
	            	        }
	            	    }
	                };

	                levelObject = player;
	            }
	            else if(type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BACKGROUND))
	            {
	                levelObject = new Sprite(0, 0, screenWidth, screenHeight, resourcesManager.gameBackground_region, vbom);
	            }
	            else
	            {
	                throw new IllegalArgumentException();
	            }

	            levelObject.setCullingEnabled(true);

	            return levelObject;
	        }
	    });
	    
	    levelLoader.loadLevelFromAsset(activity.getAssets(), "level/" + levelID + ".lvl");

	    levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(LevelConstants.TAG_LEVEL)
	    		{
	    		    public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException 
	    		    {
	    		        return GameScene.this;
	    		    }
	    		});

	}

    //---------------------------------------------
    // SCENE METHODS
    //---------------------------------------------
	
	@Override
	public void createScene(int x)
	{
        // Set Screen Dimensions
    	DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
    	screenHeight = metrics.heightPixels;
    	screenWidth = metrics.widthPixels;
		
		// Set Sensors
		sensorManager=(SensorManager) activity.getSystemService(activity.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        // Instantiate Prizes
        prizes = new LinkedList<Sprite>();
        
        //instantiate bars
        bars = new LinkedList<Sprite>();
        
        // Attempt to set and play music
	    try
	    {
	        tetris = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "mfx/ClawGoingDown_Tetris_converted.ogg");
	        coin = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "mfx/smw_coin.ogg");   
	        victory = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "mfx/Win(FFVI_Victory_Fanfare).ogg");   
	        tube = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "mfx/smb_pipe.ogg");   	       
	        levelup = MusicFactory.createMusicFromAsset(engine.getMusicManager(),activity, "mfx/smb3_power-up.ogg");
	        tetris.setLooping(true);
	        tetris.play();
	    }
	    catch (IOException e)
	    {
	        e.printStackTrace();
	    }
    	
	    createBackground();
	    createHUD();
	    createPhysics();
	    loadLevel(x);
	}

    @Override
    public SceneType getSceneType()
    {
        return SceneType.SCENE_GAME;
    }
   
    // Set background as Black
    private void createBackground()
    {
        setBackground(new Background(Color.BLACK));
    }

    // Create world with 60 fps and gravity in y axis
    private void createPhysics()
    {
        physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 17), false); 
        registerUpdateHandler(physicsWorld);
    }
    
    // Stop everything when disposing scene
    @Override
    public void disposeScene()
    {
        camera.setHUD(null);
        camera.setCenter(screenWidth/2,screenHeight/2);
        camera.setChaseEntity(null);
        
        tube.stop();
        coin.stop();
        tetris.stop();
        victory.stop();
        levelup.stop();
    }
    
    // Load Menu Scene when back button is pressed
	@Override
	public void onBackKeyPressed()
	{
	    SceneManager.getInstance().loadMenuScene(engine);
	}
    
    //---------------------------------------------
    // HELPER METHODS
    //---------------------------------------------
	   
	// Created this method because AndEngine's Collision methods aren't working
	// Call this to figure out collision between two sprites
	private boolean isCollides(Sprite animSprite1 ,Sprite animSprite2) throws Exception
		{
			float diffX = Math.abs( (animSprite1.getX() +  animSprite1.getWidth()/2 )- 
			                (animSprite2.getX() + animSprite2.getWidth()/2 ));
			float diffY = Math.abs( (animSprite1.getY() +  animSprite1.getHeight()/2 )- 
			                (animSprite2.getY() + animSprite2.getHeight()/2 ));
	
			if(diffX < (animSprite1.getWidth()/2 + animSprite2.getWidth()/3) 
			              && diffY < (animSprite1.getHeight()/2 + animSprite2.getHeight()/3))
			{
	
			      return true;
			}
			   else
			     return false;
		}
	
	// collision method just to detect the claw part of the entire claw+wire
	private boolean isCollidesBar(Sprite animSprite1 ,Sprite animSpriteBar) throws Exception
	{
		float diffX = Math.abs( (animSprite1.getX() +  (animSprite1.getWidth())/2 )- 
		                (animSpriteBar.getX() + animSpriteBar.getWidth()/2 ));
		float diffY = Math.abs( ((animSprite1.getY()+1132) +  (animSprite1.getHeight()-1132)/2 )- 
		                (animSpriteBar.getY() + animSpriteBar.getHeight()/2 ));

		if(diffX < ((animSprite1.getWidth())/2 + animSpriteBar.getWidth()/3) 
		              && diffY < ((animSprite1.getHeight()-1132)/2 + animSpriteBar.getHeight()/3))
		{

		      return true;
		}
		   else
		     return false;
	}

    // helper method to add score
    // Play coin music everytime score is added
    private void addToScore(int i)
    {
        score += i;
        scoreText.setText("Score: " + score);
        coin.play();
    }
    
	
    //---------------------------------------------
    // ACCELEROMETER METHODS
    //---------------------------------------------   
    
    // Accelerometer sensor methods
    // Set ax and ay every update from the sensor
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1)
	{
		   
	}

	@Override
	public void onSensorChanged(SensorEvent event)
	{
	     if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
	     {
	            		ax=event.values[0];
	                    ay=event.values[1];
	     }
	}
    
    //---------------------------------------------
    // GUI/HUD METHODS
    //---------------------------------------------
    
    // Create HUD or GUI elements
    private void createHUD()
    {
        gameHUD = new HUD();
        
        // CREATE SCORE TEXT
        scoreText = new Text(20, 0, resourcesManager.font, "Score: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
        scoreText.setSkewCenter(0, 0);    
        scoreText.setText("Score: 0");
        
        // CREATE TIME TEXT
        timeText = new Text(20, 50, resourcesManager.font, "Time: 0123456789.0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
        timeText.setSkewCenter(0, 0);    
        timeText.setText("Time: 0.0");       
        
        // CREATE LEVEL TEXT
        levelText = new Text(20, 100, resourcesManager.font, "Level: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
        levelText.setSkewCenter(0,0);
        levelText.setText("Level: 0");
        
        gameHUD.attachChild(scoreText);
        gameHUD.attachChild(timeText);
        gameHUD.attachChild(levelText);
        
        camera.setHUD(gameHUD);
    }
}
