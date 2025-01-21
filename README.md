# Algorithmes de Recherche de Chemin : Dijkstra vs A*

## Description
Ce projet consiste à implémenter et analyser deux algorithmes de recherche de chemin :
- **Dijkstra** : Utilisé pour trouver le chemin le plus court dans un graphe pondéré sans heuristique.
- **A\*** : Une optimisation basée sur une heuristique pour guider la recherche.

Le graphe est modélisé avec une connexité étendue (8 voisins), et l'heuristique Euclidienne a été choisie pour prendre en compte les déplacements diagonaux. Il est possible de tester d'autres graphes mais ils doivent respecter le format de graph.txt .

## Fonctionnalités
- **Lecture de la carte** : Chargement d'un graphe défini dans un fichier texte.
- **Calcul de chemins** : Implémentation des algorithmes de Dijkstra et A*.
- **Affichage graphique** : Visualisation du graphe et des chemins trouvés.
- **Comparaison des performances** : Analyse du nombre de nœuds explorés et du temps total.

## Heuristique Utilisée
L'heuristique Euclidienne a été utilisée pour le calcul des coûts dans l'algorithme A*. Elle est adaptée pour les graphes où les déplacements diagonaux sont autorisés.

## Commandes pour exécution
1. Compiler le projet :
   ```bash
   javac -d bin src/*.java```

2. Exécuter :
   ```bash
   java -cp bin MainApp.App```

## Contribution
Squelette de code fournie par le professeur Sylvain Lobry.
