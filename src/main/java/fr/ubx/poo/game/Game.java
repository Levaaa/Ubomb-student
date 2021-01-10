/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.game;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

import fr.ubx.poo.model.go.Bomb;
import fr.ubx.poo.model.go.Monster;
import fr.ubx.poo.model.go.character.Player;



public class Game {

    /**
     * Monde actuel dans lequel la partie se déroule.
     */
    private World world;

    /**
     * Joueur actuel
     */
    private final Player player;
    
    /**
     * Chemin d'accès aux fichiers niveau.
     */
    private final String worldPath;
    
    /**
     * Valeur d'affectation d'initialisation du nombre de vie du joueur.
     */
    public int initPlayerLives;

    /**
     * Numéro du niveau
     */
    private int level = 1;

    /**
     * Nom du préfixe des fichiers de niveau.
     */
    private String prefixLevel;

    /**
     * Valeur de niveau où les monstres auront leur IA d'activée.
     */
    private int IA;

    /**
     * Valeur pour savoir si on change de niveau ou non.
     */
    private boolean changed = false; 

    /**
     * Valeur pour savoir si on avance ou recule d'un niveau
     */
    private boolean backing = false;
        
    /** 
     * Charge depuis le fichier config.properties les informations de vie de joueur, du nom préfixe des niveaux, et enfin 
     * du niveau qui activera l'ia des monstres.
     * @param path
     */
    private void loadConfig(String path) {
        try (InputStream input = new FileInputStream(new File(path, "config.properties"))) {
            Properties prop = new Properties();
            // load the configuration file
            prop.load(input);
            this.initPlayerLives = Integer.parseInt(prop.getProperty("lives", "3"));
            this.prefixLevel = prop.getProperty("prefix", "level");
            this.IA = Integer.parseInt(prop.getProperty("IA", "2"));

        } catch (IOException ex) {
            System.err.println("Error loading configuration");
        }
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
     *  Si l'on est en train de visiter un nouveau monde, on mettra à jour la liste des nouveaux monstres.
     *
     *  @param isDiscoveringNewWorld Booléen permettant de savoir si le monde a déjà été visité ou non, afin de savoir
     *                              si on doit mettre les nouveaux monstres ou non. (Si c'est un monde déjà visité
     *                              la liste monsters doit être dans le même état qu'à la sortie de ce monde)
     */
    public void changeLevel(boolean isDiscoveringNewWorld) {    	
        Position positionPlayer = null;
        if (isDiscoveringNewWorld){
            List<Position> posMonsters = world.findMonster();
            List<Monster> monsters = this.getWorld().getMonsters();
            for (Position p : posMonsters){
                if (level > IA - 1){
                    monsters.add(new Monster(this, p, true, new Random().nextInt(2) + 1));//Random entre 1 et 3 (inclu)
                }else{
                    monsters.add(new Monster(this, p, false, new Random().nextInt(2) + 1));
                }
                
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
     * Getter
     * @return int
     */
    public int getLevel() {
        return this.level;
    }
    
    /** 
     * Setter
     * @param level
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /** 
     * Getter
     * @return World
     */
    public World getWorld() {
        return world;
    }
    
    /** 
     * Setter
     * @param world
     */
    public void setWorld(World world) {
        this.world = world;
    }
    
    /** 
     * Getter
     * @return Player
     */
    public Player getPlayer() {
        return this.player;
    }
    
    /** 
     * Getter
     * @return int
     */
    public int getInitPlayerLives() {
        return initPlayerLives;
    }

    /**
     * Constructeur
     * Permet d'initialiser de nouveaux les propriétés du joueur.
     * 
     * @param worldPath Chemin d'accès aux fichiers des niveaux
     * @param lives Points de vie à initialiser au joueur
     * @param nbBombs Nombre de bombes initialiser au joueur
     * @param range Porté des bombes à initialiser au joueur
     */
    public Game(String worldPath, int lives, int nbBombs, int range) {
        this(worldPath);
        this.getPlayer().setLives(lives);
        this.getPlayer().setBombs(nbBombs);
        this.getPlayer().setnbAvailable(nbBombs);
        this.getPlayer().setRange(range);
    }

    /**
     * Constructeur de base
     * @param worldPath Chemin d'accès aux fichiers des niveaux
     */
    public Game(String worldPath) {
        loadConfig(worldPath);
        //load world
        LoadFromFile lvl = new LoadFromFile(level, worldPath, prefixLevel);
        world = new WorldFromFile(lvl.getMapEntities());
        this.worldPath = worldPath;
        Position positionPlayer = null;
        List<Position> posMonsters = world.findMonster();
        List<Monster> monsters = this.getWorld().getMonsters();
        for (Position p : posMonsters){
            monsters.add(new Monster(this, p, false, 1));
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
     * Getter
     * @return boolean
     */
    public boolean isChanged() {
    	return changed;
    }
    
    /** 
     * Setter
     * @param b
     */
    public void setChanged(boolean b) {
    	changed = b;
    }
    
    /** 
     * Setter
     * @param b
     */
    public void setBacking(boolean b) {
    	backing = b;
    }

    /** 
     * Getter
     * @return boolean
     */
    public boolean isBacking() {
    	return backing;
    }

}
