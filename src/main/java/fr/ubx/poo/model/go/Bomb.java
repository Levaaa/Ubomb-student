/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.model.go;

import fr.ubx.poo.game.Position;
import fr.ubx.poo.game.World;

import java.sql.Time;

import fr.ubx.poo.game.Game;
import fr.ubx.poo.model.Entity;

import java.util.Timer;
import java.util.TimerTask;

public class Bomb extends GameObject {
    private int phase = 4;
    private Timer t = new Timer();

    public Bomb(Game game, Position position) {
        super(game, position);
    }


    public int getPhase() {
        return this.phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    //execute toute la séquence de la bombe 
    public void doExplosion(){
        t.schedule(new decompte(), 1000);
    }

    public class decompte extends TimerTask{

        public void run(){
            phase--;
            System.out.println(phase);
            t.cancel();
            if(phase > -1){
                t = new Timer();
                t.schedule(new decompte(), 1000);
            } 
            //execution explosion
            if(phase == 0){
                //redonne la bombe
                game.getPlayer().setnbAvailable(game.getPlayer().getnbAvailable() + 1);
                makeExplosion();
            }
            
        }
    }

    //execute juste la partie explosion avec les différentes interactions liées
    private void makeExplosion(){
        return;
    }


}
