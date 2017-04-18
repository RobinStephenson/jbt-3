/*  JBT Assessment 4 Page: http://robins.tech/jbt/assfour.html
 */

package io.github.teamfractal.entity;

import io.github.teamfractal.RoboticonQuest;
import io.github.teamfractal.TesterFile;
import io.github.teamfractal.entity.enums.ResourceType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the AI can be found here and in the Manual Testing part of the Test3 document
 * https://github.com/NotKieran/DRTN-Fractal/blob/Assessment3_Docs/Test3.pdf
 */

public class AIPlayerTest extends TesterFile {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private Player player;
    private RoboticonQuest game = new RoboticonQuest();

    @Before
    public void setUp() {
        //game.create();
        player = new AIPlayer(game);
    }

    /**
     * This part of the Test Class is a duplicate of PlayerTest but calling an AI player instead, this checks that the AI inherits correctly
     */
    //Money Tests
    @Test
    public void testPlayerInitialMoney() {
        assertEquals(20000, player.getMoney());
    }


    @Test
    public void testAIPlayerCannotSellMoreEnergyThanAllowed() {
        Market market = new Market();

        player.setEnergy(15);
        player.sellResourceToMarket(20, market, ResourceType.ENERGY);
        Assert.assertEquals(15, player.getEnergy());

    }

    @Test
    public void testAIPlayerCannotSellMoreOreThanAllowed() {
        Market market = new Market();

        player.setOre(15);
        player.sellResourceToMarket(20, market, ResourceType.ORE);
        Assert.assertEquals(15, player.getOre());
    }

    @Test
    public void testAIPlayerCannotSellMoreFoodThanAllowed() {
        Market market = new Market();

        player.setFood(15);
        player.sellResourceToMarket(20, market, ResourceType.FOOD);
        Assert.assertEquals(15, player.getFood());
    }
}
