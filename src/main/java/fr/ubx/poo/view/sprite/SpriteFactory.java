/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.view.sprite;

import static fr.ubx.poo.view.image.ImageResource.*;

import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.go.character.Player;
import fr.ubx.poo.view.image.ImageFactory;
import javafx.scene.layout.Pane;
import fr.ubx.poo.model.Entity;
import fr.ubx.poo.model.decor.*;
import fr.ubx.poo.model.go.*;

public final class SpriteFactory {
    
    public static Sprite createDecor(Pane layer, Position position, Entity entity) {
        ImageFactory factory = ImageFactory.getInstance();
        Decor decor = (Decor) entity;
        if (decor instanceof Stone)
            return new SpriteDecor(layer, factory.get(STONE), position);
        if (decor instanceof Tree)
            return new SpriteDecor(layer, factory.get(TREE), position);
        if (decor instanceof Box)
            return new SpriteDecor(layer, factory.get(BOX), position);
        if (decor instanceof Princess)
            return new SpriteDecor(layer, factory.get(PRINCESS), position);
        if (decor instanceof BombNbDec)
            return new SpriteDecor(layer, factory.get(BOMB_NB_DEC), position);
        if (decor instanceof BombNbInc)
            return new SpriteDecor(layer, factory.get(BOMB_NB_INC), position);
        if (decor instanceof BombRangeDec)
            return new SpriteDecor(layer, factory.get(BOMB_RANGE_DEC), position);
        if (decor instanceof BombRangeInc)
            return new SpriteDecor(layer, factory.get(BOMB_RANGE_INC), position);
        if (decor instanceof DoorNextClosed)
            return new SpriteDecor(layer, factory.get(DOOR_CLOSED), position);
        if (decor instanceof DoorNextOpened)
            return new SpriteDecor(layer, factory.get(DOOR_OPENED), position); 
        if (decor instanceof DoorPrevOpened)
            return new SpriteDecor(layer, factory.get(DOOR_OPENED), position);  
        if (decor instanceof Key)
            return new SpriteDecor(layer, factory.get(KEY), position); 
        if (decor instanceof Heart)
            return new SpriteDecor(layer, factory.get(HEART), position); 
        if (decor instanceof Explosion)
            return new SpriteDecor(layer, factory.get(EXPLOSION), position); 
        if (decor instanceof Malediction)
            return new SpriteDecor(layer, factory.get(MALEDICTION), position); 
        return null;
    }

    public static Sprite createPlayer(Pane layer, Player player) {
        return new SpritePlayer(layer, player);
    }

    public static Sprite createBomb(Pane layer, Bomb bomb) {
        return new SpriteBomb(layer, bomb);
    }

    public static Sprite createMonster(Pane layer, Monster monster) {
        return new SpriteMonster(layer, monster);
    }

}
