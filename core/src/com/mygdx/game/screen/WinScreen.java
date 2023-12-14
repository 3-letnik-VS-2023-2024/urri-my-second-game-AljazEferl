package com.mygdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.BingoBlitz;
import com.mygdx.game.assets.AssetDescriptors;
import com.mygdx.game.assets.RegionNames;
import com.mygdx.game.config.GameConfig;

public class WinScreen extends ScreenAdapter {

    private final BingoBlitz game;
    private final AssetManager assetManager;
    private Viewport viewport;
    private Viewport hudViewport;

    private Stage gameplayStage;
    private Stage hudStage;

    private Skin skin;
    private TextureAtlas gameplayAtlas;
    private Table table;
    private  String selectedCity;
    private int score;
    private String aiOrplayer;

    public WinScreen(BingoBlitz game, int score,String aiOrplayer) {
        this.game = game;
        this.assetManager = game.getAssetManager();
        this.score = score;
        this.aiOrplayer = aiOrplayer;

    }
    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        hudViewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);

        gameplayStage = new Stage(viewport, game.getBatch());
        hudStage = new Stage(hudViewport, game.getBatch());


        skin = assetManager.get(AssetDescriptors.UI_SKIN);
        gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY);
        createUI();

    }

    private void createUI(){
        table = new Table();
        table.defaults().padTop(100);
        table.defaults().padBottom(100);
        table.defaults().padLeft(200);
        table.defaults().padRight(200);

        TextButton button1 = new TextButton("Retry", skin);
        button1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
        });

        TextButton button2 = new TextButton("Exit", skin);
        button2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });
        Label gameOverLabel = new Label("",skin,"big");
        Label scoreLabel = new Label("SCORE: " + score, skin);
        TextureRegion background = gameplayAtlas.findRegion(RegionNames.GREY);
        table.setBackground(new TextureRegionDrawable(background));

        gameOverLabel.setText("COMPUTER HAS WON");
        if(aiOrplayer == "player"){
            gameOverLabel.setText("YOU HAVE WON");
            background = gameplayAtlas.findRegion(RegionNames.BEIGE);
            table.setBackground(new TextureRegionDrawable(background));
        }

        Table btnTable = new Table();
        btnTable.defaults().padLeft(70).padRight(70);

        btnTable.add(gameOverLabel).padBottom(20).row();
        btnTable.add(button1).padBottom(20).row();
        btnTable.add(button2).padBottom(20).row();
        btnTable.add(scoreLabel).padBottom(20).row();

        table.add(btnTable).center().padBottom(120).row();
        table.setFillParent(true);
        table.center();

        gameplayStage.addActor(table);
        Gdx.input.setInputProcessor(gameplayStage);
    }
    @Override
    public void render(float delta) {

        gameplayStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        gameplayStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Update the viewport when the screen size changes
        viewport.update(width, height, true);
        hudViewport.update(width, height, true);
    }


}
