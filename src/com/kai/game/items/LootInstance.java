package com.kai.game.items;

import com.kai.game.core.GameObject;
import com.kai.game.core.Updatable;
import com.kai.game.entities.Player;
import com.kai.game.util.MPoint;
import com.kai.game.util.MTimer;
import com.kai.game.util.Parameters;
import com.kai.game.util.ResourceManager;

import java.awt.*;

import java.util.*;
import java.util.List;

public class LootInstance extends GameObject {
    //A multiplier of 2 means the drop chances are doubled.
    private double lootChanceMultiplier;
    //If true, no common or uncommon items will be dropped.
    private boolean dropCommons, dropUncommons;
    private List<Item> containedItems;
    private boolean displayContents = false;

    private Image lootInstanceContents;

    public LootInstance(int x, int y, double lootChanceMultiplier, boolean dropCommons, boolean dropUncommons) {
        super(ResourceManager.getImage("chest.png", 48, 32), x, y, 48, 32);
        this.lootChanceMultiplier = lootChanceMultiplier;
        this.dropCommons = dropCommons;
        this.dropUncommons = dropUncommons;
        this.lootInstanceContents = ResourceManager.getImage("LootInstanceContents.png");

        containedItems = new ArrayList<>();
        populateLoot();

    }

    public LootInstance(int x, int y) {
        this(x, y, 1, true, true);
    }

    public void testClicked(Player owner, int mouseX, int mouseY) {
        if (displayContents) {
            for (int i = 0; i < containedItems.size(); i++) {
                if (containedItems.get(i).distanceTo(mouseX, mouseY) <= 35) {
                    Item newEquip = containedItems.get(i);
                    int index = 0;
                    if (owner.getRings()[1] == null) {
                        index = 1;
                    }
                    Item oldEquip = owner.equipRing(index, newEquip);
                    if (oldEquip != null) {
                        containedItems.set(i, oldEquip);
                    } else {
                        containedItems.remove(i);
                        i--;
                    }
                }
            }
        }
    }

    private MPoint contentPoint = new MPoint(968, 518);
    private MPoint contentLength = new MPoint(50, 22);
    private MPoint contentRectD = new MPoint(46, 46);

    @Override
    public void drawMe(Graphics g) {
        g.drawImage(getSelfImage(), getX(), getY(), null);
        if (displayContents) {
            drawContents(g);
        } else {
            for (Item item: containedItems) {
                item.setX(-100);
                item.setY(-100);
            }
        }
    }

    private MPoint rectL = new MPoint(232, 82);
    public void updateImages() {
        this.lootInstanceContents = ResourceManager.getImage("LootInstanceContents.png", rectL.getX(), rectL.getY());
    }

    private void drawContents(Graphics g) {
        g.drawImage(lootInstanceContents, contentPoint.getX(), contentPoint.getY(), null);
        for (int i = 0; i < containedItems.size(); i++) {
            Item item = containedItems.get(i);
            Item.setRarityColor(item.getRarity(), g);
            g.fillRect(contentPoint.getX() + (18 + (i* contentLength.getX())), contentPoint.getY() + (contentLength.getY()-4), contentRectD.getX(), contentRectD.getY());
            item.setX(contentPoint.getHardX() + (22 + (i* contentLength.getHardX())));
            item.setY(contentPoint.getHardY() + (contentLength.getHardY()));
            item.drawMe(g);
        }
    }

    public void checkIfStandingOn(Player player) {
        displayContents = checkCollision(player);
    }

    private void populateLoot() {
        if (dropCommons) {
            if (randomNumber(10000) <= (Parameters.COMMON_CHANCE * 10000 * lootChanceMultiplier)) {
                addItemToLoot(getRandomItem(1));
            }
        }
        if (dropUncommons) {
            if (randomNumber(10000) <= (Parameters.UNCOMMON_CHANCE * 10000 * lootChanceMultiplier)) {
                addItemToLoot(getRandomItem(2));
            }
        }
        if (randomNumber(10000) <= Parameters.RARE_CHANCE * 10000 * lootChanceMultiplier) {
            setSelf(ResourceManager.getImage("chestrare.png", getWidth(), getHeight()));
            addItemToLoot(getRandomItem(3));
        }
        if (randomNumber(10000) <= Parameters.MYSTIC_CHANCE * 10000 * lootChanceMultiplier) {
            setSelf(ResourceManager.getImage("chestmystic.png", getWidth(), getHeight()));
            addItemToLoot(getRandomItem(4));
        }
        if (randomNumber(10000) <= Parameters.TWISTED_CHANCE * 10000 * lootChanceMultiplier) {
            setSelf(ResourceManager.getImage("chesttwisted.png", getWidth(), getHeight()));
            addItemToLoot(getRandomItem(5));
        }
    }

    private void addItemToLoot(Item item) {
        containedItems.add(item);
    }

    private Item getRandomItem(int rarity) {
        HashMap<String, Item> allPossibleItems = ItemLoader.getAllItems();
        Item item;
        do {
            Object[] keys = allPossibleItems.keySet().toArray();
            String name =  (String)keys[new Random().nextInt(keys.length)];
            item = ItemLoader.getItem(name);
        } while (!(item.getRarity() == rarity));
        return item;
    }

    private int randomNumber(int range) {
        return (int)(Math.random() * range + 1);
    }

    public List<Item> getContainedItems() {
        return containedItems;
    }

    public boolean isDisplayContents() {
        return displayContents;
    }

    public static void changeLootBoost(double newBoost) {
        Parameters.GLOBAL_LOOT_BOOST = newBoost;
        updateDropChances();
    }

    public static void updateDropChances() {
        Parameters.COMMON_CHANCE = Parameters.LOOT_CHANCES[0] * Parameters.GLOBAL_LOOT_BOOST; // 7.0%
        Parameters.UNCOMMON_CHANCE = Parameters.LOOT_CHANCES[1] * Parameters.GLOBAL_LOOT_BOOST; // 3.0%
        Parameters.RARE_CHANCE = Parameters.LOOT_CHANCES[2] * Parameters.GLOBAL_LOOT_BOOST; // 0.6%
        Parameters.MYSTIC_CHANCE = Parameters.LOOT_CHANCES[3] * Parameters.GLOBAL_LOOT_BOOST; // 0.25%
        Parameters.TWISTED_CHANCE = Parameters.LOOT_CHANCES[4] * Parameters.GLOBAL_LOOT_BOOST; //0.05%

    }
}
