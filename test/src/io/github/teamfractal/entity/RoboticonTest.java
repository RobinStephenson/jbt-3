/*  JBT Assessment 4 Page: http://robins.tech/jbt/assfour.html
 *  JBT Changes to this file:
 *      Added more tests to cover the sad paths / increase coverage on all paths
 *      Added documentation for some existing tests
 */

package io.github.teamfractal.entity;

import io.github.teamfractal.TesterFile;
import io.github.teamfractal.entity.enums.ResourceType;
import io.github.teamfractal.exception.AlreadyInstalledException;
import io.github.teamfractal.exception.NotInstalledException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RoboticonTest extends TesterFile {
    private Roboticon roboticon;
    private final int roboticonID = 1;

    @Before
    public void setup() {
        roboticon = new Roboticon(roboticonID);
    }

    @Test
    public void robotitonIDTest() {
        assertEquals(roboticon.getID(), roboticonID);
    }

    @Test
    public void initialisationTest(){
        assertEquals(roboticon.getCustomisation(), ResourceType.Unknown);
        assertFalse(roboticon.isInstalled());
    }

    @Test
    public void customisationTest(){
        roboticon.setCustomisation(ResourceType.ORE);
        assertEquals(roboticon.getCustomisation(), ResourceType.ORE);
    }

    // JBT altered this test
    /**
     * landplot getters and setters happy path
     */
    @Test
    public void setLandPlotTest() throws AlreadyInstalledException {
        LandPlot plot = new LandPlot(0, 0, 0);
        roboticon.setInstalledLandPlot(plot);
        assertEquals(roboticon.getInstalledLandPlot(), plot);

    }

    // JBT created this test
    /**
     * setInstalledLandPlot should throw an exception if the given landplot is null
     */
    @Test(expected = NullPointerException.class)
    public void setNullPlotTest() throws AlreadyInstalledException {
        roboticon.setInstalledLandPlot(null);
    }

    // JBT Created this test
    /**
     * setInstalledLandPlot should throw an exception if roboticon is already installed somewhere
     */
    @Test(expected = AlreadyInstalledException.class)
    public void alreadyInstalledSetPlotTest() throws AlreadyInstalledException {
        LandPlot plot = new LandPlot(0, 0, 0);
        LandPlot anotherPlot = new LandPlot(1,1,1);
        roboticon.setInstalledLandPlot(plot);
        roboticon.setInstalledLandPlot(anotherPlot);
    }

    /**
     * getLandPlot should return the plot the roboticon is installed on, if installed
     */
    @Test
    public void getLandPlotTest() throws AlreadyInstalledException {
        LandPlot plot = new LandPlot(0, 0, 0);
        roboticon.setInstalledLandPlot(plot);
        assertEquals(roboticon.getInstalledLandPlot(), plot);
    }

    // JBT Created this test
    /**
     * getLandPlot should return null when the roboticon is not installed on a landplot
     */
    @Test
    public void getLandPlotWhenNoneSetTest() {
        assertNull(roboticon.getInstalledLandPlot());
    }

    // JBT Created this test
    /**
     * removeFromLandPlot should update isInstalled
     */
    @Test
    public void removeFromPlotTest() throws AlreadyInstalledException, NotInstalledException {
        LandPlot plot = new LandPlot(0, 0, 0);
        roboticon.setInstalledLandPlot(plot);
        roboticon.removeFromLandPlot();
        assertFalse(roboticon.isInstalled());
    }

    // JBT Created this test
    /**
     * removeFromLandPlot should throw an error if called when the roboticon is not installed on a plot
     */
    @Test(expected = NotInstalledException.class)
    public void removeFromPlotNotInstalledTest() throws NotInstalledException {
        roboticon.removeFromLandPlot();
    }
}
