/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.model.go.character;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.game.World;
import fr.ubx.poo.game.WorldEntity;
import fr.ubx.poo.model.Movable;
import fr.ubx.poo.model.go.Bomb;
import fr.ubx.poo.model.go.GameObject;
import fr.ubx.poo.model.go.Monster;
import fr.ubx.poo.game.Game;

import java.util.ArrayList;
import java.util.List;

import fr.ubx.poo.model.decor.*;

public class Player extends GameObject implements Movable {

    private final boolean alive = true;
    Direction direction;
    private boolean moveRequested = false;
    private int lives = 9;
    private boolean winner;
    private int range = 9;
    private int bombs = 9;
    private int key = 9;
    private int nbAvailable = bombs;
    private boolean invincible = false;
    private long timeCheck;
    private boolean gotHurt = false;

    
    /** 
     * @return int
     */
    public int getRange() {
        return range;
    }
    
    /** 
     * @return int
     */
    public int getBombs() {
        return bombs;
    }
    
    /** 
     * @return int
     */
    public int getKey() {
        return key;
    }
    
    /** 
     * @return int
     */
    public int getnbAvailable() {
        return nbAvailable;
    }
    
    /** 
     * @param nb
     */
    public void setnbAvailable(int nb) {
        this.nbAvailable = nb;
    }
    
    /** 
     * @param nb
     */
    public void setLives(int nb) {
        this.lives = nb;
    }


    public Player(Game game, Position position) {
        super(game, position);
        this.direction = Direction.S;
        this.lives = game.getInitPlayerLives();
    }

    
    /** 
     * @return int
     */
    public int getLives() {
        return lives;
    }

    
    /** 
     * @return Direction
     */
    public Direction getDirection() {
        return direction;
    }

    
    /** 
     * @param direction
     */
    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
        }
        moveRequested = true;
    }

    
    /** 
     * @param direction
     * @return boolean
     */
    @Override
    public boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        World world = game.getWorld();

        List<Bomb> bombList = game.getBombs();

        for (Bomb bomb : bombList) {
            if (nextPos.equals(bomb.getPosition())) {
                return false;
            }   
        }

        //collision avec les bords
        if (nextPos.inside(world.dimension)){
            Decor decor = world.get(nextPos);
            if (decor == null) return true;
            if (decor.toString() ==  "Stone") return false;
            if (decor.toString() ==  "Tree") return false;
            if (decor.toString() ==  "Box") {
                return canMoveBox(direction, nextPos);
            }
            if (decor.toString() ==  "Heart") { 
            	lives ++; 
                world.clear(nextPos);
            }
            if (decor.toString() ==  "Princess") winner = true;
            if (decor.toString() ==  "BombNbDec"){
                if (bombs > 1) {
                    bombs --;
                    nbAvailable--;
                }
                world.clear(nextPos);
            }
            if (decor.toString() ==  "BombNbInc") {
                bombs ++;
                nbAvailable++;
                world.clear(nextPos);
            }
            if (decor.toString() ==  "BombRangeDec") {
                if (range > 1) range --;
                world.clear(nextPos);
            }
            if (decor.toString() ==  "BombRangeInc"){
                range ++;
                world.clear(nextPos);
            }
            if (decor.toString() ==  "Key") {
                key ++;
                world.clear(nextPos);
            }
            if (decor.toString() ==  "DoorNextOpened"){
                game.setBacking(false);
                game.setChanged(true);
            }            
            if (decor.toString() ==  "DoorPrevOpened"){
                game.setBacking(true);
                game.setChanged(true);
            }

            return true;
        }
        return false;
    }

    
    /** 
     * Cherche si une boîte à une position donnée peut être déplacée vers une direction donnée.
     * 
     * @param direction direction du mouvement.
     * @param pos position de la boîte.
     * @return boolean vrai si elle peut être bougée, faux sinon.
     */
    private boolean canMoveBox(Direction direction, Position pos){
        Position nextPos = direction.nextPosition(pos);
        World world = game.getWorld();
        List<Monster> monsters = game.getMonsters();
        //collision avec les bords
        for (Monster monster : monsters) {
            if (nextPos.equals(monster.getPosition())){
                return false;
            }   
        }
        if (nextPos.inside(world.dimension)){
            
            Decor decor = world.get(nextPos);
            if (decor == null){
                world.setChanged(true);
                world.clear(pos);
                world.set(nextPos, new Box());
                return true;
            }
        }
        return false;

    }

    
    /**
     * Applique le mouvement dans la direction donnée.
     * 
     * @param direction Direction du mouvement.
     */
    public void doMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        setPosition(nextPos);
    }

    
    /** 
     * Gère l'actualisation du joueur en temps réel.
     * Applique le mouvement demandé.
     * Applique l'invulnérabilité.
     * Applique les dommages si le personnage se trouve sur une explosion.
     * 
     * @param now Temps donnée par le moteur de jeu.
     */
    public void update(long now) {
        if (moveRequested) {
            if (canMove(direction)) {
                doMove(direction);
            } 
        }
        moveRequested = false;

        if (gotHurt){
            this.timeCheck = now;
            gotHurt = false;
        }

        if(invincible && now - timeCheck >= 1 * 1000000000){
            invincible = false;
        }

        if (game.getWorld().get(getPosition()) instanceof Explosion) hurtPlayer();
    }

    
    /** 
     * Retourne vrai si le joueur a gagné, faux sinon.
     * 
     * @return boolean
     */
    public boolean isWinner() {
        return winner;
    }

    
    /** 
     * Retourne vrai si le joueur est en vie, faux sinon.
     * 
     * @return boolean
     */
    public boolean isAlive() {
        if (lives == 0) return !alive;
        return alive;
    }

    
    /** 
     * Retourne vrai si le joueur est invicible, faux sinon.
     * 
     * @return boolean
     */
    public boolean isInvincible(){
        return this.invincible;
    }
    
    /**
     * Applique le processus de dommage au joueur :
     * Il lui enlève un point de vie.
     * Lui applique l'état invulnérable. 
     * 
     */
    public void hurtPlayer(){
        if (invincible == true) return;
        this.lives --;
        this.invincible = true;
        this.gotHurt = true;
    }
    /**
     * Appelée lors de l'ouverture d'une porte.
     * Elle réduit de un le nombre de clés que possède le joueur. 
     */
    public void useKey(){
        key --;
    }
}
