/*  JBT Assessment 4 Page: http://robins.tech/jbt/assfour.html
 *  JBT Changes to this file:
 *      Updated documentation
 *      Renamed some methods
 *          Some had the return Type in the method name
 *      Throw exceptions instead of returning a enum
 *      Added throws to documentation
 *      Deleted unused and untested methods
 *      Removed un-needed synchronization
 */

package io.github.teamfractal.entity;

import com.badlogic.gdx.utils.Array;
import io.github.teamfractal.RoboticonQuest;
import io.github.teamfractal.entity.enums.ResourceType;
import io.github.teamfractal.exception.*;

import java.util.ArrayList;
import java.util.Random;


public class Player {
    public RoboticonQuest game;
    Array<Roboticon> roboticonList;
    ArrayList<LandPlot> landList = new ArrayList<LandPlot>();
    private int money = 100;
    private int ore = 0;
    private int energy = 0;
    private int food = 0;
    private int chancellorsCaught = 0;

    public Player(RoboticonQuest game){
        this.game = game;
        this.roboticonList = new Array<Roboticon>();
    }

    public int getMoney() {
        return money;
    }

    /**
     * Set the amount of money player has
     * @param money The amount of new money. Cannot be negative
     * @throws IllegalArgumentException if money is negative
     */
    public void setMoney(int money){
        if (money < 0) {
            throw new IllegalArgumentException("money cannot be negative");
        } else {
            this.money = money;
        }
    }

    public int getOre() {
        return ore;
    }

    /**
     * Set the amount of ore player has
     * @param amount The new amount for ore. Cannot be negative
     * @throws IllegalArgumentException if amount is negative
     */
    void setOre(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        } else {
            this.ore = amount;
        }
    }

    public int getEnergy() {
        return energy;
    }

    /**
     * Set the amount of energy player has
     * @param amount The new amount for energy. Cannot be negative
     * @throws IllegalArgumentException if amount is negative
     */
    void setEnergy(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        } else {
            this.energy = amount;
        }
    }

    public int getFood() {
        return food;
    }

    /**
     * Set the amount of food player has
     * @param amount The new amount for food. Cannot be negative,
     * @throws IllegalArgumentException if amount is negative
     */
    void setFood(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        } else {
            this.food = amount;
        }
    }

    /**
     * Set the resource amount current player have.
     * @param resource  The {@link ResourceType}
     * @param amount    The new amount.
     * @throws NotCommonResourceException if resource type is incorrect
     */
    public void setResource(ResourceType resource, int amount) {
        switch (resource) {
            case ENERGY:
                setEnergy(amount);
                break;

            case ORE:
                setOre(amount);
                break;

            case FOOD:
                setFood(amount);
                break;

            default:
                throw new NotCommonResourceException(resource);
        }
    }

    /**
     * Get the resource amount current player have.
     * @param type   The {@link ResourceType}
     * @return       The amount of specified resource.
     * @throws NotCommonResourceException if resource type is invalid
     */
    public int getResource(ResourceType type) {
        switch (type) {
            case ENERGY:
                return getEnergy();
            case ORE:
                return getOre();
            case FOOD:
                return getFood();
            default:
                throw new NotCommonResourceException(type);
        }
    }

    /**
     * Purchase roboticon from the market.
     * @param amount number of roboticons requested
     * @param market the market being purchased from
     * @throws IllegalArgumentException if amount <= 0
     * @throws NotEnoughResourceException if market does not have enough roboticons
     * @throws NotEnoughMoneyException if the player does not have enough money for the transaction
     */
    public void purchaseRoboticonsFromMarket(int amount, Market market) {
        if (market == null) {
            throw new NullPointerException("market cannot be null");
        }
        if (!market.hasEnoughResources(ResourceType.ROBOTICON, amount)) {
            throw new NotEnoughResourceException();
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("amount cannot be <= 0");
        }
        int cost = amount * market.getSellPrice(ResourceType.ROBOTICON);
        int money = getMoney();
        if (cost > money) {
            throw new NotEnoughMoneyException();
        }

        Random random = new Random();
        market.sellResource(ResourceType.ROBOTICON, amount);
        setMoney(money - cost);
        for (int roboticon = 0; roboticon < amount; roboticon++) {
            roboticonList.add(new Roboticon(random.nextInt()));
        }
    }

    /**
     * Purchase roboticon customisation from the market.
     * @param resource    The resource type of the customisation
     * @param roboticon   The roboticon to be customised.
     * @param market      The market
     * @throws InvalidResourceTypeException if the chosen customisation is invalid
     * @throws NotEnoughMoneyException if the player does not have enough moeny for the transaction
     */
    public void purchaseCustomisationFromMarket(ResourceType resource, Roboticon roboticon, Market market) {
        if (market == null) {
            throw new NullPointerException("market cannot be null");
        }
        if (roboticon == null) {
            throw new NullPointerException("roboticon cannot be null");
        }
        if (resource != ResourceType.FOOD && resource != ResourceType.ENERGY && resource != ResourceType.ORE) {
            throw new InvalidResourceTypeException();
        }
        int cost = market.getSellPrice(ResourceType.CUSTOMISATION);
        int money = getMoney();
        if (cost > money) {
            throw new NotEnoughMoneyException();
        }

        market.sellResource(ResourceType.CUSTOMISATION, 1);
        setMoney(money - cost);
        customiseRoboticon(roboticon, resource);
    }

    /**
     * Action for player to purchase resources from the market.
     *
     * @param amount     Amount of resources to purchase.
     * @param market     The market instance.
     * @param resource   The resource type.
     * @throws IllegalArgumentException if amount is < 1
     * @throws NotEnoughResourceException if the market does not have enough of the resource
     * @throws NotEnoughMoneyException if the player does not have enough money for the transaction
     */
    public void purchaseResourceFromMarket(int amount, Market market, ResourceType resource) {
        if (amount < 1) {
            throw new IllegalArgumentException("amount cannot be < 1");
        }
        if (!market.hasEnoughResources(resource, amount)) {
            throw new NotEnoughResourceException();
        }

        int cost = amount * market.getSellPrice(resource);
        int money = getMoney();
        if (cost > money) {
            throw new NotEnoughMoneyException();
        }

        market.sellResource(resource, amount);
        setMoney(money - cost);
        setResource(resource, getResource(resource) + amount);
    }

    /**
     * Sell resources to the market.
     * @param amount    Amount of resources to sell.
     * @param market    The market instance.
     * @param resource  The resource type.
     */
    public void sellResourceToMarket(int amount, Market market, ResourceType resource) {
        int resourcePrice = market.getBuyPrice(resource);

        if (getResource(resource) >= amount) {
            market.buyResource(resource, amount);
            setResource(resource, getResource(resource) - amount);
            setMoney(getMoney() + amount * resourcePrice);
        }
    }

    /**
     * Player add a landplot to their inventory for gold
     * @param plot           The landplot to purchase
     * @throws NotEnoughMoneyException if the player does not have enough money for the tile
     */
    public void purchaseLandPlot(LandPlot plot){
        if(plot == null) {
            throw new NullPointerException("Tile is null");
        }
        if (plot.hasOwner()) {
            throw new PlotAleadyOwnedException();
        }
        if (money < 10) {
            throw new NotEnoughMoneyException();
        }

        landList.add(plot);
        this.setMoney(this.getMoney() - 10);
        plot.setOwner(this);

        game.landPurchasedThisTurn();
    }

    /**
     * Apply roboticon customisation
     * @param roboticon  The roboticon to be customised
     * @param type       The roboticon customisation type.
     * @return           The roboticon
     */
    Roboticon customiseRoboticon(Roboticon roboticon, ResourceType type) {
        roboticon.setCustomisation(type);
        return roboticon;
    }

    /**
     * Add landplot to current user.
     *
     * @param landPlot  LandPlot to be bind to the user.
     *                  <code>LandPlot.setOwner(this_user)</code> first.
     */
    void addLandPlot(LandPlot landPlot) {
        if (landPlot != null && !landList.contains(landPlot) && landPlot.getOwner() == this) {
            landList.add(landPlot);
        }
    }

    /**
     * Get a string list of roboticons available for the player.
     * Mainly for the dropdown selection.
     *
     * @return  The string list of roboticons.
     */
    public Array<String> getRoboticonQuantities() {
        int ore = 0;
        int energy = 0;
        int food = 0;
        int uncustomised = 0;
        Array<String> roboticonAmountList = new Array<String>();

        for (Roboticon r : roboticonList) {
            if (!r.isInstalled()) {
                switch (r.getCustomisation()) {
                    case ORE:
                        ore += 1;
                        break;
                    case ENERGY:
                        energy += 1;
                        break;
                    case FOOD:
                        food += 1;
                        break;
                    default:
                        uncustomised += 1;
                        break;
                }
            }
        }

        roboticonAmountList.add("Ore Specific x "    + ore);
        roboticonAmountList.add("Energy Specific x " + energy);
        roboticonAmountList.add("Food Specific x " + food);
        roboticonAmountList.add("Uncustomised x "    + uncustomised);
        return roboticonAmountList;
    }

    public Array<Roboticon> getRoboticons(){
        return this.roboticonList;
    }

    /**
     * Generate resources produced from each LandPlot
     */
    public void generateResources() {
        int energy = 0;
        int food = 0;
        int ore = 0;

        for (LandPlot land : landList) {
            energy += land.produceResource(ResourceType.ENERGY);
            ore += land.produceResource(ResourceType.ORE);
            food += land.produceResource(ResourceType.FOOD);
        }

        setEnergy(getEnergy() + energy);
        setFood(getFood() + food);
        setOre(getOre() + ore);

        game.genOverlay.updateYieldLabels(energy, ore, food);
    }

    /**
     * Returns the array of plots owned by this player
     * @return ArrayList of LandPlot The array of plots owned by this player
     */
    public ArrayList<LandPlot> getOwnedPlots() {
        return landList;
    }

    /**
     * Returns the score of the player which is a combination of ore, energy and food.
     * @return The score of the player.
     */
    public int calculateScore(){
        return ore + energy + food;
    }

    /**
     * Method to be overloaded by AI inheritance
     */
    public void takeTurn(int phase) {
        //Overload in AIPlayer
        System.out.println("Human turn");
    }

    /**
     * Added by JBT
     * Increments the players chancellors caught field
     */
    public void caughtChancellor()
    {
        chancellorsCaught++;
    }

    //JBT
    public int chancellorsCaught() {return chancellorsCaught;}
}