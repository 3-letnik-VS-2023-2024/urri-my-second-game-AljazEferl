package com.mygdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
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

import org.lwjgl.Sys;

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
    private Music music;
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
    private Sound soundWrong;
    private Sound soundCorrect;

    private Timer.Task numberDisplayTask;
    private Image powerUpImage;
    private Image numImage;
    private int greenOnes = 1;
    private TextureRegion defaultPowerUpRegion;
    private TextureRegion alternatePowerUpRegion;

    private TextureRegion bomb;
    private TextureRegion numberImage;

    private GameDifficulty difficulty = GameManager.INSTANCE.getInitMove();
    private TextureRegionDrawable defaultPowerUpDrawable;

    private TextureRegionDrawable numberImgDrawable;
    private TextureRegionDrawable alternatePowerUpDrawable;
    private int lastClickedNumber = -1;

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

            matrixTable.setBackground(getGridBackground(1, 1, 1, 0.5f,80));
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
                    matrixTablePlayer2.add(matrixLabelsPlayer2[row][col]).pad(5);
                }

                matrixTable.row();
                matrixTablePlayer2.row();
            }

            mainTable.add(matrixTable).pad(10);
            mainTable.add(matrixTablePlayer2).pad(10);

        } else {
            matrixLabels = new Label[difficulty.getSize()][difficulty.getSize()];
            ArrayList<Integer> availableNumbers = new ArrayList<Integer>();

            for (int i = 1; i <= difficulty.getMaxNumber(); i++) {
                availableNumbers.add(i);
            }

            Collections.shuffle(availableNumbers);

            Table matrixTable = new Table();
         //   matrixTable.setBackground(getColoredBackground(1, 1, 1, 0.5f));
            matrixTable.setBackground(getGridBackground(1, 1, 1, 0.5f,100));
            matrixTable.center();

            for (int row = 0; row < difficulty.getSize(); row++) {
                for (int col = 0; col < difficulty.getSize(); col++) {
                    int randomNumber = availableNumbers.remove(0);
                    matrixLabels[row][col] = new Label(String.valueOf(randomNumber), skin, "black");
                    matrixLabels[row][col].setTouchable(Touchable.enabled);
                    matrixLabels[row][col].addListener(createClickListener(randomNumber));
                    matrixTable.add(matrixLabels[row][col]).pad(10).center();

                }
                matrixTable.row();
            }

            mainTable.add(matrixTable).pad(20);
        }

        return mainTable;
    }
    private Drawable getGridBackground(float r, float g, float b, float a, int gridSize) {
        Pixmap pixmap = new Pixmap(gridSize, gridSize, Pixmap.Format.RGBA8888);

        // Set the background color
        pixmap.setColor(r, g, b, a);
        pixmap.fill();

        // Draw grid lines
        pixmap.setColor(0, 0, 0, 1); // Black color for grid lines

        // Draw horizontal lines
        for (int i = 1; i < difficulty.getSize(); i++) {
            pixmap.drawLine(0, i * gridSize / difficulty.getSize(), gridSize, i * gridSize / difficulty.getSize());
        }

        // Draw vertical lines
        for (int i = 1; i < difficulty.getSize(); i++) {
            pixmap.drawLine(i * gridSize / difficulty.getSize(), 0, i * gridSize / difficulty.getSize(), gridSize);
        }

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    /*private Drawable getColoredBackground(float r, float g, float b, float a) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(r, g, b, a);
        pixmap.fill();

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        return new TextureRegionDrawable(new TextureRegion(texture));
    }*/
    //click event listener

    private ClickListener createClickListener(final int clickedNumber) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Clicked Number: " + clickedNumber);
                int displayedNumber = Integer.parseInt(numberLabel.getText().toString());

                if (clickedNumber == displayedNumber) {
                    lastClickedNumber = clickedNumber;
                   /* TextureRegion greenCheckRegion = gameplayAtlas.findRegion(RegionNames.GREY);
                    Image greenCheck = new Image(new TextureRegionDrawable(greenCheckRegion));
                    float imageSize = 50f;
                    greenCheck.setSize(imageSize, imageSize);
                    float labelX = matrixLabels[getClickedRow(event)][getClickedColumn(event)].getX();
                    float labelY = matrixLabels[getClickedRow(event)][getClickedColumn(event)].getY();

                    greenCheck.setPosition(labelX+ greenCheck.getImageWidth()*5, labelY);
                    gameplayStage.addActor(greenCheck);*/

                    //numberLabel
                    System.out.println("ANIMACIJA");
                    numberLabel.addAction(Actions.sequence(
                            Actions.fadeOut(0.5f),
                            Actions.run(new Runnable() {
                                @Override
                                public void run() {
                                    // Update the numberLabel text and fade it back in
                                    numberLabel.setText(String.valueOf(0));
                                    numberLabel.addAction(Actions.fadeIn(0.5f));
                                }
                            })
                    ));

                soundCorrect.play();
                    matrixLabels[getClickedRow(event)][getClickedColumn(event)].setStyle(skin.get("greenLabel", Label.LabelStyle.class));//new Label.LabelStyle(smallFont, Color.GREEN));

                    score +=1;
                    ++greenOnes;
                    if (greenOnes % n == 0) {
                        powerUpImage.setDrawable(alternatePowerUpDrawable);
                    } else {
                        powerUpImage.setDrawable(defaultPowerUpDrawable);
                    }
                    soundCorrect.play();
                    if(isBingo()){
                        //System.out.println("Bingo");
                        game.setScreen(new WinScreen(game,score,"player"));
                    }
                } else {
                    matrixLabels[getClickedRow(event)][getClickedColumn(event)].setStyle(skin.get("redLabel", Label.LabelStyle.class));//new Label.LabelStyle(smallFont, Color.RED));
                        soundWrong.play();
                        health -= 25;
                        if (health <= 0){
                            game.setScreen(new GameOver(game,selectedCity,score));
                        }
                        //healthLabel.setText(": " + health);

                }

                System.out.println("GG"+greenOnes);
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
    private int bombCount = 0;

    private int bonusClickCount = 0;
    private static final int REQUIRED_CLICKS = 5;

    private void spawnBonusElement() {
        TextureRegion bonusRegion = gameplayAtlas.findRegion(RegionNames.WARNING);

        final Image bonusImage = new Image(bonusRegion);
        float bonusImageX = (GameConfig.WORLD_WIDTH - bonusImage.getWidth()) / 2f;
        float bonusImageY = (GameConfig.WORLD_HEIGHT - bonusImage.getHeight()) / 2f;

        bonusImage.setPosition(bonusImageX, bonusImageY);

        bonusImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleBonusElementClick(bonusImage);
            }
        });

        gameplayStage.addActor(bonusImage);
    }


   // private int bonusCount = 0;
    private void handleBonusElementClick(Image bonusImage) {
        bonusClickCount++;

        if (bonusClickCount >= REQUIRED_CLICKS) {
            bonusImage.remove();
            bonusClickCount = 0;
           // bonusCount++;
            removeBombs();
        }
    }


    private boolean shouldSpawnBonus  =true;
    private void removeBombs() {
        bombCount = 0;
        for (Image bombImage : bombImages) {
            bombImage.remove();
        }
        bombImages.clear();
        shouldSpawnBonus = true;
    }

    private Array<Image> bombImages = new Array<>();

    private void spawnBomb() {
        bombCount++;
        TextureRegion bombRegion = gameplayAtlas.findRegion(RegionNames.BOMB);
        float randomX = MathUtils.random(0, GameConfig.WORLD_WIDTH - bombRegion.getRegionWidth());
        float randomY = MathUtils.random(0, GameConfig.WORLD_HEIGHT - bombRegion.getRegionHeight());

        final Image bombImage = new Image(bombRegion);
        bombImage.setPosition(randomX, randomY);
        if(bombCount > 5 && shouldSpawnBonus){
            //if(bonusCount >= 2) {
                spawnBonusElement();
                shouldSpawnBonus = false;
          //  }
           // else {

           //     game.setScreen(new GameOver(game,selectedCity,score));
            //}

        }
        bombImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleBombClick(bombImage);
            }
        });

        gameplayStage.addActor(bombImage);

        bombImages.add(bombImage);
    }


    private void handleBombClick(final Image bombImage) {
        bombImage.clearActions(); // Clear existing actions to avoid conflicts
        bombCount--;
        // Define the loop action
        Action loopAction = Actions.sequence(
                Actions.moveTo(0, 0, 2f),  // Move to the bottom-left corner
                Actions.moveTo(GameConfig.WORLD_WIDTH - bombImage.getWidth(), 0, 2f),  // Move to the top-right corner
                Actions.moveTo(GameConfig.WORLD_WIDTH - bombImage.getWidth(), GameConfig.WORLD_HEIGHT - bombImage.getHeight(), 2f),  // Move to the bottom-right corner
                Actions.moveTo(0, GameConfig.WORLD_HEIGHT - bombImage.getHeight(), 2f),  // Move to the top-left corner
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        // After completing the loop, remove the bomb image
                        bombImage.remove();



                    }
                })
        );

        bombImage.addAction(Actions.forever(loopAction));
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


        Label[] mainDiagonal = new Label[matrixLabelsPlayer2.length];
        for (int i = 0; i < matrixLabelsPlayer2.length; i++) {
            mainDiagonal[i] = matrixLabelsPlayer2[i][i];
        }
        if (checkLine(mainDiagonal)) {
            System.out.println("Bingo!");
            return true;
        }


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

    private Array<Integer> displayedNumbers = new Array<>();
    private void displayNextNumber() {
        if (currentNumberIndex < tombolaNumbers.size()) {
            int number = tombolaNumbers.get(currentNumberIndex);
            System.out.println("Displaying Number: " + number);

            if (numberLabel == null) {
                numberLabel = new Label("", skin, "big");

                hudTable = new Table();
                hudTable.top().left();  // Ensure top-left alignment
                hudTable.setFillParent(true);
                hudTable.add(numberLabel).padTop(20).padLeft(20);  // Add padding as needed

                hudStage.addActor(hudTable);
            }

            displayedNumbers.add(number);
            

            numberLabel.setText(String.valueOf(number));


            hudTable.clear();
            for (int i = Math.max(0, displayedNumbers.size - 5); i < displayedNumbers.size; i++) {
                Label historyLabel = new Label(String.valueOf(displayedNumbers.get(i)), skin, "big");

                // Create a container for the label and set the background
                Container<Label> container = new Container<>(historyLabel);
                container.background(getNumberBackground(displayedNumbers.get(i)));

                hudTable.add(container).padTop(10).padLeft(10);  // Adjust padding as needed
                hudTable.row();  // Move to the next row for the next number
            }

            if (difficulty == GameDifficulty.EXTREME) {
                for (int row = 0; row < difficulty.getSize(); row++) {
                    for (int col = 0; col < difficulty.getSize(); col++) {
                        int secondPlayerNumber = Integer.parseInt(matrixLabelsPlayer2[row][col].getText().toString());
                        if (number == secondPlayerNumber) {
                            matrixLabelsPlayer2[row][col].setStyle(skin.get("greenLabel", Label.LabelStyle.class));
                        }
                    }
                }

                if (isBingoAi()) {
                    game.setScreen(new WinScreen(game, score, "ai"));
                }
            }

            currentNumberIndex++;
        } else {
            currentNumberIndex = 0;
        }
    }


    private Drawable getNumberBackground(int number) {
        if (displayedNumbers.size > 0 && number == displayedNumbers.get(displayedNumbers.size - 1)) {
            // Return the background for the last displayed number
            return new TextureRegionDrawable(gameplayAtlas.findRegion(RegionNames.DIFFERENT_BALL));
        } else {
            // Return the default background for other numbers
            return new TextureRegionDrawable(gameplayAtlas.findRegion(RegionNames.BALL));
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
    private int n = 5;



    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        hudViewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);

        gameplayStage = new Stage(viewport, game.getBatch());
        hudStage = new Stage(hudViewport, game.getBatch());


        skin = assetManager.get(AssetDescriptors.UI_SKIN);
        gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY);
        music = assetManager.get(AssetDescriptors.PIRATES);
        soundWrong = assetManager.get(AssetDescriptors.WRONG);
        soundCorrect = assetManager.get(AssetDescriptors.CORRECT);


        defaultPowerUpRegion = gameplayAtlas.findRegion(RegionNames.COLD);
        defaultPowerUpDrawable = new TextureRegionDrawable(defaultPowerUpRegion);
        powerUpImage = new Image(defaultPowerUpDrawable);
        powerUpImage.setSize(50, 50); // Set the size as needed
        powerUpImage.setPosition(GameConfig.HUD_WIDTH / 2f - powerUpImage.getWidth() / 2f, GameConfig.HUD_HEIGHT - 65);
        alternatePowerUpRegion = gameplayAtlas.findRegion(RegionNames.HOT);
        alternatePowerUpDrawable = new TextureRegionDrawable(alternatePowerUpRegion);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                System.out.println(bombCount);
                if (bombCount <= 5) {
                    spawnBomb();
                }
            }
        }, 3f, 3f);

        // numberImage = gameplayAtlas.findRegion(RegionNames.GREY);
       // numberImgDrawable = new TextureRegionDrawable(numberImage);
       // numImage = new Image(numberImgDrawable);
        //numImage.setSize(90,90);
       // numImage.setPosition(10,GameConfig.HUD_HEIGHT - 100 );


        powerUpImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (greenOnes % n == 0) {
                    applyPowerUp();

                    powerUpImage.clearActions();

                    Action fadeInOutAction = Actions.sequence(
                            Actions.parallel(
                                    Actions.fadeOut(0.5f),
                                    Actions.scaleTo(1.3f, 1.3f, 0.5f)
                            ),
                            Actions.delay(1.0f),
                            Actions.parallel(
                                    Actions.fadeIn(0.5f),
                                    Actions.scaleTo(1.0f, 1.0f, 0.5f)
                            )
                    );

                    powerUpImage.addAction(fadeInOutAction);

                    n += 5;
                    powerUpImage.setDrawable(defaultPowerUpDrawable);
                }
            }
        });




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


        healthLabel = new Label("", skin, "big");
        healthTable.setPosition(GameConfig.HUD_WIDTH-healthLabel.getWidth()-40,0);
        healthTable.add(healthLabel).padTop(20).padRight(20);
        hudStage.addActor(healthTable);
        hudStage.addActor(backButton);
        hudStage.addActor(powerUpImage);
       // hudStage.addActor(numImage);

       Gdx.input.setInputProcessor(new InputMultiplexer(hudStage, gameplayStage));
    }

    private void applyPowerUp() {
        int randomNonGreenIndex = getRandomNonGreenRowAndColumn();
        int randomRow = randomNonGreenIndex / difficulty.getSize();
        int randomCol = randomNonGreenIndex % difficulty.getSize();

        matrixLabels[randomRow][randomCol].setStyle(skin.get("greenLabel", Label.LabelStyle.class));

        if (matrixLabelsPlayer2 == null) {
            if (isBingo()) {
                //System.out.println("Bingo");
                game.setScreen(new WinScreen(game, score, "player"));
            }
        } else {
            if (isBingoAi()) {
                //System.out.println("KOMP JE ZMAGo");
                game.setScreen(new WinScreen(game, score, "ai"));
            }
            if (isBingo()) {
                game.setScreen(new WinScreen(game, score, "player"));
            }
        }
    }

    private boolean isGreen(int row, int column) {
        return matrixLabels[row][column].getStyle().equals(skin.get("greenLabel", Label.LabelStyle.class));
    }
    private int getRandomRow() {
        return new Random().nextInt(difficulty.getSize());
    }

    private int getRandomColumn() {
        return new Random().nextInt(difficulty.getSize());
    }
    private int getRandomNonGreenRowAndColumn() {
        int row, column;

        do {
            row = getRandomRow();
            column = getRandomColumn();
        } while (isGreen(row, column));

        return row * difficulty.getSize() + column;
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

       /* if (greenOnes % n == 0) {
            applyPowerUp();
            n += 5;

            // Change the drawable when the condition is true
            powerUpImage.setDrawable(alternatePowerUpDrawable);
        } else {
            // Change it back to the default drawable when the condition is false
            powerUpImage.setDrawable(defaultPowerUpDrawable);
        }*/


    }

    @Override
    public void hide() {
        music.stop();
        music.dispose();
        soundCorrect.dispose();
        soundWrong.dispose();
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
