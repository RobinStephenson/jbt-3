/*  JBT Assessment 4 Page: http://robins.tech/jbt/assfour.html
 *  JBT Changes to this file:
 *		Added new UI elements to let the user input how many players and of what type they want
 *		Added small amount of logic (only related to UI and preventing false input)
 */

package io.github.teamfractal.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import io.github.teamfractal.RoboticonQuest;

import java.nio.InvalidMarkException;


public class HomeMainMenu extends Table {
    private static Texture titleTexture = new Texture(Gdx.files.internal("roboticon_images/Duck-Related Roboticon Quest (Small).png"));
    private RoboticonQuest game;

    private TextButton btnNewGame;
	private TextButton btnExit;

	private TextButton btnIncreaseNumberHumanPlayers;
	private TextButton btnIncreaseNumberAIPlayers;
	private TextButton btnDecreaseNumberHumanPlayers;
	private TextButton btnDecreaseNumberAIPlayers;
	private Label numberOfHumansLabel;
	private Label numberOfAiLabel;

	private int numberOfHumanPlayers = 1;
	private int numberOfAIPlayers = 0;

	// Updated by JBT
	/**
	 * Initialise the Home Menu.
	 * @param game    The game object.
	 */
	public HomeMainMenu(RoboticonQuest game) {
		this.game = game;

		// Create UI Components
		final Image imgTitle = new Image();
		imgTitle.setDrawable(new TextureRegionDrawable(new TextureRegion(titleTexture)));

		btnNewGame = new TextButton("Begin Game", game.skin);

		btnExit = new TextButton("Exit", game.skin);

		btnIncreaseNumberHumanPlayers = new TextButton("+", game.skin);
		btnDecreaseNumberHumanPlayers = new TextButton("-", game.skin);
		btnIncreaseNumberAIPlayers = new TextButton("+", game.skin);
		btnDecreaseNumberAIPlayers = new TextButton("-", game.skin);
		Label.LabelStyle style = new Label.LabelStyle(game.smallFontRegular.font(), Color.BLACK);
		numberOfHumansLabel = new Label("Human Players: " + Integer.toString(numberOfHumanPlayers), style);
		numberOfAiLabel = new Label("AI Players: " + Integer.toString(numberOfAIPlayers), style);
		numberOfHumansLabel.setAlignment(Align.center);
        numberOfAiLabel.setAlignment(Align.center);

		btnIncreaseNumberHumanPlayers.pad(10);
		btnDecreaseNumberHumanPlayers.pad(10);
		btnIncreaseNumberAIPlayers.pad(10);
		btnDecreaseNumberAIPlayers.pad(10);


		// Bind events to buttons
		bindEvents();

		updateWhichButtonsAreDisabled();

		// Adjust properties.
		btnNewGame.pad(10);
		btnExit.pad(10);

		// Add the UI components for selecting how many players
        add(btnDecreaseNumberHumanPlayers).right().width(40);
		add(numberOfHumansLabel).center().width(160);
        add(btnIncreaseNumberHumanPlayers).left().width(40);
		row();
        add(btnDecreaseNumberAIPlayers).right().width(40);
        add(numberOfAiLabel).center().width(160);
		add(btnIncreaseNumberAIPlayers).left().width(40);
		row();

		add(btnNewGame).colspan(3).center();
		row();
		add(btnExit).colspan(3).center();
	}

	// Created by JBT
	/**
	 * Increase the number of other human players the player wants to play with and update GUI accordingly
	 */
	private void incrementNumberOfHumanPlayers() {
		numberOfHumanPlayers++;
		updateWhichButtonsAreDisabled();
		numberOfHumansLabel.setText("Human Players: " + Integer.toString(numberOfHumanPlayers));

	}

	// Created by JBT
	/**
	 * decrease the number of other human players the player wants to play with and update GUI accordingly
	 */
	private void decrementNumberOfHumanPlayers() {
		numberOfHumanPlayers--;
		updateWhichButtonsAreDisabled();
		numberOfHumansLabel.setText("Human Players: " + Integer.toString(numberOfHumanPlayers));

	}

	// Created by JBT
	/**
	 * increase the number of AI players the player wants to play with and update GUI accordingly
	 */
	private void incrementNumberOfAIPlayers() {
		numberOfAIPlayers++;
		updateWhichButtonsAreDisabled();
		numberOfAiLabel.setText("AI Players: " + Integer.toString(numberOfAIPlayers));

	}

	// Created by JBT
	/**
	 * decrease the number of AI players the player wants to play with and update GUI accordingly
	 */
	private void decrementNumberOfAIPlayers() {
		numberOfAIPlayers--;
		updateWhichButtonsAreDisabled();
		numberOfAiLabel.setText("AI Players: " + Integer.toString(numberOfAIPlayers));
	}

	// Created by JBT
	/**
	 * Updates which UI buttons are disabled based on the current Human/AI players configuration
	 * Disables buttons which should not be pressed because they would make the game enter an invalid state
	 * Enables buttons which have become vaild
	 */
	private void updateWhichButtonsAreDisabled() {
		int totalPlayers = numberOfHumanPlayers + numberOfAIPlayers;
		if (totalPlayers == 4) {
			btnIncreaseNumberHumanPlayers.setDisabled(true);
			btnIncreaseNumberHumanPlayers.setVisible(false);
			btnIncreaseNumberAIPlayers.setDisabled(true);
            btnIncreaseNumberAIPlayers.setVisible(false);
		} else {
			btnIncreaseNumberHumanPlayers.setDisabled(false);
            btnIncreaseNumberHumanPlayers.setVisible(true);
			btnIncreaseNumberAIPlayers.setDisabled(false);
            btnIncreaseNumberAIPlayers.setVisible(true);
		}

		if (numberOfHumanPlayers == 1) {
			btnDecreaseNumberHumanPlayers.setDisabled(true);
            btnDecreaseNumberHumanPlayers.setVisible(false);
		} else {
			btnDecreaseNumberHumanPlayers.setDisabled(false);
            btnDecreaseNumberHumanPlayers.setVisible(true);
		}

		if (numberOfAIPlayers == 0) {
			btnDecreaseNumberAIPlayers.setDisabled(true);
            btnDecreaseNumberAIPlayers.setVisible(false);
		} else {
			btnDecreaseNumberAIPlayers.setDisabled(false);
            btnDecreaseNumberAIPlayers.setVisible(true);
		}

		if (totalPlayers < 2) {
			btnNewGame.setDisabled(true);
		} else {
			btnNewGame.setDisabled(false);
		}
	}

	/**
	 * Bind button events.
	 */
	private void bindEvents() {
		btnNewGame.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				game.setScreen(game.gameScreen);
				game.gameScreen.newGame(numberOfHumanPlayers, numberOfAIPlayers);
			}
		});

		btnDecreaseNumberAIPlayers.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				decrementNumberOfAIPlayers();
			}
		});

		btnIncreaseNumberAIPlayers.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				incrementNumberOfAIPlayers();
			}
		});

		btnIncreaseNumberHumanPlayers.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				incrementNumberOfHumanPlayers();;
			}
		});

		btnDecreaseNumberHumanPlayers.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				decrementNumberOfHumanPlayers();
			}
		});

		btnExit.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
	}
}
