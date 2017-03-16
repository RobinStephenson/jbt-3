/**
 * @author DRTN
 * Team Website with download:
 * https://misterseph.github.io/DuckRelatedFractalProject/
 *
 * This Class contains either modifications or is entirely new in Assessment 3
 *
 * If you are in any doubt a complete changelog can be found here:
 * https://github.com/NotKieran/DRTN-Fractal/compare/Fractal_Initial...development
 *
 * And a more concise report can be found in our Change3 document.
 **/

package io.github.teamfractal.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import io.github.teamfractal.RoboticonQuest;
import io.github.teamfractal.screens.EndGameScreen;

import java.util.ArrayList;

/**
 * Created by Jack on 04/02/2017.
 */

public class EndGameActors extends Table {
    private RoboticonQuest game;
    private EndGameScreen screen;
    private ArrayList<Label> playerScores = new ArrayList<Label>();
    private Label winner;
    private Label title;
    private Label space;
    
    /**
     -     * Creates the labels that are to appear in the end game screen
     -     * @param game The current game
     -     * @param screen The screen the actors are to be placed on
     -     */
    public EndGameActors(final RoboticonQuest game, EndGameScreen screen){
        this.game = game;
        this.screen = screen;

        //Populate the player score list with scores from all of the players in the game
        for(int i = 0 ; i < game.getPlayerList().size(); i++)
        {
            playerScores.add(new Label("Player " + (i + 1) + " Score = " + game.getPlayerList().get(i).calculateScore(), game.skin));
        }

        this.winner = new Label(game.getWinnerText(), game.skin);
        winner.setAlignment(Align.center);
        this.title = new Label("End of Game", game.skin);
        this.space = new Label("      ", game.skin);
        add(title).padTop(-100);
        row();

        //Display all player scores
        for(Label score : playerScores)
        {
            row();
            add(score).padTop(10);
        }
        row();
        add(winner).padTop(50);
    }
}
