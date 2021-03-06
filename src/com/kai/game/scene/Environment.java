package com.kai.game.scene;

import com.kai.game.core.GameObject;
import com.kai.game.entities.Entity;
import com.kai.game.util.ResourceManager;
import com.kai.game.core.Screen;
import javafx.scene.Scene;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Environment extends GameObject {
    private List<SceneObject> sceneObjects;


    public Environment() {
        super(ResourceManager.getImage("background.png", Screen.WINDOW_WIDTH, Screen.WINDOW_HEIGHT), 0, 0, 1200, 600);
        sceneObjects = new ArrayList<>();
    }

    //TODO: More different backgrounds for various levels.

    @Override
    public void drawMe(Graphics g) {
        g.drawImage(getSelfImage(), getX(), getY(), null);

        for (GameObject o: sceneObjects) {
            o.drawMe(g);
        }


    }

    public void updateSceneObjects() {
        List<SceneObject> toRemove = new ArrayList<>();
        for (SceneObject so: sceneObjects) {
            so.update();
            if (so.isMarkedForRemoval()) {
                toRemove.add(so);
            }
        }
        sceneObjects.removeAll(toRemove);
    }

    @Override
    public void updateSelfImage() {
        super.updateSelfImage();
        for (SceneObject so: sceneObjects) {
            so.updateSelfImage();
        }
    }

    public boolean sceneCollisions(Entity obj) {
        boolean collision = false;
        for (SceneObject o: sceneObjects) {
            if (obj.checkCollision(o)) {
                o.onTouch(obj);
                collision = true;
            }
        }
        return collision;
    }

    public void clearAllSceneObjects() {
        sceneObjects.clear();
    }

    public void addSceneObject(SceneObject s) {
        sceneObjects.add(s);
    }

}
