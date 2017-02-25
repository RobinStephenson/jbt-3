/*  JBT Assessment 4 Page: http://robins.tech/jbt/assfour.html
 *  JBT Changes to this file:
 *      Added new tests to handle more paths / increase coverage
 */

package io.github.teamfractal.entity;

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

    @Test
    public void testInstallRobiticon() throws Exception {
        Roboticon roboticon = new Roboticon(0);
        int[] intProductionModifiers = new int[3];

        roboticon.setCustomisation(ResourceType.ORE);
        assertTrue(plot.installRoboticon(roboticon));

        for (int i = 0; i < 3; i++) {
            intProductionModifiers[i] = (int) plot.productionModifiers[i];
        }
        assertArrayEquals(new int[]{1, 0, 0}, intProductionModifiers);


        Roboticon roboticon2 = new Roboticon(0);
        roboticon2.setCustomisation(ResourceType.ENERGY);
        assertTrue(plot.installRoboticon(roboticon2));
        for (int i = 0; i < 3; i++) {
            intProductionModifiers[i] = (int) plot.productionModifiers[i];
        }
        assertArrayEquals(new int[] {1, 1, 0},intProductionModifiers);

        Roboticon roboticon3 = new Roboticon(0);
        roboticon3.setCustomisation(ResourceType.ORE);
        assertTrue(plot.installRoboticon(roboticon3));
        for (int i = 0; i < 3; i++) {
            intProductionModifiers[i] = (int) plot.productionModifiers[i];
        }
        assertArrayEquals(new int[] {2, 1, 0}, intProductionModifiers);

        Roboticon roboticon4= new Roboticon(0);
        roboticon4.setCustomisation(ResourceType.FOOD);
        assertTrue(plot.installRoboticon(roboticon4));
        for (int i = 0; i < 3; i++) {
            intProductionModifiers[i] = (int) plot.productionModifiers[i];
        }
        assertArrayEquals(new int[] {2, 1, 1}, intProductionModifiers);
    }

    @Test
    public void landPlotShouldNotReinstallRoboticon () {
        Roboticon roboticon = new Roboticon(0);
        int[] intProductionModifiers = new int[3];

        roboticon.setCustomisation(ResourceType.ORE);
        assertTrue(plot.installRoboticon(roboticon));
        for (int i = 0; i < 3; i++) {
            intProductionModifiers[i] = (int) plot.productionModifiers[i];
        }
        assertArrayEquals(new int[] {1, 0, 0}, intProductionModifiers);

        assertFalse(plot.installRoboticon(roboticon));
        for (int i = 0; i < 3; i++) {
            intProductionModifiers[i] = (int) plot.productionModifiers[i];
        }
        assertArrayEquals(new int[] {1, 0, 0}, intProductionModifiers);
    }

    @Test
    public void testProduceResources() throws Exception {
        Roboticon roboticon = new Roboticon(0);
        roboticon.setCustomisation(ResourceType.ORE);
        plot.installRoboticon(roboticon);
        assertArrayEquals(new int[] {3, 0, 0}, plot.produceResources());
        Roboticon roboticon2 = new Roboticon(0);
        roboticon2.setCustomisation(ResourceType.ORE);
        plot.installRoboticon(roboticon2);
        assertArrayEquals(new int[] {6, 0, 0}, plot.produceResources());
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
}
