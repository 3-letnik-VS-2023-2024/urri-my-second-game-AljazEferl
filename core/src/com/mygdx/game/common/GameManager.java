package com.mygdx.game.common;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.mygdx.game.CellState;
import com.mygdx.game.BingoBlitz;

public class GameManager {

    public static final GameManager INSTANCE = new GameManager();

    private static final String INIT_MOVE_KEY = "initMove";

    private final Preferences PREFS;
    private CellState initMove = CellState.X;

    private GameManager() {
        PREFS = Gdx.app.getPreferences(BingoBlitz.class.getSimpleName());
        String moveName = PREFS.getString(INIT_MOVE_KEY, CellState.X.name());
        initMove = CellState.valueOf(moveName);
    }

    public CellState getInitMove() {
        return initMove;
    }

    public void setInitMove(CellState move) {
        initMove = move;

        PREFS.putString(INIT_MOVE_KEY, move.name());
        PREFS.flush();
    }
}
