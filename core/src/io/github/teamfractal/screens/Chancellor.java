/*  JBT Assessment 4 Page: http://robins.tech/jbt/assfour.html
 *  JBT Created this file for use with the 'Catch the Chancellor' minigame random event
 */

package io.github.teamfractal.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

/**
 * Used for the catch the chancellor mini-game to hold information about a chancellor object
 * @author JBT
 */
public class Chancellor {
    private final Rectangle positionBounds = new Rectangle(-150,-100,650,250);
    private Vector2 currentPosition;
    private Vector2 targetPosition;

    public Sprite sprite;

    /**
     * Creates a new chancellor instance
     */
    public Chancellor()
    {
        //Set chancellor texture and sprite
        sprite = new Sprite(new Texture("roboticon_images/chancellor.png"));
        sprite.setScale(0.25f);

        initialise();
    }

    /**
     * Called to setup starting position and initial target position
     */
    public void initialise()
    {
        //Select a random start and target position
        currentPosition = randomBoundsPosition();
        targetPosition = randomBoundsPosition();

        //Set the current position to be the actual position of the sprite
        sprite.setPosition(currentPosition.x, currentPosition.y);
    }

    /**
     * Moves the chancellors position closer to the target position
     * Selects a new target position if near the current target
     */
    public void updatePosition()
    {
        currentPosition.lerp(targetPosition, 0.1f);
        sprite.setPosition(currentPosition.x, currentPosition.y);

        //If close to the target position, select another target
        if(Vector2.len(currentPosition.x - targetPosition.x, currentPosition.y - targetPosition.y) < 5)
        {
            targetPosition = randomBoundsPosition();
        }
    }

    /**
     * Gets a position where the chancellor can walk to
     * @return A random position inside the chancellors walkable bounds
     */
    private final Vector2 randomBoundsPosition()
    {
        Random r = new Random();
        return new Vector2(
                r.nextInt((int)(positionBounds.width - positionBounds.x)) + positionBounds.x,
                r.nextInt((int)(positionBounds.height - positionBounds.y)) + positionBounds.y);
    }

    /**
     * Releases all disposable memory used by this instance
     */
    public void dispose()
    {
        sprite.getTexture().dispose();
    }
}
