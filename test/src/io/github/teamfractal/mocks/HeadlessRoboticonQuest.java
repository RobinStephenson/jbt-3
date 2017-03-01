package io.github.teamfractal.mocks;

import io.github.teamfractal.RoboticonQuest;
import io.github.teamfractal.entity.Roboticon;
import org.lwjgl.Sys;

/**
 * Created by micha on 01/03/2017.
 */
public class HeadlessRoboticonQuest extends RoboticonQuest{

    public HeadlessRoboticonQuest()
    {
        super();
    }

    @Override
    public void create() {
        System.out.println("Create function called");
    }

    @Override
    public void render() {
        System.out.println("Render function called");
    }

    @Override
    public void resize(int width, int height) {
        System.out.println("Resize function called");
    }

    @Override
    public void dispose() {
        System.out.println("Dispose function called");
    }

    @Override
    protected void implementPhase() {
        System.out.println("Implemented phase " + this.getPhase());
    }
}
