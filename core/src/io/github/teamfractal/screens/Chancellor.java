package io.github.teamfractal.screens;


import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class Chancellor {

    private final Rectangle positionBounds = new Rectangle(-150,-100,650,250);
    public Sprite sprite;

    public Chancellor()
    {
//        Vector2 startPositon
//        sprite.setPosition();
    }

    private final Vector2 RandomBoundsPosition()
    {
        Random r = new Random();
        return new Vector2(
                r.nextInt((int)(positionBounds.width - positionBounds.x)) - positionBounds.x,
                r.nextInt((int)(positionBounds.height - positionBounds.y)) - positionBounds.y);
    }
}
