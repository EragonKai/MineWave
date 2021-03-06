package com.kai.game.hud;

import com.kai.game.core.GameObject;
import com.kai.game.entities.Player;
import com.kai.game.util.MFont;
import com.kai.game.util.MRectangle;
import com.kai.game.util.ResourceManager;
import com.kai.game.core.Screen;
import com.kai.game.skills.*;

import java.util.ArrayList;
import java.util.List;
import com.kai.game.util.MPoint;

import java.awt.*;

public class SelectionScreen extends GameObject {
    private static List<Skill> selectableAbilities;
    public static Skill currentlySelected;

    // == This class/gamestate needs a decent amount of work. ==

    private static final MPoint startDraw = new MPoint(20, 275);

    private final MPoint textDrawX = new MPoint(30, 0);
    private final MPoint textDrawX2 = new MPoint(900, 0);

    private final MPoint textDrawY = new MPoint(0, 65);
    private final MPoint textDrawY2 = new MPoint(0, 95);
    private final MPoint textDrawY3 = new MPoint(0, 125);
    private final MPoint textDrawY4 = new MPoint(0, 442);

    private final int abilityDescIncrement = (int)(30.0/600.0 * Screen.WINDOW_HEIGHT);

    private final MRectangle playButton = new MRectangle(new MPoint(506, 533), new MPoint(694, 594));
    private final MPoint playText = new MPoint(560, 580);
    private boolean playHover = false;

    public SelectionScreen() {
        super(ResourceManager.getImage("selection.png", Screen.WINDOW_WIDTH, Screen.WINDOW_HEIGHT), 0, 0, 1200, 600);

        selectableAbilities = new ArrayList<>();
        addPlayerAbilities();
    }

    private void addPlayerAbilities() {
        selectableAbilities.add(new TeleportSkill(null));
        selectableAbilities.add(new ShieldSkill(null));
        selectableAbilities.add(new ComboSkill(null));
        selectableAbilities.add(new GreatMineSkill(null));
    }

    @Override
    public void updateSelfImage() {
        super.updateSelfImage();
        for (Skill s: selectableAbilities) {
            s.updateSelfImage();
        }
    }

    public boolean checkHover(int mouseX, int mouseY) {
        playHover = (mouseX > playButton.getTopLeft().getX() && mouseX < playButton.getBottomRight().getX() &&
                mouseY > playButton.getTopLeft().getY() && mouseY < playButton.getBottomRight().getY()) && currentlySelected != null;
        return playHover;
    }

    public static Skill getCurrentlySelected(Player p) {
        return Skill.getFreshSkill(currentlySelected.getName(), p);
    }

    public void abilitySelectionAttempt(int mouseX, int mouseY) {
        for (int i = 0; i < selectableAbilities.size(); i++) {
            int abilityX = startDraw.getX() + (i*10) + (i * Skill.SKILL_SIZE.getWidth());
            int abilityY = startDraw.getY();
            if (mouseX > abilityX && mouseX < abilityX + Skill.SKILL_SIZE.getWidth()
                && mouseY > abilityY && mouseY < abilityY + Skill.SKILL_SIZE.getWidth()) {
                currentlySelected = selectableAbilities.get(i);
                break;
            }
        }
    }

    @Override
    public void drawMe(Graphics g) {
        g.drawImage(getSelfImage(), getX(), getY(), null);

        for (int i = 0; i < selectableAbilities.size(); i++) {
            g.setColor(new Color(247, 181, 26));
            if (currentlySelected == selectableAbilities.get(i)) {
                g.fillRect((startDraw.getX() + (i*10) + (i * Skill.SKILL_SIZE.getWidth()))-5, startDraw.getY()-5, Skill.SKILL_SIZE.getWidth()+10, Skill.SKILL_SIZE.getHeight()+10);
            }
            selectableAbilities.get(i).drawMe(g, startDraw.getX() + (i*10) + (i * Skill.SKILL_SIZE.getWidth()), startDraw.getY());
        }

        g.setColor(new Color(255, 255, 255));
        g.setFont(new MFont(1.2));
        g.drawString("Welcome to MineWave! Click on a skill. This skill is activated by pressing E.", textDrawX.getX(), textDrawY.getY());
        g.drawString("Info: Items are equipped by clicking on them. Click on an equipped item to change its position.", textDrawX.getX(), textDrawY2.getY());
        g.drawString("Info: You can't place more mines than your maximum or outside your range.", textDrawX.getX(), textDrawY3.getY());

        g.drawString("W/A/S/D to move.", textDrawX2.getX(), textDrawY.getY());
        g.drawString("Left click to place a mine.", textDrawX2.getX(), textDrawY2.getY());
        g.drawString("Have fun!", textDrawX2.getX(), textDrawY3.getY());

        if (currentlySelected != null) {
            for (int i = 0; i<currentlySelected.description.length; i++) {
                g.drawString(currentlySelected.description[i], textDrawX.getX(), textDrawY4.getY() + (i * abilityDescIncrement));
            }
        }

        if (currentlySelected != null) {
            g.setFont(new MFont(3.4));
            if (playHover) {
                g.setColor(new Color(56, 39, 255));
                g.fillRect(playButton.getTopLeft().getX(), playButton.getTopLeft().getY(), playButton.getWidth(), playButton.getHeight());
            }
            g.setColor(Color.WHITE);
            g.drawString("Play", playText.getX(), playText.getY());
        }


    }


}
