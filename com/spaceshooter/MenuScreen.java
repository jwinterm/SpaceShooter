package com.spaceshooter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;

public class MenuScreen implements Screen {
	private SpaceShooter game;

	// setup the dimensions of the menu buttons
	private static final float BUTTON_WIDTH = 500f;
	private static final float BUTTON_HEIGHT = 60f;
	private static final float BUTTON_SPACING = 10f;
	private Skin defaultSkin;
	private Skin gameSkin;
	private static Stage stage;

	private Texture bgtexture;
	private Sprite bgsprite;

	private Texture bgtexture2;
	private Sprite bgsprite2;
	private float scrollTimer = 0.0f;
	private SpriteBatch batch;
	
	private OrthographicCamera camera;
	
	private Player showPlayer;
	private static boolean hasAnswered;
	private static boolean hasLoaded;
	private TextButton startGameButton;
	private TextButton exitButton;
	private TextButton howToButton;
	private Label welcomeLabel;
	public static class MuteDialog extends Dialog {

		public MuteDialog(Skin skin) {
			super("Mute", skin);
			text("Would you like to mute the in-game sound?");
			button("Yes","mute");
			button("No","nomute");
		}
		protected void result(Object object) {
			if (object.toString().equals("mute"))
				SpaceShooter.muteSound();
			
			hasAnswered = true;
			
		}
		
	}
	public static class HowToDialog1 extends Dialog {
		private Skin skin;
		public HowToDialog1(Skin skin) {
			super("How to Play 1", skin);
			this.skin = skin;
			text("To play, use the touch screen to maneuver and fire your weapon.\nThe objective of the game is to survive against the enemies and reach a new high score.");
			button("Next","next");
			button("Exit","nomute");
		}
		protected void result(Object object) {
			
			if (object.toString().equals("next"))
				new HowToDialog2(skin).show(stage);
		}
		
	}
	public static class HowToDialog2 extends Dialog {

		public HowToDialog2(Skin skin) {
			super("How to Play 2", skin);
			text("As you progress, the enemies will spawn more frequently and become increasingly difficult.\nUse the power-ups to aid you.");
			button("Exit","exit");
		}
		protected void result(Object object) {
			
		}
		
	}
	public MenuScreen(final SpaceShooter game) {
		ResourceManager.loadAll();
		this.game = game;
		Gdx.input.setCursorCatched(false);
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		defaultSkin = new Skin((Gdx.files.internal("media/skinsandfonts/default/uiskin.json")));
		gameSkin = new Skin((Gdx.files.internal("media/skinsandfonts/game/uiskin.json")));
		
		hasAnswered = false;
		hasLoaded = false;
		MuteDialog mute = new MuteDialog(defaultSkin);
		mute.show(stage);
		
		final float buttonX = (Gdx.graphics.getWidth() - BUTTON_WIDTH) / 2;
		//float currentY = 280f;
		float currentY = Gdx.graphics.getHeight() / 1.5f;

		// label "welcome"
		welcomeLabel = new Label("SPACE SHOOTER", gameSkin);
		welcomeLabel
				.setX((Gdx.graphics.getWidth() - welcomeLabel.getWidth()) / 2);
		welcomeLabel.setY(currentY + 100);
		//stage.addActor(welcomeLabel);

		// button "start game"
		startGameButton = new TextButton("START GAME", gameSkin,
				"default");
		startGameButton.setX(buttonX);
		//startGameButton.pack();
		startGameButton.setY(currentY);
		startGameButton.setWidth(BUTTON_WIDTH);
		startGameButton.setHeight(BUTTON_HEIGHT);
		//stage.addActor(startGameButton);
		
		howToButton = new TextButton("HOW TO PLAY", gameSkin,
				"default");
		howToButton.setX(buttonX);
		howToButton.setY(currentY - startGameButton.getHeight() - BUTTON_SPACING);
		howToButton.setWidth(BUTTON_WIDTH);
		howToButton.setHeight(BUTTON_HEIGHT);
		
		exitButton = new TextButton("EXIT GAME", gameSkin,
				"default");
		exitButton.setX(buttonX);
		exitButton.setY(howToButton.getY() - howToButton.getHeight() - BUTTON_SPACING);
		exitButton.setWidth(BUTTON_WIDTH);
		exitButton.setHeight(BUTTON_HEIGHT);
		// Load the stars with alpha channel
		bgtexture = ResourceManager.getAssetManager().get(
				ResourceManager.PrimaryBG1, Texture.class);
		bgtexture.setWrap(Texture.TextureWrap.Repeat,
				Texture.TextureWrap.Repeat);

		bgtexture2 = ResourceManager.getAssetManager().get(
				ResourceManager.ParallaxBG, Texture.class);
		bgtexture2.setWrap(Texture.TextureWrap.Repeat,
				Texture.TextureWrap.Repeat);

		// first scroll
		bgsprite = new Sprite(bgtexture);
		bgsprite.setPosition(SpaceShooter.getLeftBound(),
				SpaceShooter.getBottomBound());
		// Load the stars with alpha channel

		// second scroll to make parallax
		bgsprite2 = new Sprite(bgtexture2);

		// bgsprite2.translate(getLeftBound()*2,getBottomBound());
		bgsprite2.setPosition(
				SpaceShooter.getLeftBound() - (bgsprite2.getWidth() / 4),
				SpaceShooter.getLeftBound());
		bgsprite2.setColor(bgsprite2.getColor().r, bgsprite2.getColor().g,
				bgsprite2.getColor().b, 125);
		
		batch = new SpriteBatch();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		
		showPlayer = new Player(0,SpaceShooter.getBottomBound()+ 200,false);
		showPlayer.hideHitbox();
		//stage.getRoot().setColor(1f,1f,1f,0f);
		//stage.getRoot().addAction(Actions.fadeIn(2f));
	}

	@Override
	public void render(float delta) {
		if (hasAnswered && !hasLoaded) {
			stage.addActor(welcomeLabel);
			stage.addActor(startGameButton);
			stage.addActor(howToButton);
			stage.addActor(exitButton);
			stage.getRoot().setColor(1f,1f,1f,0f);
			stage.getRoot().addAction(Actions.fadeIn(3f));
			startGameButton.addListener(new ClickListener() {
				public void clicked(InputEvent event, float x, float y) {
					game.setScreen(new LevelScreen(game));
				}
			});
			howToButton.addListener(new ClickListener() {
				public void clicked(InputEvent event, float x, float y) {
					new HowToDialog1(defaultSkin).show(stage);
				}
			});
			exitButton.addListener(new ClickListener() {
				public void clicked(InputEvent event, float x, float y) {
					Gdx.app.exit();
				}
			});
			hasLoaded = true;
		}
		
		if ((Gdx.input.isKeyPressed(Keys.BACK) || Gdx.input.isKeyPressed(Keys.ESCAPE))) {
			  Gdx.app.exit();
		}
		
		stage.act(Gdx.graphics.getDeltaTime());
		scrollTimer += (Gdx.graphics.getDeltaTime() / 5);

		if (scrollTimer > 2.0f)
			scrollTimer = 0.0f;

		bgsprite.setV(0.5f * scrollTimer);
		bgsprite.setV2(0.5f * scrollTimer - 1);

		bgsprite2.setV(2f * scrollTimer);
		bgsprite2.setV2(2f * scrollTimer - 1);
		
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		bgsprite.draw(batch);
		bgsprite2.draw(batch);
		showPlayer.draw(batch);
		batch.end();
		stage.draw();
		Table.drawDebug(stage);
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		dispose();
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		ResourceManager.loadAll();
	}

	@Override
	public void dispose() {
		stage.dispose();
		gameSkin.dispose();
		defaultSkin.dispose();
	}

}