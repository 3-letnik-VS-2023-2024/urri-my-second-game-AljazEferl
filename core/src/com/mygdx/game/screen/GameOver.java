package com.mygdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.BingoBlitz;
import com.mygdx.game.PlayerData;
import com.mygdx.game.assets.AssetDescriptors;
import com.mygdx.game.assets.RegionNames;
import com.mygdx.game.config.GameConfig;

public class GameOver extends ScreenAdapter {

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
    private static final String LEADERBOARD_JSON_FILE = "assets/leaderboard/leaderboard.json";
    public GameOver(BingoBlitz game, String selectedCity, int score) {
        this.game = game;
        this.assetManager = game.getAssetManager();
        this.selectedCity = selectedCity;
        this.score = score;

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
    private void createUI() {
        table = new Table();
        table.defaults().padTop(100);
        table.defaults().padBottom(100);
        table.defaults().padLeft(200);
        table.defaults().padRight(200);



        if (selectedCity.equals("Pariz")) {
            TextureRegion backgroundRegion = gameplayAtlas.findRegion(RegionNames.PARIZ);
            table.setBackground(new TextureRegionDrawable(backgroundRegion));
        } else if (selectedCity.equals("New York")) {
            TextureRegion backgroundRegion = gameplayAtlas.findRegion(RegionNames.NEW_YORK);
            table.setBackground(new TextureRegionDrawable(backgroundRegion));
        } else {
            TextureRegion backgroundRegion = gameplayAtlas.findRegion(RegionNames.MADRID);
            table.setBackground(new TextureRegionDrawable(backgroundRegion));
        }

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
                savePlayerScore();
                game.setScreen(new MenuScreen(game));

            }
        });
        Label gameOverLabel = new Label("GAME OVER", skin, "big");
        Label scoreLabel = new Label("SCORE: " + score, skin);
        Table btnTable = new Table();
        btnTable.defaults().padLeft(70).padRight(70);
        TextureRegion menuBackgroundRegion = gameplayAtlas.findRegion(RegionNames.GREY);
        btnTable.setBackground(new TextureRegionDrawable(menuBackgroundRegion));
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

    private void savePlayerScore() {
        Array<PlayerData> leaderboard = loadLeaderboard();

        if (leaderboard == null) {
            leaderboard = new Array<>();
        }

        PlayerData playerData = new PlayerData("Aljaz", score);
        leaderboard.add(playerData);

        saveLeaderboard(leaderboard);
    }


    private void saveLeaderboard(Array<PlayerData> leaderboard) {
        Json json = new Json();
        FileHandle file = Gdx.files.local(LEADERBOARD_JSON_FILE);

        try {
            String jsonStr = json.toJson(leaderboard);
            file.writeString(jsonStr, false);
            Gdx.app.log("SaveLeaderboard", "Leaderboard saved successfully!");
           System.out.println("SaveLeaderboardLeaderboard saved successfully!");
        } catch (Exception e) {
            Gdx.app.error("SaveLeaderboard", "Error saving leaderboard: " + e.getMessage());
        }
    }

    private Array<PlayerData> loadLeaderboard() {
        Json json = new Json();
        FileHandle file = Gdx.files.local(LEADERBOARD_JSON_FILE);

        try {
            if (file.exists()) {
                String jsonStr = file.readString();
                return json.fromJson(Array.class, PlayerData.class, jsonStr);
            } else {
                return new Array<>();
            }
        } catch (Exception e) {
            Gdx.app.error("LoadLeaderboard", "Error loading leaderboard: " + e.getMessage());
            System.out.println("LoadLeaderboard Error loading leaderboard ");
            return new Array<>();
        }
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
