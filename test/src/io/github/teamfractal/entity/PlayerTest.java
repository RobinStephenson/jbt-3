/*  JBT Assessment 4 Page: http://robins.tech/jbt/assfour.html
 *  JBT Changes to this file:
 *      Created additional tests to increase coverage / rigorousness
 *      Replaced some poorly written tests
 */

package io.github.teamfractal.entity;

import io.github.teamfractal.RoboticonQuest;
import io.github.teamfractal.TesterFile;
import io.github.teamfractal.entity.enums.ResourceType;
import io.github.teamfractal.exception.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.lwjgl.Sys;

import static org.junit.Assert.assertEquals;

public class PlayerTest extends TesterFile {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private Player player;
    private RoboticonQuest game;  //Added by JBT

    @Before
    public void setUp() {
        game = new RoboticonQuest();
        player = new Player(game);
    }

    @Test
    public void testPlayerInitialMoney() {
        assertEquals(100, player.getMoney());
    }

    // Test Created by JBT
    /**
     * trying to set Money to a negative amount should throw an UnsupportedOperationException
     */
    @Test(expected = IllegalArgumentException.class)
    public void setNegativeMoney() {
        player.setMoney(-1);
    }

    /**
     * Selling a resource to the market should update the players money and resources, and the markets resources
     */
    @Test
    public void testPlayerSellResource() {
        Market market = new Market();

        player.setMoney(1000);
        player.setResource(ResourceType.ORE, 15);
        player.setResource(ResourceType.ENERGY, 15);

        int orePrice = market.getBuyPrice(ResourceType.ORE);
        //sell 5 ore
        player.sellResourceToMarket(5, market, ResourceType.ORE);
        assertEquals(1000 + 5 * orePrice, player.getMoney());
        assertEquals(10, player.getOre());
        assertEquals(5, market.getOre());

        int energyPrice = market.getBuyPrice(ResourceType.ENERGY);
        player.setMoney(1000);
        //sell 5 energy
        player.sellResourceToMarket(5, market, ResourceType.ENERGY);
        assertEquals(1000 + 5 * energyPrice, player.getMoney());
        assertEquals(10, player.getEnergy());
        assertEquals(21, market.getEnergy());
    }

    // Test created by JBT
    /**
     * attempting to set ore to a negative amount should throw an error
     */
    @Test(expected = IllegalArgumentException.class)
    public void setNegativeOre() {
        player.setOre(-1);
    }

    // Test created by JBT
    /**
     * attempting to set energy to a negative amount should throw an error
     */
    @Test(expected = IllegalArgumentException.class)
    public void setNegativeEnergy() {
        player.setEnergy(-1);
    }

    // Test created by JBT
    /**
     * attempting to set Food to a negative amount should throw an error
     */
    @Test(expected = IllegalArgumentException.class)
    public void setNegativeFood() {
        player.setFood(-1);
    }

    // Test created by JBT
    /**
     * attempting to set an invalid resource type should throw an exception
     */
    @Test(expected = NotCommonResourceException.class)
    public void setUnknownResource() {
        player.setResource(ResourceType.Unknown, 1);
    }

    // Test created by JBT
    /**
     * attempting to set an invalid resource type should throw an exception
     */
    @Test(expected = NotCommonResourceException.class)
    public void setCustomisationResource() {
        player.setResource(ResourceType.CUSTOMISATION, 1);
    }

    // Test created by JBT
    /**
     * getResource should throw a NotCommonResourceException if called with a resource other that food, ore, energy
     */
    @Test(expected = NotCommonResourceException.class)
    public void getResourceCustomisation() {
        player.getResource(ResourceType.CUSTOMISATION);
    }

    // Test created by JBT
    /**
     * getResource should throw a NotCommonResourceException if called with a resource other that food, ore, energy
     */
    @Test(expected = NotCommonResourceException.class)
    public void getResourceUnknown() {
        player.getResource(ResourceType.Unknown);
    }

    // Test Created by JBT
    /**
     * purchaseRoboticonFromMarket should throw an exception if amount specified is 0
     */
    @Test(expected = IllegalArgumentException.class)
    public void purchaseRoboticonsZero() {
        Market market = new Market();
        market.setRoboticon(1);
        player.setMoney(Integer.MAX_VALUE);
        player.purchaseRoboticonsFromMarket(0, market);
    }

    // Test Created by JBT
    /**
     * purchaseRoboticonFromMarket should throw an exception if amount specified is 0
     */
    @Test(expected = IllegalArgumentException.class)
    public void purchaseRoboticonsNegative() {
        Market market = new Market();
        market.setRoboticon(1);
        player.setMoney(Integer.MAX_VALUE);
        player.purchaseRoboticonsFromMarket(-1, market);
    }

    // Test Created by JBT
    /**
     * purchaseRoboticonFromMarket should throw an exception if the given market is null
     */
    @Test(expected = NullPointerException.class)
    public void purchaseRoboticonsNullMarket() {
        player.setMoney(Integer.MAX_VALUE);
        player.purchaseRoboticonsFromMarket(1, null);
    }

    // Test created by JBT
    /**
     * purchaseRoboticonsFromMarket should throw an exception if the market does not have enough stock
     */
    @Test(expected = NotEnoughResourceException.class)
    public void purchaseRoboticonsFromMarketNotEnoughInMarket() {
        Market market = new Market();
        market.setRoboticon(0);
        player.setMoney(0);
        player.purchaseRoboticonsFromMarket(1, market);
    }

    // Test created by JBT
    /**
     * purchaseRoboticonsFromMarket should throw an exception if the player does not have enough money for the transaction
     */
    @Test(expected = NotEnoughMoneyException.class)
    public void purchaseRoboticonsFromMarketNotEnoughMoney() {
        Market market = new Market();
        market.setRoboticon(1);
        player.setMoney(0);
        player.purchaseRoboticonsFromMarket(1, market);
    }

    // Test created by JBT
    /**
     * purchaseRoboticons should subtract the cost of the transaction from the player when a transaction is successful
     */
    @Test
    public void purchaseRoboticonsMoneySubtracted() {
        Market market = new Market();
        market.setRoboticon(1);
        player.setMoney(market.getSellPrice(ResourceType.ROBOTICON));
        player.purchaseRoboticonsFromMarket(1, market);
        assertEquals(0, player.getMoney());
    }

    // Test created by JBT
    /**
     * purchaseRoboticonsFromMarket should update the players list of roboticons when a transaction is successful
     */
    @Test
    public void purchaseRoboticonsListUpdated() {
        Market market = new Market();
        market.setRoboticon(1);
        player.setMoney(market.getSellPrice(ResourceType.ROBOTICON));
        player.purchaseRoboticonsFromMarket(1, market);
        assertEquals(1, player.roboticonList.size);
    }

    // Test Created by JBT
    /**
     * purchaseCustomisationFromMarket should throw an exception if the requested customisation is invalid
     */
    @Test(expected = InvalidResourceTypeException.class)
    public void purchaseInvalidCustomisation() {
        Market market = new Market();
        Roboticon roboticon = new Roboticon(1);
        player.setMoney(Integer.MAX_VALUE);
        player.purchaseCustomisationFromMarket(ResourceType.Unknown, roboticon, market);
    }

    // Test Created by JBT
    /**
     * purchaseCustomisationFromMarket should throw an exception if the given market is null
     */
    @Test(expected = NullPointerException.class)
    public void purchaseCustomisationNullMarket() {
        Roboticon roboticon = new Roboticon(1);
        player.setMoney(Integer.MAX_VALUE);
        player.purchaseCustomisationFromMarket(ResourceType.FOOD, roboticon, null);
    }

    // Test Created by JBT
    /**
     * purchaseCustomisationFromMarket should throw an exception if the given roboticon is null
     */
    @Test(expected = NullPointerException.class)
    public void purchaseCustomisationNullRoboticon() {
        Market market = new Market();
        player.setMoney(Integer.MAX_VALUE);
        player.purchaseCustomisationFromMarket(ResourceType.FOOD, null, market);
    }

    // Test Created by JBT
    /**
     * purchaseCustomisationFromMarket should throw an exception if the player does not have enough money
     */
    @Test(expected = NotEnoughMoneyException.class)
    public void purchaseCustomisationNotEnoughMoney() {
        Market market = new Market();
        Roboticon roboticon = new Roboticon(1);
        player.setMoney(0);
        player.purchaseCustomisationFromMarket(ResourceType.ENERGY, roboticon, market);
    }

    // Test Created by JBT
    /**
     * purchaseCustomisationFromMarket should add the customisation to the roboticon if the transaction is successful
     * player money should also be updated
     */
    @Test
    public void purchaseCustomisationSuccessful() {
        Market market = new Market();
        Roboticon roboticon = new Roboticon(1);
        player.setMoney(0);
        player.setMoney(market.getSellPrice(ResourceType.CUSTOMISATION));
        player.purchaseCustomisationFromMarket(ResourceType.ORE, roboticon, market);
        assertEquals(ResourceType.ORE, roboticon.getCustomisation());
        assertEquals(0, player.getMoney());
    }

    // Test Created by JBT
    /**
     * purchaseResourceFromMarket should throw an exception if the player does not have enough money
     */
    @Test(expected = NotEnoughMoneyException.class)
    public void purchaseResourceNotEnoughMoney() {
        Market market = new Market();
        market.setFood(1);
        player.setMoney(0);
        player.purchaseResourceFromMarket(1, market, ResourceType.FOOD);
    }

    // Test Created by JBT
    /**
     * purchaseResourceFromMarket should throw an exception if the market does not have enough resources
     */
    @Test(expected = NotEnoughResourceException.class)
    public void purchaseResourceNotEnoughStock() {
        Market market = new Market();
        market.setFood(0);
        player.setMoney(Integer.MAX_VALUE);
        player.purchaseResourceFromMarket(1, market, ResourceType.FOOD);
    }

    // Test Created by JBT
    /**
     * purchaseResourceFromMarket should throw an exception if the amount requested is = 0
     */
    @Test(expected = IllegalArgumentException.class)
    public void purchaseResourceZero() {
        Market market = new Market();
        market.setFood(1);
        player.setMoney(Integer.MAX_VALUE);
        player.purchaseResourceFromMarket(0, market, ResourceType.FOOD);
    }

    // Test Created by JBT
    /**
     * purchaseResourceFromMarket should throw an exception if the amount requested is < 0
     */
    @Test(expected = IllegalArgumentException.class)
    public void purchaseResourceNegative() {
        Market market = new Market();
        market.setFood(1);
        player.setMoney(Integer.MAX_VALUE);
        player.purchaseResourceFromMarket(-1, market, ResourceType.FOOD);
    }

    // Test Created by JBT
    /**
     * purchaseResourceFromMarket should throw an exception if the given market is null
     */
    @Test(expected = NullPointerException.class)
    public void purchaseResourceNullMarket() {
        player.setMoney(Integer.MAX_VALUE);
        player.purchaseResourceFromMarket(1, null, ResourceType.FOOD);
    }

    /**
     * purchaseResourceFromMarket should update the players resources and money if the transaction is successful
     */
    @Test
    public void testPlayerBuyResource() {
        Market market = new Market();
        market.setOre(16);
        player.setMoney(1000);


        int playerMoney = player.getMoney();
        int orePrice = market.getSellPrice(ResourceType.ORE);
        //Purchase 5 ore
        player.purchaseResourceFromMarket(5, market, ResourceType.ORE);
        // Player should now have 5 more ores, and the market have 5 less ores.
        assertEquals(playerMoney - 5 * orePrice, player.getMoney());
        assertEquals(5, player.getOre());
        assertEquals(11, market.getOre());


        playerMoney = player.getMoney();
        int energyPrice = market.getSellPrice(ResourceType.ENERGY);
        //purchase 10 energy
        player.purchaseResourceFromMarket(10, market, ResourceType.ENERGY);
        assertEquals(playerMoney - 10 * energyPrice, player.getMoney());
        assertEquals(10, player.getEnergy());
        assertEquals(6, market.getEnergy());
    }

    // Test created by JBT
    /**
     * purchaseLandPlot should throw an exception if the given plot is null
     */
    @Test(expected = NullPointerException.class)
    public void purchaseNullLandPlot() {
        player.purchaseLandPlot(null);
    }

    // Test created by JBT
    /**
     * purchaseLandPlot should throw an exception if the player cannot afford the tile
     */
    @Test(expected = NotEnoughMoneyException.class)
    public void cantAffordLandPlot() {
        player.setMoney(5);
        LandPlot plot = new LandPlot(1,2,2);
        player.purchaseLandPlot(plot);
    }

    // Test created by JBT
    /**
     * purchaseLandPlot should throw an exception if the tile is already owned
     */
    @Test(expected = PlotAleadyOwnedException.class)
    public void landPlotAlreadyOwned() {
        player.setMoney(20);
        LandPlot plot = new LandPlot(1,2,2);
        player.purchaseLandPlot(plot);
        player.purchaseLandPlot(plot);
    }
}
