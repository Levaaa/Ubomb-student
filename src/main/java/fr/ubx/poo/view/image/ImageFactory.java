/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.view.image;

import fr.ubx.poo.game.Direction;
import javafx.scene.image.Image;

import static fr.ubx.poo.view.image.ImageResource.*;

public final class ImageFactory {
    private final Image[] images;

    private final ImageResource[] directions = new ImageResource[]{
            // Direction { N, E, S, W }
            PLAYER_UP, PLAYER_RIGHT, PLAYER_DOWN, PLAYER_LEFT,
    };

    private final ImageResource[] digits = new ImageResource[]{
            DIGIT_0, DIGIT_1, DIGIT_2, DIGIT_3, DIGIT_4,
            DIGIT_5, DIGIT_6, DIGIT_7, DIGIT_8, DIGIT_9,
    };
    
    private final ImageResource[] monsters = new ImageResource[]{
        // Direction { N, E, S, W }
        MONSTER_UP, MONSTER_RIGHT, MONSTER_DOWN, MONSTER_LEFT,
    };


    private final ImageResource[] bombs = new ImageResource[]{
        BOMB_1, BOMB_2, BOMB_3, BOMB_4, 
    };

    private ImageFactory() {
        images = new Image[ImageResource.values().length];
    }

    /**
     * Point d'accès pour l'instance unique du singleton
     */
    public static ImageFactory getInstance() {
        return Holder.instance;
    }

    
    /** 
     * @param file
     * @return Image
     */
    private Image loadImage(String file) {
        return new Image(getClass().getResource("/images/" + file).toExternalForm());
    }

    public void load() {
        for (ImageResource img : ImageResource.values()) {
            images[img.ordinal()] = loadImage(img.getFileName());
        }
    }

    
    /** 
     * @param img
     * @return Image
     */
    public Image get(ImageResource img) {
        return images[img.ordinal()];
    }

    
    /** 
     * Retourne l'image des chiffres correspondant au chiffre donné.
     * 
     * @param i
     * @return Image
     */
    public Image getDigit(int i) {
        if (i < 0 || i > 9)
            throw new IllegalArgumentException();
        return get(digits[i]);
    }

    
    /** 
     * Retourne l'image du joueur correspondant à sa direction actuelle.
     * 
     * @param direction
     * @return Image
     */
    public Image getPlayer(Direction direction) {
        return get(directions[direction.ordinal()]);
    }

    
    /** 
     * Retourne l'image du monstre correspondant à sa direction actuelle.
     * 
     * @param direction
     * @return Image
     */
    public Image getMonster(Direction direction) {
        return get(monsters[direction.ordinal()]);
    }
    
    
    /** 
     * Retourne l'image de la bombe correspondant à sa phase actuelle.
     * 
     * @param phase
     * @return Image
     */
    public Image getBomb(int phase) {
        if (phase >= 3 && phase <= 6) return get(bombs[phase - 3]);
        return null;
    }



    /**
     * Holder
     */
    private static class Holder {
        /**
         * Instance unique non préinitialisée
         */
        private final static ImageFactory instance = new ImageFactory();
    }

}
