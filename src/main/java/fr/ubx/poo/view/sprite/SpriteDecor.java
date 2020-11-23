package fr.ubx.poo.view.sprite;

import fr.ubx.poo.game.Position;
import fr.ubx.poo.view.image.ImageFactory;
import fr.ubx.poo.view.image.ImageResource;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;


public class SpriteDecor extends Sprite {
    private Position position;

    public SpriteDecor(Pane layer, Image image, Position position) {
        super(layer, image);
        this.position = position;
    }

    @Override
    public void updateImage() {
        
        //setImage(ImageFactory.getInstance().get(ImageResource.PLAYER_DOWN));
    }

    @Override
    public Position getPosition() {
        return position;
    }
}
