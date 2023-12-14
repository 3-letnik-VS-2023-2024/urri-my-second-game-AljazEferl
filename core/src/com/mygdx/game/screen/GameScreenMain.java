package com.mygdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.BingoBlitz;
import com.mygdx.game.GameDifficulty;
import com.mygdx.game.assets.AssetDescriptors;
import com.mygdx.game.assets.RegionNames;
import com.mygdx.game.common.GameManager;
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
    private Label[][] matrixLabelsPlayer2;
    BitmapFont smallFont;
    private TextButton backButton;
    private Label healthLabel;
    private int health = 100;
    private int score = 0;

    private Timer.Task numberDisplayTask;

    private GameDifficulty difficulty = GameManager.INSTANCE.getInitMove();


    public GameScreenMain(BingoBlitz game, String selectedCity) {
        this.game = game;
        assetManager = game.getAssetManager();
        this.selectedCity = selectedCity;
        tombolaNumbers =randomNumbers();
        scheduleNumberDisplay();


    }
    private float initialInterval = 2f;
    private void scheduleNumberDisplay() {
        numberDisplayTask = new Timer.Task() {
            private float initialDelay = 0f;

            private float intervalDecreaseRate = 0.1f;
            private float timeElapsed = 0f;

            @Override
            public void run() {
                displayNextNumber();

                timeElapsed += initialInterval;

                if (timeElapsed >= 6f) {

                    initialInterval -= intervalDecreaseRate;


                    timeElapsed = 0f;
                }
            }
        };
        Timer.schedule(numberDisplayTask, 0f, initialInterval);

    }


    //matrix numbers
    private Table generateMatrixLabels() {
        Table mainTable = new Table();
        mainTable.center();
        mainTable.setFillParent(true);

        if (difficulty == GameDifficulty.EXTREME) {
            System.out.println("EXTREME MODE ");
            matrixLabels = new Label[difficulty.getSize()][difficulty.getSize()];
            matrixLabelsPlayer2 = new Label[difficulty.getSize()][difficulty.getSize()];
            ArrayList<Integer> availableNumbers = new ArrayList<Integer>();

            for (int i = 1; i <= difficulty.getMaxNumber(); i++) {
                availableNumbers.add(i);
            }

            Collections.shuffle(availableNumbers);

            Table matrixTable = new Table();
            matrixTable.center();

            Table matrixTablePlayer2 = new Table();
            matrixTablePlayer2.center();

            for (int row = 0; row < difficulty.getSize(); row++) {
                for (int col = 0; col < difficulty.getSize(); col++) {
                    int randomNumber = availableNumbers.remove(0);

                    matrixLabels[row][col] = new Label(String.valueOf(randomNumber),skin,"black");// new Label.LabelStyle(skin.get("font", BitmapFont.class), Color.BLACK));
                    matrixLabels[row][col].setTouchable(Touchable.enabled);
                    matrixLabels[row][col].addListener(createClickListener(randomNumber));
                    matrixTable.add(matrixLabels[row][col]).pad(10);


                    int randomNumberPlayer2 = availableNumbers.remove(0);
                    matrixLabelsPlayer2[row][col] = new Label(String.valueOf(randomNumberPlayer2), skin,"black");//new Label.LabelStyle(skin.get("font", BitmapFont.class), Color.BLACK));
                    matrixTablePlayer2.add(matrixLabelsPlayer2[row][col]).pad(10);
                }

                matrixTable.row();
                matrixTablePlayer2.row();
            }

            mainTable.add(matrixTable).pad(20);
            mainTable.add(matrixTablePlayer2).pad(20);

        } else {
            matrixLabels = new Label[difficulty.getSize()][difficulty.getSize()];
            ArrayList<Integer> availableNumbers = new ArrayList<Integer>();

            for (int i = 1; i <= difficulty.getMaxNumber(); i++) {
                availableNumbers.add(i);
            }

            Collections.shuffle(availableNumbers);

            Table matrixTable = new Table();
            matrixTable.center();

            for (int row = 0; row < difficulty.getSize(); row++) {
                for (int col = 0; col < difficulty.getSize(); col++) {
                    int randomNumber = availableNumbers.remove(0);
                    matrixLabels[row][col] = new Label(String.valueOf(randomNumber), skin, "black");
                    matrixLabels[row][col].setTouchable(Touchable.enabled);
                    matrixLabels[row][col].addListener(createClickListener(randomNumber));
                    matrixTable.add(matrixLabels[row][col]).pad(10);
                }
                matrixTable.row();
            }

            mainTable.add(matrixTable).pad(20);
        }

        return mainTable;
    }


    //click event listener
    private ClickListener createClickListener(final int clickedNumber) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Clicked Number: " + clickedNumber);
                int displayedNumber = Integer.parseInt(numberLabel.getText().toString());
                if (clickedNumber == displayedNumber) {

                    matrixLabels[getClickedRow(event)][getClickedColumn(event)].setStyle(skin.get("greenLabel", Label.LabelStyle.class));//new Label.LabelStyle(smallFont, Color.GREEN));
                    score +=1;

                    if(isBingo()){
                        //System.out.println("Bingo");
                        game.setScreen(new WinScreen(game,score,"player"));
                    }
                } else {
                    matrixLabels[getClickedRow(event)][getClickedColumn(event)].setStyle(skin.get("redLabel", Label.LabelStyle.class));//new Label.LabelStyle(smallFont, Color.RED));

                        health -= 25;
                        if (health <= 0){
                            game.setScreen(new GameOver(game,selectedCity,score));
                        }
                        healthLabel.setText("health: " + health);

                }
            }
        };
    }

    private int getClickedRow(InputEvent event) {
        Actor actor = event.getTarget();
        int row = -1;

        for (int i = 0; i < matrixLabels.length; i++) {
            for (int j = 0; j < matrixLabels[i].length; j++) {
                if (matrixLabels[i][j] == actor) {
                    row = i;
                    break;
                }
            }
        }

        return row;
    }

    private int getClickedColumn(InputEvent event) {
        Actor actor = event.getTarget();
        int col = -1;

        for (int i = 0; i < matrixLabels.length; i++) {
            for (int j = 0; j < matrixLabels[i].length; j++) {
                if (matrixLabels[i][j] == actor) {
                    col = j;
                    break;
                }
            }
        }

        return col;
    }

    private int getBingoCount() {
        int bingoCount = 0;
        int consecutiveGreenRows = 0;

        for (int i = 0; i < matrixLabels.length; i++) {
            boolean allGreen = true;

            for (int j = 0; j < matrixLabels[i].length; j++) {
                Label label = matrixLabels[i][j];
                Label.LabelStyle style = label.getStyle();
                Color color = style.fontColor;

                if (!color.equals(Color.GREEN)) {
                    allGreen = false;
                    break;
                }
            }


            if (allGreen) {
                consecutiveGreenRows++;
                System.out.println("Bingo " + consecutiveGreenRows);
            } else {
                consecutiveGreenRows = 0;
            }

        }

        return consecutiveGreenRows;
    }


    private boolean isBingo() {
        // Check rows
        for (int i = 0; i < matrixLabels.length; i++) {
            if (checkLine(matrixLabels[i])) {
                System.out.println("Bingo!");
                return true;
            }
        }

        // Check columns
        for (int j = 0; j < matrixLabels[0].length; j++) {
            Label[] column = new Label[matrixLabels.length];
            for (int i = 0; i < matrixLabels.length; i++) {
                column[i] = matrixLabels[i][j];
            }

            if (checkLine(column)) {
                System.out.println("Bingo!");
                return true;
            }
        }

        // Check main diagonal
        Label[] mainDiagonal = new Label[matrixLabels.length];
        for (int i = 0; i < matrixLabels.length; i++) {
            mainDiagonal[i] = matrixLabels[i][i];
        }
        if (checkLine(mainDiagonal)) {
            System.out.println("Bingo!");
            return true;
        }

        // Check secondary diagonal
        Label[] secondaryDiagonal = new Label[matrixLabels.length];
        for (int i = 0; i < matrixLabels.length; i++) {
            secondaryDiagonal[i] = matrixLabels[i][matrixLabels.length - 1 - i];
        }
        if (checkLine(secondaryDiagonal)) {
            System.out.println("Bingo!");
            return true;
        }

        return false;
    }


    private boolean isBingoAi() {
        // Check rows
        for (int i = 0; i < matrixLabelsPlayer2.length; i++) {
            if (checkLine(matrixLabelsPlayer2[i])) {
                System.out.println("Bingo!");
                return true;
            }
        }

        // Check columns
        for (int j = 0; j < matrixLabelsPlayer2[0].length; j++) {
            Label[] column = new Label[matrixLabelsPlayer2.length];
            for (int i = 0; i < matrixLabelsPlayer2.length; i++) {
                column[i] = matrixLabelsPlayer2[i][j];
            }

            if (checkLine(column)) {
                System.out.println("Bingo  AI! ");
                return true;
            }
        }

        // Check main diagonal
        Label[] mainDiagonal = new Label[matrixLabelsPlayer2.length];
        for (int i = 0; i < matrixLabelsPlayer2.length; i++) {
            mainDiagonal[i] = matrixLabelsPlayer2[i][i];
        }
        if (checkLine(mainDiagonal)) {
            System.out.println("Bingo!");
            return true;
        }

        // Check secondary diagonal
        Label[] secondaryDiagonal = new Label[matrixLabelsPlayer2.length];
        for (int i = 0; i < matrixLabelsPlayer2.length; i++) {
            secondaryDiagonal[i] = matrixLabelsPlayer2[i][matrixLabelsPlayer2.length - 1 - i];
        }
        if (checkLine(secondaryDiagonal)) {
            System.out.println("Bingo!");
            return true;
        }

        return false;
    }


    private boolean checkLine(Label[] line) {
        for (Label label : line) {
            Label.LabelStyle style = label.getStyle();
            Color color = style.fontColor;

            if (!color.equals(Color.GREEN)) {
                return false;
            }
        }

        return true;
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
            if (difficulty == GameDifficulty.EXTREME) {
                for (int row = 0; row < difficulty.getSize(); row++) {
                    for (int col = 0; col < difficulty.getSize(); col++) {
                        int secondPlayerNumber = Integer.parseInt(matrixLabelsPlayer2[row][col].getText().toString());
                        if (number == secondPlayerNumber) {
                            matrixLabelsPlayer2[row][col].setStyle(skin.get("greenLabel", Label.LabelStyle.class));
                        }
                    }
                }

                if(isBingoAi()){
                    //System.out.println("KOMP JE ZMAGo");
                    game.setScreen(new WinScreen(game,score,"ai"));
                }

            }

            currentNumberIndex++;
        } else {

            currentNumberIndex = 0;
        }
    }
    private ArrayList<Integer> randomNumbers() {
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        for (int i = 1; i <= difficulty.getMaxNumber(); i++) {
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
        backButton = new TextButton("Back", skin);
        backButton.setPosition(GameConfig.HUD_WIDTH - backButton.getWidth() - 20, 20);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });
        Table healthTable = new Table();
        healthTable.top().left();
        healthTable.setFillParent(true);


        healthLabel = new Label("health: "+ health , skin, "big");
        healthTable.setPosition(GameConfig.HUD_WIDTH-healthLabel.getWidth()-40,0);
        healthTable.add(healthLabel).padTop(20).padRight(20);
        hudStage.addActor(healthTable);
        hudStage.addActor(backButton);

       Gdx.input.setInputProcessor(new InputMultiplexer(hudStage, gameplayStage));
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
        cancelTimer();
        dispose();
    }

    @Override
    public void dispose() {
        cancelTimer();
        gameplayStage.dispose();
        hudStage.dispose();
    }

    private void cancelTimer() {
        if (numberDisplayTask != null) {
            numberDisplayTask.cancel();
        }
    }

}
