/*  JBT Assessment 4 Page: http://robins.tech/jbt/assfour.html
 *  JBT Changes to this file:
 *      Improved existing accessors with null pointer checks and more other invalid operations checks
 *      Throw exceptions rather than returning false when things go wrong
 *      Created some new accessors
 */

package io.github.teamfractal.entity;

import io.github.teamfractal.entity.enums.ResourceType;
import io.github.teamfractal.exception.AlreadyInstalledException;
import io.github.teamfractal.exception.NotInstalledException;

public class Roboticon {
    private int ID;
    private ResourceType customisation;

    private LandPlot installedLandPlot;

    Roboticon(int ID) {
        this.ID = ID;
        customisation = ResourceType.Unknown;
    }

    /**
     * Getter for the roboticon ID
     * @return The roboticon ID
     */
    public int getID () {
        return this.ID;
    }

    /**
     * Getter for the customisation
     * @return The customisation of the roboticon
     */
    public ResourceType getCustomisation() {
        return this.customisation;
    }

    /**
     * Sets the customisation of the roboticon to the specific type
     * @param type The type of customisation
     */
    void setCustomisation(ResourceType type) {
        this.customisation = type;
    }

    /**
     * Getter for installedLandPlot
     * @return The state of installedLandplot, true if installed and false if otherwise
     */
    public synchronized boolean isInstalled() {
        return installedLandPlot != null;
    }

    // JBT altered this method
    /**
     * set the landplot this roboticon in installed on
     * does NOT update the landplot to reflect this
     * @param landplot which roboticon is installed to
     * @return true if roboticon is installed, false if not
     */
    public synchronized void setInstalledLandPlot(LandPlot landplot) throws AlreadyInstalledException {
        if (isInstalled()) {
            throw new AlreadyInstalledException("This roboticon is already installed");
        }
        if (landplot == null) {
            throw new NullPointerException("Given landplot cannot be null");
        }
        installedLandPlot = landplot;
    }

    // JBT created this method
    /**
     * get the LandPlot this roboticon is installed on
     * @return the LandPlot
     */
    public LandPlot getInstalledLandPlot() {
        return installedLandPlot;
    }

    // JBT created this method
    /**
     * remove this Roboticon from the LandPlot its currently installed on
     * does NOT update the LandPlot to reflect this
     * @throws NotInstalledException if the roboticon is not installed on a plot
     */
    public synchronized void removeFromLandPlot() throws NotInstalledException{
        if (!isInstalled()) {
            throw new NotInstalledException("This roboticon is not currently installed");
        }
        installedLandPlot = null;
    }
}
