package com.mygdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.BingoBlitz;
import com.mygdx.game.assets.AssetDescriptors;
import com.mygdx.game.assets.RegionNames;
import com.mygdx.game.config.GameConfig;

public class LeaderboardScreen extends ScreenAdapter {

    private final BingoBlitz game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private Stage stage;

    private Skin skin;
    private TextureAtlas gameplayAtlas;

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
        Label title = new Label("Leaderboard", skin,"big");

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.center();

        TextureRegion backgroundRegion = gameplayAtlas.findRegion(RegionNames.GREY);
        Drawable backgroundDrawable = new TextureRegionDrawable(backgroundRegion);
        rootTable.setBackground(backgroundDrawable);

        // Add title label at the top
        rootTable.row().top().padTop(10);
        rootTable.add(title).expandX().colspan(2).center();
        rootTable.row();

        Actor leaderboardTable = createLeaderboardTable();
        Actor backButtonTable = createBackButton();

        rootTable.add(leaderboardTable).expand().center().top().padTop(80);
        rootTable.row();
        rootTable.add(backButtonTable).bottom().right().pad(20);

        stage.addActor(rootTable);

        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
      //  ScreenUtils.clear(195 / 255f, 195 / 255f, 195 / 255f, 0f);

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

    private Actor createLeaderboardTable() {
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.defaults().pad(10);
       /* TextureRegion menuBackgroundRegion = gameplayAtlas.findRegion(RegionNames.GREY);
        table.setBackground(new TextureRegionDrawable(menuBackgroundRegion));*/


        for (int i = 1; i <= 10; i++) {
            Label nameLabel = new Label("Player " + i, skin);
            Label scoreLabel = new Label("Score: " + (10 - i), skin);
            table.add(nameLabel).left();
            table.add(scoreLabel).right();
            table.row();
        }


        ScrollPane scrollPane = new ScrollPane(table, skin);
        scrollPane.setFadeScrollBars(false);

        return scrollPane;
    }

    private Actor createBackButton() {
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        Table table = new Table();
        table.bottom().right();

        table.add(backButton).pad(20);

        return table;
    }

}
