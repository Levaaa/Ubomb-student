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

    @Override
    public void updateImage() {
        Player player = (Player) go;
        if (player.isInvincible()){
            
            ///////////////A corriger///////////////////
            effect.setBrightness(0.2);
            Image sprite = ImageFactory.getInstance().getPlayer(player.getDirection());
            ImageView view = new ImageView(sprite);
            
            view.setVisible(false);
            
            setImage(view.getImage());
            return;
        }
        setImage(ImageFactory.getInstance().getPlayer(player.getDirection()));
    }
}
