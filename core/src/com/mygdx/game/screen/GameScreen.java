package com.mygdx.game.screen;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.CellActor;
import com.mygdx.game.CellState;
import com.mygdx.game.BingoBlitz;
import com.mygdx.game.assets.AssetDescriptors;
import com.mygdx.game.assets.RegionNames;
import com.mygdx.game.common.GameManager;
import com.mygdx.game.config.GameConfig;

public class GameScreen extends ScreenAdapter {
    private static final Logger log = new Logger(GameScreen.class.getSimpleName(), Logger.DEBUG);

    private final BingoBlitz game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private Viewport hudViewport;

    private Stage gameplayStage;
    private Stage hudStage;

    private Skin skin;
    private TextureAtlas gameplayAtlas;
    private Music music;


    public GameScreen(BingoBlitz game) {
        this.game = game;
        assetManager = game.getAssetManager();
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        hudViewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);

        gameplayStage = new Stage(viewport, game.getBatch());
        hudStage = new Stage(hudViewport, game.getBatch());


        skin = assetManager.get(AssetDescriptors.UI_SKIN);
        gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY);

        music = assetManager.get(AssetDescriptors.PIRATES);
        if(GameManager.INSTANCE.isMusicEnabled()) {
            music.setLooping(true);
            music.setVolume(0.5f);
            music.play();
        }
        else {
            music.stop();
        }
        Table backgroundTable = new Table();
        backgroundTable.setFillParent(true);

        TextureRegion backgroundRegion = gameplayAtlas.findRegion(RegionNames.MAP);
        backgroundTable.setBackground(new TextureRegionDrawable(backgroundRegion));


        gameplayStage.addActor(backgroundTable);

        TextButton btnPariz = new TextButton("Pariz", skin);

        btnPariz.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Handle button click here
                game.setScreen(new GameScreenMain(game,"Pariz"));
                log.debug("Pariz button clicked");
            }
        });

        TextButton btnNewYork = new TextButton("New York", skin);

        btnNewYork.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreenMain(game,"New York"));
                log.debug("New York button clicked");
            }
        });
        TextButton btnMadrid = new TextButton("Madrid", skin);

        btnMadrid.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreenMain(game,"Madrid"));
                log.debug("Madridbutton clicked");
            }
        });

        Actor backBtn = createBackButton();
        Table buttonTable = new Table();
        buttonTable.setFillParent(true);
        buttonTable.center();
        buttonTable.add(btnPariz).padBottom(40f);
        buttonTable.row();
        buttonTable.add(btnNewYork).padBottom(40f);
        buttonTable.row();
        buttonTable.add(btnMadrid).padBottom(40f);
        buttonTable.row();
        buttonTable.add(backBtn).bottom().right().pad(20);




        hudStage.addActor(buttonTable);
        Gdx.input.setInputProcessor(new InputMultiplexer(gameplayStage, hudStage));
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hudViewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(195 / 255f, 195 / 255f, 195 / 255f, 0f);

        // update
        gameplayStage.act(delta);
        hudStage.act(delta);

        // draw
        gameplayStage.draw();
        hudStage.draw();
    }

    @Override
    public void hide() {

        dispose();
    }
    private Actor createBackButton() {
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                music.stop();
                game.setScreen(new MenuScreen(game));
            }
        });

        Table table = new Table();
        table.bottom().right();

        table.add(backButton).pad(20);

        return table;
    }


    @Override
    public void dispose() {
        gameplayStage.dispose();
        hudStage.dispose();
    }


}
