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
    
    /**
     * Direction actuelle du personnage
     */
    Direction direction;

    /**
     *  Booléen permettant de déterminer s'il y a eu une demande de mouvement du joueur. 
     */
    private boolean moveRequested = false;

    /**
     * Vies du joueur
     */
    private int lives;

    /**
     * Booléen permettant de déterminer si le joueur a gagné ou non. 
     */
    private boolean winner;

    /**
     * Portée de bombe 
     */
    private int range;
    
    /**
     * Nombre de bombes totales
     */
    private int bombs;

    /**
     * Nombre de clés
     */
    private int key = 0;

    /**
     * Nombre de bombes disponible.
     */
    private int nbAvailable = bombs;

    /**
     * Permet de savoir si le joueur est invincible.
     */
    private boolean invincible = false;

    /**
     * Marqueur de temps où le joueur est devenu invincible pour la dernière fois.
     */
    private long timeCheckInvincible;

    /**
     * Permet de savoir si le joueur vient de se faire toucher.
     */
    private boolean gotHurt = false;

    /**
     * Permet de savoir si le joueur est touché par la malediction.
     */
    private boolean malediction = false;

    /**
     * Permet de savoir si la joueur vient de se faire toucher par la malediction.
     */
    private boolean gotMaledictioned = false;
    
    /**
     * Marqueur de temps où le joueur a été touch par la malediction pour la dernière fois.
     */
    private long timeCheckMalediction;

    /**
     * Define de repère d'une seconde d'un temps géré en milliseconde.
     */
    private static final long SECOND = 1000000000; 
    
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

        List<Bomb> bombList = game.getWorld().getBombs();

        for (Bomb bomb : bombList) {
            if (nextPos.equals(bomb.getPosition())) {
                // La bombe en phase 0 est toujours là mais a théoriquement explosé, 
                //elle ne doit donc pas gêner le déplacement.
                if (bomb.getPhase() == 0) return true;
                return false;
            }   
        }

        //collision avec les bords
        if (nextPos.inside(world.dimension)){
            Decor decor = world.get(nextPos);

            //null <> rien sur la case
            if (decor == null) return true;
            if (decor instanceof Stone) return false;
            if (decor instanceof Tree) return false;
            if (decor instanceof Box) return canMoveBox(direction, nextPos);
            if (decor instanceof Heart) { 
            	lives ++; 
                world.clear(nextPos);
            }
            if (decor instanceof Princess) winner = true;
            if (decor instanceof BombNbDec) {
                if (bombs > 1) {
                    bombs --;
                    nbAvailable--;
                }
                world.clear(nextPos);
            }
            if (decor instanceof BombNbInc) {
                bombs ++;
                nbAvailable++;
                world.clear(nextPos);
            }
            if (decor instanceof BombRangeDec) {
                if (range > 2) range --;
                world.clear(nextPos);
            }
            if (decor instanceof BombRangeInc) {
                range ++;
                world.clear(nextPos);
            }
            if (decor instanceof Key) {
                key ++;
                world.clear(nextPos);
            }
            if (decor instanceof DoorNextOpened) {
                game.setBacking(false);
                game.setChanged(true);
            }            
            if (decor instanceof DoorPrevOpened) {
                game.setBacking(true);
                game.setChanged(true);
            }
            if (decor instanceof DoorNextClosed) {
                return false;
            }
            if (decor instanceof Malediction) {
                world.clear(nextPos);
                malediction = true;
                gotMaledictioned = true;
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
        List<Monster> monsters = game.getWorld().getMonsters();
        //collision avec les bords
        for (Monster monster : monsters) {
            if (nextPos.equals(monster.getPosition())){
                return false;
            }   
        }
        if (nextPos.inside(world.dimension)){
            
            Decor decor = world.get(nextPos);
            if (decor == null){
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

        //gestion temps invulnérabilité
        if (gotHurt){
            this.timeCheckInvincible = now;
            gotHurt = false;
        }
        if(invincible && now - timeCheckInvincible >= 1 * SECOND){
            invincible = false;
        }

        //gestion temps melediction
        if (gotMaledictioned){
            this.timeCheckMalediction = now;
            gotMaledictioned = false;
        }
        if(malediction && now - timeCheckMalediction >= 8 * SECOND){
            malediction = false;
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
     * Retourne vrai si le joueur est Malediction, faux sinon.
     * 
     * @return boolean
     */
    public boolean isMalediction() {
        return this.malediction;
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

    
    /** 
     * Getter
     * @return int
     */
    public int getRange() {
        return range;
    }
    
    /** 
     * Getter
     * @return int
     */
    public int getBombs() {
        return bombs;
    }
    
    /** 
     * Getter
     * @return int
     */
    public int getKey() {
        return key;
    }
    
    /** 
     * Getter
     * @return int
     */
    public int getnbAvailable() {
        return nbAvailable;
    }
    
    /** 
     * Setter
     * @param nb
     */
    public void setnbAvailable(int nb) {
        this.nbAvailable = nb;
    }
    
    /** 
     * Setter
     * @param nb
     */
    public void setLives(int nb) {
        this.lives = nb;
    }

    /**
     * Setter
     * @param range
     */
    public void setRange(int range) {
        this.range = range;
    }

    /**
     * Setter
     * @param bombs
     */
    public void setBombs(int bombs) {
        this.bombs = bombs;
    }
    
    
    /** 
     * Getter
     * @return int
     */
    public int getLives() {
        return lives;
    }
    
    /** 
     * Getter
     * @return Direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Contructeur
     */
    public Player(Game game, Position position) {
        super(game, position);
        this.direction = Direction.S;
        this.lives = game.getInitPlayerLives();
    }
}
