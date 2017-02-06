package io.github.teamfractal.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import io.github.teamfractal.screens.Overlay;

import java.util.ArrayList;

/**
 * Created by Joseph on 31/01/2017.
 */
public class PlotEffect extends Array<Float[]> {

    /**
     * The name of the effect
     */
    private String name;

    /**
     * A description of the effect
     */
    private String description;

    /**
     * Object containing a method that the effect can automatically trigger if and when it is run
     */
    private Runnable runnable;

    /**
     * Array storing all of the plots to have been affected by this effect (in the order by which they were affected)
     */
    private Array<LandPlot> plotRegister;

    /**
     * Overlay to provide a visual indication of the effect's presence and influences
     */
    private Overlay overlay;

    /**
     * Constructor that assigns a name, a description, variably-applicable modifiers and a custom method to the effect
     *
     * @param name The name of the effect
     * @param description A description of the effect
     * @param modifiers The production modifiers that the effect can impose {0: ORE | 1: ENERGY | 2: FOOD}
     *
     */
    public PlotEffect(String name, String description, Float[] modifiers, Runnable runnable) {
        this.name = name;
        this.description = description;
        //Stores the effect's name and description for future reference

        super.add(modifiers);
        //Store the effect's modifiers at the base of the internal stack

        this.runnable = runnable;
        //Assign the effect to the proprietary method provided

        this.plotRegister = new Array<LandPlot>();
        //Establish the separate LandPlot stack to track affected tiles

        this.overlay = new Overlay(Color.GRAY, Color.WHITE, 200, 100, 3);
        //Construct a visual interface through which the effect can be identified

        constructOverlay();
        //Populate the effect's associated overlay with information regarding it
    }

    /**
     * Method that populates the effect's associated overlay
     */
    private void constructOverlay() {
    }

    /**
     * Overloaded constructor that assigns a name, a description and variably-applicable modifiers to the effect
     *
     * @param name The name of the effect
     * @param description A description of the effect
     * @param modifiers The production modifiers that the effect can impose {0: ORE | 1: ENERGY | 2: FOOD}
     */
    public PlotEffect(String name, String description, Float[] modifiers) {
        this(name, description, modifiers, new Runnable() {
            @Override
            public void run() {
                //This is meant to be empty, so enjoy a serving of copypasta to make up for the lack of stuff here.

                //"I'd just like to interject for moment. What you're refering to as Linux, is in fact, GNU/Linux, or as
                // I've recently taken to calling it, GNU plus Linux. Linux is not an operating system unto itself, but
                // rather another free component of a fully functioning GNU system made useful by the GNU corelibs,
                // shell utilities and vital system components comprising a full OS as defined by POSIX. Many computer
                // users run a modified version of the GNU system every day, without realizing it. Through a peculiar
                // turn of events, the version of GNU which is widely used today is often called Linux, and many of its
                // users are not aware that it is basically the GNU system, developed by the GNU Project.

                // There really is a Linux, and these people are using it, but it is just a part of the system they use.
                // Linux is the kernel: the program in the system that allocates the machine's resources to the other
                // programs that you run. The kernel is an essential part of an operating system, but useless by itself;
                // it can only function in the context of a complete operating system. Linux is normally used in
                // combination with the GNU operating system: the whole system is basically GNU with Linux added, or
                // GNU/Linux. All the so-called Linux distributions are really distributions of GNU/Linux!"
                //      - [Refactoring of quotes credited to] Richard Stallman, software freedom activist
            }
        });
    }

    /**
     * Imposes the effect's modifiers on the provided plot
     * Assumes that the modifiers to be imposed at any given time will be at the head of the internal stack
     *
     * @param plot The plot to be affected
     * @param mode The mode of effect [0: ADD | 1: MULTIPLY | 2: OVERWRITE]
     */
    public void impose(LandPlot plot, int mode) {
        Float[] originalModifiers = new Float[3];
        Float[] newModifiers;
        //Declare temporary arrays to handle modifier modifications

        newModifiers = super.pop();
        //Assume that the modifiers on the top of the stack are the modifiers to be imposed

        for (int i = 0; i < 3; i++) {
            originalModifiers[i] = plot.productionModifiers[i];
            //Save each of the specified tile's original modifiers

            switch (mode) {
                case (0):
                    plot.productionModifiers[i] = plot.productionModifiers[i] + newModifiers[i];
                    //MODE 0: Add/subtract to/from the original modifiers
                    break;
                case (1):
                    plot.productionModifiers[i] = plot.productionModifiers[i] * newModifiers[i];
                    //MODE 1: Multiply the original modifiers
                    break;
                case (2):
                    plot.productionModifiers[i] = newModifiers[i];
                    //MODE 2: Replace the original modifiers
                    break;
            }
        }

        super.add(originalModifiers);
        //Add the tile's original modifiers to the stack for later access...

        super.add(newModifiers);
        //...and return the imposed modifiers to the top of the stack

        plotRegister.add(plot);
        //Push the plot that's about to be modified on to the appropriate registration stack
    }


    public void revert() {
        if (plotRegister.size > 0) {
            Float[] originalModifiers;
            LandPlot lastPlot;

            swapTop();
            originalModifiers = super.pop();

            lastPlot = plotRegister.pop();

            for (int i = 0; i < 3; i++) {
                lastPlot.productionModifiers[i] = originalModifiers[i];
            }
        }
    }

    public void revertAll() {
        while (plotRegister.size > 0) {
            revert();
        }
    }

    private void swapTop() {
        if (super.size > 1) {
            Float[] i = super.pop();
            Float[] j = super.pop();

            super.add(i);
            super.add(j);
        }
    }

    /**
     * If the size of the effect's plot-register is greater than 0 at any given time, then at least 1 tile is still
     * being affected by this object
     */
    public boolean active() {
        return (plotRegister.size > 0);
    }

    /**
     * Sets the method that the effect will run when it's imposed on a given tile
     *
     * @param runnable The method to be executed when this effect is invoked
     */
    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public void executeRunnable() {
        runnable.run();
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public Overlay overlay() { return overlay; }
}
