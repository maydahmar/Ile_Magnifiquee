import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * On commence par poser des types énumérés représentant les éléments de base du jeu, avec leurs méthodes, pour les manipuler plus simplement
 */
enum Etat {
    SEC,
    INONDE,
    SUBMERGE
}


enum Type {
    HELIPORT(0),
    ORDINAIRE(1),
    TERRE(2),
    FEU(3),
    AIR(4),
    EAU(5);

    private int value;

    private Type(int value){
        this.value = value;
    }
    public static Type fromInt(int value) {
        switch(value) {
            case 0: return Type.HELIPORT;
            case 1: return Type.ORDINAIRE;
            case 2: return Type.TERRE;
            case 3: return Type.FEU;
            case 4: return Type.AIR;
            case 5: return Type.EAU;
            default: return Type.ORDINAIRE;
            
        }
    }

    public int getValue() {return this.value;}
}

enum Dir {
    HAUT,
    BAS,
    GAUCHE,
    DROITE
}


enum Artefact {
    TERRE(2),
    FEU(3),
    AIR(4),
    EAU(5);
    
    private int value;
    
    private Artefact(int value){
        this.value = value;
    }

    public static Artefact fromInt(int value) {
        switch(value) {
            case 2: return Artefact.TERRE;
            case 3: return Artefact.FEU;
            case 4: return Artefact.AIR;
            case 5: return Artefact.EAU;
            default : return Artefact.EAU;
        }
    }

    public int getValue() {return this.value;}
}

enum Cle {
    TERRE(2),
    FEU(3),
    AIR(4),
    EAU(5),
    SACSABLE(0),
    HELICOPTERE(1);

    private int value;
    
    private Cle(int value){
        this.value = value;
    }

    public static Cle fromInt(int value) {
        switch(value) {
            case 2: return Cle.TERRE;
            case 3: return Cle.FEU;
            case 4: return Cle.AIR;
            case 5: return Cle.EAU;
            case 0: return Cle.SACSABLE;
            case 1: return Cle.HELICOPTERE;
            default: return Cle.TERRE;  
        }
    }

    public int getValue() {return this.value;}
}


/**
 * Nous allons commencer à construire notre application, en voici la classe
 * principale.
 */
public class IleInterdite {
    /**
     * L'amorçage est fait en créant le modèle et la vue, par un simple appel
     * à chaque constructeur.
     * Ici, le modèle est créé indépendamment (il s'agit d'une partie autonome
     * de l'application), et la vue prend le modèle comme paramètre (son
     * objectif est de faire le lien entre modèle et utilisateur).
     */
    public static void main(String[] args) {
	/**
	 * Pour les besoins du jour on considère la ligne EvenQueue... comme une
	 * incantation qu'on pourra expliquer plus tard.
	 */
	EventQueue.invokeLater(() -> {
		/** Voici le contenu qui nous intéresse. */
                IModele modele = new IModele();
                IVue vue = new IVue(modele);
	    });
    }

}
/** Fin de la classe principale. */

/**
 * Interface des objets observateurs.
 */ 
interface Observer {
    /**
     * Un observateur doit posséder une méthode [update] déclenchant la mise à
     * jour.
     */
    public void update();
    /**
     * La version officielle de Java possède des paramètres précisant le
     * changement qui a eu lieu.
     */
}

/**
 * Classe des objets pouvant être observés.
 */
abstract class Observable {
    /**
     * On a une liste [observers] d'observateurs, initialement vide, à laquelle
     * viennent s'inscrire les observateurs via la méthode [addObserver].
     */
    private ArrayList<Observer> observers;
    public Observable() {
	this.observers = new ArrayList<Observer>();
    }
    public void addObserver(Observer o) {
	observers.add(o);
    }

    /**
     * Lorsque l'état de l'objet observé change, il est convenu d'appeler la
     * méthode [notifyObservers] pour prévenir l'ensemble des observateurs
     * enregistrés.
     * On le fait ici concrètement en appelant la méthode [update] de chaque
     * observateur.
     */
    public void notifyObservers() {
	for(Observer o : observers) {
	    o.update();
	}
    }
}
/** Fin du schéma observateur/observé. */



/**
 * Le modèle : le coeur de l'application.
 *
 * Le modèle étend la classe [Observable] : il va posséder un certain nombre
 * d'observateurs (ici, un : la partie de la vue responsable de l'affichage)
 * et devra les prévenir avec [notifyObservers] lors des modifications.
 * Voir la méthode [avance()] pour cela.
 */
class IModele extends Observable {
    /** On fixe la taille de la grille. */
    public static final int HAUTEUR=10, LARGEUR=10;
    /** On stocke un tableau de cases, et jun tableau de joueurs pour les faire communiquer enseble par la suite. */
    private Case[][] cases;
    private Joueur[] joueurs;

    /** On a quelques compteurs, qui seront modifiés au fil de l'exécution */
    int compteurActions;
    int currentJoueur;
    int nbjoueurs;

   /** Ces booléens définirons la fin de la partie, ainsi que les actions spéciales */
    boolean fin;
    boolean perdu;

    boolean sacSable;
    boolean helico;

    /** Construction : on initialise un tableau de cases. */
    public IModele() {
    /**
     * Pour éviter les problèmes aux bords, on ajoute une ligne et une
     * colonne de chaque côté, dont les cellules n'évolueront pas.
     */ 
    cases = new Case[LARGEUR+2][HAUTEUR+2];
    for(int i=0; i<LARGEUR+2; i++) {
        for(int j=0; j<HAUTEUR+2; j++) {
        cases[i][j] = new Case(this,i, j);
        }
    }

    /** Les variables de base sont initialisés à false, rien ne s'est encore passé */
    fin = false;
    perdu = false;

    sacSable = false;
    helico = false;
    nbjoueurs = 4;

    /** On définit notre tableau de joueurs, ici avec 4 joueurs */
    joueurs = new Joueur[4];
    for (int i=0; i<nbjoueurs; i++) {
        joueurs[i] = new Joueur(this, i+1, i+1, i);
    }
    init();

    /** On a le compteur d'actions, qui s'incrémente à chaque fin de tour */
    this.compteurActions = 0;
    }

    /** Initialisation des cases, avec positionement aléatoires des cases d'artefacts */
    public void init() {
    for(int i=1; i<=LARGEUR; i++) {
        for(int j=1; j<=HAUTEUR; j++) {
            cases[i][j].type = Type.ORDINAIRE;
            cases[i][j].etat = Etat.SEC;
        }
    }
    int i=0;
    while(i<=5) {
        int x = (int)(Math.random() * LARGEUR);
        int y = (int)(Math.random() * HAUTEUR);
        if (cases[x][y].type == Type.ORDINAIRE) {
            cases[x][y].type = Type.fromInt(i);
            cases[x][y].setArtefactStatus(true);
            i++;
        } 
    }
    

    }

    /**
     * Calcul du tour suivant.
     */
    public void findetour() {
    /**
     * On procède en plusieurs étapes.
     *  - On inonde trois cases au hasard, en faisant s'echapper si possible ceux qui y seraient encore
     *  - Ensuite, on tire une probabilité, qui va définir si le joueur courant récupère une clé, ou bien si une montée des eaux survient.
     *  - On incrémente le compteur d'actions
     *  - Enfin, on vérifie si la partie est gagnante ou non
     */ 
        int i = 0;
        while (i<3){
            int x = (int)(Math.random() * LARGEUR);
            int y = (int)(Math.random() * HAUTEUR);
            if(cases[x][y].etat != Etat.SUBMERGE){
                cases[x][y].setInondation();
                echappeSubmergee(cases[x][y]);
                //if (cases[x][y].type != Type.ORDINAIRE && cases[x][y].etat == Etat.SUBMERGE) perdu = true;
                i +=1;
            }
        }
        double proba = 0.2;
        if(Math.random() < proba){
            joueurs[currentJoueur].addCle();
        }else if(Math.random()<proba){
            this.monteeDesEaux(this.getCase(joueurs[currentJoueur].getX(), joueurs[currentJoueur].getY()), currentJoueur);
        }
        this.compteurActions = 0;
        currentJoueur = (currentJoueur+1) % 4 ;


        if (partieGagnante()) fin = true;
    /**
     * Pour finir, le modèle ayant changé, on signale aux observateurs
     * qu'ils doivent se mettre à jour.
     */
        notifyObservers();
    }
    /**
     * Quand une case est submergée et qu'un joueur s'y trouve, il essaiera de rejoindre une case adjacente exploitable.
     * Deux cas de figures :
     * - Une case voisine est exploitable, auquel cas il ira dessus
     * - Aucune case n'est disponible, le joueur se noie et c'est la fin de la partie
     */
    public void echappeSubmergee(Case c){
        if(c.isSubmerge()){
            for(int i =0 ; i< getNbJoueurs(); i++){
                if(getCase(joueurs[i].getX(),joueurs[i].getY()) == c){
                    ArrayList<Case> voisines = this.caseVoisines(this.getCase(joueurs[i].getX(), joueurs[i].getY()));
                    boolean effectue = false;
                    for(int j= 0; j<voisines.size(); j++) {
                        if(voisines.get(j).getEtat() != Etat.SUBMERGE){
                            joueurs[i].setX(voisines.get(j).getX());
                            joueurs[i].setY(voisines.get(j).getY());
                            effectue = true;
                            break;
                        }
                    }
                    if (!effectue) {
                        perdu = true;
                    }

                }
            }
        }
    }

    
    /**
     * Fontion principale qui permettra de déplacer un joueur, si la case qu'il veux atteindre n'est pas submergée
     * On vérifie si on peut aller sur la case, et si oui on y va et on incrémente le compteur d'actions
     */
    public void deplace(int id_joueur, Dir direction){
        if(this.compteurActions < 3){
            switch(direction) {
                case HAUT :
                if (joueurs[id_joueur].getY() - 1 >= 1 && !(cases[joueurs[id_joueur].getX()] [joueurs[id_joueur].getY()-1].isSubmerge())){
                    joueurs[id_joueur].addY(-1);
                    this.compteurActions++;
                    
                } break;
                case BAS :
                if (joueurs[id_joueur].getY() + 1 < HAUTEUR+1  && cases[joueurs[id_joueur].getX()][joueurs[id_joueur].getY()+1].etat != Etat.SUBMERGE){
                    joueurs[id_joueur].addY(1);
                    this.compteurActions++;
                } break;
                case DROITE :
                if (joueurs[id_joueur].getX() + 1 < LARGEUR+1 && cases[joueurs[id_joueur].getX()+1][joueurs[id_joueur].getY()].etat != Etat.SUBMERGE){ 
                    joueurs[id_joueur].addX(1);
                    
                    this.compteurActions++;
                } break;
                case GAUCHE :
                if (joueurs[id_joueur].getX() - 1 >= 1 && cases[joueurs[id_joueur].getX()-1][joueurs[id_joueur].getY()].etat != Etat.SUBMERGE){
                    joueurs[id_joueur].addX(-1);
                    
                    this.compteurActions++;
                } break;

            }
        }
        notifyObservers();
    }

     /**
     * En cas de montée des eaux, si jamais une case spéciale est submergée, c'est perdu
     */

    public void monteeDesEaux(Case c, int joueur){
        c.setInondation();
        if(c.isSubmerge()){
            if (c.getType() != Type.ORDINAIRE) perdu = true;
            this.deplaceSubmerge(joueur);
        }
    }

    /**
     * Si c'est submergé, cette fonction essayera de déplacer les joueurs présente dessus
     */
    public void deplaceSubmerge(int id_joueur){
        ArrayList<Case> voisines = this.caseVoisines(this.getCase(joueurs[id_joueur].getX(), joueurs[id_joueur].getY()));
        boolean effectue = false;
        int i = 0;
        while(i < voisines.size() || !effectue){
            if(voisines.get(i).getEtat() != Etat.SUBMERGE){
                joueurs[id_joueur].setX(voisines.get(i).getX());
                joueurs[id_joueur].setY(voisines.get(i).getY());
                effectue = true;
            }
            i++;
        }
        if (!effectue) {
            perdu = true;
        }
    }

    
    /**
     * Assèche la zone que l'on vise 
     */
    public void assecher(int id_joueur, Dir direction){
        if(this.getCompteurActions()<3){
            switch(direction){
                case HAUT : 
                if(joueurs[id_joueur].getY() - 1 >= 1 && this.getCase(joueurs[id_joueur].getX(), joueurs[id_joueur].getY()-1).getEtat() == Etat.INONDE){
                    this.setEtatsec2(joueurs[id_joueur].getX(), joueurs[id_joueur].getY()-1);
                    this.compteurActions++;
                } break;
                case BAS :
                if (joueurs[id_joueur].getY() + 1 <= HAUTEUR  && this.getCase(joueurs[id_joueur].getX(), joueurs[id_joueur].getY()+1).getEtat() == Etat.INONDE){
                    this.setEtatsec2(joueurs[id_joueur].getX(), joueurs[id_joueur].getY()+1);
                    this.compteurActions++;
                } break;
                case DROITE :
                if (joueurs[id_joueur].getX() + 1 <= LARGEUR && this.getCase(joueurs[id_joueur].getX()+1, joueurs[id_joueur].getY()).getEtat() == Etat.INONDE){ 
                    this.setEtatsec2(joueurs[id_joueur].getX()+1, joueurs[id_joueur].getY());
                    this.compteurActions++;
                } break;
                case GAUCHE :
                if (joueurs[id_joueur].getX() - 1 >= 1 && this.getCase(joueurs[id_joueur].getX()-1, joueurs[id_joueur].getY()).getEtat() == Etat.INONDE){
                    this.setEtatsec2(joueurs[id_joueur].getX()-1, joueurs[id_joueur].getY());
                    this.compteurActions++;
                } break;
            }
        }
        notifyObservers();
    }
    /**
     * Si on asseche la zone sur laquelle on est, la fonction appelée est différente 
     */
    public void assecherCentre(int id_joueur){
        if(this.getCompteurActions()<3){
            if( this.getCase(joueurs[id_joueur].getX(), joueurs[id_joueur].getY()).getEtat() == Etat.INONDE){
                this.setEtatsec2(joueurs[id_joueur].getX(), joueurs[id_joueur].getY());
                this.compteurActions++;
            }
        }
        notifyObservers();
    }

    /**
     * Une série de getters pour permettre aux classes externes de récuperer les attributs du modèle
     */
    public Case getCase(int x, int y) {
	return cases[x][y];
    }

    public Case getCaseFromJoueur(Joueur j) {
        return cases[j.getX()][j.getY()];
    }

    public int getCompteurActions() {return this.compteurActions;}
    public int getNbJoueurs() {return this.nbjoueurs;}

    public Joueur getJoueur(int i) { return joueurs[i];}

    public int getCurrentJoueur(){ return currentJoueur;}

    /**
     * Permets d'utiliser un sac de sable pour assecher une case de la map
     */

    public void setEtatsec2(int x, int y){
        if (cases[x][y].getEtat() ==  Etat.INONDE){
            cases[x][y].setAssechement();
            
        }
    }
    public void setEtatsec(int x, int y){
        if (cases[x+1][y+1].getEtat() ==  Etat.INONDE){
            cases[x+1][y+1].setAssechement();
            joueurs[currentJoueur].removeCle(Cle.SACSABLE);
        }
    }

    /**
     * Produit une liste de toutes les cases voisines existantes pour la case donnée en paramètre
     */
    public ArrayList<Case> caseVoisines(Case c) {
        ArrayList<Case> res = new ArrayList<Case>();
        if (c.getX()-1 > 0) res.add(this.getCase(c.getX()-1, c.getY()));// GAUCHE
        if (c.getX()+1 < LARGEUR) res.add(this.getCase(c.getX()+1, c.getY()));// DROITE
        if (c.getY()-1 > 0) res.add(this.getCase(c.getX(), c.getY()-1));// HAUT
        if (c.getY()+1 < HAUTEUR) res.add(this.getCase(c.getX(), c.getY()+1));// BAS
        return res;
    }

    /**
     * Permets a un joueur de récuperer un artefact, si il est sur la bonne case et si il a la clé correspondante
     */
    public void addArtefact(int id_joueur){
        int type = this.cases[this.getJoueur(id_joueur).getX()][this.getJoueur(id_joueur).getY()].getType().getValue();
        boolean trouve = false;
        int i = 0;
        while(i < this.getJoueur(id_joueur).getCle().size() && !trouve){
            if(type == this.getJoueur(id_joueur).getCle().get(i).getValue() && this.compteurActions < 3 &&
                cases[this.getJoueur(id_joueur).getX()][this.getJoueur(id_joueur).getY()].getArtefactStatus()){
                joueurs[id_joueur].addArtefactBis(Artefact.fromInt(type));
                cases[this.getJoueur(id_joueur).getX()][this.getJoueur(id_joueur).getY()].setArtefactStatus(false);
                trouve = true;
                this.compteurActions++;
            }
            i++;
        }
        System.out.println("artefacts"+joueurs[id_joueur].getArtefact());
        notifyObservers();
    }
    /**
     * Notez qu'à l'intérieur de la classe [CModele], la classe interne est
     * connue sous le nom abrégé [Cellule].
     * Son nom complet est [CModele.Cellule], et cette version complète est
     * la seule à pouvoir être utilisée depuis l'extérieur de [CModele].
     * Dans [CModele], les deux fonctionnent.
     */

     /**
     * Méthode qui est vérifiée a chaque fin de tour, pour savoir si la partie est gagnante 
     */
    public boolean partieGagnante() {
        int artefacts = 0;
        boolean reunis = true;
        for (int i=0; i < getNbJoueurs(); i++) {
            artefacts+= joueurs[i].getArtefact().size();
            if (getCaseFromJoueur(joueurs[i]).getType() != Type.HELIPORT) reunis = false;
        }
        return artefacts == 4 && reunis;
     }

     /**
     * Méthode qui est vérifiée a chaque fin de tour, pour savoir si la partie est perdue 
     */
     public boolean partiePerdue() { return this.perdu; }


     /**
     * Evenements déclenchés en cas de clic sur les boutons d'action spéciales
     */
     public void setSacSable(boolean b) { this.sacSable = b;}

     public boolean sacSableEvent() {
         return joueurs[currentJoueur].getCle().contains(Cle.SACSABLE) && sacSable;
     }

    public boolean helicoEvent(){
        return joueurs[currentJoueur].getCle().contains(Cle.HELICOPTERE) && helico;
    }

    public void setJoueur(int x, int y) {
        if (cases[x+1][y+1].getEtat() != Etat.SUBMERGE) {
            joueurs[currentJoueur].setX(x+1);
            joueurs[currentJoueur].setY(y+1);
            joueurs[currentJoueur].removeCle(Cle.HELICOPTERE);
        }
        
        
    }

    public void setHelico(boolean b) { this.helico = b;}

}

/** Fin de la classe IModele. */

/**
 * Définition d'une classe pour les cases.
 */
class Case {
    /** On conserve un pointeur vers la classe principale du modèle. */
    private IModele modele;

    /** Nos cases auront toutes un état, un type de case, et une propriété d'artefacts */
    protected Etat etat;

    protected Type type;

    boolean contientArtefact;

    private final int x, y;


    /** Constructeur de Case */
    
    public Case(IModele modele, int x, int y) {
        this.modele = modele;
        this.etat = Etat.SEC;
        this.x = x; this.y = y;
        contientArtefact = false;
    }
    
    /** Serie de getters pour les attributs de Case */
    public Type getType() {
        return this.type;
    }

    public Etat getEtat() {
        return this.etat;
    }

    public int getX() { return x;}
    public int getY() { return y;}


    public boolean getArtefactStatus() { return this.contientArtefact; }

    
    /** Serie de setters pour les attributs de Case */


    public void setArtefactStatus(boolean b) { this.contientArtefact = b; }

    public void setInondation(){
        switch(this.getEtat()){
            case SEC : this.etat = Etat.INONDE;  break;
            case INONDE : this.etat = Etat.SUBMERGE; break;
            case SUBMERGE : break;
        }
    }

    public void setAssechement() {
        this.etat = Etat.SEC;
    }

    

    
    /** Vérifie si la case est submergée*/

    public boolean isSubmerge(){
        return this.etat == Etat.SUBMERGE;
    }

    
}    
/** Fin de la classe Case. */

/**
 * Définition d'une classe pour les cases.
 */
class Joueur {
    /** Attributs de Joueur : 
     * - Le modèle, pour en communiquer avec le reste du programme
     * - Des coordonnées et un identifiant
     * - Une couleur associée
     * - Un Array d'Artefacts en sa possession
     * - Un array de Clés en sa possession
    */
    private IModele modele;

    private int x, y;
    private final int id;

    private Color couleur;
    private ArrayList<Artefact> artefacts;
    private ArrayList<Cle> cles;

    /** Constructeur de Joueur */
    public Joueur(IModele modele, int x, int y, int id) {
        this.modele = modele;
        this.x = x; this.y = y;
        this.id = id;
        this.artefacts = new ArrayList<Artefact>();
        this.cles = new ArrayList<Cle>();

        switch (this.id) {
            case 0:
                this.couleur = new Color(218, 165, 32);
                break;
            case 1:
                this.couleur = new Color(125, 0, 0);
                break;
            case 2:
                this.couleur = new Color(255, 0, 0);
                break;
            case 3:
                this.couleur = new Color(0, 125, 0);
                break;
            default:
                this.couleur = new Color(0, 0, 0);
                break;
        }
    }

    /** Série de getters */
    public int getX() {return this.x;}
    public int getY() {return this.y;}
    public Color getColor() {return this.couleur;}
    public int getId(){ return this.id;}

    public ArrayList<Cle> getCle(){ 
        return this.cles;
    }
    public ArrayList<Artefact> getArtefact(){ return this.artefacts;}

    /** Fonctions qui serviront a la VueJoueur, pour renvoyer les clés et les artefacts de chaque joueur dans le bon format */
    public String printCle(){
        StringBuilder sb = new StringBuilder();
        sb.append("Cles : ");
        for (Cle s : this.cles){
            sb.append(s.toString());
            sb.append(" ");
        }
        return sb.toString();
    }

    public String printArtefact(){
        StringBuilder sb = new StringBuilder();
        sb.append("Artefacts : ");
        for (Artefact s : this.artefacts){
            sb.append(s.toString());
            sb.append(" ");
        }
        return sb.toString();
    }

    /** Setters pour déplacer le joueur. */
    public void addX(int x) {this.x = this.x + x;}
    public void addY(int y) {this.y = this.y + y;}

    public void setX(int x) {this.x =  x;}
    public void setY(int y) {this.y =  y;}

    /** Ajoute une clé a l'array de clés du Joueur. */

    public void addCle(){
        switch((int) (Math.random()*6)){
            case 0 : this.cles.add(Cle.TERRE); break;
            case 1 : this.cles.add(Cle.FEU); break;
            case 2 : this.cles.add(Cle.AIR); break;
            case 3 : this.cles.add(Cle.EAU); break;
            case 4 : this.cles.add(Cle.SACSABLE); break;
            case 5 : this.cles.add(Cle.HELICOPTERE); break;
            default: this.cles.add(Cle.SACSABLE); break;
        }     
    System.out.println("les cles du joueurs"+this.id + " "+this.cles);
    }

    public void addArtefactBis(Artefact a){
        artefacts.add(a);
    }

        /** Quand un joueur réalise une action spéciale, on lui retire la clé correspondante */
    public void removeCle(Cle c) {
        cles.remove(c);
    }

}

/** Fin de la classe Joueur. */


class IVue {
    /**
     * JFrame est une classe fournie pas Swing. Elle représente la fenêtre
     * de l'application graphique.
     */
    private JFrame frame;
    /**
     * VuePlateau, VueCommandes et VueJoueurs sont deux classes définies plus loin, pour
     * nos deux parties de l'interface graphique.
     */

    private VuePlateau plateau;
    private VueCommandes commandes;
    private VueJoueurs joueurs;

    /** Importation du modèle, pour interagir avec les clics de souris plus tard */

    private IModele model;

    /** Construction d'une vue attachée à un modèle. */
    public IVue(IModele modele) {
	/** Définition de la fenêtre principale. */
    this.model = modele;
	frame = new JFrame();
	frame.setTitle("Jeu de l'Ile Interdite");
	/**
	 * On précise un mode pour disposer les différents éléments à
	 * l'intérieur de la fenêtre. Quelques possibilités sont :
	 *  - BorderLayout (défaut pour la classe JFrame) : chaque élément est
	 *    disposé au centre ou le long d'un bord.
	 *  - FlowLayout (défaut pour un JPanel) : les éléments sont disposés
	 *    l'un à la suite de l'autre, dans l'ordre de leur ajout, les lignes
	 *    se formant de gauche à droite et de haut en bas. Un élément peut
	 *    passer à la ligne lorsque l'on redimensionne la fenêtre.
	 *  - GridLayout : les éléments sont disposés l'un à la suite de
	 *    l'autre sur une grille avec un nombre de lignes et un nombre de
	 *    colonnes définis par le programmeur, dont toutes les cases ont la
	 *    même dimension. Cette dimension est calculée en fonction du
	 *    nombre de cases à placer et de la dimension du contenant.
	 */
	frame.setLayout(new FlowLayout());

	/** Définition des trois vues et ajout à la fenêtre. */
	

    plateau = new VuePlateau(modele);
    frame.add(plateau);

    commandes = new VueCommandes(modele);
	frame.add(commandes);

    joueurs = new VueJoueurs(modele);
    frame.add(joueurs);


	/**
	 * Remarque : on peut passer à la méthode [add] des paramètres
	 * supplémentaires indiquant où placer l'élément. Par exemple, si on
	 * avait conservé la disposition par défaut [BorderLayout], on aurait
	 * pu écrire le code suivant pour placer la grille à gauche et les
	 * commandes à droite.
	 *     frame.add(grille, BorderLayout.WEST);
	 *     frame.add(commandes, BorderLayout.EAST);
	 */

	/**
	 * Fin de la plomberie :
	 *  - Ajustement de la taille de la fenêtre en fonction du contenu.
	 *  - Indiquer qu'on quitte l'application si la fenêtre est fermée.
	 *  - Préciser que la fenêtre doit bien apparaître à l'écran.
	 */
	frame.pack();
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setVisible(true);
    }


    /** Fonction qui va freeze le jeu en cas de fin de partie, si il y a une victoire ou une defaite */
    public void paintComponent(Graphics g) throws InterruptedException {
        if (model.partieGagnante()) {
            this.wait();
        }
        if (model.partiePerdue()) {
            this.wait();
        }
    }

}



/**
 * Une classe pour représenter la zone d'affichage des cases.
 *
 * JPanel est une classe d'éléments graphiques, pouvant comme JFrame contenir
 * d'autres éléments graphiques.
 *
 * Cette vue va être un observateur du modèle et sera mise à jour à chaque
 * nouvelle génération des cellules.
 */
class VuePlateau extends JPanel implements Observer, MouseListener {
    /** On maintient une référence vers le modèle. */
    private IModele modele;
    /** Définition d'une taille (en pixels) pour l'affichage des cases. */
    private final static int TAILLE = 50;

    /** Constructeur. */
    public VuePlateau(IModele modele) {
	this.modele = modele;
	/** On enregistre la vue [this] en tant qu'observateur de [modele]. */
	modele.addObserver(this);
    this.addMouseListener(this);
	/**
	 * Définition et application d'une taille fixe pour cette zone de
	 * l'interface, calculée en fonction du nombre de cellules et de la
	 * taille d'affichage.
	 */
	Dimension dim = new Dimension(TAILLE*IModele.LARGEUR,
				      TAILLE*IModele.HAUTEUR);
	this.setPreferredSize(dim);
    }

    /**
     * L'interface [Observer] demande de fournir une méthode [update], qui
     * sera appelée lorsque la vue sera notifiée d'un changement dans le
     * modèle. Ici on se content de réafficher toute la grille avec la méthode
     * prédéfinie [repaint].
     */
    public void update() { 
        repaint();
    }

    /**
     * Les éléments graphiques comme [JPanel] possèdent une méthode
     * [paintComponent] qui définit l'action à accomplir pour afficher cet
     * élément. On la redéfinit ici pour lui confier l'affichage des cellules.
     *
     * La classe [Graphics] regroupe les éléments de style sur le dessin,
     * comme la couleur actuelle.
     */
    public void paintComponent(Graphics g) {
	super.repaint();
	/** Pour chaque cellule... */
	for(int i=1; i<=IModele.LARGEUR; i++) {
	    for(int j=1; j<=IModele.HAUTEUR; j++) {
		/**
		 * ... Appeler une fonction d'affichage auxiliaire.
		 * On lui fournit les informations de dessin [g] et les
		 * coordonnées du coin en haut à gauche.
		 */
		paint(g, modele.getCase(i, j), (i-1)*TAILLE, (j-1)*TAILLE);
	    }
	}

    /** On suit le meme principe pour afficher les joueurs, les uns après les autres */
    for (int i=0; i<modele.getNbJoueurs(); i++) {
        paintJoueur(g, modele.getJoueur(i), (modele.getJoueur(i).getX()+1) * TAILLE, (modele.getJoueur(i).getY()+1)*TAILLE);

    }
    

    }
    /**
     * Fonction auxiliaire de dessin d'une cellule.
     * Ici, la classe [Case] ne peut être désignée que par l'intermédiaire
     * de la classe [IModele] à laquelle elle est interne, d'où le type
     * [CModele.Cellule].
     * Ceci serait impossible si [Case] était déclarée privée dans [IModele].
     */
    private void paint(Graphics g, Case c, int x, int y) {
        /** Sélection d'une icone a appliquer, en fonction du type de la case. */

        ImageIcon icon = new ImageIcon();
        switch(c.getType()) {
            case HELIPORT: icon = new ImageIcon("resources/helico.png"); break;
            case ORDINAIRE: 
            switch(c.getEtat()){
                case INONDE : icon = new ImageIcon("resources/inonde.jpg"); break;
                case SEC : icon = new ImageIcon("resources/ordinaire.png"); break;
                case SUBMERGE : icon = new ImageIcon("resources/submerge.jpg"); break;
            }
            ; break;
            case FEU: icon = new ImageIcon("resources/fire.png"); break;
            case TERRE : icon = new ImageIcon("resources/terre.png"); break;
            case EAU : icon = new ImageIcon("resources/water.png"); break;
            case AIR : icon = new ImageIcon("resources/air.png"); break;
            default : icon = new ImageIcon();
        }

        icon.paintIcon(this, g, (c.getX()-1)*TAILLE, (c.getY()-1)*TAILLE);
    }


    private void paintJoueur(Graphics g, Joueur j, int x, int y) {

         /** Sélection d'une icone a appliquer, en fonction de l'Id du Joueur. */
        
        ImageIcon icon = new ImageIcon();

        switch (j.getId()) {
            case 0: icon = new ImageIcon("resources/j1.png"); break;
            case 1: icon = new ImageIcon("resources/j2.png"); break;
            case 2: icon = new ImageIcon("resources/j3.png"); break;
            case 3: icon = new ImageIcon("resources/j4.png"); break;
        
            default: icon = new ImageIcon();
        }



        icon.paintIcon(this, g, (j.getX()-1)*TAILLE, (j.getY()-1)*TAILLE);

    }
    /** Fonction qui permettra de trouver la ou un clique sur la grille avec la souris  */
    @Override
    public void mouseClicked(MouseEvent e) {
         /** On normalise pour récuperer le numéro de la case depuis les coordonées de la souris */
        final int a = (int)e.getX()/TAILLE;
        final int b = (int)e.getY()/TAILLE;

    /** Si les conditions des actions spéciales sont réunies, on les executen avec leurs fonctions associées */
        if (modele.sacSableEvent()) {
            modele.setEtatsec(a, b);
            modele.setSacSable(false);
        }

        if (modele.helicoEvent()) {
            modele.setJoueur(a, b);
            modele.setHelico(false);
        }
        
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}



/**
 * Une classe pour représenter la zone contenant les boutons.
 * Comme la zone précédente, celle-ci est un panneau [JPanel].
 */
class VueCommandes extends JPanel {
    /**
     * Pour que les boutons puisse transmettre ses ordres, on garde une
     * référence au modèle, et on liste les attributs que l'on va afficher dans cette classe
     */
    private IModele modele;
    JLabel nBTourRestant;
    JLabel currentJ ;
    JLabel Gagne;


    
    /** Constructeur. */
    public VueCommandes(IModele modele) {
	this.modele = modele;

    nBTourRestant = new JLabel("Nombre de tours restants : " + (3 - modele.getCompteurActions()), JLabel.CENTER);

    currentJ = new JLabel("Joueur actuel : " + (1+modele.getCurrentJoueur()), JLabel.CENTER);

    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	/**
	 * On crée un nouveau bouton, de classe [JButton], en précisant le
	 * texte qui doit l'étiqueter.
	 * Puis on ajoute ce bouton au panneau [this].
     * On procède de la meme facon pour les JLabel.
	 */
	JButton finDeTour = new JButton("fdt");
	this.add(finDeTour);
    JButton Haut = new JButton("Haut");
	this.add(Haut);
    JButton Bas = new JButton("Bas");
	this.add(Bas);
    JButton Gauche = new JButton("Gauche");
	this.add(Gauche);
    JButton Droite = new JButton("Droite");
	this.add(Droite);

    JButton AssecHaut = new JButton("Assecher en haut");
	this.add(AssecHaut);
    JButton AssecBas = new JButton("Assecher en bas");
	this.add(AssecBas);
    JButton AssecGauche = new JButton("Assecher a gauche");
	this.add(AssecGauche);
    JButton AssecDroite = new JButton("Assecher a droite");
	this.add(AssecDroite);
    JButton AssecMilieu = new JButton("Assecher au centre");
	this.add(AssecMilieu);
    

    this.add(nBTourRestant);

    this.add(currentJ);

    Gagne = new JLabel(" ");

    this.add(Gagne);

    JButton Recup = new JButton("Recupérer artefact");
    this.add(Recup);
    JButton SacDeSable = new JButton("Sac de Sable");
	this.add(SacDeSable);

    JButton Helicoptere = new JButton("Hélicoptère");
	this.add(Helicoptere);


	/**
	 * Le bouton, lorsqu'il est cliqué par l'utilisateur, produit un
	 * événement, de classe [ActionEvent].
	 *
	 * On a ici une variante du schéma observateur/observé : un objet
	 * implémentant une interface [ActionListener] va s'inscrire pour
	 * "écouter" les événements produits par le bouton, et recevoir
	 * automatiquements des notifications.
	 * D'autres variantes d'auditeurs pour des événements particuliers :
	 * [MouseListener], [KeyboardListener], [WindowListener].
	 *
	 * Cet observateur va enrichir notre schéma Modèle-Vue d'une couche
	 * intermédiaire Contrôleur, dont l'objectif est de récupérer les
	 * événements produits par la vue et de les traduire en instructions
	 * pour le modèle.
	 * Cette strate intermédiaire est potentiellement riche, et peut
	 * notamment traduire les mêmes événements de différentes façons en
	 * fonction d'un état de l'application.
	 * Ici nous avons un seul bouton réalisant une seule action, notre
	 * contrôleur sera donc particulièrement simple. Cela nécessite
	 * néanmoins la création d'une classe dédiée.
	 */	
	


	/** Enregistrement des fonctions associées comme auditeurs de nos boutons, via des lambda fonctions. */
	
    
    finDeTour.addActionListener(e -> modele.findetour());

    Haut.addActionListener(e -> {modele.deplace(modele.currentJoueur, Dir.HAUT);} );
    Bas.addActionListener(e -> {modele.deplace(modele.currentJoueur, Dir.BAS);} );
    Gauche.addActionListener(e -> {modele.deplace(modele.currentJoueur, Dir.GAUCHE);} );
    Droite.addActionListener(e -> {modele.deplace(modele.currentJoueur, Dir.DROITE);} );

    AssecHaut.addActionListener(e -> {modele.assecher(modele.currentJoueur, Dir.HAUT);} );
    AssecBas.addActionListener(e -> {modele.assecher(modele.currentJoueur, Dir.BAS);} );
    AssecGauche.addActionListener(e -> {modele.assecher(modele.currentJoueur, Dir.GAUCHE);} );
    AssecDroite.addActionListener(e -> {modele.assecher(modele.currentJoueur, Dir.DROITE);} );

    AssecMilieu.addActionListener(e -> {modele.assecherCentre(modele.currentJoueur);} );

    Recup.addActionListener(e -> {modele.addArtefact(modele.currentJoueur);} );
    
    SacDeSable.addActionListener(e -> {modele.setSacSable(true);} );
    Helicoptere.addActionListener(e -> {modele.setHelico(true);} );

	

    }

     /** Fonction qui va rafraichir l'affichage de notre vue, pour mettre a jour les compteurs du joueur courant, le nombre d'actions restantes ainsi que si on a gagné ou perdu. */

    public void paintComponent(Graphics g) {
        super.repaint();

        nBTourRestant.setText("Nombre de tours restants : " + (3 - modele.getCompteurActions()));

        currentJ.setText("Joueur actuel : " + (1+modele.getCurrentJoueur()));

        if (modele.partiePerdue()) {
            this.Gagne = new JLabel("PERDU SALE NUL BOUUUHH", JLabel.CENTER);
            this.add(Gagne);
        }

        if (modele.partieGagnante()) {
            this.Gagne = new JLabel("GAGNEEEEE", JLabel.CENTER);
            this.add(Gagne);
        }


    
    }
}

/** Fin de la vue. */
class VueJoueurs extends JPanel {

    /**
     * Pour que l'affichage puis récuperer des informations du modèle, on garde une
     * référence au modèle.
     */

    private IModele modele;

    /** On liste la structure de notre panneau, qui se compose de :
     * Plusieurs JPanel, imbriqués dans this, le JPanel global
     * Les clés qui seront contenues dans ces tableaux
     * Les attrivuts qui seront contenus dans ces tableaux
     */
    JPanel pan1;
    JPanel pan2;
    JPanel pan3;
    JPanel pan4;


    JLabel cles1;
    JLabel cles2;
    JLabel cles3;
    JLabel cles4;

    JLabel artefacts1;
    JLabel artefacts2;
    JLabel artefacts3;
    JLabel artefacts4;

     /** Constructeur de VueJoueurs */
    public VueJoueurs(IModele modele) {

        

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));


        cles1 = new JLabel();
        cles2 = new JLabel();
        cles3 = new JLabel();
        cles4 = new JLabel();

        artefacts1 = new JLabel();
        artefacts2 = new JLabel();
        artefacts3 = new JLabel();
        artefacts4 = new JLabel();

        this.modele = modele;


         /** Chaque joueur a son propre JPanel, avec ses clés et ses artefacts a l'intérieur. */
        pan1 = new JPanel();
        pan1.setLayout(new BoxLayout(pan1, BoxLayout.Y_AXIS ));
        pan1.setBorder(BorderFactory.createTitledBorder("J1")); 
        pan1.add(cles1);
        pan1.add(artefacts1);


        pan2 = new JPanel();
        pan2.setLayout(new BoxLayout(pan2, BoxLayout.Y_AXIS ));
        pan2.setBorder(BorderFactory.createTitledBorder("J2")); 
        pan2.add(cles2);
        pan2.add(artefacts2);

        pan3 = new JPanel();
        pan3.setLayout(new BoxLayout(pan3, BoxLayout.Y_AXIS ));
        pan3.setBorder(BorderFactory.createTitledBorder("J3")); 
        pan3.add(cles3);
        pan3.add(artefacts3);

        pan4 = new JPanel();
        pan4.setLayout(new BoxLayout(pan4, BoxLayout.Y_AXIS ));
        pan4.setBorder(BorderFactory.createTitledBorder("J4")); 
        pan4.add(cles4);
        pan4.add(artefacts4);

        
        this.add(pan1); 
        this.add(pan2);
        this.add(pan3);
        this.add(pan4);
        
    }

     /** Rafraichit l'affichage de notre vue, avec les clés et les artefacts réecrits dynamiquement sur la vue */
    public void paintComponent(Graphics g) {
        super.repaint();
        super.revalidate();

        
        cles1.setText(modele.getJoueur(0).printCle());
        cles2.setText(modele.getJoueur(1).printCle());
        cles3.setText(modele.getJoueur(2).printCle());
        cles4.setText(modele.getJoueur(3).printCle());


        artefacts1.setText(modele.getJoueur(0).printArtefact());
        artefacts2.setText(modele.getJoueur(1).printArtefact());
        artefacts3.setText(modele.getJoueur(2).printArtefact());
        artefacts4.setText(modele.getJoueur(3).printArtefact());


    }
}

