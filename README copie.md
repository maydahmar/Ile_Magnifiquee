# Ile Magnifique

## Parties traitées :

### Déroulement du jeu :

- L'Ile interdite se constitue d'un plateau de cases (hauteur et largeur modulables) et de 4 joueurs qui collaborent afin de récupérer 4 artefacts aléatoirement distribués sur l'ile (représentés par des icones).

- Un joueur peut récupérer un artefact, s'il y en a un sur la case sur laquelle il se trouve, et qu'il possède la clé corresondante.

- A chaque tour, le joueur possède trois actions afin de :
1- Se déplacer sur une case adjacente en cliquant sur le bouton correspondant.
2- Assécher une zone adjacente ou centrale à l'aide de boutons également.
3- Récupérer un artefact.

- L'enchaînement des tours se fait en cliquant sur le bouton "fdt" déclenchant à chaque fin de tour : 
1- 3 cases sont inondées au hasard.
2- Récupérer une clé, Inonder la case sur laquelle il se trouve ou alors rien ne se passe (avec une probabilité qu'on a fixé à 0.2 à chaque fois).

- Un joueur peut récupérer deux clés spéciales : 
1- Sac de sable : Lui permet d'assécher une case (même lointaine) en cliquant sur le bouton "Sac de sable" puis sur la zone qu'il veut assécher.
2- Hélicoptère : Lui permet de se déplacer sur une case (même lointaine) en cliquand dessus aussi.

- La partie est finie quand :
1- Gagnante : Les joueurs se trouvent tous sur l'héliport et possèdent les quatre artefacts.
2- Perdante : L'un des joueurs meurt ou l'héliport ou l'un des artefacts est submergé.


### Affichage : 
- Nous avons trois fenêtres (la dernière est à dérouler) avec :
1- Vue plateau : Avec les icones de joueurs qui se déplacent. (Big up Power Rangers). Ainsi que la représentation des zones inondées, sumergées et les zones spéciales.
2- Vue commandes : Avec les différents boutons cités ci-dessus suivant l'action qu'ils déclenchent et l'affichage du joueur auquel c'est le tour et le nombre de tours qui lui restent.
3- Vue Joueurs : Quatre blocs avec comme titre le joueur correspondant et à l'intérieur, les clés et les artefacts qu'il possède.

## Architecture du projet : 

- Une classe principale "Ile" constituée d'un modéle et d'une vue.

1- Le modèle : 
- Un tableau 2D de cases.
- Un arrayList de joueurs.
- Construction : On initialise le type à ordinaire ainsi que les cases spéciales (aléatoirement). L'état des cases à sec. Et les quatres joueurs sont disposés en escalier en haut à gauche de la grille.

2- Les cases : 
- Coordonnées x et y.
- Un attribut Type de type énuméré pour désigner s'il s'agit d'un type ordinaire, artefact(terre, feu, air, eau) ou héliport.
- Un attribut Etat de type énuméré pour désigner si la case est sèche, inondée ou submergée.
- Un attribut contientArtefact, initialisé à vrai puis remis à faux si l'un des joueurs a récupéré l'artefact en question.

3- Les joueurs : 
- Coordonnées x et y et l'identifiant.
- Deux arrayList pour les cles et les artefacts.

4- Cles : Type énuméré {TERRE, FEU, AIR, EAU, SACDESABLE, HELICOPTERE}

5- Artefacts : Type énuméré {TERRE, FEU, AIR, EAU}

### La vue:

IVue : Il s'agit d'un JFrame composé de 3 vues :

1- Vue Plateau : Il s'agit d'un JPanel avec :
- Un mouseListener à la construction ainsi qu'une fonction mouseclicked qui nous permet de récupérer les coordonnées de la case cliquée et choisie par le joueur pour l'option Sac de Sable et Hélicoptère.
- Une fonction paintComponent ou on parcourt les cases et on appelle la fonction paint, qui attribut l'icone correspondante à chaque case suivant son état et son type. Et dans cette même fonction, on parcourt les joueurs, et auxquels on attribut aussi des icones.

2- Vue Commandes : Il s'agit d'un JPanel aussi avec:
- Des JButton associés à chaque déplacement/ assèchement de case, findetour, recupérer artefact, sac de sable et hélicoptère.
- Des JLabel afin d'afficher le joueur actuel et le nombre d'actions restantes.

3- Vue Joueurs : Il s'agit d'un JPanel avec :
- 4 JPanel pour chaque joueurs et à l'intérieurs, deux JLabel pour les clés et les artefacts.



## Provenance du code et répartition du travail: 
Nous avons travaillé sur toutes les parties du code ensemble, en vocal sur discord. Il nous est arrivé de nous diviser les taches pour les plus faciles d'entre elles.
Nous nous sommes inspirés de bout de codes d'internet pour l'affichage principalement.