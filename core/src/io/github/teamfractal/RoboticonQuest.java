package io.github.teamfractal;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Timer;
import io.github.teamfractal.animation.AnimationPhaseTimeout;
import io.github.teamfractal.animation.AnimationShowPlayer;
import io.github.teamfractal.animation.IAnimationFinish;
import io.github.teamfractal.entity.*;
import io.github.teamfractal.screens.*;
import io.github.teamfractal.util.PlotEffectSource;
import io.github.teamfractal.util.PlotManager;

import java.util.ArrayList;
import java.util.Random;

/**
 * This is the main game start up class.
 * It will set up all the necessary classes.
 */
public class RoboticonQuest extends Game {
    private static RoboticonQuest _instance;
	public TiledMap tmx;
	public Skin skin;
	public GameScreen gameScreen;
	public Market market;
    public PlotManager plotManager;
    public RoboticonMarketScreen roboticonMarket;
    public GenerationOverlay genOverlay;
    private int trueTurnNumber = 1;
    private SpriteBatch batch;
    private MainMenuScreen mainMenuScreen;
    private ArrayList<Player> playerList;
    private int phase;
	private int landBoughtThisTurn;
	private float effectChance;
	private int currentPlayerIndex;
	private PlotEffectSource plotEffectSource;

	public RoboticonQuest() {
		_instance = this;
		reset(false);
	}

	public static RoboticonQuest getInstance() {
		return _instance;
	}

	/**
	 * Getter for the index of the current Player
	 * @param player The player that the index is being retrieved for
	 * @return The index of the specified player
	 */
	public int getPlayerIndex (Player player) {

		return playerList.indexOf(player);
	}
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		setupSkin();

		// Setup other screens.
		mainMenuScreen = new MainMenuScreen(this);
        gameScreen = new GameScreen(this);
        roboticonMarket = new RoboticonMarketScreen(this, Color.GRAY, Color.WHITE, 3);
        genOverlay = new GenerationOverlay(Color.GRAY, Color.WHITE, 3);

		//Setup tile and player effects for later application
		setupEffects();

		setScreen(mainMenuScreen);
	}
	/**
	 * Getter for the batch
	 * @return The batch of the game
	 */
	public Batch getBatch() {
		return batch;
	}

	/**
	 * Setup the default skin for GUI components.
	 */
	private void setupSkin() {
		skin = new Skin(
			Gdx.files.internal("skin/skin.json"),
			new TextureAtlas(Gdx.files.internal("skin/skin.atlas"))
		);
	}

	/**
	 * Clean up
	 */
	@Override
	public void dispose () {
		mainMenuScreen.dispose();
		gameScreen.dispose();
		skin.dispose();
		batch.dispose();
	}
	/**
	 * Getter for the current phase
	 * @return The current phase of the game
	 */
	public int getPhase(){
		return this.phase;
	}
	/**
	 * Setter for the current phase
	 * @param phase The phase that the current phase is to be set to
	 */
	public void setPhase(int phase) {
		this.phase = phase;
		implementPhase();
	}
	/**
	 * Resets the statistics of all the game's entities
	 * @param AI A boolean describing whether an AI player is playing or not
	 */
	public void reset(boolean AI) {
        this.currentPlayerIndex = 0;
        this.phase = 0;
        plotManager = new PlotManager();
        Player player1;
        Player player2;
        if (AI) {
            player1 = new AIPlayer(this);
            player2 = new Player(this);
        } else{
            player1 = new Player(this);
            player2 = new Player(this);
        }

        this.playerList = new ArrayList<Player>();
        this.playerList.add(player1);
		this.playerList.add(player2);
        this.currentPlayerIndex = 0;
        this.market = new Market();

    }
	/**
	 * Implements the functionality of the current phase
	 */
    private void implementPhase() {
        System.out.println("RoboticonQuest::nextPhase -> newPhaseState: " + phase);
		switch (phase) {
			// Phase 2: Purchase Roboticon
			case 2:
                Gdx.input.setInputProcessor(roboticonMarket);

				AnimationPhaseTimeout timeoutAnimation = new AnimationPhaseTimeout(getPlayer(), this, phase, 30);
				gameScreen.addAnimation(timeoutAnimation);

				roboticonMarket.actors().widgetUpdate();

				gameScreen.getActors().setNextButtonVisibility(false);
				this.getPlayer().takeTurn(2);
                break;


			// Phase 3: Roboticon Customisation
			case 3:
                Gdx.input.setInputProcessor(gameScreen.getStage());

				timeoutAnimation = new AnimationPhaseTimeout(getPlayer(), this, phase, 30);
				gameScreen.addAnimation(timeoutAnimation);
				timeoutAnimation.setAnimationFinish(new IAnimationFinish() {
					@Override
					public void OnAnimationFinish() {
						gameScreen.getActors().hideInstallRoboticon();
					}
				});
				gameScreen.getActors().updateRoboticonSelection();

				gameScreen.getActors().switchNextButton();
				this.getPlayer().takeTurn(3);
                break;

			// Phase 4: Generate resources for player
			case 4:
                Gdx.input.setInputProcessor(genOverlay);

                this.getPlayer().generateResources();

                Timer timer = new Timer();
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
						nextPhase();
                        //This check is needed to stop any future phases from being cut short by accident
                    }
                }, 3);
                timer.start();

				gameScreen.getActors().switchNextButton();
                break;

			// Phase 5: Purchase resources
			case 5:
			    ResourceMarketScreen RMS = new ResourceMarketScreen(this);
				setScreen(RMS);

                Gdx.input.setInputProcessor(RMS.getStage());

				this.getPlayer().takeTurn(5);
				break;

			// End phase - CLean up and move to next player.
			case 6:
				phase = 1;

                if (checkGameEnded()) {
					setScreen(new EndGameScreen(this));
					break;
				}

                this.trueTurnNumber += 1;
                this.nextPlayer();

				// No "break;" here!
				// Let the game to do phase 1 preparation.

			// Phase 1: Enable of purchase LandPlot
			case 1:
                Gdx.input.setInputProcessor(gameScreen.getStage());

				setScreen(gameScreen);
				landBoughtThisTurn = 0;
				gameScreen.addAnimation(new AnimationShowPlayer(getPlayerInt() + 1));

				clearEffects();
				setEffects();

                System.out.println("Player: " + this.currentPlayerIndex + " Turn: " + this.getTurnNumber());

				if (getPlayer().getMoney() < 10) {
					gameScreen.getActors().setNextButtonVisibility(true);
				} else {
					gameScreen.getActors().setNextButtonVisibility(false);
				}
        		this.getPlayer().takeTurn(1);
				break;
		}


		if (gameScreen != null)
			gameScreen.getActors().textUpdate();
	}
	/**
	 * Advances the current phase
	 */
	public void nextPhase() {
        if ((phase == 1) && (landBoughtThisTurn == 0) && (this.getPlayer().getMoney() >= 10)) {
            return;
        }
        phase += 1;
        implementPhase();
	}

	/**
	 * Event callback on player bought a {@link io.github.teamfractal.entity.LandPlot}
	 */
	public void landPurchasedThisTurn() {
		landBoughtThisTurn ++;
	}
	/**
	 * Getter for landBoughtThisTurn
	 -	 * @return Returns true if land hasn't been purchased this turn, false otherwise
	 -	 */
	public boolean canPurchaseLandThisTurn () {
		return (landBoughtThisTurn < 1 && getPlayer().getMoney() >= 10);
	}
	/**
	 * Returns a string describing the current phase
	 * @return A string with the description of the current phase
	 */
	public String getPhaseString () {
		int phase = getPhase();

		switch(phase){
			case 1:
				return "Buy Land Plot";

			case 2:
				return "Purchase Roboticons";

			case 3:
				return "Install Roboticons";

			case 4:
				return "Resource Generation";

			case 5:
				return "Resource Auction";

			default:
				return "Unknown phase";
		}

	}
	/**
	 * Getter for the current player
	 * @return The current player
	 */
	public Player getPlayer(){

        return this.playerList.get(this.currentPlayerIndex);
    }
	/**
	 * Getter for the index of the current player
	 * @return The index of the current player
	 */
    public int getPlayerInt() {
        return this.currentPlayerIndex;
    }

	/**
	 * Changes the current player
	 */
    private void nextPlayer() {


        if (this.currentPlayerIndex == playerList.size() - 1) {
            this.currentPlayerIndex = 0;
        } else {
            this.currentPlayerIndex++;
        }


    }

	/**
	 * Creates and initialises all of the effects
	 */
	private void setupEffects() {
		//Initialise the fractional chance of any given effect being applied at the start of a round
		effectChance = (float) 0.05;

		plotEffectSource = new PlotEffectSource(this);

		for (PlotEffect PE : plotEffectSource) {
			PE.constructOverlay(gameScreen);
		}
	}
	/**
	 * Randomly applies the effects
	 */
	private void setEffects() {
		Random RNGesus = new Random();

		for (PlotEffect PE : plotEffectSource) {
			if (RNGesus.nextFloat() <= effectChance) {
				PE.executeRunnable();

				gameScreen.addOverlay(PE.overlay());
			}
		}
	}
	/**
	 * Clears the effects of all the effects
	 */
	private void clearEffects() {
		for (PlotEffect PE : plotEffectSource) {
			PE.revertAll();
		}
	}

	/**
	 * Checks whether the game has ended based on whether all of the tiles have been claimed
	 * @return Returns true if ended, false if not
	 */
    private boolean checkGameEnded() {
        boolean ended = true;
		LandPlot[][] plots = plotManager.getLandPlots();
        for (LandPlot[] plot : plots) {
            for (LandPlot aPlot : plot) {
                if (aPlot == null) {
					ended = false;
                }
            }
        }
        return ended;
	}

	/**
	 * Returns the winner of the game, based on which player has the highest score
     * @return String returning the winning player
     */

	public String getWinner(){
        String winner;
        if(playerList.get(0).calculateScore() > playerList.get(1).calculateScore()) {
			winner = "Player 1";
		}
		else{
				winner = "Player 2";
			}
		return winner;
	}

	/**
	 * Getter for the players of the game.
	 * @return The array containing the two players
	 */
	public ArrayList<Player> getPlayerList(){
		return this.playerList;
	}

    public int getTurnNumber() {
        return (int) Math.ceil((double) trueTurnNumber / 2);
    }
}


