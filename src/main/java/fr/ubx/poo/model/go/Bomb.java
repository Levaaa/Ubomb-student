/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.model.go;

import fr.ubx.poo.game.Position;
import fr.ubx.poo.game.World;
import fr.ubx.poo.model.decor.*;

import java.sql.Time;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.model.Entity;

import java.util.ArrayList;
import java.util.List;

public class Bomb extends GameObject {
    private int phase = 3;
    /*
    differentes phases
    3 sprite
    2 sprite
    1 sprite
    0 sprite
    -1 explosion sprite
    -2 suppresion delete sprite
    */
    private long timeCheck;
    private boolean explosion = false;
    private List<Position> zone = new ArrayList<>();

    public Bomb(Game game, Position position, long now) {
        super(game, position);
        timeCheck = now;
    }


    public int getPhase() {
        return this.phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public boolean isExploded(){
        return phase == -1;
    }

    public List<Position> getZone(){
        return this.zone;
    }

    public void update(long now) {
        if(phase > -2 && now - timeCheck >= 1 * 1000000000){
            timeCheck = now;
            phase --;
            if (phase == 0){
                game.getPlayer().setnbAvailable(game.getPlayer().getnbAvailable() + 1);
            }
        }
    }    

    public void doExplosion() {
        makeExplosion(game.getPlayer().getRange(), getPosition(), Direction.W);
        makeExplosion(game.getPlayer().getRange(), getPosition(), Direction.E);
        makeExplosion(game.getPlayer().getRange(), getPosition(), Direction.N);
        makeExplosion(game.getPlayer().getRange(), getPosition(), Direction.S);
        phase --; //permet de faire qu'une fois l'explosion dans update
        System.out.println("explosion done");
    }

    //execute juste la partie explosion avec les différentes interactions liées
    private void makeExplosion(int range, Position pos, Direction direction){
        if (range == 0) return;

        //diminution vie player
        if (pos.equals(game.getPlayer().getPosition())) {
            game.getPlayer().hurtPlayer();
        }

        List<Bomb> bombList = game.getBombs();
        for (Bomb bomb : bombList) {
            if (pos.equals(bomb.getPosition()) && !this.equals(bomb)) {
                bomb.setPhase(-1);
                return;
            }   
        }

        List<Monster> monsterList = game.getMonsters();
        for (Monster monster : monsterList) {
            if (pos.equals(monster.getPosition())) {
                monster.kill();
                return;
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
                //world.set(pos, new Explosion());
                return;
            }
            if (decor instanceof BombNbInc){
                world.clear(pos);
                zone.add(pos);
                //world.set(pos, new Explosion());
                return;                
            }
            if (decor instanceof BombRangeDec){
                world.clear(pos);
                zone.add(pos);
                //world.set(pos, new Explosion());
                return;
            }
            if (decor instanceof BombRangeInc){
                //world.clear(pos);
                zone.add(pos);
                //world.set(pos, new Explosion());
                return;
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
                return;
            }
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
