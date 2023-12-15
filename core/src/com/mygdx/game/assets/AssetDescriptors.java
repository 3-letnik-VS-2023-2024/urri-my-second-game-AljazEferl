package com.mygdx.game.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AssetDescriptors {

    public static final AssetDescriptor<BitmapFont> UI_FONT =
            new AssetDescriptor<BitmapFont>(AssetPaths.UI_FONT, BitmapFont.class);

    public static final AssetDescriptor<Skin> UI_SKIN =
            new AssetDescriptor<Skin>(AssetPaths.UI_SKIN, Skin.class);

    public static final AssetDescriptor<TextureAtlas> GAMEPLAY =
            new AssetDescriptor<TextureAtlas>(AssetPaths.GAMEPLAY, TextureAtlas.class);

    public static final AssetDescriptor<Music> OG =
            new AssetDescriptor<Music>(AssetPaths.OG, Music.class);

    public static final AssetDescriptor<Music> PIRATES =
            new AssetDescriptor<Music>(AssetPaths.PIRATES, Music.class);
    public static final AssetDescriptor<Sound> WRONG =
            new AssetDescriptor<Sound>(AssetPaths.WRONG, Sound.class);

    public static final AssetDescriptor<Sound> CORRECT =
            new AssetDescriptor<Sound>(AssetPaths.CORRECT, Sound.class);


    private AssetDescriptors() {
    }
}
