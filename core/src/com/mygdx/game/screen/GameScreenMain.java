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
    BitmapFont smallFont;
    private TextButton backButton;
    private Label healthLabel;
    private int health = 100;
    private int score = 0;


    private GameDifficulty difficulty = GameManager.INSTANCE.getInitMove();
    public GameScreenMain(BingoBlitz game, String selectedCity) {
        this.game = game;
        assetManager = game.getAssetManager();
        this.selectedCity = selectedCity;
        tombolaNumbers =randomNumbers();
        scheduleNumberDisplay();


    }
    //Timer for  bingo numbers 5 seconds
    private void scheduleNumberDisplay() {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                displayNextNumber();
            }
        }, 0f, 5f);
    }
    //matrix numbers
    private Actor generateMatrixLabels() {
        Table matrixTable = new Table();
        matrixTable.center();
        matrixTable.setFillParent(true);

        matrixLabels = new Label[difficulty.getSize()][difficulty.getSize()];
        ArrayList<Integer> availableNumbers = new ArrayList<Integer>();

        for (int i = 1; i <= difficulty.getMaxNumber(); i++) {
            availableNumbers.add(i);
        }

        Collections.shuffle(availableNumbers);

       // smallFont = new BitmapFont();
      //  smallFont.getData().setScale(0.2f);

        for (int row = 0; row < difficulty.getSize(); row++) {
            for (int col = 0; col < difficulty.getSize(); col++) {
                int randomNumber = availableNumbers.remove(0);
                matrixLabels[row][col] = new Label(String.valueOf(randomNumber),skin,"black");// new Label.LabelStyle(skin.get("font-big", BitmapFont.class),Color.BLACK));
                matrixLabels[row][col].setTouchable(Touchable.enabled);
                matrixLabels[row][col].addListener(createClickListener(randomNumber));
                matrixTable.add(matrixLabels[row][col]).pad(10);
            }
            matrixTable.row();
        }

        return matrixTable;
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
                  //  healthLabel.setText("health: " + health);
                    int bingoCount = getBingoCount();

                    if (bingoCount > 0) {
                        System.out.println("Bingo " + bingoCount);
                        switch (bingoCount) {
                            case 1:
                                // Code for bingoCount == 1
                                score += 2;
                              //  healthLabel.setText("health: " + health);
                                break;

                            case 2:
                                // Code for bingoCount == 2
                                score *= 1.5;
                              //  healthLabel.setText("health: " + health);
                                break;

                            case 3:
                                // Code for bingoCount == 3
                                score *= 2.5;
                             //   healthLabel.setText("health: " + health);
                               // break;

                            case 4:
                                // Code for bingoCount == 4
                                score *= 3.5;
                               // healthLabel.setText("health: " + health);
                                break;

                            case 5:
                                // Code for bingoCount == 5
                                score *= 4.5;
                               // healthLabel.setText("health: " + health);
                                break;

                            default:
                                // Code for other values of bingoCount (if needed)
                                break;
                        }
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

                if (color != Color.GREEN) {
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
        dispose();
    }

    @Override
    public void dispose() {
        gameplayStage.dispose();
        hudStage.dispose();
    }

}
