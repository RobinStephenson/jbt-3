/*  JBT Assessment 4 Page: http://robins.tech/jbt/assfour.html
 *  JBT Changes to this file:
 *		Added 4 player support in newGame
 *		Removed some commented out code
 */

package io.github.teamfractal.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.IsometricStaggeredTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.teamfractal.RoboticonQuest;
import io.github.teamfractal.actors.GameScreenActors;
import io.github.teamfractal.entity.AIPlayer;
import io.github.teamfractal.entity.HumanPlayer;
import io.github.teamfractal.entity.LandPlot;
import io.github.teamfractal.entity.Player;
import io.github.teamfractal.entity.enums.ResourceType;

import java.util.ArrayList;

public class GameScreen extends AbstractAnimationScreen implements Screen  {
	private final RoboticonQuest game;
	private final OrthographicCamera camera;
	private final Stage stage;
	private IsometricStaggeredTiledMapRenderer renderer;

	private TiledMap tmx;
	private TiledMapTileLayer mapLayer;
	private TiledMapTileLayer playerOverlay;

	private float oldX;
	private float oldY;

	private GameScreenActors actors;

	private LandPlot selectedPlot;
	private TiledMapTileSets tiles;

	private ArrayList<Overlay> overlayStack;

	private Chancellor chancellor;              //JBT
	private SpriteBatch chanceBatch;            //JBT
    private boolean chancellorEvent;            //JBT
    private float chancellorEventElapsed;       //JBT

	/**
	 * Initialise the class
	 * @param game  The game object
	 */
	public GameScreen(final RoboticonQuest game) {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.update();

		/**
		 * Defines the amount of pixels from each edge over which the map can be dragged off-screen
		 */
		final int spaceEdgePadding = 0;

		this.game = game;

		this.stage = new Stage(new ScreenViewport());
		this.actors = new GameScreenActors(game, this);
		actors.constructElements();
		// actors.textUpdate();

		overlayStack = new ArrayList<Overlay>();
		//Prepare the overlay stack to allow for numerous overlays to be stacked on top of one-another

        // Drag the map within the screen.
        stage.addListener(new DragListener() {
            /**
			 * On start of the drag event, record current position.
			 * @param event    The event object
			 * @param x        X position of mouse (on screen)
			 * @param y        Y position of mouse (on screen)
			 * @param pointer  unknown argument, not used.
			 */
			@Override
			public void dragStart(InputEvent event, float x, float y, int pointer) {
				oldX = x;
				oldY = y;
			}

			/**
			 * During the drag event, check against last recorded
			 * mouse positions, apply the offset in an opposite
			 * direction to the camera to create the drag effect.
			 *
			 * @param event    The event object.
			 * @param x        X position of mouse (on screen)
			 * @param y        Y position of mouse (on screen)
			 * @param pointer  unknown argument, not used.
			 */
			@Override
			public void drag(InputEvent event, float x, float y, int pointer) {
				// Prevent drag if the button is visible.
				if (actors.getBuyLandPlotBtn().isVisible()
						|| actors.installRoboticonVisible()) {
					return;
				}

				float deltaX = x - oldX;
				float deltaY = y - oldY;

				// The camera translates in a different direction...
				camera.translate(-deltaX, -deltaY);
				if (camera.position.x < 188 - spaceEdgePadding) camera.position.x = 188 - spaceEdgePadding;
				if (camera.position.y < 100 - spaceEdgePadding) camera.position.y = 100 - spaceEdgePadding;
				if (camera.position.x > 462 + spaceEdgePadding) camera.position.x = 462 + spaceEdgePadding;
				if (camera.position.y > 255 + spaceEdgePadding) camera.position.y = 255 + spaceEdgePadding;

				// Record cords
				oldX = x;
				oldY = y;

				// System.out.println("drag to " + x + ", " + y);
			}
		});

		// Set initial camera position
		camera.position.x = 325;
		camera.position.y = 220;

		//<editor-fold desc="Click event handler. Check `tileClicked` for how to handle tile click.">
		// Bind click event.
		stage.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (event.isStopped()) {
					return ;
				}

				// Hide dialog if it has focus.
				switch(game.getPhase()){
				case 1:
					if (actors.getBuyLandPlotBtn().isVisible()) {
						actors.hideBuyLand();
						return;
					}
					break;
				case 3:
					// Only click cancel will hide the dialog,
					// so don't do anything here.
					if (actors.installRoboticonVisible()) {
						return ;
					}
					break;
				}

				// The Y from screen starts from bottom left.
				Vector3 cord = new Vector3(x, Gdx.graphics.getHeight() - y, 0);
				camera.unproject(cord);

				// Padding offset
				cord.y -= 20;  // Padding from tile
				cord.x += 50;

				// Convert to grid index
				// http://2dengine.com/doc/gs_isometric.html

				float tile_height = mapLayer.getTileHeight();
				float tile_width = mapLayer.getTileWidth();

				float ty = cord.y - cord.x/2 - tile_height;
				float tx = cord.x + ty;
				ty = MathUtils.ceil(-ty/(tile_width/2));
				tx = MathUtils.ceil(tx/(tile_width/2)) + 1;
				int tileIndexX = MathUtils.floor((tx + ty)/2);
				int tileIndexY = -(int)(ty - tx);

				// Those magic numbers based on observation of number patterns
				tileIndexX -= 1;
				if (tileIndexY % 2 == 0) {
					tileIndexX --;
				}

                setSelectedPlot(game.plotManager.getPlot(tileIndexX, tileIndexY));
				if (selectedPlot != null) {
					actors.tileClicked(selectedPlot, x, y);
				}

			}
		});
		//</editor-fold>

        //JBT - Create a new chancellor instance for the catch the chancellor mini-game
        chancellor = new Chancellor();
        chanceBatch = new SpriteBatch();

		// Finally, start a new game and initialise variables.
		// newGame();
	}

    public LandPlot getSelectedPlot() {
        return selectedPlot;
    }

	public void setSelectedPlot(LandPlot plot) {
		selectedPlot = plot;
	}

	/**
	 * gets the players tile to put over a tile they own
	 * @param player player to buy plot
	 * @return tile that has the coloured outline associated with the player
	 */
	public TiledMapTile getPlayerTile(Player player) {
		return tiles.getTile(71 + game.getPlayerIndex(player)); //where 71 is the total amount of tiles in raw folder, 71+ flows into player folder
	}
	/**
	 * gets the tile with the players colour and the roboticon specified to mine that resource
	 * @param player player who's colour you want
	 * @param type type of resource roboticon is specified for
     * @return the tile image
     */
    public TiledMapTile getResourcePlayerTile(Player player, ResourceType type){
		switch(type){

            case ORE:
                return tiles.getTile(71 + game.getPlayerIndex(player) + 4);
			case ENERGY:
				return tiles.getTile(71 + game.getPlayerIndex(player) + 8);
			case FOOD:
				return tiles.getTile(71 + game.getPlayerIndex(player) + 16);
		default:
			return tiles.getTile(71 + game.getPlayerIndex(player) + 12);
		}
	}

	// Updated by JBT
	/**
	 * Set the state of the game to a new game with the given configuration of players
	 * @param humanPlayers how many human players there should be
	 * @param aiPlayers how many AI players there should be
	 */
	public void newGame(int humanPlayers, int aiPlayers) {
		// Setup the game board.
		if (tmx != null) tmx.dispose();
		if (renderer != null) renderer.dispose();
		this.tmx = new TmxMapLoader().load("tiles/city.tmx");
		tiles = tmx.getTileSets();
		renderer = new IsometricStaggeredTiledMapRenderer(tmx);
		game.reset(humanPlayers, aiPlayers);

		mapLayer = (TiledMapTileLayer)tmx.getLayers().get("MapData");
		playerOverlay = (TiledMapTileLayer)tmx.getLayers().get("PlayerOverlay");

        game.plotManager.setup(tiles, tmx.getLayers());
        game.nextPhase();
	}

    public void plotmanagerSetup() {
        game.plotManager.setup(tiles, tmx.getLayers());
    }

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();

		renderer.setView(camera);
		renderer.render();

		stage.act(delta);
		stage.draw();

		renderAnimation(delta);

		//Disable the chancellor event if not in the first phase
		if(game.getPhase() != 1) {
            chancellorEvent = false;
            actors.hideChancellorLabel();
        }

		switch (game.getPhase()) {
			case (1):
			    //Get the actual mouse coords relative to the camera
			    Vector3 mouseCoords = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(),0));

			    //Extract the X and Y mouse coords from the relative coords
                float mouseX = mouseCoords.x;
                float mouseY = mouseCoords.y;

			    //Draw any overlays in the stack
				if (overlayStack.isEmpty() || overlayStack == null) {
					Gdx.input.setInputProcessor(stage);
                    //If the chancellor event is happening, draw the chancellor and enable catching
                    if(chancellorEvent) {
                        //Add the time since the last frame to the elapsed time of he chancellor event
                        chancellorEventElapsed += delta;

                        //If 15 seconds have passed since the start of the event, then stop the chancellor event
                        if(chancellorEventElapsed > 15)
                        {
                            chancellorEvent = false;
                            actors.showChancellorLabel(false);
                        }

                        //Update the position of the chancellor sprite and draw it
                        chanceBatch.begin();
                        chanceBatch.setProjectionMatrix(camera.combined);
                        chancellor.updatePosition();
                        chancellor.sprite.draw(chanceBatch);
                        chanceBatch.end();

                        //If the mouse is within the bounds of the chancellor sprite and the left button is clicked, then catch it
                        if (Gdx.input.isButtonPressed(0) && chancellor.sprite.getBoundingRectangle().contains(mouseX, mouseY)) {
                            chancellorEvent = false;
                            game.getPlayer().caughtChancellor();
                            actors.showChancellorLabel(true);
                        }
                    }
				} else {
					Gdx.input.setInputProcessor(overlayStack.get(overlayStack.size() - 1));

					overlayStack.get(overlayStack.size() - 1).act(delta);
					overlayStack.get(overlayStack.size() - 1).draw();
				}
				break;
			case (2):
				game.roboticonMarket.act(delta);
				game.roboticonMarket.draw();
				break;
			case (4):
				game.genOverlay.act(delta);
				game.genOverlay.draw();
				break;
			case (5):
				game.resourceMarket.act(delta);
				game.resourceMarket.draw();
		}
	}

	/**
	 * Resize the viewport as the render window's size change.
     * @param width   The new x
     * @param height  The new y
     */
    @Override
	public void resize(int width, int height) {
		/*
		stage.getViewport().update(width, height, true);
		game.getBatch().setProjectionMatrix(stage.getCamera().combined);
		camera.setToOrtho(false, width, height);
		actors.resizeScreen(width, height);
		oldW = width;
		oldH = height;

		if (mapLayer != null) {
			camera.translate(-((Gdx.graphics.getWidth() - (mapLayer.getTileWidth() * mapLayer.getWidth())) / 2), -((Gdx.graphics.getHeight() - (mapLayer.getTileHeight() * mapLayer.getHeight())) / 2));
		}
		//NEED TO TRANSLATE BY (WINDOW WIDTH - MAP WIDTH) / 2, AND SAME FOR HEIGHT
		*/

		//Disabled this code for now as the game window is not currently resizable
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		if(tmx != null){
			tmx.dispose();
		}
		if(renderer != null) {
			renderer.dispose();
		}
		if(stage != null) {
			stage.dispose();
		}

		//Added by JBT
        if(chancellor != null) {
            chancellor.dispose();
        }
        if(chanceBatch != null) {
		    chanceBatch.dispose();
        }
	}

	@Override
	public RoboticonQuest getGame() {
		return game;
	}

	public Stage getStage() {
		return stage;
	}

	@Override
	public Size getScreenSize() {
		Size s = new Size();
		s.Width = Gdx.graphics.getWidth();
		s.Height = Gdx.graphics.getHeight();
		return s;
	}

	//Added by JBT
    /**
     * Called by the chancellor random event when started
     */
	public void startChancellorEvent()
    {
        //Only start the chancellor event if the player is human
        if(game.getPlayer() instanceof HumanPlayer)
        {
            System.out.println("Chancellor event started!");
            chancellorEvent = true;
            chancellorEventElapsed = 0;
        }
    }

	public TiledMap getTmx(){
		return this.tmx;
	}
	
	public GameScreenActors getActors(){
		return this.actors;
	}

	public void addOverlay(Overlay overlay) {
		overlayStack.add(overlay);
	}

	public void removeOverlay() {
		if (!overlayStack.isEmpty()) {
			overlayStack.remove(overlayStack.size() - 1);
		}
	}
}