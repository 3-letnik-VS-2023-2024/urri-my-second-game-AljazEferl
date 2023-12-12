package com.mygdx.game.screen;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.BingoBlitz;
import com.mygdx.game.assets.AssetDescriptors;
import com.mygdx.game.assets.RegionNames;
import com.mygdx.game.config.GameConfig;


public class IntroScreen extends ScreenAdapter {

    public static final float INTRO_DURATION_IN_SEC = 0f;

    private final BingoBlitz game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private TextureAtlas gameplayAtlas;

    private float duration = 0f;

    private Stage stage;

    public IntroScreen(BingoBlitz game) {
        this.game = game;
        assetManager = game.getAssetManager();
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        stage = new Stage(viewport, game.getBatch());


        assetManager.load(AssetDescriptors.UI_FONT);
        assetManager.load(AssetDescriptors.UI_SKIN);
        assetManager.load(AssetDescriptors.GAMEPLAY);
        assetManager.finishLoading();

        gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY);

        stage.addActor(createAnimationBall1());
        stage.addActor(createAnimationBall2());
        stage.addActor(createAnimationBall3());
        stage.addActor(createAnimationBall4());
        stage.addActor(createAnimationBall5());
        stage.addActor(createParalelAnimation1());
        stage.addActor(createParalelAnimation2());
       stage.addActor(createParalelAnimation3());
        stage.addActor(createParalelAnimation4());
        stage.addActor(createParalelAnimation5());
        stage.addActor(createParalelAnimationRU1());
        stage.addActor(createParalelAnimationRU2());
        stage.addActor(createParalelAnimationRU3());
        stage.addActor(createParalelAnimationRU4());
        stage.addActor(createParalelAnimationRU5());

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0,0,0,1);

        duration += delta;
        if (duration  < INTRO_DURATION_IN_SEC && stage.getActors().size == 0) {
            stage.addActor(createBingoBlitz());
        }

        // go to the MenuScreen after INTRO_DURATION_IN_SEC seconds
        if (duration > INTRO_DURATION_IN_SEC) {
            game.setScreen(new MenuScreen(game));
        }

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

    private Actor createAnimationBall(String regionName, float spawnPositionX, float delay) {
        final Image ball = new Image(gameplayAtlas.findRegion(regionName));


        ball.setSize(50, 50);

        float initialPosX = spawnPositionX - ball.getWidth() / 2f;
        float initialPosY = 0;

        float finalPosX = spawnPositionX - ball.getWidth() / 2f;
        float finalPosY = viewport.getWorldHeight() / 2f - ball.getHeight() / 2f;

        ball.setPosition(initialPosX, initialPosY);

        ball.addAction(
                Actions.sequence(
                        Actions.delay(delay),
                        Actions.parallel(
                                Actions.scaleTo(2f, 2f, 3f),
                                Actions.moveTo(finalPosX, finalPosY, 3f)
                        ),
                        Actions.delay(1f),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {

                                ball.addAction(
                                        Actions.sequence(
                                                Actions.repeat(3, Actions.sequence(
                                                        Actions.fadeOut(0.25f),
                                                        Actions.fadeIn(0.25f)
                                                )),
                                                Actions.fadeIn(1f)
                                        )
                                );

                                IntroScreen.this.moveRight(ball);
                            }
                        })
                )
        );

        return ball;
    }




    private void moveRight(final Actor actor) {
        float targetX = actor.getX() + viewport.getWorldWidth();
        actor.addAction(
                Actions.sequence(
                        Actions.moveTo(targetX, actor.getY(), 1f),
                        Actions.removeActor(),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {

                            }
                        })
                )
        );
    }

    private Actor createParallelAnimationBallLeftDown(String regionName, float spawnPositionX, float delay) {
        final Image ball = new Image(gameplayAtlas.findRegion(regionName));

        ball.setSize(50, 50);

        float initialPosX = -ball.getWidth(); // Start from the left side
        float initialPosY = viewport.getWorldHeight() / 2f - ball.getHeight() / 2f;

        float finalPosX = spawnPositionX - ball.getWidth() / 2f;
        float finalPosY = viewport.getWorldHeight() / 2f - ball.getHeight() / 2f;

        ball.setPosition(initialPosX, initialPosY);

        ball.addAction(
                Actions.sequence(
                        Actions.delay(delay),
                        Actions.parallel(
                                Actions.scaleTo(2f, 2f, 3f),
                                Actions.moveTo(finalPosX, finalPosY, 3f)
                        ),
                        Actions.delay(1f),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {

                                ball.addAction(
                                        Actions.sequence(
                                                Actions.repeat(3, Actions.sequence(
                                                        Actions.fadeOut(0.25f),
                                                        Actions.fadeIn(0.25f)
                                                )),
                                                Actions.fadeIn(1f)
                                        )
                                );

                                IntroScreen.this.moveDown(ball);
                            }
                        })
                )
        );

        return ball;
    }

    private void moveDown(final Actor actor) {
        float targetY = actor.getY() - viewport.getWorldHeight();
        actor.addAction(
                Actions.sequence(
                        Actions.moveTo(actor.getX(), targetY, 1f),
                        Actions.removeActor(),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {

                            }
                        })
                )
        );
    }

    private Actor createParallelAnimationBallRightUp(String regionName, float spawnPositionX, float delay) {
        final Image ball = new Image(gameplayAtlas.findRegion(regionName));

        ball.setSize(50, 50);

        float initialPosX =viewport.getWorldWidth(); // Start from the right side
        float initialPosY = viewport.getWorldHeight() / 2f - ball.getHeight() / 2f;

        float finalPosX = spawnPositionX - ball.getWidth() / 2f;
        float finalPosY = viewport.getWorldHeight() / 2f - ball.getHeight() / 2f;

        ball.setPosition(initialPosX, initialPosY);

        ball.addAction(
                Actions.sequence(
                        Actions.delay(delay),
                        Actions.parallel(
                                Actions.scaleTo(2f, 2f, 3f),
                                Actions.moveTo(finalPosX, finalPosY, 3f)
                        ),
                        Actions.delay(1f),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {

                                ball.addAction(
                                        Actions.sequence(
                                                Actions.repeat(3, Actions.sequence(
                                                        Actions.fadeOut(0.25f),
                                                        Actions.fadeIn(0.25f)
                                                )),
                                                Actions.fadeIn(1f)
                                        )
                                );

                                IntroScreen.this.moveUp(ball);
                            }
                        })
                )
        );

        return ball;
    }
    private void moveUp(final Actor actor) {
        float targetY = actor.getY() + viewport.getWorldHeight();
        actor.addAction(
                Actions.sequence(
                        Actions.moveTo(actor.getX(), targetY, 1f),
                        Actions.removeActor(),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {

                            }
                        })
                )
        );
    }


    private Actor createAnimationBall1() {
        return createAnimationBall(RegionNames.B, 0.5f * viewport.getWorldWidth() / 5f,1f);
    }

    private Actor createAnimationBall2() {
        return createAnimationBall(RegionNames.I, 1.5f  * viewport.getWorldWidth() / 5f,0.75f);
    }

    private Actor createAnimationBall3() {
        return createAnimationBall(RegionNames.N, 2.5f * viewport.getWorldWidth() / 5f,0.5f);
    }

    private Actor createAnimationBall4() {
        return createAnimationBall(RegionNames.G, 3.5f * viewport.getWorldWidth() / 5f,0.25f);
    }
    private Actor createAnimationBall5() {
        return createAnimationBall(RegionNames.O, 4.5f * viewport.getWorldWidth() / 5f,0f);
    }

    private Actor createParalelAnimation1(){
        return createParallelAnimationBallLeftDown(RegionNames.B, 0.5f * viewport.getWorldWidth() / 5f,1f);
    }
    private Actor createParalelAnimation2(){
        return createParallelAnimationBallLeftDown(RegionNames.I, 1.5f  * viewport.getWorldWidth() / 5f,0.75f);
    }
    private Actor createParalelAnimation3(){
        return createParallelAnimationBallLeftDown(RegionNames.N, 2.5f * viewport.getWorldWidth() / 5f,0.5f);
    }
    private Actor createParalelAnimation4(){
        return createParallelAnimationBallLeftDown(RegionNames.G, 3.5f * viewport.getWorldWidth() / 5f,0.25f);
    }
    private Actor createParalelAnimation5(){
        return createParallelAnimationBallLeftDown(RegionNames.O, 4.5f * viewport.getWorldWidth() / 5f,0f);
    }
   private Actor createParalelAnimationRU1(){
        return createParallelAnimationBallRightUp(RegionNames.B, 0.5f * viewport.getWorldWidth() / 5f,1f);
    }
    private Actor createParalelAnimationRU2(){
        return createParallelAnimationBallRightUp(RegionNames.I, 1.5f  * viewport.getWorldWidth() / 5f,0.75f);
    }
    private Actor createParalelAnimationRU3(){
        return createParallelAnimationBallRightUp(RegionNames.N, 2.5f * viewport.getWorldWidth() / 5f,0.5f);
    }
    private Actor createParalelAnimationRU4(){
        return createParallelAnimationBallRightUp(RegionNames.G, 3.5f * viewport.getWorldWidth() / 5f,0.25f);
    }
    private Actor createParalelAnimationRU5(){
        return createParallelAnimationBallRightUp(RegionNames.O, 4.5f * viewport.getWorldWidth() / 5f,0f);
    }

    private Actor createBingoBlitz() {
        Image blitzImg = new Image(gameplayAtlas.findRegion(RegionNames.BLITZ));
        blitzImg.setPosition(
                (GameConfig.HUD_WIDTH - blitzImg.getWidth()) / 2f,
                (GameConfig.HUD_HEIGHT - blitzImg.getHeight()) / 2f
        );
        blitzImg.addAction(
                Actions.forever(Actions.sequence(
                        Actions.fadeOut(1f),
                        Actions.fadeIn(1f)

                )
                )
        );

        return blitzImg;
    }

}
