package com.mygdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
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
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;

import java.util.Comparator;


public class LeaderboardScreen extends ScreenAdapter {

    private static final String LEADERBOARD_JSON_FILE = "assets/leaderboard/leaderboard.json";
    private final BingoBlitz game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private Stage stage;

    private Skin skin;
    private Table leaderboardTable;
    private TextureAtlas gameplayAtlas;
    private TextButton backButton;

    public LeaderboardScreen(BingoBlitz game) {
        this.game = game;
        assetManager = game.getAssetManager();
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        stage = new Stage(viewport, game.getBatch());
        gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY);
        skin = assetManager.get(AssetDescriptors.UI_SKIN);

        Table buttonTable = new Table();
        buttonTable.setFillParent(true);
        buttonTable.top().right().padTop(10).padRight(10);
        buttonTable.add(backButton);

        stage.addActor(buttonTable);
        loadAndDisplayLeaderboard();

        Gdx.input.setInputProcessor(stage);

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

    private void loadAndDisplayLeaderboard() {
        Array<PlayerData> leaderboard = loadLeaderboard();
        initializeLeaderboard(leaderboard);
    }

    private void initializeLeaderboard(Array<PlayerData> leaderboard) {

        leaderboard.sort(new Comparator<PlayerData>() {
            @Override
            public int compare(PlayerData player1, PlayerData player2) {
                return Integer.compare(player2.getScore(), player1.getScore());
            }
        });

        leaderboardTable = new Table(skin);
        TextureRegion background = gameplayAtlas.findRegion(RegionNames.GREY);
        leaderboardTable.setBackground(new TextureRegionDrawable(background));
        leaderboardTable.center().top().padTop(50);

        Label titleLabel = new Label("LEADERBOARD", skin, "big");
        leaderboardTable.add(titleLabel).colspan(2).padBottom(20).row();

        leaderboardTable.add(new Label("Name", skin, "default")).padRight(50);
        leaderboardTable.add(new Label("Score", skin, "default")).padRight(50);
        leaderboardTable.row();

        for (PlayerData playerData : leaderboard) {
            leaderboardTable.add(new Label(playerData.getName(), skin, "default")).padRight(50);
            leaderboardTable.add(new Label(String.valueOf(playerData.getScore()), skin, "default")).padRight(50);
            leaderboardTable.row();
        }
        backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        leaderboardTable.row().padTop(20);
        leaderboardTable.add(backButton).colspan(2).padTop(20);

        ScrollPane scrollPane = new ScrollPane(leaderboardTable, skin);
        scrollPane.setFillParent(true);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        stage.addActor(scrollPane);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
