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

import java.util.Timer;
import java.util.TimerTask;

public class Bomb extends GameObject {
    private int phase = 4;
    private Timer t = new Timer();

    private long timeCheck;
    private boolean explosion = false;

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
        return explosion;
    }

    public void update(long now) {
        if(!explosion && now - timeCheck >= 1 * 1000000000){
            timeCheck = now;
            phase --;
            if (phase == 0) explosion = true; 
        }

    }

    //execute toute la séquence de la bombe 
    public void doExplosion(){
        t.schedule(new Decompte(), 1000);
    }

    private class Decompte extends TimerTask{ 

        public void run(){
            phase--;
            t.cancel();
            if(phase > -1){
                t = new Timer();
                t.schedule(new Decompte(), 1000);
            } 
            //execution explosion
            if(phase == 0){
                //redonne la bombe
                game.getPlayer().setnbAvailable(game.getPlayer().getnbAvailable() + 1);
                
                makeExplosion(game.getPlayer().getRange(), 
                              getPosition(),
                              Direction.W
                              );
                makeExplosion(game.getPlayer().getRange(), 
                              getPosition(),
                              Direction.E
                              );

                makeExplosion(game.getPlayer().getRange(), 
                              getPosition(),
                              Direction.S
                              );
                makeExplosion(game.getPlayer().getRange(), 
                              getPosition(),
                              Direction.N
                              );
            }
            
        }
    

    //execute juste la partie explosion avec les différentes interactions liées
    private void makeExplosion(int range, Position pos, Direction direction){
        if (range == 0) return;
        //diminution vie player
        if (pos.equals(game.getPlayer().getPosition())) {
            game.getPlayer().hurtPlayer();
            return;
        }
        World world = game.getWorld();
        Decor decor = world.get(pos);
        if (decor != null){
            if (decor instanceof Stone)
                return;
            if (decor instanceof Tree){
                world.clear(pos);
                //world.set(pos, new Explosion());
                return;
            }
            if (decor instanceof Box){
                world.clear(pos);
                return;
            }
            if (decor instanceof Princess)
                return;
            if (decor instanceof BombNbDec){
                world.clear(pos);
                //world.set(pos, new Explosion());
                return;
            }
            if (decor instanceof BombNbInc){
                world.clear(pos);
                //world.set(pos, new Explosion());
                return;                
            }
            if (decor instanceof BombRangeDec){
                world.clear(pos);
                //world.set(pos, new Explosion());
                return;
            }
            if (decor instanceof BombRangeInc){
                world.clear(pos);
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
                //world.set(pos, new Explosion());
                return;
            }
        }else{
            if (world.isInside(direction.nextPosition(pos))){
                //world.set(pos, new Explosion());
                makeExplosion(range - 1, direction.nextPosition(pos), direction);
            }
            return;            
        }
    }
    }
}
