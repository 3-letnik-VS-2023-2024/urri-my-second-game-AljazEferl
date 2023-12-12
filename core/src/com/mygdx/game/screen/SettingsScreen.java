package com.mygdx.game.screen;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.BingoBlitz;
import com.mygdx.game.CellState;
import com.mygdx.game.GameDifficulty;
import com.mygdx.game.assets.AssetDescriptors;
import com.mygdx.game.assets.RegionNames;
import com.mygdx.game.common.GameManager;
import com.mygdx.game.config.GameConfig;

public class SettingsScreen extends ScreenAdapter {

    private final BingoBlitz game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private Stage stage;

    private ButtonGroup<CheckBox> checkBoxGroup;
    private CheckBox checkBoxX;
    private CheckBox checkBoxO;
    private CheckBox hard;

    public SettingsScreen(BingoBlitz game) {
        this.game = game;
        assetManager = game.getAssetManager();
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        stage = new Stage(viewport, game.getBatch());

        stage.addActor(createUi());
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 0f);

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

    private Actor createUi() {
        Table table = new Table();
        table.defaults().pad(20);

        Skin uiSkin = assetManager.get(AssetDescriptors.UI_SKIN);
        TextureAtlas gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY);

        TextureRegion backgroundRegion = gameplayAtlas.findRegion(RegionNames.GREY);
        table.setBackground(new TextureRegionDrawable(backgroundRegion));

        ChangeListener listener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CheckBox checked = checkBoxGroup.getChecked();
                if (checked == checkBoxX) {
                    GameManager.INSTANCE.setInitMove(GameDifficulty.EASY);
                } else if (checked == checkBoxO) {
                    GameManager.INSTANCE.setInitMove(GameDifficulty.MEDIUM);
                }else if(checked == hard){
                    GameManager.INSTANCE.setInitMove(GameDifficulty.HARD);
                }
            }
        };

        checkBoxX = new CheckBox(GameDifficulty.EASY.name(), uiSkin);
        checkBoxO = new CheckBox(GameDifficulty.MEDIUM.name(), uiSkin);
        hard = new CheckBox(GameDifficulty.HARD.name(),uiSkin);

        checkBoxX.addListener(listener);
        checkBoxO.addListener(listener);
        hard.addListener(listener);

        checkBoxGroup = new ButtonGroup<>(checkBoxX, checkBoxO,hard);
        checkBoxGroup.setChecked(GameManager.INSTANCE.getInitMove().name());

        TextButton backButton = new TextButton("Back", uiSkin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        Table contentTable = new Table(uiSkin);

        TextureRegion menuBackground = gameplayAtlas.findRegion(RegionNames.GREY);
        contentTable.setBackground(new TextureRegionDrawable(menuBackground));

        contentTable.add(new Label("Settings", uiSkin)).padBottom(50).colspan(2).row();
        contentTable.add(new Label("Choose init move:", uiSkin)).colspan(2).row();
        contentTable.add(checkBoxX);
        contentTable.add(checkBoxO).row();
        contentTable.add(hard).row();
        contentTable.add(backButton).width(100).padTop(50).colspan(2);

        table.add(contentTable);
        table.center();
        table.setFillParent(true);
        table.pack();

        return table;
    }
}