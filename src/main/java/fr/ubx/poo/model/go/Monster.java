package fr.ubx.poo.model.go;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.Movable;
import fr.ubx.poo.model.go.GameObject;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.World;
import fr.ubx.poo.game.WorldEntity;
import fr.ubx.poo.model.decor.*;

import java.util.Timer;
import java.util.TimerTask;

public class Monster extends GameObject implements Movable {

    private final boolean alive = true;
    Direction direction;    
    private long timeCheck;
    private boolean moving = false;


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

    public void timeCheck(long now) {
        if (!moving){
            this.timeCheck = now;
            moving = true;
        }

        if(now - timeCheck >= 1 * 1000000000){
            getMove();
            moving = false;
        }
    }

    public void getMove(){
        searchPlayer();
        Direction nextMove = Direction.random();
        if (canMove(nextMove)){
            doMove(nextMove);
        }else   
            getMove();
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
    public boolean canMove(Direction direction, Position pos){
        Position nextPos = direction.nextPosition(pos);
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

    //Bassé sur Algorithme de Lee
    private void searchPlayer(){
        World world = this.game.getWorld();
        boolean grid[][] = new boolean[world.dimension.height][world.dimension.width];
        
        for (int x = 0; x < world.dimension.width; x++) {
            for (int y = 0; y < world.dimension.height; y++) {
                grid[y][x] = false;
            }
        }
        boolean found;
        found = searchPlayerRec(this.getPosition(), game.getPlayer().getPosition(), grid,0);

        System.out.println("found = " + found");
    }
    private boolean searchPlayerRec(Position pos, Position playerPos, boolean grid[][], int distance){
        if (pos.equals(playerPos)){
            return true;
        }
        grid[pos.y][pos.x] = true;
        
        
        Direction direction = Direction.W;
        Position newPos = direction.nextPosition(pos);
        if (canMove(direction, pos) && !grid[newPos.y][newPos.x]){
            if (searchPlayerRec(newPos, playerPos, grid, distance + 1)) return true;
        }

        direction = Direction.E;
        newPos = direction.nextPosition(pos);
        if (canMove(direction, pos) && !grid[newPos.y][newPos.x]){
            if (searchPlayerRec(newPos, playerPos, grid, distance + 1)) return true;
        }

        direction = Direction.N;
        newPos = direction.nextPosition(pos);
        if (canMove(direction, pos) && !grid[newPos.y][newPos.x]){
            if (searchPlayerRec(newPos, playerPos, grid, distance + 1)) return true;
        }

        direction = Direction.S;
        newPos = direction.nextPosition(pos);
        if (canMove(direction, pos) && !grid[newPos.y][newPos.x]){
            if (searchPlayerRec(newPos, playerPos, grid, distance + 1)) return true;
        }
        
        return false;
    }

}

