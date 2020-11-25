package fr.ubx.poo.model.go;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.Movable;
import fr.ubx.poo.model.go.GameObject;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.World;
import fr.ubx.poo.model.decor.*;

public class Monster extends GameObject implements Movable {

    private final boolean alive = true;
    Direction direction;
    private int speed = 0;

    public Monster(Game game, Position position) {
        super(game, position);
        this.direction = Direction.S;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        World world = game.getWorld();
        //collision avec les bords
        if (nextPos.inside(world.dimension)){
            Decor decor = world.get(nextPos);
            if (decor == null) return true;
        }
        return false;
    }

    @Override
    public void doMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        setPosition(nextPos);
    }

    public boolean isAlive() {
        return alive;
    }

    public void move(){
        Direction nextmove = Direction.random();
        if (canMove(nextmove)){
            updateDirection(nextmove);
            doMove(nextmove);
        }
    }

    private void updateDirection(Direction direction){
        if (direction != this.direction) {
            this.direction = direction;
        }
    }
}

