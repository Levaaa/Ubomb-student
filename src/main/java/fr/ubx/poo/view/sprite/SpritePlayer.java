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
    private final ColorAdjust effect = new ColorAdjust();

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
        if (player.isInvincible()){
            
            effect.setBrightness(0.6);
            effect.setSaturation(0.6);
            
            Image image = getImage();
            ImageView imageView = new ImageView(image);

            imageView.setEffect(effect);
            setImageView(imageView);
            /*
            colorAdjust.setContrast(0.1);
            colorAdjust.setHue(-0.05);
            colorAdjust.setBrightness(0.1);
            colorAdjust.setSaturation(0.2);

            Image image = new Image("boat.jpg");
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(200);
            imageView.setPreserveRatio(true);
            imageView.setEffect(colorAdjust);
            */
        }
        setImage(ImageFactory.getInstance().getPlayer(player.getDirection()));
    }
}
