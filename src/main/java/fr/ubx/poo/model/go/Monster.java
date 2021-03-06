package fr.ubx.poo.model.go;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.Movable;
import fr.ubx.poo.model.go.GameObject;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.World;
import fr.ubx.poo.game.WorldEntity;
import fr.ubx.poo.model.decor.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Monster extends GameObject implements Movable {

    /**
     * Booléen si le monstre est vivant.
     */
    private boolean alive = true;

    /**
     * Direction du monstre.
     */
    private Direction direction; 
    
    /**
     * Temps (en ms) du dernier traitement en date.
     */
    private long timeCheck;

    /**
     * Booléen pour savoir si on est en phase d'attante avant le prochain mouvement.
     */
    private boolean moving = false;

    /**
     * Mémoire du dernier chemin trouvé vers le joueur. [Module IA]
     */
    private List<Direction> memoryPath = new ArrayList<>();

    /**
     * Booléen d'activation de l'IA du monstre.
     */
    private final boolean enableIA;

    /**
     * Vitesse du monstre.
     */
    private final double speed;

    /**
     * Constructeur par défaut 
     * vitesse de base
     * IA désactivée
     * 
     * @param game
     * @param position
     */
    public Monster(Game game, Position position) {
        super(game, position);
        this.direction = Direction.S;
        enableIA = false;
        speed = 1;
    }

    /**
     * Constructeur complet 
     * Choix possible de la vitesse (1, 2 ou 3). Sinon par défaut 1.
     * Choix d'activation de l'IA
     * 
     * La vitesse est directement convertie de coefficient qui sera appliqué au temps entre 2 déplacements.
     * vitesse : 
     * 1 -> 1   sec
     * 2 -> 0.8 sec
     * 3 -> 0.6 sec
     * 
     * @param game
     * @param position
     * @param enableIA
     * @param speed
     */
    public Monster(Game game, Position position, boolean enableIA, double speed) {
        super(game, position);
        this.direction = Direction.S;
        this.enableIA = enableIA;
        if (speed == 2) this.speed = 0.8;
        else if (speed == 3) this.speed = 0.6;
        else this.speed = 1;
    }

    
    /** 
     * @return Direction
     */
    public Direction getDirection() {
        return direction;
    }

    
    /** 
     * @return boolean
     */
    public boolean isAlive() {
        return alive;
    }

    public void kill() {
        this.alive = false;
    }
    
    
    /** 
     * Gère l'actualisation du monstre en temps réel.
     * Applique le mouvement.
     * Applique les dommages si le personnage se trouve sur la même cellule que le joueur.
     * 
     * @param Temps donnée par le moteur de jeu.
     */
    public void update(long now) {
        if (!moving){
            this.timeCheck = now;
            moving = true;
        }

        if(now - timeCheck >= speed * 1000000000){
            getMove();
            moving = false;
        }

        if (game.getPlayer().getPosition().equals(getPosition()))
            game.getPlayer().hurtPlayer();

        if (game.getWorld().get(getPosition()) instanceof Explosion) 
            kill();
    }

    /**
     * Trouve et applique le prochain déplacement du Monstre.
     * Le déplacement est soit aléatoire ou se dirige sur le joueur
     */
    public void getMove(){
        Direction nextMove;

        //Si bloqué
        if (!canMove(Direction.N) && !canMove(Direction.S) && !canMove(Direction.W) && !canMove(Direction.E)){
            return;
        }

        if (this.enableIA && (pathfinder() || memoryPath.isEmpty() == false)){
            nextMove = memoryPath.get(0);
            memoryPath.remove(0);
            if (canMove(nextMove)){
                doMove(nextMove);
            }else{
                memoryPath.clear();
            }
        }else{
            nextMove = Direction.random();
            while(!canMove(nextMove)){
                nextMove = Direction.random();
            }
            doMove(nextMove);
        }
    }

    
    /** 
     * Retourne un boolean indiquant si le mouvement d'une direction est applicable ou non.
     * Il prend par défaut la position du monstre.
     * 
     * @param direction Direction du mouvement.
     * @return boolean Vrai si le mouvement est appliquable, faux sinon.
     */
    @Override
    public boolean canMove(Direction direction) {
        return canMove(direction, getPosition());
    }
    
    /** 
     * Retourne un boolean indiquant si le mouvement d'une direction est applicable ou non.
     * 
     * @param direction Direction du mouvement.
     * @param pos Position de départ.
     * @return boolean Vrai si le mouvement est appliquable, faux sinon.
     */
    public boolean canMove(Direction direction, Position pos){
        Position nextPos = direction.nextPosition(pos);
        World world = game.getWorld();
        List<Bomb> bombList = game.getWorld().getBombs();

        for (Bomb bomb : bombList) {
            if (nextPos.equals(bomb.getPosition())) {
                return false;
            }   
        }

        //collision avec les bords
        if (nextPos.inside(world.dimension)){
            Decor decor = world.get(nextPos);
            if (decor == null) return true;
            if (decor instanceof Stone) return false;
            if (decor instanceof Tree) return false;
            if (decor instanceof Box) return false;
            if (decor instanceof Princess) return false;
            if (decor instanceof BombNbDec) return true;
            if (decor instanceof BombNbInc) return true;
            if (decor instanceof BombRangeDec) return true;
            if (decor instanceof BombRangeInc) return true;
            if (decor instanceof DoorNextClosed) return false;
            if (decor instanceof DoorNextOpened) return false;
            if (decor instanceof DoorPrevOpened) return false;
            if (decor instanceof Key) return true;
            if (decor instanceof Heart) return true;
        }
        return false;
    }

    
    /** 
     * Applique le mouvement vers une direction
     * 
     * @param direction Direction du mouvement.
     */
    @Override
    public void doMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        setPosition(nextPos);
        if (direction != this.direction) {
            this.direction = direction;
        }
    }

    /*////////////////////////////////////////////////////////////////////////////////////////////////////////
                                            PARTIE IA
    ////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    /*
    Description de l'IA :

    L’IA sera plus ou moins réaliste de ce que ferait un monstre : 
    tant qu’il a une idée d’où est le personnage, il va chercher à aller voir à cet endroit. 
    Sinon il fera une sorte de “ronde de garde”. 

    L’algorithme utilisé est celui de A* qui permet d’obtenir un chemin cohérent et assez rapide. 
    J’ai repris les codes donnés par cette page. (première partie de l’article + vidéo à la fin)

    Pour commencer, l’IA sera dotée d’une mémoire du dernier chemin valide pour atteindre le joueur.
    Elle effectuera en priorité les mouvements qui sont dans sa mémoire.
    Cette mémoire sera rafraîchie avant chaque déplacement afin d’optimiser le déplacement du monstre.
    Si le chemin entre le Joueur et le Monstre vient à disparaître au moment du nouveau calcul, 
    le monstre utilisera le chemin en mémoire s’il existe, sinon se déplacera aléatoirement. 
    Et enfin, si lors du parcours du chemin de la mémoire il y a un imprévu, 
    (càd un mouvement non possible) pour faciliter le codage, elle va juste abandonner le chemin qu'elle a en mémoire.

    L’IA apparaît à partir du niveau 2 (peut être modifié dans Game.java)


    Description de l'algorithme A* :
    C'est la description donné sur un cours du site suivant. J'ai appliqué ces étapes en plus de la vidéo sur la même page
    présentant l'algorithme de façon théorique.
    https://www.createursdemondes.fr/2015/03/pathfinding-algorithmes-en-a/

    A chaque itération, A* va tenter de prendre le chemin le plus court pour aller d’une position Src à une position Dst.

    Pour déterminer si un nœud est suceptible de faire partie du chemin solution, il faut pouvoir quantifier sa qualité. Le plus souvent, on utilise la distance en “vol d’oiseau” entre la position du noeud et Dst, mais d’autres fonctions peuvent être utilisées.

    On travaille à partir d’une position de départ, d’une arrivée, d’un terrain, et de deux listes de nœuds (ouverte et fermée), vides à l’origine.

    A partir du départ, on regarde quels sont les nœuds voisins (correspondant aux libertés de mouvement — Par exemple, on peut aller à droite, à gauche, sauter ou ramper, reculer ou avancer – 6 possibilités = 6 noeuds). On place le nœud de départ dans la liste fermée.
    2) Pour chaque nœud voisin : 
    Si c’est un obstacle, on passe (on ne traite pas ce nœud, il ne sera ajouté nulle part)
    S’il est déjà dans la liste fermée, on passe (on a déjà étudié ce trajet)
    S’il est déjà dans la liste ouverte, On vérifie sa qualité. Si la qualité actuelle est meilleure, c’est que le chemin est plus court en passant par le trajet actuel, donc on met à jour sa qualité dans la liste ouverte, et on met à jour son lien parent (avec noeud courant qui correspond à un meilleur chemin)
    Sinon, on ajoute ce nœud à la liste ouverte avec comme parent le nœud courant
    On parcours la liste ouverte à la recherche du nœud ayant la meilleure qualité. Si la liste ouverte est vide, il n’y a pas de solution, fin de l’algorithme.
    On prend le meilleur, et on le retire de la liste ouverte pour le mettre dans la liste fermée
    Nœud courant = nœud qu’on vient d’insérer
    Si nœud courant = Dst alors c’est bon, on a trouvé un bon chemin, sinon on réitère en 2.
    La liste ouverte contient toutes les pistes de trajet qui sont encore possibles. La liste fermée contient toutes les solutions étudiées, bonnes et mauvaises.

    Le “bon chemin” trouvé se retrouve en remontant le long des parents de nœuds
    */
    /**
     * Chaque cellules de la grille de jeu sera représenté par des noeuds. Qui sont ceux décrit ci-dessus. 
     */
    private class Node {
        /**
         * Position de la cellule sur la grille de jeu
         */
        public Position position;

        /**
         * Qualité (décrite dans l'algorithme) qui représente le coût de déplacement pour parvenir à ce noeud.
         * Un déplacement vaut 1 de poids. 
         */
        public int quality;

        /**
         * Noeud père, permettra de retracer le chemin solution.
         */
        public Node parent;

        /**
         * Direction par laquelle on est arrivé, permet le retracage plus facile dans cette application de A*.
         * Elle n'est initialement pas utilisée.
         */
        public Direction direction;
        
        /**
         * Constructeur
         * 
         * @param position
         * @param quality
         * @param parent
         * @param direction
         */
        public Node(Position position, int quality, Node parent, Direction direction){
            this.position = position;
            this.quality = quality;
            this.parent = parent;
            this.direction = direction;
        }
    }

    
    /** 
     * Retourne le premier élement de la liste qui contient le même champs pos donné en paramètre.
     *  
     * @param list Liste à inspecter.
     * @param pos Position que doit contenir le Node.
     * @return Node Résultat de la recherche.
     */
    private Node containPos(List<Node> list, Position pos){
        for (Node node : list) {
            if (node.position.equals(pos)) return node;
        }
        return null;
    }

    
    /**
     * Applique l'iteration de l'algorithme de A*.
     * Il vérifie pour un Node (currentNode)
     *  
     * Pour chaque nœud voisin : 
     * Si c’est un obstacle, on passe (on ne traite pas ce nœud, il ne sera ajouté nulle part)
     * S’il est déjà dans la liste fermée, on passe (on a déjà étudié ce trajet)
     * S’il est déjà dans la liste ouverte, On vérifie sa qualité. Si la qualité actuelle est meilleure, c’est que le chemin est plus court en passant par le trajet actuel, donc on met à jour sa qualité dans la liste ouverte, et on met à jour son lien parent (avec noeud courant qui correspond à un meilleur chemin)
     * Sinon, on ajoute ce nœud à la liste ouverte avec comme parent le nœud courant
     * @https://www.createursdemondes.fr/2015/03/pathfinding-algorithmes-en-a/ (Description faite dans l'article)
     * 
     * @param direction Direction du mouvement.
     * @param currentNode Noeud courant à traiter
     * @param startPos Position de départ de l'algorithme
     * @param endPos Position d'arrivée
     * @param openList Liste ouverte
     * @param closeList Liste fermée
     */
    private void iteration(Direction direction, Node currentNode, Position startPos, Position endPos, List<Node> openList, List<Node> closeList){
        if(canMove(direction, currentNode.position)){
            Position newPos = direction.nextPosition(currentNode.position);
            Node newNode = new Node(newPos, currentNode.quality + 1 + Math.abs(endPos.x - currentNode.position.x) + Math.abs(endPos.y - currentNode.position.y), currentNode, direction);                
            //Verification closeList
            Node node = containPos(closeList, newPos);
            //S'il est dans la liste alors on ne le revérifie pas
            if (node != null){
                return;
            }
            //Verification openList
            node = containPos(openList, newPos);
            //S'il est dans la liste alors on vérifie la qualité
            if (node != null && !node.position.equals(startPos)){
                if (newNode.quality < node.quality){
                    openList.remove(node);
                    openList.add(newNode);
                }
            }else{//Sinon on l'ajoute
                openList.add(newNode);
            }
        }
    }

    
    /** 
     * Application de l'algorithme A*.
     * Se charge de trouver un chemin depuis la position actuelle du monstre vers la position actuelle du joueur.
     * Met en mémoire si un tel chemin existe (cf memoryPath)
     * 
     * @return boolean Vrai si un chemin existe entre ce Monstre et le Joueur, faux sinon.
     */
    private boolean pathfinder(){
        Position startPos = getPosition();
        Position endPos = game.getPlayer().getPosition();

        if (startPos.equals(endPos)) return false;

        List<Node> openList = new ArrayList<>();
        List<Node> closeList = new ArrayList<>();

        Node currentNode = new Node(startPos, 0, null, null);

        while (!currentNode.position.equals(endPos)){
            iteration(Direction.S, currentNode, startPos, endPos, openList, closeList);
            iteration(Direction.W, currentNode, startPos, endPos, openList, closeList);
            iteration(Direction.E, currentNode, startPos, endPos, openList, closeList);
            iteration(Direction.N, currentNode, startPos, endPos, openList, closeList);

            if (openList.isEmpty()) return false; //Pas de solutions

            //On prend la Nodeule avec la plus petite qualité
            Node min = openList.get(0);
            for (Node node : openList) {
                if (node.quality < min.quality) min = node;
            }
            openList.remove(min);
            closeList.add(min);

            currentNode = min;
        }

        //currentNode devrait être la case d'arrivée à ce moment
        List<Direction> path = new ArrayList<>();
        while(currentNode != null){
            path.add(currentNode.direction);
            currentNode = currentNode.parent;
            
        }
        Collections.reverse(path);
        //suppression du null à cause de l'origine
        path.remove(0);        
        
        this.memoryPath = path; 
        //retrace le chemin et le met en mémoire
        return true;

    }
}

