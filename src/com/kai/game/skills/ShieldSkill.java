package com.kai.game.skills;

import com.kai.game.entities.Player;
import com.kai.game.util.ResourceManager;

public class ShieldSkill extends Skill {

    public ShieldSkill(Player owner) {
        super("ShieldSkill", owner, ResourceManager.getImage("ShieldImage.png", Skill.SKILL_SIZE.getWidth(), Skill.SKILL_SIZE.getHeight()), 6,
                new String[] {
                        "Shield: 6 second cooldown.",
                        "Places up to 6 mines around the player.",
                });
    }

    @Override
    public void _use(int targetedX, int targetedY) {
        Player p = (Player)getOwner();
        int x = p.getX();
        int y = p.getY();

        p.removeAllMines();

        p.createProjectile(x+p.getScaledWidth(30), y+p.getScaledHeight(-10), true);
        p.currentShotTick = 2000;
        p.createProjectile(x+p.getScaledWidth(-10), y+p.getScaledHeight(-10), true);
        p.currentShotTick = 2000;
        p.createProjectile(x+p.getScaledWidth(30), y+p.getScaledHeight(30), true);
        p.currentShotTick = 2000;
        p.createProjectile(x+p.getScaledWidth(-10), y+p.getScaledHeight(30), true);
        p.currentShotTick = 2000;
        p.createProjectile(x+p.getScaledWidth(-10), y+p.getScaledHeight(70), true);
        p.currentShotTick = 2000;
        p.createProjectile(x+p.getScaledWidth(30), y+p.getScaledHeight(70), true);

    }

}
