/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.game;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;

import fr.ubx.poo.model.go.Monster;
import fr.ubx.poo.model.go.character.Player;



public class Game {

    private World world;
    private final Player player;
    private List<Monster> monsters = new ArrayList<>();
    private final String worldPath;
    public int initPlayerLives;
    private int level = 1;
    private String prefixLevel;
    private List<World> worldsList = new ArrayList<>();
    boolean changed = false; //boolean changement de niveau
    

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<Monster> getMonsters() {
        return this.monsters;
    }

    public Game(String worldPath) {
        loadConfig(worldPath);
        //load world
        LoadFromFile lvl = new LoadFromFile(level, worldPath, prefixLevel);
        worldsList.add(new WorldFromFile(lvl.getMapEntities()));
        world = worldsList.get(0);
        this.worldPath = worldPath;
        Position positionPlayer = null;
        List<Position> posMonsters = world.findMonster();
        for (Position p : posMonsters){
            monsters.add(new Monster(this, p));
        }
        try {
            positionPlayer = world.findPlayer();
            player = new Player(this, positionPlayer);
        } catch (PositionNotFoundException e) {
            System.err.println("Position not found : " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    public int getInitPlayerLives() {
        return initPlayerLives;
    }

    private void loadConfig(String path) {
        try (InputStream input = new FileInputStream(new File(path, "config.properties"))) {
            Properties prop = new Properties();
            // load the configuration file
            prop.load(input);
            initPlayerLives = Integer.parseInt(prop.getProperty("lives", "3"));
            this.prefixLevel = prop.getProperty("prefix", "level");
            

        } catch (IOException ex) {
            System.err.println("Error loading configuration");
        }
    }

    public World getWorld() {
        return world;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void changeLevel(boolean nextDoor) {
    	if (nextDoor) level ++;
    	else level --;
    	
    	if (worldsList.size() < level) {
    		LoadFromFile lvl = new LoadFromFile(level, worldPath, prefixLevel);
            worldsList.add(new WorldFromFile(lvl.getMapEntities()));
    	}

    	world = worldsList.get(level-1);
    	Position positionPlayer = null;
        List<Position> posMonsters = world.findMonster();
        for (Position p : posMonsters){
            monsters.add(new Monster(this, p));
        }
        try {
            positionPlayer = world.findOpenedDoor(nextDoor);
            player.setPosition(positionPlayer);
        } catch (PositionNotFoundException e) {
            System.err.println("Position not found : " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    	changed = true;
    }
    
    public boolean getChanged() {
    	return changed;
    }
    
    public void setChanged(boolean b) {
    	changed = b;
    }
}
