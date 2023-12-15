package com.mygdx.game.common;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.mygdx.game.CellState;
import com.mygdx.game.BingoBlitz;
import com.mygdx.game.GameDifficulty;

public class GameManager {

    public static final GameManager INSTANCE = new GameManager();

    private static final String INIT_MOVE_KEY = "difficulty";
    private static final String MUSIC_ENABLED_KEY = "musicEnabled";

    private final Preferences PREFS;
    private GameDifficulty initMove = GameDifficulty.EASY;
    private boolean musicEnabled = true;

    private GameManager() {
        PREFS = Gdx.app.getPreferences(BingoBlitz.class.getSimpleName());
        String moveName = PREFS.getString(INIT_MOVE_KEY, GameDifficulty.MEDIUM.name());
        initMove = GameDifficulty.valueOf(moveName);

        musicEnabled = PREFS.getBoolean(MUSIC_ENABLED_KEY, true);
    }

    public GameDifficulty getInitMove() {
        return initMove;
    }

    public void setInitMove(GameDifficulty move) {
        initMove = move;

        PREFS.putString(INIT_MOVE_KEY, move.name());
        PREFS.flush();
    }
    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public void setMusicEnabled(boolean enabled) {
        musicEnabled = enabled;

        PREFS.putBoolean(MUSIC_ENABLED_KEY, enabled);
        PREFS.flush();
    }



}
