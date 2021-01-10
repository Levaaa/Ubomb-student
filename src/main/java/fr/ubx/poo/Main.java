/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import fr.ubx.poo.engine.GameEngine;
import fr.ubx.poo.engine.Menu;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.view.image.ImageFactory;
import fr.ubx.poo.view.sprite.Sprite;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        /*
        A été déplacé, on commence le programme en lancant un engine menu, similaire au GameEngine.
        Ensuite, si on lance 

        ImageFactory.getInstance().load();
        String path = getClass().getResource("/sample").getFile();
        Game game = new Game(path);
        GameEngine engine = new GameEngine("UBomb", game, stage);
        engine.start();
        */

        String path = getClass().getResource("/sample").getFile();
        boolean menu = true;
        try (InputStream input = new FileInputStream(new File(path, "config.properties"))) {
            Properties prop = new Properties();
            // load the configuration file
            prop.load(input);
            menu = Boolean.parseBoolean(prop.getProperty("menu", "true"));

        } catch (IOException ex) {
            System.err.println("Error loading configuration");
        }

        if (menu){
            new Menu("UBomb", stage);
        }
        else {
            ImageFactory.getInstance().load();
            Game game = new Game(path);
            GameEngine engine = new GameEngine("UBomb", game, stage);
            engine.start();
        }


        //menu.start();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
