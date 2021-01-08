/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.engine;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.view.sprite.Sprite;
import fr.ubx.poo.view.sprite.SpriteBomb;
import fr.ubx.poo.view.sprite.SpriteDecor;
import fr.ubx.poo.view.sprite.SpriteFactory;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.game.World;
import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.decor.DoorNextClosed;
import fr.ubx.poo.model.decor.DoorNextOpened;
import fr.ubx.poo.model.decor.DoorPrevOpened;
import fr.ubx.poo.model.decor.Explosion;
import fr.ubx.poo.model.go.Bomb;
import fr.ubx.poo.model.go.Monster;
import fr.ubx.poo.model.go.character.Player;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class GameEngine {

    private static AnimationTimer gameLoop;
    private final String windowTitle;
    private final Game game;
    private final Player player;
    private StatusBar statusBar;
    private Pane layer;
    private Input input;
    private Stage stage;
    private Sprite spritePlayer;
    private final List<Sprite> sprites = new ArrayList<>();
    private final List<Sprite> spritesBomb = new ArrayList<>();
    private final List<Sprite> spritesMonster = new ArrayList<>();
    private final List<Sprite> spritesExplosion = new ArrayList<>();
    private final List<World> memoryWorld = new ArrayList<>(); //Permet de mettre en mémoires les mondes au second plan
    

    public GameEngine(final String windowTitle, Game game, final Stage stage) {
        this.windowTitle = windowTitle;
        this.game = game;
        this.player = game.getPlayer();
        initialize(stage, game);
        buildAndSetGameLoop();
    }

    
    /** 
     * @param stage
     * @param game
     */
    private void initialize(Stage stage, Game game) {
        this.stage = stage;
        Group root = new Group();
        layer = new Pane();

        int height = game.getWorld().dimension.height;
        int width = game.getWorld().dimension.width;
        int sceneWidth = width * Sprite.size;
        int sceneHeight = height * Sprite.size;
        Scene scene = new Scene(root, sceneWidth, sceneHeight + StatusBar.height);
        scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

        stage.setTitle(windowTitle);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        input = new Input(scene);
        root.getChildren().add(layer);
        statusBar = new StatusBar(root, sceneWidth, sceneHeight, game);
        // Create decor sprites
        game.getWorld().forEach( (pos,d) -> sprites.add(SpriteFactory.createDecor(layer, pos, d)));
        spritePlayer = SpriteFactory.createPlayer(layer, player);    

        List<Monster> monsters = game.getWorld().getMonsters();
        for (Monster m : monsters){
            spritesMonster.add(SpriteFactory.createMonster(layer, m));
        }
        List<Bomb> bombs = game.getWorld().getBombs();
        for (Bomb bomb : bombs) {
            spritesBomb.add(SpriteFactory.createBomb(layer, bomb));
        }
   }

    protected final void buildAndSetGameLoop() {
        gameLoop = new AnimationTimer() {
            public void handle(long now) {
                // Check keyboard actions
                processInput(now);

                // Do actions
                update(now);

                // Graphic update
                render();
                statusBar.update(game);
            }
        };
    }

    
    /** 
     * Gère les différents input possibles : 
     * Flèche haut    -> Tentative mouvement vers le haut
     * Flèche bas     -> Tentative mouvement vers le bas 
     * Flèche gauche  -> Tentative mouvement vers la gauche 
     * Flèche droite  -> Tentative mouvement vers la droite
     *
     * Echap -> Fermer le jeu
     * Space -> Poser une bombe
     * Enter -> Utiliser une clé
     * 
     * @param now
     */
    private void processInput(long now) {
        if (input.isExit()) {
            gameLoop.stop();
            Platform.exit();
            System.exit(0);
        }
        if (input.isMoveDown()) {
            player.requestMove(Direction.S);
        }
        if (input.isMoveLeft()) {
            player.requestMove(Direction.W);
        }
        if (input.isMoveRight()) {
            player.requestMove(Direction.E);
        }
        if (input.isMoveUp()) {
            player.requestMove(Direction.N);
        }
        if (input.isBomb()) {
            playerUseBomb(now);
        }
        if (input.isKey()){
            //Si la position de la case où regarde le joueur est la porte alors on l'ouvre
            World world = game.getWorld();
            Position doorPosition = player.getDirection().nextPosition(player.getPosition());
            if (world.get(doorPosition) instanceof DoorNextClosed){
                if (player.getKey() > 0){
                    world.set(doorPosition, new DoorNextOpened());
                    player.useKey();
                }   
            }
        }
        input.clear();
    }

    
    /** 
     * @param msg
     * @param color
     */
    private void showMessage(String msg, Color color) {
        Text waitingForKey = new Text(msg);
        waitingForKey.setTextAlignment(TextAlignment.CENTER);
        waitingForKey.setFont(new Font(60));
        waitingForKey.setFill(color);
        StackPane root = new StackPane();
        root.getChildren().add(waitingForKey);
        Scene scene = new Scene(root, 400, 200, Color.WHITE);
        stage.setTitle(windowTitle);
        stage.setScene(scene);
        input = new Input(scene);
        stage.show();
        new AnimationTimer() {
            public void handle(long now) {
                processInput(now);
            }
        }.start();
    }

    /** 
     * Exécute les fonctions de mise à jour des éléments intéractifs du jeu :
     * - le joueur
     * - les monstres
     * - les bombes.
     * 
     * Supprime le monstre s'il est mort.
     * Applique l'explosion de la bombe.
     * Applique le changement de décor.
     * Applique le changement de niveau.
     * Regarde si le joueur a gagné ou perdu.
     * 
     * @param now Temps donnée par le moteur de jeu.
     */
    private void update(long now) {
        player.update(now);

        if (player.isMalediction()){
            playerUseBomb(now);
        }
        
        //Update de tous les monstres
        List<Monster> monsters = game.getWorld().getMonsters();
        Iterator<Monster> iteratorMonster = monsters.iterator();
        while (iteratorMonster.hasNext()) {
            Monster monster = iteratorMonster.next();
            monster.update(now);

            if (!monster.isAlive()) {
                //On supprime tout et remmet tous les monstres moins le mort.
                spritesMonster.forEach(Sprite::remove);
                spritesMonster.clear();
                iteratorMonster.remove();
                for (Monster m : monsters){
                    spritesMonster.add(SpriteFactory.createMonster(layer, m));
                }
            }
        }

        //Update de toutes les bombes
        List<Bomb> bombs = game.getWorld().getBombs();
        Iterator<Bomb> iterator = bombs.iterator();
        while (iterator.hasNext()) {
            Bomb bomb = iterator.next();
            bomb.update(now);
            //gestion non GUI
            if (bomb.isExploded()){
                //si la bombe se trouve dans un niveau autre que celui affiché
                //on applique l'explosion dans ce monde.
                if (game.getLevel() != bomb.getLevel()) {
                    World explosionWorld = memoryWorld.get(bomb.getLevel() - 1); 
                    bomb.doExplosion(explosionWorld);
                    iterator.remove();
                }else{    
                    bomb.doExplosion();
                }
                game.getPlayer().setnbAvailable(game.getPlayer().getnbAvailable() + 1);
            }
            //Gestion gui
            if (bomb.getPhase() == 1 && game.getLevel() == bomb.getLevel()){
                bomb.setPhase(0);
                List<Position> zone = bomb.getZone();
                for (Position position : zone) {
                    game.getWorld().set(position, new Explosion());
                }
            }
            if (bomb.getPhase() == -1 && game.getLevel() == bomb.getLevel()){
                List<Position> zone = bomb.getZone();
                for (Position position : zone) { 
                    game.getWorld().clear(position);
                }
                iterator.remove();
            }
            
        }

        //Update les décors (s'il y a eu un changement)
        if(game.getWorld().hasChanged()){
            sprites.forEach(Sprite::remove); 
            sprites.clear();
            game.getWorld().setChanged(false);
            game.getWorld().forEach( (pos,d) -> sprites.add(SpriteFactory.createDecor(layer, pos, d)));
        }

        //Change de monde (s'il y a un changement)
        if (game.isChanged()) {
            //met le niveau actuel dans la liste de mémoire (à l'indice level - 1)
            if (memoryWorld.size() <= game.getLevel() && !memoryWorld.contains(game.getWorld())){
                memoryWorld.add(game.getWorld());
            }
            
            //actualise le numero de level (le niveau où on va)
            if (game.isBacking()) game.setLevel(game.getLevel() - 1);
            else game.setLevel(game.getLevel() + 1);

            //retrait monstres
            //monsters.clear();
            //spritesMonster.forEach(Sprite::remove); 
            spritesExplosion.forEach(Sprite::remove);

            //retrait fenêtre courante
            stage.close();

            //chargement du nouveau monde 
            //vérifie s'il est en mémoire & le charge 
            if (memoryWorld.size() >= game.getLevel()){
                game.setWorld(memoryWorld.get(game.getLevel() - 1));

                //met à jour la position du joueur
                game.changeLevel(false);
            }

            //sinon on va chercher le nouveau
            else {
                game.loadWorldFromFile();
                //met à jour les monstres & positions du joueur
                game.changeLevel(true);
            }

            initialize(stage, game);
            game.setChanged(false);
        }

        if (player.isAlive() == false) {
            gameLoop.stop();
            showMessage("Perdu!", Color.RED);
        }
        if (player.isWinner()) {
            gameLoop.stop();
            showMessage("Gagné", Color.BLUE);
        }        
    }

    /**
     * Applique les fonctions de rendu GUI des élements du jeu.
     */
    private void render() {
        spritesBomb.forEach(Sprite::render);
        sprites.forEach(Sprite::render);
        spritesMonster.forEach(Sprite::render); 
        spritesExplosion.forEach(Sprite::render);
        // last rendering to have player in the foreground
        spritePlayer.render();
    }

    public void start() {
        gameLoop.start();
    }

    /**
     * Tout le processus de création de la bombe.
     */
    private void playerUseBomb(long now){
        //Pas de bombe déjà sur la case
        List<Bomb> bombs = game.getWorld().getBombs();
        for (Bomb bomb : bombs) {
            if (game.getPlayer().getPosition() == bomb.getPosition()) return;
        }
        Decor decor = game.getWorld().get(game.getPlayer().getPosition());
        if (decor != null){
            //pas de bombe sur les portes
            if (decor instanceof DoorNextClosed) return;
            if (decor instanceof DoorPrevOpened) return;
        }

        //Création de la bombe (GUI et non GUI)
        if (player.getnbAvailable() > 0){
            player.setnbAvailable(player.getnbAvailable() - 1);
            Bomb bomb = new Bomb(game, player.getPosition(), now, game.getLevel());
            game.getWorld().addBombs(bomb);
            spritesBomb.add(SpriteFactory.createBomb(layer, bomb)); 
        }
    }

}
