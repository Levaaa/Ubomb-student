package fr.ubx.poo.model.go;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.Movable;
import fr.ubx.poo.model.go.GameObject;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.World;
import fr.ubx.poo.model.decor.*;

import java.util.Timer;
import java.util.TimerTask;

public class Monster extends GameObject implements Movable {

    private final boolean alive = true;
    Direction direction;
    private int speed = 0;
    private Timer t = new Timer();

    public Monster(Game game, Position position) {
        super(game, position);
        this.direction = Direction.S;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isAlive() {
        return alive;
    }

    public void getMove(){
        Direction nextMove = Direction.random();
        if (canMove(nextMove)){
            doMove(nextMove);
        }else   
            move();
    }

    public void move(){
        t.schedule(new Move(), 1000);
    }


    @Override
    public boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        World world = game.getWorld();
        //collision avec les bords
        if (nextPos.inside(world.dimension)){
            Decor decor = world.get(nextPos);
            if (decor == null) return true;
            if (decor instanceof Stone) return false;
            if (decor instanceof Tree) return false;
            if (decor instanceof Box) return false;
            if (decor instanceof Princess) return false;
            if (decor instanceof BombNbDec) return true;
            if (decor instanceof BombNbInc) return true;
            if (decor instanceof BombRangeDec) return true;
            if (decor instanceof BombRangeInc) return true;
            if (decor instanceof DoorNextClosed) return false;
            if (decor instanceof DoorNextOpened) return false;
            if (decor instanceof DoorPrevOpened) return false;
            if (decor instanceof Key) return true;
            if (decor instanceof Heart) return true;
        }
        return false;
    }

    @Override
    public void doMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        setPosition(nextPos);
        if (direction != this.direction) {
            this.direction = direction;
        }
    }

    private class Move extends TimerTask{ 

        public void run(){
            getMove();
            t.cancel();
            if (alive && game.getPlayer().isWinner() == false && game.getPlayer().isAlive() == true){
                t = new Timer();
                t.schedule(new Move(), 1000);
            } 
        }
    }
}

