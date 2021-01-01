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

import fr.ubx.poo.model.go.Bomb;
import fr.ubx.poo.model.go.Monster;
import fr.ubx.poo.model.go.character.Player;



public class Game {

    private World world;
    private final Player player;
    private List<Monster> monsters = new ArrayList<>();
    private List<Bomb> bombs = new ArrayList<>();
    private final String worldPath;
    public int initPlayerLives;
    private int level = 1;
    private String prefixLevel;
    private boolean changed = false; //boolean changement de niveau
    private boolean backing = false; //boolean changement de niveau pour savoir si on avance ou recule d'un niveau
    

    
    /** 
     * @return int
     */
    public int getLevel() {
        return this.level;
    }

    
    /** 
     * @param level
     */
    public void setLevel(int level) {
        this.level = level;
    }

    
    /** 
     * @return List<Monster>
     */
    public List<Monster> getMonsters() {
        return this.monsters;
    }

    
    /** 
     * @return List<Bomb>
     */
    public List<Bomb> getBombs() {
        return this.bombs;
    }

    
    /** 
     * @param bomb
     */
    public void setBombs(Bomb bomb) {
        bombs.add(bomb);
    }

    public Game(String worldPath) {
        loadConfig(worldPath);
        //load world
        LoadFromFile lvl = new LoadFromFile(level, worldPath, prefixLevel);
        world = new WorldFromFile(lvl.getMapEntities());
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

    
    /** 
     * @return int
     */
    public int getInitPlayerLives() {
        return initPlayerLives;
    }

    
    /** 
     * @param path
     */
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

    
    /** 
     * @return World
     */
    public World getWorld() {
        return world;
    }

    
    /** 
     * @param world
     */
    public void setWorld(World world) {
        this.world = world;
    }


    
    /** 
     * @return Player
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Mets dans la variable world le monde depuis le fichier correspondant.
     */
    public void loadWorldFromFile(){
        LoadFromFile lvl = new LoadFromFile(level, worldPath, prefixLevel);
        world = new WorldFromFile(lvl.getMapEntities());
    }

    /**
     *  Recherche les positions des monstres et de la nouvelle position du joueur du monde en cours de chargement.
     */
    public void changeLevel() {    	
    	Position positionPlayer = null;
        List<Position> posMonsters = world.findMonster();
        for (Position p : posMonsters){
            if (level > 1){
                monsters.add(new Monster(this, p, true));
            }else{
                monsters.add(new Monster(this, p));
            }
            
        }
        try {
            positionPlayer = world.findOpenedDoor(backing);
            player.setPosition(positionPlayer);
        } catch (PositionNotFoundException e) {
            System.err.println("Position not found : " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
        
    	changed = true;
    }
    
    
    /** 
     * @return boolean
     */
    public boolean isChanged() {
    	return changed;
    }
    
    
    /** 
     * @param b
     */
    public void setChanged(boolean b) {
    	changed = b;
    }
    
    
    /** 
     * @param b
     */
    public void setBacking(boolean b) {
    	backing = b;
    }

    
    /** 
     * @return boolean
     */
    public boolean isBacking() {
    	return backing;
    }

}
