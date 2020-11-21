package fr.ubx.poo.model.decor;

import fr.ubx.poo.model.Movable;
import fr.ubx.poo.game.Direction;

public class Box extends Decor implements Movable{
    @Override
    public String toString() {
        return "Box";
    }
    @Override
    public boolean canMove(Direction direction){
        return true;
    }

    @Override
    public void doMove(Direction direction){

    }
}