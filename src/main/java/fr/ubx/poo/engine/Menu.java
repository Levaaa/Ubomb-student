/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import fr.ubx.poo.game.Game;
import fr.ubx.poo.view.image.ImageFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public final class Menu {

    private static AnimationTimer menuloop;
    private final String windowTitle;
    private Pane layer;
    private Input input;
    private Stage stage;

    /**
     * Paramètres :
     * 0 - lives
     * 1 - nbBombs
     * 2 - explosion
     * 
     */
    public static final int LIVES = 0;
    public static final int NBBOMBS = 1;
    public static final int EXPLOSION = 2;

    private int lives = 3;
    private Text livesText = new Text(String.valueOf(lives));

    private int nbBombs = 3;
    private Text bombsText = new Text(String.valueOf(nbBombs));

    private int explosion = 3;
    private Text explosionText = new Text(String.valueOf(explosion));

    public Menu(final String windowTitle, final Stage stage) {
        this.windowTitle = windowTitle;
        getPropertiesLives();
        initialize(stage);
    }

    
    /** 
     * Initialise l'affichage du menu et mets en place les variables que l'on va pouvoir modifier
     * 
     * @param stage
     * @param game
     */
    private void initialize(Stage stage) {
        this.stage = stage;
        double width = 800;
        double height = 800;

        Font font = new Font(42);

        Image player = new Image(getClass().getResource("/images/" + "playerMenu.png").toExternalForm());
        ImageView playerV = new ImageView(player);
        playerV.setTranslateY(height / 4);
        playerV.setTranslateX(5 * width / 16);

        Image princess = new Image(getClass().getResource("/images/" + "princessMenu.png").toExternalForm());
        ImageView princessV = new ImageView(princess);
        princessV.setTranslateY(- 1 * height / 8);
        princessV.setTranslateX(- 5 * width / 16);


        Image logo = new Image(getClass().getResource("/images/" + "logo.png").toExternalForm());
        ImageView logoV = new ImageView(logo);
        logoV.setTranslateY(-(height/3));
        logoV.setTranslateX(15);
        logoV.setFitHeight(200);
        logoV.setPreserveRatio(true);

        Image play = new Image(getClass().getResource("/images/" + "play.png").toExternalForm());
        ImageView playV = new ImageView(play);
        playV.setOnMouseClicked(event -> launchGame());
        playV.setTranslateY(-(height/8));

        Image quit = new Image(getClass().getResource("/images/" + "quit.png").toExternalForm());
        ImageView quitV = new ImageView(quit);
        quitV.setOnMouseClicked(event -> closeGame());
        quitV.setTranslateY(-(height/16));
 
        ///////////////////////////////// 

        Text livesStr = new Text("Vies");
        livesStr.setTextAlignment(TextAlignment.CENTER);
        livesStr.setFont(font);
        livesStr.setFill(Color.BLACK);
        livesStr.setTranslateY(height / 16 - height / 32);


        Image decLives = new Image(getClass().getResource("/images/" + "arrow.png").toExternalForm());
        ImageView decLivesV = new ImageView(decLives);
        decLivesV.setOnMouseClicked(event -> modifyParameter(LIVES, -1));
        decLivesV.setTranslateY(height / 8 - height / 32);
        decLivesV.setTranslateX(-(width / 16));

        livesText.setTextAlignment(TextAlignment.CENTER);
        livesText.setFont(font);
        livesText.setFill(Color.BLUEVIOLET);
        livesText.setTranslateY(height / 8 - height / 32);

        Image incLives = new Image(getClass().getResource("/images/" + "arrow.png").toExternalForm());
        ImageView incLivesV = new ImageView(incLives);
        incLivesV.setOnMouseClicked(event -> modifyParameter(LIVES, 1));
        incLivesV.setScaleX(-1);
        incLivesV.setTranslateY(height / 8 - height / 32);
        incLivesV.setTranslateX(width / 16);

        ///////////////////////////////
        
        Text bombsStr = new Text("Bombes");
        bombsStr.setTextAlignment(TextAlignment.CENTER);
        bombsStr.setFont(font);
        bombsStr.setFill(Color.BLACK);
        bombsStr.setTranslateY(7 * height / 32 - height / 32);

        Image decBombs = new Image(getClass().getResource("/images/" + "arrow.png").toExternalForm());
        ImageView decBombsV = new ImageView(decBombs);
        decBombsV.setOnMouseClicked(event -> modifyParameter(NBBOMBS, -1));
        decBombsV.setTranslateY(9 * height / 32 - height / 32);
        decBombsV.setTranslateX(-(width / 16));
        
        bombsText.setTextAlignment(TextAlignment.CENTER);
        bombsText.setFont(font);
        bombsText.setFill(Color.BLACK);
        bombsText.setTranslateY(9 * height / 32 - height / 32);
        
        Image incBombs = new Image(getClass().getResource("/images/" + "arrow.png").toExternalForm());
        ImageView incBombsV = new ImageView(incBombs);
        incBombsV.setOnMouseClicked(event -> modifyParameter(NBBOMBS, 1));
        incBombsV.setScaleX(-1);
        incBombsV.setTranslateY(9 * height / 32 - height / 32);
        incBombsV.setTranslateX(width / 16);

        ////////////////////////////////
        Text explosionStr = new Text("Explosion");
        explosionStr.setTextAlignment(TextAlignment.CENTER);
        explosionStr.setFont(font);
        explosionStr.setFill(Color.BLACK);
        explosionStr.setTranslateY(12 * height / 32 - height / 32);

        Image decExplosion = new Image(getClass().getResource("/images/" + "arrow.png").toExternalForm());
        ImageView decExplosionV = new ImageView(decExplosion);
        decExplosionV.setOnMouseClicked(event -> modifyParameter(EXPLOSION, -1));
        decExplosionV.setTranslateY(14 * height / 32 - height / 32);
        decExplosionV.setTranslateX(-(width / 16));

        explosionText.setTextAlignment(TextAlignment.CENTER);
        explosionText.setFont(font);
        explosionText.setFill(Color.RED);
        explosionText.setTranslateY(14 * height / 32 - height / 32);

        Image incExplosion = new Image(getClass().getResource("/images/" + "arrow.png").toExternalForm());
        ImageView incExplosionV = new ImageView(incExplosion);
        incExplosionV.setOnMouseClicked(event -> modifyParameter(EXPLOSION, 1));
        incExplosionV.setScaleX(-1);
        incExplosionV.setTranslateY(14 * height / 32 - height / 32);
        incExplosionV.setTranslateX(width / 16);

        //ajout des éléments à l'affichage
        StackPane root = new StackPane();

        root.getChildren().add(playerV);
        root.getChildren().add(princessV);

        root.getChildren().add(logoV);
        root.getChildren().add(playV);
        root.getChildren().add(quitV);

        root.getChildren().add(decLivesV);
        root.getChildren().add(livesStr);
        root.getChildren().add(incLivesV);
        root.getChildren().add(livesText);

        root.getChildren().add(decBombsV);
        root.getChildren().add(bombsStr);
        root.getChildren().add(incBombsV);
        root.getChildren().add(bombsText);

        root.getChildren().add(decExplosionV);
        root.getChildren().add(explosionStr);
        root.getChildren().add(incExplosionV);
        root.getChildren().add(explosionText);

        //Scene
        Scene scene = new Scene(root, width, height, Color.WHITE);
        stage.setTitle(windowTitle);
        stage.setScene(scene);
        stage.show();

        //Input
        input = new Input(scene);
        menuloop = new AnimationTimer() {
            public void handle(long now) {
                processInput(now);

                render();
            }
        };
        menuloop.start();
   }

    
    /** 
     * Permet de quitter le jeu avec echap et le lancer avec entrer.
     * @param now
     */
    private void processInput(long now) {
        if (input.isExit()) {
            closeGame();
        }
        if (input.isKey()){
            launchGame();
        }
        input.clear();
    }

    /**
     * Met à jour l'affichage des 3 paramètres du menu 
     */
    private void render() {
        bombsText.setText(String.valueOf(nbBombs));
        livesText.setText(String.valueOf(lives));
        explosionText.setText(String.valueOf(explosion));
    }

    /**
     * Lance l'engine du menu
     */
    public void start() {
        menuloop.start();
    }

    /**
     * Lance le jeu
     */
    private void launchGame() {
        menuloop.stop();
        stage.close();
        ImageFactory.getInstance().load();
        String path = getClass().getResource("/sample").getFile();
        Game game = new Game(path, lives, nbBombs, explosion);
        GameEngine engine = new GameEngine("UBomb", game, stage);
        engine.start();
    }

    /**
     * Ferme le jeu
     */
    private void closeGame() {
        menuloop.stop();
        Platform.exit();
        System.exit(0);
    }

    
    /** 
     * Modifie le paramètre du menu séléctionné à l'aide des constantes et ajoute 1 ou retire 1 en fonction du mode utilisé. 
     * 
     * @param parameter Paramètre à modifier :
     * 0 - lives
     * 1 - nbBombs
     * 2 - explosion
     * @param mod Permet d'ajouter ou retirer un au paramètre.
     */
    private void modifyParameter(int parameter, int mod) {
        switch (parameter) {
            case 0:
                if (mod == 1 && lives < 9) lives ++;
                else if (mod == -1 && lives > 1) lives --;
                break;
            
            case 1:
                if (mod == 1 && nbBombs < 9) nbBombs ++;
                else if (mod == -1 && nbBombs > 1) nbBombs --;
                break;

            case 2:
                if (mod == 1 && explosion < 9) explosion ++;
                else if (mod == -1 && explosion > 1) explosion --;
                break;
        }
    }

    /**
     * Récupère les valeurs initialisées dans config.properties des 3 paramètres du Menu et les met à jour.
     */
    private void getPropertiesLives(){
        String path = getClass().getResource("/sample").getFile();
        try (InputStream input = new FileInputStream(new File(path, "config.properties"))) {
            Properties prop = new Properties();
            // load the configuration file
            prop.load(input);
            this.lives = Integer.parseInt(prop.getProperty("lives", "3"));
            this.nbBombs = Integer.parseInt(prop.getProperty("bombs", "3"));
            this.explosion = Integer.parseInt(prop.getProperty("range", "3"));

        } catch (IOException ex) {
            System.err.println("Error loading configuration");
        }
    }
}
