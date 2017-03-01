/*  JBT Assessment 4 Page: http://robins.tech/jbt/assfour.html
 *  JBT Changes to this file:
 *      Minor style changes (ie. adding brackets to one line if)
 *      Removed hasRoboticon which could be calculated when needed rather than having to be updated in many places.
 *      Split InstallRoboticon into smaller methods
 *      Deleted removeOwner as it was unused and untested
 *      Made x, y private as they were never accessed outside the class, and shouldnt be
 */

package io.github.teamfractal.entity;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import io.github.teamfractal.entity.enums.ResourceType;
import io.github.teamfractal.exception.AlreadyInstalledElsewhereException;
import io.github.teamfractal.exception.InvalidResourceTypeException;
import io.github.teamfractal.exception.NotCommonResourceException;
import io.github.teamfractal.util.PlotManager;

public class LandPlot {
    private static final int IndexOre = 0;
    private static final int IndexEnergy = 1;
    private static final int IndexFood = 2;
    private int x, y;

    /**
     * Saved modifiers for LandPlot.
     * [ Ore, Energy, Food ]
     */
    float[] productionModifiers = {0, 0, 0};

    // the different map layers for this tile
    private TiledMapTileLayer.Cell mapTile;
    private TiledMapTileLayer.Cell playerTile;
    private TiledMapTileLayer.Cell roboticonTile;

    /**
     * the owner of the tile
     */
    private Player owner;

    /**
     * The base production amounts.
     * [ Ore, Energy, Food ]
     */
    private int[] productionAmounts;

    /**
     * the roboticon installed on this tile
     */
    private Roboticon installedRoboticon;

    /**
     * Initialise LandPlot with specific base amount of resources.
     *
     * @param ore    Amount of ore
     * @param energy Amount of energy
     * @param food   Amount of food
     */
    public LandPlot(int ore, int energy, int food) {
        this.productionAmounts = new int[]{ore, energy, food};
    }

    public TiledMapTileLayer.Cell getMapTile() {
        return mapTile;
    }

    public TiledMapTileLayer.Cell getPlayerTile() {
        return playerTile;
    }

    public TiledMapTileLayer.Cell getRoboticonTile() {
        return roboticonTile;
    }

    public Player getOwner() {
        return owner;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * Sets the owner of the land plot to the specified player
     * @param player The player to be set as owner
     * @return Returns true if the land plot didn't already have an owner, false if it did
     */
    public boolean setOwner(Player player) {
        if (hasOwner()) {
            return false;
        }

        owner = player;
        player.addLandPlot(this);
        return true;
    }

    /**
     * Returns the state of the land plots ownership
     * @return True if owned, false otherwise
     */
    public boolean hasOwner() {
        return getOwner() != null;
    }


    /**
     * Retrieves the overlays for the specific tile
     * @param plotManager The plotmanager storing the images of the current map
     * @param x The x coordinate of the tile
     * @param y The y coordinate if the tile
     */
    public void setupTile (PlotManager plotManager, int x, int y) {
        this.x = x;
        this.y = y;
        this.mapTile = plotManager.getMapLayer().getCell(x, y);
        this.playerTile = plotManager.getPlayerOverlay().getCell(x, y);
        this.roboticonTile = plotManager.getRoboticonOverlay().getCell(x, y);
    }

    /**
     * Get the type index from the {@link ResourceType}
     * @param resource   The {@link ResourceType}
     * @return           The index.
     * @throws NotCommonResourceException Exception is thrown if the resource is invalid.
     */
    private static int resourceTypeToIndex(ResourceType resource) {
        switch (resource) {
            case ORE:    return IndexOre;
            case FOOD:   return IndexFood;
            case ENERGY: return IndexEnergy;
        }
        throw new NotCommonResourceException(resource);
    }

    // JBT Modified this method, separated out into different functions
    /**
     * Install a roboticon on this LandPlot.
     * @param roboticon    The roboticon to be installed.
     */
    public synchronized boolean installRoboticon(Roboticon roboticon) {
        if (hasRoboticon()) {
            return false;
        }
        boolean installedSuccessfully = tryInstallRoboticon(roboticon);
        if (installedSuccessfully) {
            installedRoboticon = roboticon;
            updateProductionModifiers(roboticon);
        }
        return installedSuccessfully;
    }

    // JBT created this method
    /**
     * get the roboticon installed on this tile
     * @return the roboticon installed on this tile, null if none is installed
     */
    public Roboticon getInstalledRoboticon() {
        return installedRoboticon;
    }

    // JBT Created this method
    /**
     * update the production modifiers based on a new roboticon
     * @param roboticon the roboticon which has just been installed
     */
    private synchronized void updateProductionModifiers(Roboticon roboticon) {
        ResourceType customisation = installedRoboticon.getCustomisation();
        if (customisation != ResourceType.Unknown) {
            int resourceIndex = resourceTypeToIndex(customisation);
            productionModifiers[resourceIndex] += 1;
        }
    }

    // JBT Created this method
    /**
     * attempt to install the roboticon on this tile
     * @param roboticon the roboticon to install
     * @return true on success, false on failure
     */
    private synchronized boolean tryInstallRoboticon(Roboticon roboticon) {
        try {
            roboticon.setInstalledLandPlot(this);
        } catch (AlreadyInstalledElsewhereException ex) {
            return false;
        }
        return true;
    }

    /**
     * Calculate the amount of resources to be produced.
     * Uses ProduceResource
     * @return The amount of resources to be produced in an array.
     */
    public int[] produceResources() {
        int[] produced = new int[productionModifiers.length];
        for (int i = 0; i < 2; i++) {
            produced[i] = (int) ((float) productionAmounts[i] * productionModifiers[i]);
        }
        return produced;
    }

    /**
     * Calculate the amount of resources to be produced for specific resource.
     * @param resource  The resource type to be calculated.
     * @return          Calculated amount of resource to be generated.
     */
    public int produceResource(ResourceType resource) {
        if (hasRoboticon()){
            int resIndex = resourceTypeToIndex(resource);
            return (int) ((float) productionAmounts[resIndex] * productionModifiers[resIndex]);
        } else {
            return 0;
        }
    }

    // JBT Changed this method so that exceptins are thrown for invalid resources
    /**
     * Gets the production of the specific resource
     * @param resource The resource selected
     * @return The index of the resource
     */
    public int getResource(ResourceType resource) throws InvalidResourceTypeException {
        if (resource == ResourceType.CUSTOMISATION ||
                resource == ResourceType.ROBOTICON ||
                resource == ResourceType.Unknown) {
            throw new InvalidResourceTypeException();
        }
        int resIndex = resourceTypeToIndex(resource);
        return productionAmounts[resIndex];
    }

    // JBT created this method so that installed roboticon could be calculated rather than having to be updated
    /**
     * Checks if the tile contains a roboticon
     * @return True if the tile contains a roboticon, false otherwise
     */
    public boolean hasRoboticon(){
        return installedRoboticon != null;
    }

}
