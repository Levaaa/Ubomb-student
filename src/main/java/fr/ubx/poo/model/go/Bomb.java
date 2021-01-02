/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.model.go;

import fr.ubx.poo.game.Position;
import fr.ubx.poo.game.World;
import fr.ubx.poo.model.decor.*;
import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Game;
import java.util.ArrayList;
import java.util.List;

public class Bomb extends GameObject {
    /**
     * On répartit le processus d'explosion de la bombe en 7 phases :
     * 6) sprite (1ere seconde)
     * 5) sprite (2eme seconde)
     * 4) sprite (3eme seconde)
     * 3) sprite (4eme seconde)
     * 2) explosion (gestion interne de l'explosion)
     * 1) ajout sprite (gestion GUI après avoir traiter l'explosion)
     * 0) suppression sprite après la seconde d'explosion
     * 
     * A savoir que la phase 2 & 1 s'enchaîne directement à l'inverse des autres qui sont exécutées après 1 seconde.
     */
    private int phase = 6;
    
    
    /**
     * Permet de mettre en mémoire la dernière fois qu'on a exécuté une des phases de la bombe.
     * (Pour veiller à bien attendre 1 seconde avant tout nouveau traitement)
     */
    private long timeCheck;

    /**
     * Liste des coordonées de l'explosion de la bombe
     */
    private List<Position> zone = new ArrayList<>();

    /**
     * Constructeur
     * 
     * @param game Partie actuelle
     * @param position Position de la bombe
     * @param now Temps au moment de la création
     */
    public Bomb(Game game, Position position, long now) {
        super(game, position);
        timeCheck = now;
    }
    
    /** 
     * Getter
     * @return int
     */
    public int getPhase() {
        return this.phase;
    }
    
    /** 
     * Setter
     * @param phase
     */
    public void setPhase(int phase) {
        this.phase = phase;
    }
    /** 
     * Setter
     * @param now 
     */
    public void setTimeCheck(long now) {
        this.timeCheck = now;
    }

    /** 
     * Retourne vrai si la bombe est dans sa phase d'explosion, faux sinon.
     * @return boolean
     */
    public boolean isExploded(){
        return phase == 2;
    }
    
    /** 
     * Getter
     * @return List<Position>
     */
    public List<Position> getZone(){
        return this.zone;
    }
    
    /** 
     * Gère l'actualisation de la bombe en temps réel.
     * Applique le changement de phase de la bombe
     * 
     * @param now Temps donnée par le moteur de jeu.
     */
    public void update(long now) {
        if(phase >= 0 && now - timeCheck >= 1 * 1000000000){
            timeCheck = now;
            phase --;
        }
    }    

    /**
     * Applique l'explosion de la bombe dans les 4 directions cardinales.
     * Met à jour le champs zone correspondant à la zone d'explosion de la bombe.
     * 
     */
    public void doExplosion() {
        phase = 1;//permet de faire qu'une fois l'explosion dans update
        List<Bomb> bombsExploded = new ArrayList<>();
        bombsExploded.add(this);
        makeExplosion(game.getPlayer().getRange(), getPosition(), Direction.W);
        makeExplosion(game.getPlayer().getRange(), getPosition(), Direction.E);
        makeExplosion(game.getPlayer().getRange(), getPosition(), Direction.N);
        makeExplosion(game.getPlayer().getRange(), getPosition(), Direction.S);
    }
    
    /** 
     * Execute le traitement de l'explosion pour une position de cellule donnée.
     * 
     * Gère la destruction du décor.
     * Gère la destruction des Monstres.
     * 
     * @param range Portée de la bombe restant à traiter.
     * @param pos Position courrante à traiter.
     * @param direction Direction de la propagation de l'explosion.
     */
    private void makeExplosion(int range, Position pos, Direction direction){
        if (range == 0) return;

        List<Bomb> bombList = game.getBombs();
        for (Bomb bomb : bombList) {
            if (bomb.getPhase() > 2 && pos.equals(bomb.getPosition())) {                
                //active la phase d'explosion
                bomb.setPhase(2);
                //synchronise les bombes sur l'explosion initiale
                bomb.setTimeCheck(this.timeCheck);
                return;
            }   
        }

        List<Monster> monsterList = game.getMonsters();
        for (Monster monster : monsterList) {
            if (pos.equals(monster.getPosition())) {
                monster.kill();
            }   
        }

        World world = game.getWorld();
        Decor decor = world.get(pos);
        if (decor != null){
            if (decor instanceof Stone)
                return;
            if (decor instanceof Tree){
                world.clear(pos);
                zone.add(pos);
                //world.set(pos, new Explosion());
                return;
            }
            if (decor instanceof Box){
                world.clear(pos);
                zone.add(pos);
                return;
            }
            if (decor instanceof Princess)
                return;
            if (decor instanceof BombNbDec){
                world.clear(pos);
                zone.add(pos);
            }
            if (decor instanceof BombNbInc){
                world.clear(pos);
                zone.add(pos);
                //world.set(pos, new Explosion());              
            }
            if (decor instanceof BombRangeDec){
                world.clear(pos);
                zone.add(pos);
                //world.set(pos, new Explosion());
            }
            if (decor instanceof BombRangeInc){
                world.clear(pos);
                zone.add(pos);
                //world.set(pos, new Explosion());
            }
            if (decor instanceof DoorNextClosed)
                return;
            if (decor instanceof DoorNextOpened)
                return;
            if (decor instanceof DoorPrevOpened)
                return;
            if (decor instanceof Key)
                return;
            if (decor instanceof Heart){
                world.clear(pos);
                zone.add(pos);
                //world.set(pos, new Explosion());
            }
            makeExplosion(range - 1, direction.nextPosition(pos), direction);
        }else{
            if(!zone.contains(pos))  zone.add(pos);
            if (world.isInside(direction.nextPosition(pos))){
                //world.set(pos, new Explosion());
                makeExplosion(range - 1, direction.nextPosition(pos), direction);
            }
            return;            
        }
        
    }
}
