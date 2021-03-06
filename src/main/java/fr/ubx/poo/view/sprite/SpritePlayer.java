/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.view.sprite;

import fr.ubx.poo.model.go.character.Player;
import fr.ubx.poo.view.image.ImageFactory;
import fr.ubx.poo.view.image.ImageResource;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class SpritePlayer extends SpriteGameObject {

    public SpritePlayer(Pane layer, Player player) {
        super(layer, null, player);
        updateImage();
    }

    /**
     * Met à jour l'image du joueur en fonction de sa direction.
     * Applique aussi un effet visuel si le joueur est invulnérable.
     */
    @Override
    public void updateImage() {
        Player player = (Player) go;

        /**
         * Applique l'effet visuel d'invincibilité
         */
        if (player.isInvincible()){
            ColorAdjust effect = new ColorAdjust();

            //charge les effets
            effect.setBrightness(0.6);
            effect.setSaturation(0.6);
            
            Image image = getImage();
            ImageView imageView = new ImageView(image);

            //applique les effets
            imageView.setEffect(effect);
            
            //le met à jour pour la fonction Sprite.render() 
            setImageView(imageView);
        }

        /**
         * Applique l'effet visuel de malédiction
         */
        if (player.isMalediction()){
            ColorAdjust effect = new ColorAdjust();

            effect.setHue(1);
            effect.setSaturation(1);
            
            Image image = getImage();
            ImageView imageView = new ImageView(image);

            //applique les effets
            imageView.setEffect(effect);
            
            //le met à jour pour la fonction Sprite.render() 
            setImageView(imageView);
        }

        //le met à jour l'image du joueur pour la fonction Sprite.render()
        setImage(ImageFactory.getInstance().getPlayer(player.getDirection()));
    }
}
