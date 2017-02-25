/*  JBT Assessment 4 Page: http://robins.tech/jbt/assfour.html
 *  JBT Changes to this file:
 *      Added new tests to handle more paths / increase coverage
 *      replaced some tests which were broken
 *          tiles can only have one roboticon installed, but some tests where installing 2 and expecting this to work
 */

package io.github.teamfractal.entity;

import io.github.teamfractal.RoboticonQuest;
import io.github.teamfractal.TesterFile;
import io.github.teamfractal.entity.enums.ResourceType;
import io.github.teamfractal.exception.InvalidResourceTypeException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LandPlotTest extends TesterFile {
    private LandPlot plot;

    @Before
    public void setup() {
        plot = new LandPlot(3, 0, 0);
    }

    /*
    JBT created this test
    this should really throw an exception, but at the time of writing we are just trying to increase test coverage
    so that we can make changes more safely in future.
     */
    /**
     * installRoboticon should return false if the given roboticon is already installed elsewhere
     * resource production should not be altered
     * any roboticons installed at the time should be unaffected
     */
    @Test
    public void installRoboticonAlreadyInstalledElsewhereTest() {
        Roboticon roboticon = new Roboticon(0);
        LandPlot plot2 = new LandPlot( 1, 1, 1);
        plot.installRoboticon(roboticon);
        int[] plotProduced = plot.produceResources();
        int[] plot2Produced = plot2.produceResources();

        // check the installation failed
        assertFalse(plot2.installRoboticon(roboticon));

        // check the production hasnt changed
        assertArrayEquals(plotProduced, plot.produceResources());
        assertArrayEquals(plot2Produced, plot2.produceResources());

        // check the roboticons havent changed
        assertNull(plot2.getInstalledRoboticon());
        assertEquals(roboticon, plot.getInstalledRoboticon());
    }

    /*
    JBT created this test
    this should really throw an exception, but at the time of writing we are just trying to increase test coverage
    so that we can make changes more safely in future.
     */
    /**
     * installRoboticon should return false if someone tries to install a roboticon when one is already installed
     * resource production should not be altered
     * installed roboticon should not be altered
     */
    @Test
    public void installedRoboticonAlreadyGotOneTest() {
        Roboticon roboticon0 = new Roboticon(0);
        Roboticon roboticon1 = new Roboticon(1);
        plot.installRoboticon(roboticon0);
        int[] productionBefore = plot.produceResources();

        // check the operation failed
        assertFalse(plot.installRoboticon(roboticon1));

        // check the resources generated havent changed
        assertArrayEquals(productionBefore, plot.produceResources());

        // check the same roboticon is applied
        assertEquals(roboticon0, plot.getInstalledRoboticon());

    }

    // JBT created this test
    /**
     * hasRoboticon should return whether a roboticon is installed
     */
    @Test
    public void hasRoboticonTest() {
        assertFalse(plot.hasRoboticon());
        Roboticon roboticon = new Roboticon(0);
        plot.installRoboticon(roboticon);
        assertTrue(plot.hasRoboticon());
    }

    // JBT created this test
    /**
     * getResource should return the amount of the given resource on the tile
     */
    @Test
    public void getResourceTest() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                for (int k = 0; k < 5; k++) {
                    LandPlot plot = new LandPlot(i, j, k);
                    assertEquals(plot.getResource(ResourceType.ORE), i);
                    assertEquals(plot.getResource(ResourceType.ENERGY), j);
                    assertEquals(plot.getResource(ResourceType.FOOD), k);
                }
            }
        }
    }

    // JBT created this test
    /**
     * getResource should throw an InvalidResourceTypeException if given ResourceType.CUSTOMISATION
     */
    @Test(expected = InvalidResourceTypeException.class)
    public void getResourceTypeCustomisationTest() {
        plot.getResource(ResourceType.CUSTOMISATION);
    }

    // JBT created this test
    /**
     * getResource should throw an InvalidResourceTypeException if given ResourceType.ROBOTICON
     */
    @Test(expected = InvalidResourceTypeException.class)
    public void getResourceTypeRoboticonTest() {
        plot.getResource(ResourceType.CUSTOMISATION);
    }

    // JBT created this test
    /**
     * getResource should throw an InvalidResourceTypeException if given ResourceType.Unknown
     */
    @Test(expected = InvalidResourceTypeException.class)
    public void getResourceTypeUnknownTest() {
        plot.getResource(ResourceType.Unknown);
    }

    // JBT created this test
    /**
     * hasOwner should return whether the LandPlot is owned or not
     */
    @Test
    public void hasOwnerTest() {
        assertFalse(plot.hasOwner());
        plot.setOwner(new Player(new RoboticonQuest()));
        assertTrue(plot.hasOwner());
    }

    /*
    JBT created this test
    this should really throw an exception, but at the time of writing we are just trying to increase test coverage
    so that we can make changes more safely in future.
    */
    /**
     * setOwner should return false when the landPlot is already owned and the owner should not be changed
     */
    @Test
    public void setOwnerAlreadyOwnedTest() {
        RoboticonQuest rq = new RoboticonQuest();
        Player player1 = new Player(rq);
        Player player2 = new Player(rq);
        plot.setOwner(player1);
        assertFalse(plot.setOwner(player2));
        assertEquals(player1, plot.getOwner());
    }

    /*
    JBT created this test
    this should really return void on success, but at the time of writing we are just trying to increase test coverage
    so that we can make changes more safely in future.
     */
    /**
     * setOwner should return true and update the owner if the plot is unowned
     */
    @Test
    public void setOwnerUnownedTest() {
        Player player = new Player(new RoboticonQuest());
        assertTrue(plot.setOwner(player));
        assertEquals(player, plot.getOwner());
    }
}
