package com.kai.game;

import com.kai.game.util.MRectangle;
import com.kai.game.util.ResourceManager;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class GameObject {
    private Image self;
    private int x, y;

    public MRectangle dimensions;

    public GameObject(Image self, int x, int y, int width, int height) {
        this.self = self;
        this.x = x;
        this.y = y;
        dimensions = new MRectangle(width, height);
    }

    public abstract void drawMe(Graphics g);

    public boolean checkCollision(GameObject otherObject) {
        return ((otherObject.getX() < getX()+getWidth()) &&
                (otherObject.getX()+otherObject.getWidth() > getX()) &&
                (otherObject.getY() < getY()+getHeight()) &&
                (otherObject.getY()+otherObject.getHeight() > getY()));
    }

    public void updateSelfImage() {
        if (getSelfImage() != null) {
            setSelf(ResourceManager.resizeImage(getSelfImage(), getWidth(), getHeight()));
        }
    }

    public int getWidth() {
        return dimensions.getWidth();
    }

    public int getHeight() {
        return dimensions.getHeight();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Image getSelfImage() {
        return self;
    }

    public void setSelf(Image self) {
        this.self = self;
    }
}
