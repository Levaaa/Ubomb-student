/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.model.go;

import fr.ubx.poo.game.Position;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.model.Entity;

/***
 * A GameObject can acces the game and knows its position in the grid.
 */
public abstract class GameObject extends Entity {
    protected final Game game;
    private Position position;

    /** 
     * Getter
     * @return Position
     */
    public Position getPosition() {
        return position;
    }

    
    /** 
     * Setter
     * @param position
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * Constructeur
     */
    public GameObject(Game game, Position position) {
        this.game = game;
        this.position = position;
    }
}
