package com.mygdx.game.screen;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.BingoBlitz;
import com.mygdx.game.assets.AssetDescriptors;
import com.mygdx.game.assets.RegionNames;
import com.mygdx.game.config.GameConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GameScreenMain extends ScreenAdapter {

    private final BingoBlitz game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private Viewport hudViewport;

    private Stage gameplayStage;
    private Stage hudStage;

    private Skin skin;
    private TextureAtlas gameplayAtlas;
    private String selectedCity;

    private ArrayList<Integer> tombolaNumbers;
    private Set<Integer> generatedNumbers;
    private int currentNumberIndex = 0;
    private Label numberLabel;
    private Table hudTable;

    private Table matrixTable;
    private Label[][] matrixLabels;
    public GameScreenMain(BingoBlitz game, String selectedCity) {
        this.game = game;
        assetManager = game.getAssetManager();
        this.selectedCity = selectedCity;
        tombolaNumbers =randomNumbers();
        scheduleNumberDisplay();

    }
    private void scheduleNumberDisplay() {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                displayNextNumber();
            }
        }, 1f, 5f);
    }
    private Actor generateMatrixLabels() {
        Table matrixTable = new Table();
        matrixTable.center();
        matrixTable.setFillParent(true);

        Label[][] matrixLabels = new Label[3][3];
        ArrayList<Integer> availableNumbers = new ArrayList<Integer>();

        for (int i = 1; i <= 99; i++) {
            availableNumbers.add(i);
        }

        Collections.shuffle(availableNumbers);

        BitmapFont smallFont = new BitmapFont();
        smallFont.getData().setScale(0.2f);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int randomNumber = availableNumbers.remove(0); // Remove and get the first number
                matrixLabels[row][col] = new Label(String.valueOf(randomNumber), new Label.LabelStyle(smallFont, Color.BLACK));
                matrixTable.add(matrixLabels[row][col]).pad(5);
            }
            matrixTable.row();
        }

        return matrixTable;
    }


    private void displayNextNumber() {
        if (currentNumberIndex < tombolaNumbers.size()) {
            int number = tombolaNumbers.get(currentNumberIndex);
            System.out.println("Displaying Number: " + number);
            if (numberLabel == null) {
                numberLabel = new Label("", skin,"big");


                hudTable = new Table();
                hudTable.top().left();
                hudTable.setFillParent(true);
                hudTable.add(numberLabel).padTop(20).padLeft(20);

                hudStage.addActor(hudTable);
            }

            numberLabel.setText(String.valueOf(number));

            currentNumberIndex++;
        } else {
            // All numbers displayed, you can handle the end of the game or reset the index
            currentNumberIndex = 0;
        }
    }
    private ArrayList<Integer> randomNumbers() {
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        for (int i = 1; i <= 99; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);
        System.out.println("Shuffled Array: " + numbers);
        return numbers;
    }
    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        hudViewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);

        gameplayStage = new Stage(viewport, game.getBatch());
        hudStage = new Stage(hudViewport, game.getBatch());

        skin = assetManager.get(AssetDescriptors.UI_SKIN);
        gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY);
        Table backgroundTable = new Table();
        backgroundTable.setFillParent(true);


        if(selectedCity == "Pariz") {
            TextureRegion backgroundRegion = gameplayAtlas.findRegion(RegionNames.PARIZ);
            backgroundTable.setBackground(new TextureRegionDrawable(backgroundRegion));
        }
        else if(selectedCity == "New York") {
            TextureRegion backgroundRegion = gameplayAtlas.findRegion(RegionNames.NEW_YORK);
            backgroundTable.setBackground(new TextureRegionDrawable(backgroundRegion));
        }
        else {
           TextureRegion backgroundRegion = gameplayAtlas.findRegion(RegionNames.MADRID);
            backgroundTable.setBackground(new TextureRegionDrawable(backgroundRegion));

        }

        Actor matrixActor = generateMatrixLabels();
        gameplayStage.addActor(backgroundTable);
        gameplayStage.addActor(matrixActor);
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hudViewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {

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

    @Override
    public void dispose() {
        gameplayStage.dispose();
        hudStage.dispose();
    }

}
