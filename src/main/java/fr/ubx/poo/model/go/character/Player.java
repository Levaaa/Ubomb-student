/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.model.go.character;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.game.World;
import fr.ubx.poo.game.WorldEntity;
import fr.ubx.poo.model.Movable;
import fr.ubx.poo.model.go.GameObject;
import fr.ubx.poo.game.Game;

import fr.ubx.poo.model.decor.*;

public class Player extends GameObject implements Movable {

    private final boolean alive = true;
    Direction direction;
    private boolean moveRequested = false;
    private int lives = 1;
    private boolean winner;
    private int range = 1;
    private int bombs = 0;
    private int key = 0;

    public int getRange() {
        return range;
    }
    public int getBombs() {
        return bombs;
    }
    public int getKey() {
        return key;
    }

    public Player(Game game, Position position) {
        super(game, position);
        this.direction = Direction.S;
        this.lives = game.getInitPlayerLives();
    }

    public int getLives() {
        return lives;
    }

    public Direction getDirection() {
        return direction;
    }

    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
        }
        moveRequested = true;
    }

    @Override
    public boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        World world = game.getWorld();
        //collision avec les bords
        if (nextPos.inside(world.dimension)){
            Decor decor = world.get(nextPos);
            if (decor == null) return true;
            if (decor.toString() ==  "Stone") return false;
            if (decor.toString() ==  "Tree") return false;
            if (decor.toString() ==  "Box") return false;
            if (decor.toString() ==  "Monster") lives --;
            if (decor.toString() ==  "Heart") lives ++;
            if (decor.toString() ==  "Princess") winner = true;
            if (decor.toString() ==  "BombNbDec"){
                if (bombs > 0) bombs --;
                world.clear(nextPos);
            }
            if (decor.toString() ==  "BombNbInc") bombs ++;
            if (decor.toString() ==  "BombRangeDec") 
                if (range > 1) range --;

            if (decor.toString() ==  "BombRangeInc"){
                range ++;
            }
            if (decor.toString() ==  "Key") {
                key ++;
                world.set(nextPos, new DoorNextOpened());
            }
            if (decor.toString() ==  "DoorNextClosed") game.setLevel(game.getLevel() + 1);
            if (decor.toString() ==  "DoorPrevOpened") game.setLevel(game.getLevel() - 1);

            return true;
        }
        return false;
    }

    public void doMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        setPosition(nextPos);
    }

    public void update(long now) {
        if (moveRequested) {
            if (canMove(direction)) {
                doMove(direction);
            } 
        }
        moveRequested = false;
    }

    public boolean isWinner() {
        return winner;
    }

    public boolean isAlive() {
        if (lives == 0) return !alive;
        return alive;
    }

}
