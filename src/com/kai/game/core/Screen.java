package com.kai.game.core;

import com.kai.game.entities.Player;
import com.kai.game.entities.enemies.Enemy;
import com.kai.game.hud.DeathScreen;
import com.kai.game.hud.InGameDisplay;
import com.kai.game.hud.MainMenu;
import com.kai.game.hud.SelectionScreen;
import com.kai.game.items.ItemLoader;
import com.kai.game.scene.Environment;
import com.kai.game.scene.SceneObject;
import com.kai.game.skills.Skill;
import com.kai.game.util.ClientConnection;
import com.kai.game.util.GameState;
import com.kai.game.util.Input;
import com.kai.game.util.Parameters;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Screen extends JPanel implements KeyListener, MouseListener {
    //Either 1000/500 or 1200/600 is recommended, imo.
    public static int WINDOW_WIDTH = 1200;
    public static int WINDOW_HEIGHT = WINDOW_WIDTH/2;

    //TODO: Fix how everything breaks when you resize the window.

    //The current state of the game
    public static GameState state;

    //Handles background and scene objects.
    private static Environment environment;
    //Handles the player.
    private static Player player;
    //Handles level management and enemies.
    private static RoomHandler roomHandler;
    //The death/end screen of the program
    private static DeathScreen deathScreen;
    //The starting screen of the program
    private static MainMenu mainMenu;
    //The screen where you select an ability/starting things
    private static SelectionScreen selectionScreen;
    //Handles misc objects that need to be drawn or updated.
    private static List<Updatable> toUpdate;
    //Handles all user interface.
    private static List<GameObject> userInterface;
    //The master JFrame, is necessary to have dialogues
    private static JFrame ownerFrame;
    //Signals whether or not the screen was resized recently.
    private static boolean wasResized = false;
    //Connection to the server for leaderboards
    private static ClientConnection connection;
    //The player HUD
    private static InGameDisplay inGameDisplay;

    public Screen(JFrame owner) {
        toUpdate = new ArrayList<>();
        userInterface = new ArrayList<>();

        connection = new ClientConnection();

        Screen.ownerFrame = owner;

        transitionToScene(GameState.MENU);

        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                wasResized = true;
            }
        });

        inGameDisplay = new InGameDisplay(0, 1200);

        ItemLoader loader = new ItemLoader();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT + InGameDisplay.ALLOCATED_HEIGHT);
    }

    public void updateDimension() {
        Screen.WINDOW_WIDTH = this.getWidth();
        Screen.WINDOW_HEIGHT = this.getHeight() - InGameDisplay.ALLOCATED_HEIGHT;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int x = (int)getLocationOnScreen().getX();
        int y = (int)getLocationOnScreen().getY();
        Input.mouseExists(x, y);

        //TODO: Replace all "x != null" with gamestate checks

        //Environment manages all scene objects.
        if (environment != null) {
            environment.drawMe(g);
        }

        for (Updatable u: toUpdate) {
            if (u instanceof GameObject) {
                ((GameObject) u).drawMe(g);
            }
        }

        //RoomHandler manages all enemies.
        if (roomHandler != null) {
            roomHandler.drawAllRoomContents(g);
        }

        for (GameObject UI: userInterface) {
            UI.drawMe(g);
        }

        inGameDisplay.drawMe(g);

    }

    private void update() {

        //Respond to all player input.
        Input.updateChanges();

        for (Updatable u: toUpdate) {
            if (u != null) {
                u.update();
            }
        }

        if (environment != null) {
            environment.updateSceneObjects();
        }

        if (roomHandler != null) {
            roomHandler.updateEnemies();
            roomHandler.updateLoot();
        }

        if (wasResized) {
            updateDimension();
            for (Updatable i: toUpdate) {
                if (i instanceof GameObject) {
                    ((GameObject) i).updateSelfImage();
                }
            }
            for (GameObject go: userInterface) {
                go.updateSelfImage();
            }
            if (environment != null) {
                environment.updateSelfImage();
            }
            if (roomHandler != null) {
                roomHandler.updateSelfImage();
            }
            wasResized = false;
        }

        if (player != null) {
            if (getPlayer().getHealth() < 1) {
                gameOver();
            }
        }

        inGameDisplay.update();
    }

    public static boolean checkState(GameState checkAgainst) {
        return (state == checkAgainst);
    }

    private void gameOver() {
        DeathScreen.Death death = constructPlayerDeath(getPlayer().getKilledBy());
        Skill skill = getPlayer().getSkills().get(0);
        transitionToScene(GameState.DEATH);
        if (ClientConnection.isCONNECTED()) {
            getConnection().setClientDeath(death);
            getConnection().start();
        }
        try { Thread.sleep(Parameters.TIMEOUT_LENGTH); } catch (InterruptedException e) { e.printStackTrace(); }
        if (getConnection().getGivenLeaderboard() == null) {
            ClientConnection.setCONNECTED(false);
        }
        getDeathScreen().setRecentPlayerDeath(death);
        getDeathScreen().setRecentPlayerSkill(skill);
        getDeathScreen().setConnected(ClientConnection.isCONNECTED());
        getDeathScreen().setLeaderboard(getConnection().getGivenLeaderboard());
        getDeathScreen().setOnLeaderboards(getConnection().isMadeOnLeaderboards());
    }

    public static void playerDied(Enemy e) {
        playerDied(e.getName());
    }
    public static void playerDied(String e) {
        getPlayer().setKilledBy(e);
    }

    private static DeathScreen.Death constructPlayerDeath(String e) {
        //You could make it show DeathScreen right away, but I like this.
        //TODO: Remove cancel button from killed dialogue.
        String s = (String)JOptionPane.showInputDialog(ownerFrame, "Enter name: ", "You have died.", JOptionPane.PLAIN_MESSAGE,
                null ,null, "");
        if (s == null) {
            s = "???";
        }
        if (s.length() > 7) {
            s = s.substring(0, 7);
        }
        if (e == null) {
            e = "null";
        }
        return new DeathScreen.Death(s, e, getPlayer().getSkills().get(0).getName().replaceAll("Skill", ""), getRoomHandler().getCurrentLevel());
        //return DeathScreen.createDeath(s, e, getPlayer().getSkills().get(0).getName(), getRoomHandler().getDisplayedLevel());
    }

    public void animate() {
        while(true) {

            repaint();

            try {
                Thread.sleep(1000/ Parameters.FRAMES_PER_SECOND);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            update();

        }
    }


    public static void transitionToScene(GameState newState) {
        state = newState;
        environment = null;
        player = null;
        roomHandler = null;
        deathScreen = null;
        mainMenu = null;
        selectionScreen = null;

        toUpdate.clear();
        userInterface.clear();

        switch(newState.getName()) {
            case "Menu":
                mainMenu = new MainMenu();
                userInterface.add(mainMenu);
                break;
            case "Selection Screen":
                selectionScreen = new SelectionScreen();
                userInterface.add(selectionScreen);
                break;
            case "Running":
                environment = new Environment();

                player = new Player(WINDOW_WIDTH/2,WINDOW_HEIGHT/2, 22, 60);
                addUpdatable(player);

                roomHandler = new RoomHandler(0);


                addUpdatable(roomHandler);
                break;
            case "Death Screen":
                deathScreen = new DeathScreen();
                userInterface.add(deathScreen);
                break;
        }
    }

    public static void addSceneObject(SceneObject so) {
        getEnvironment().addSceneObject(so);
    }

    public static SelectionScreen getSelectionScreen() {
        return selectionScreen;
    }

    public static DeathScreen getDeathScreen() {
        return deathScreen;
    }

    public static Environment getEnvironment() {
        return environment;
    }

    public static Player getPlayer() {
        return player;
    }

    public static RoomHandler getRoomHandler() { return roomHandler; }

    public static MainMenu getMainMenu() {
        return mainMenu;
    }

    public static void addUpdatable(Updatable e) {
        toUpdate.add(e);
    }

    public static ClientConnection getConnection() {
        return connection;
    }

    public static void setConnection(ClientConnection connection) {
        Screen.connection = connection;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {Input.keyPressed(e.getKeyCode());}

    @Override
    public void keyReleased(KeyEvent e) {Input.keyReleased(e.getKeyCode());}

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {Input.mouse_pressed = true;}

    @Override
    public void mouseReleased(MouseEvent e) {
        Input.mouseClicked(e.getX(), e.getY());
        Input.mouse_pressed = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}
