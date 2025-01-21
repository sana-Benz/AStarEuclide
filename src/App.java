// Par Sylvain Lobry, pour le cours "IF05X040 Algorithmique avanc�e"
// de l'Universit� de Paris, 11/2020
/**
 * Application permettant d'explorer des algorithmes de recherche de chemin
 * tels que Dijkstra et A*, avec une interface visuelle intuitive.
 *
 * Fonctionnalités :
 * - Lecture et analyse de fichiers de cartes pour construire des graphes.
 * - Affichage graphique des noeuds et des chemins trouvés.
 * - Système interactif pour tester différents algorithmes de recherche de chemin.
 */
package MainApp;

import MainApp.WeightedGraph.Edge;
import MainApp.WeightedGraph.Graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * Classe dédiée à l'affichage graphique des données du graphe et des chemins calculés.
 */
class Board extends JComponent
{
	private static final long serialVersionUID = 1L;
	Graph graph;
	int pixelSize;
	int ncols;
	int nlines;
	HashMap<Integer, String> colors;
	int start;
	int end;
	double max_distance;
	int current;
	LinkedList<Integer> path;


	/**
	 * Constructeur de la classe Board.
	 * @param graph Le graphe à afficher.
	 * @param pixelSize Taille des cases en pixels.
	 * @param ncols Nombre de colonnes de la grille.
	 * @param nlines Nombre de lignes de la grille.
	 * @param colors Couleurs associées aux types de terrains.
	 * @param start Index du noeud de départ.
	 * @param end Index du noeud de fin.
	 */
	public Board(Graph graph, int pixelSize, int ncols, int nlines, HashMap<Integer, String> colors, int start, int end)
	{
		super();
		this.graph = graph;
		this.pixelSize = pixelSize;
		this.ncols = ncols;
		this.nlines = nlines;
		this.colors = colors;
		this.start = start;
		this.end = end;
		this.max_distance = ncols * nlines;
		this.current = -1;
		this.path = null;
	}

	/**
	 * Dessine le plateau et les chemins sur la base des données du graphe.
	 * @param g Objet Graphics utilisé pour le rendu visuel.
	 */
	@Override
	public void paint(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		// Nettoyage de l'arrière-plan
		g2.setColor(Color.cyan);
		g2.fill(new Rectangle2D.Double(0,0,this.ncols*this.pixelSize, this.nlines*this.pixelSize));

		int num_case = 0;
		for (WeightedGraph.Vertex v : this.graph.vertexlist)
		{
			double type = v.indivTime;
			int i = num_case / this.ncols;
			int j = num_case % this.ncols;

			if (colors.get((int)type).equals("green"))
				g2.setPaint(Color.green);
			if (colors.get((int)type).equals("gray"))
				g2.setPaint(Color.gray);
			if (colors.get((int)type).equals("blue"))
				g2.setPaint(Color.blue);
			if (colors.get((int)type).equals("yellow"))
				g2.setPaint(Color.yellow);
			g2.fill(new Rectangle2D.Double(j*this.pixelSize, i*this.pixelSize, this.pixelSize, this.pixelSize));

			if (num_case == this.current)
			{
				g2.setPaint(Color.red);
				g2.draw(new Ellipse2D.Double(j*this.pixelSize+this.pixelSize/2, i*this.pixelSize+this.pixelSize/2, 6, 6));
			}
			if (num_case == this.start)
			{
				g2.setPaint(Color.white);
				g2.fill(new Ellipse2D.Double(j*this.pixelSize+this.pixelSize/2, i*this.pixelSize+this.pixelSize/2, 4, 4));

			}
			if (num_case == this.end)
			{
				g2.setPaint(Color.black);
				g2.fill(new Ellipse2D.Double(j*this.pixelSize+this.pixelSize/2, i*this.pixelSize+this.pixelSize/2, 4, 4));
			}

			num_case += 1;
		}

		num_case = 0;
		for (WeightedGraph.Vertex v : this.graph.vertexlist)
		{
			int i = num_case / this.ncols;
			int j = num_case % this.ncols;
			if (v.timeFromSource < Double.POSITIVE_INFINITY)
			{
				float g_value = (float) (1 - v.timeFromSource / this.max_distance);
				if (g_value < 0)
					g_value = 0;
				g2.setPaint(new Color(g_value, g_value, g_value));
				g2.fill(new Ellipse2D.Double(j*this.pixelSize+this.pixelSize/2, i*this.pixelSize+this.pixelSize/2, 4, 4));
				WeightedGraph.Vertex previous = v.prev;
				if (previous != null)
				{
					int i2 = previous.num / this.ncols;
					int j2 = previous.num % this.ncols;
					g2.setPaint(Color.black);
					g2.draw(new Line2D.Double(j * this.pixelSize + this.pixelSize/2, i * this.pixelSize + this.pixelSize/2, j2 * this.pixelSize + this.pixelSize/2, i2 * this.pixelSize + this.pixelSize/2));
				}
			}

			num_case += 1;
		}

		int prev = -1;
		if (this.path != null)
		{
			g2.setStroke(new BasicStroke(3.0f));
			for (int cur : this.path)
			{
				if (prev != -1)
				{
					g2.setPaint(Color.red);
					int i = prev / this.ncols;
					int j = prev % this.ncols;
					int i2 = cur / this.ncols;
					int j2 = cur % this.ncols;
					g2.draw(new Line2D.Double(j * this.pixelSize + this.pixelSize/2, i * this.pixelSize + this.pixelSize/2, j2 * this.pixelSize + this.pixelSize/2, i2 * this.pixelSize + this.pixelSize/2));
				}
				prev = cur;
			}
		}
	}

	/**
	 * Met à jour les données du graphe et redessine le plateau.
	 * @param graph Le graphe mis à jour.
	 * @param current Le noeud actuellement exploré.
	 */
	public void update(Graph graph, int current)
	{
		this.graph = graph;
		this.current = current;
		repaint();
	}

	/**
	 * Ajoute un chemin pour mise en évidence sur le plateau.
	 * @param graph Graphe mis à jour.
	 * @param path Chemin à afficher.
	 */
	public void addPath(Graph graph, LinkedList<Integer> path)
	{
		this.graph = graph;
		this.path = path;
		this.current = -1;
		repaint();
	}
}

/**
 * Classe principale gérant les interactions avec l'utilisateur et le lancement des algorithmes.
 */
public class App {

	/**
	 * Affiche la visualisation du plateau.
	 * @param board Composant graphique représentant le plateau.
	 * @param nlines Nombre de lignes.
	 * @param ncols Nombre de colonnes.
	 * @param pixelSize Taille des cellules.
	 */
	private static void drawBoard(Board board, int nlines, int ncols, int pixelSize)
	{
		JFrame window = new JFrame("Plus court chemin");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setBounds(0, 0, ncols*pixelSize+20, nlines*pixelSize+40);
		window.getContentPane().add(board);
		window.setVisible(true);
	}

	/**
	 * Implémente l'algorithme de recherche A* pour trouver le chemin optimal.
	 * @param graph Le graphe à explorer.
	 * @param start L'indice du noeud de départ.
	 * @param end L'indice du noeud de destination.
	 * @param board La vue graphique pour les mises à jour en temps réel.
	 * @return Une liste chaînée contenant le chemin trouvé.
	 */
	private static LinkedList<Integer> AStar(Graph graph, int start, int end, int ncols, int numberV, Board board) {
		resetGraph(graph);
		graph.vertexlist.get(start).timeFromSource = 0;
		graph.vertexlist.get(start).heuristic = calculateHeuristic(start, end, ncols);
		graph.vertexlist.get(start).f = graph.vertexlist.get(start).heuristic;

		Comparator<WeightedGraph.Vertex> comparator = Comparator.comparingDouble(v -> v.f);
		PriorityQueue<WeightedGraph.Vertex> to_visit = new PriorityQueue<>(comparator);
		to_visit.add(graph.vertexlist.get(start));

		boolean[] visited = new boolean[numberV];
		int number_tries = 0;

		while (!to_visit.isEmpty()) {
			WeightedGraph.Vertex currentVertex = to_visit.poll();
			int current = currentVertex.num;

			if (visited[current]) {
				continue;
			}

			visited[current] = true;

			if (current == end) {
				break;
			}

			number_tries++;

			for (Edge edge : currentVertex.adjacencylist) {
				int neighbor = edge.destination;
				double edgeWeight = edge.weight;
				double newDistance = currentVertex.timeFromSource + edgeWeight;

				if (newDistance < graph.vertexlist.get(neighbor).timeFromSource) {
					WeightedGraph.Vertex neighborVertex = graph.vertexlist.get(neighbor);
					neighborVertex.timeFromSource = newDistance;
					neighborVertex.f = newDistance + calculateHeuristic(neighbor, end, ncols);
					neighborVertex.prev = currentVertex;
					to_visit.add(neighborVertex);

					try {
						board.update(graph, neighbor);
						Thread.sleep(10);
					} catch (InterruptedException e) {
						System.out.println("Thread interrompu.");
					}
				}
			}
		}

		System.out.println("Done! Using A* (Euclide):");
		System.out.println("    Number of nodes explored: " + number_tries);
		System.out.println("    Total time of the path: " + graph.vertexlist.get(end).timeFromSource);

		LinkedList<Integer> path = new LinkedList<>();
		WeightedGraph.Vertex currentVertex = graph.vertexlist.get(end);

		while (currentVertex != null && currentVertex.num != start) {
			path.addFirst(currentVertex.num);
			currentVertex = currentVertex.prev;
		}

		if (currentVertex != null) {
			path.addFirst(currentVertex.num);
		}

		board.addPath(graph, path);
		return path;
	}


	// Méthode pour calculer l'heuristique (distance euclidienne)
	private static double calculateHeuristic(int current, int end, int ncols) {
		int x1 = current / ncols;
		int y1 = current % ncols;
		int x2 = end / ncols;
		int y2 = end % ncols;
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

	// Méthode pour reconstruire le chemin depuis le sommet d'arrivée
	private static LinkedList<Integer> constructPath(Graph graph, int end) {
		LinkedList<Integer> path = new LinkedList<>();
		WeightedGraph.Vertex current = graph.vertexlist.get(end);

		while (current != null) {
			path.addFirst(current.num);
			current = current.prev;
		}

		return path;
	}

	/**
	 * Implémente l'algorithme de Dijkstra pour calculer le plus court chemin.
	 * @param graph Le graphe à explorer.
	 * @param start L'indice du noeud de départ.
	 * @param end L'indice du noeud de destination.
	 * @param board La vue graphique pour les mises à jour en temps réel.
	 * @return Une liste chaînée contenant le chemin trouvé.
	 */
	private static LinkedList<Integer> Dijkstra(Graph graph, int start, int end, int numberV, Board board) {
		// Réinitialiser le graphe
		resetGraph(graph);
		graph.vertexlist.get(start).timeFromSource = 0; // La distance de la source est 0

		// Définir un Comparator pour la PriorityQueue basé sur timeFromSource
		Comparator<WeightedGraph.Vertex> comparator = Comparator.comparingDouble(v -> v.timeFromSource);
		PriorityQueue<WeightedGraph.Vertex> to_visit = new PriorityQueue<>(comparator);
		to_visit.add(graph.vertexlist.get(start));

		// Tableau pour suivre les n?uds déjà visités
		boolean[] visited = new boolean[numberV];

		int number_tries = 0;

		while (!to_visit.isEmpty()) {
			// Extraire le n?ud avec la distance minimale
			WeightedGraph.Vertex currentVertex = to_visit.poll();
			int current = currentVertex.num;

			// Si déjà visité, ignorer
			if (visited[current]) {
				continue;
			}

			// Marquer comme visité
			visited[current] = true;

			// Si nous avons atteint la destination, nous pouvons arrêter
			if (current == end) {
				break;
			}

			number_tries++;

			// Explorer les voisins du noud courant
			for (Edge edge : currentVertex.adjacencylist) {
				int neighbor = edge.destination;
				double edgeWeight = edge.weight;
				double newDistance = currentVertex.timeFromSource + edgeWeight;

				// Si un chemin plus court vers neighbor est trouvé
				if (newDistance < graph.vertexlist.get(neighbor).timeFromSource) {
					WeightedGraph.Vertex neighborVertex = graph.vertexlist.get(neighbor);
					neighborVertex.timeFromSource = newDistance;
					neighborVertex.prev = graph.vertexlist.get(current); // Mettre à jour le prédécesseur

					// Ajouter le voisin à la PriorityQueue
					to_visit.add(neighborVertex);

					// Mettre à jour l'affichage
					try {
						board.update(graph, neighbor);
						Thread.sleep(10);
					} catch (InterruptedException e) {
						System.out.println("Thread interrompu.");
					}
				}
			}
		}

		System.out.println("Done! Using Dijkstra:");
		System.out.println("    Number of nodes explored: " + number_tries);
		System.out.println("    Total time of the path: " + graph.vertexlist.get(end).timeFromSource);

		// Construire le chemin le plus court
		LinkedList<Integer> path = new LinkedList<>();
		WeightedGraph.Vertex currentVertex = graph.vertexlist.get(end);

		while (currentVertex != null && currentVertex.num != start) {
			path.addFirst(currentVertex.num);
			currentVertex = currentVertex.prev;
		}

		if (currentVertex != null) {
			path.addFirst(currentVertex.num);
		}

		// Ajouter le chemin au graphique pour l'affichage
		board.addPath(graph, path);
		return path;
	}

	// Méthode pour réinitialiser les propriétés des sommets
	private static void resetGraph(Graph graph) {
		for (WeightedGraph.Vertex v : graph.vertexlist) {
			v.timeFromSource = Double.POSITIVE_INFINITY;
			v.heuristic = 0;
			v.f = Double.POSITIVE_INFINITY;
			v.prev = null;
		}
	}

	/**
	 * Point d'entrée principal de l'application.
	 * @param args Arguments en ligne de commande.
	 */
	public static void main(String[] args) {
		// Lecture de la carte et création du graphe
		System.out.println("Bienvenue dans l'application de calcul de chemins !");
		System.out.println("Initialisation et chargement des données...");
		try {
			// Obtenir le fichier qui décrit la carte
			File myObj = new File("graph.txt");
			Scanner myReader = new Scanner(myObj);
			String data = "";
			// On ignore les trois premières lignes
			for (int i = 0; i < 3; i++) {
				if (myReader.hasNextLine()) {
					data = myReader.nextLine();
				} else {
					System.out.println("Le fichier de la carte est incomplet.");
					myReader.close();
					return;
				}
			}

			// Lecture du nombre de lignes
			int nlines = Integer.parseInt(data.split("=")[1].trim());
			// Lecture du nombre de colonnes
			if (myReader.hasNextLine()) {
				data = myReader.nextLine();
			} else {
				System.out.println("Le fichier de la carte est incomplet.");
				myReader.close();
				return;
			}
			int ncols = Integer.parseInt(data.split("=")[1].trim());

			// Initialisation du graphe
			Graph graph = new Graph();

			HashMap<String, Integer> groundTypes = new HashMap<String, Integer>();
			HashMap<Integer, String> groundColor = new HashMap<Integer, String>();
			if (myReader.hasNextLine()) {
				data = myReader.nextLine(); // Supposons que c'est "==Types=="
			} else {
				System.out.println("Le fichier de la carte est incomplet.");
				myReader.close();
				return;
			}

			if (myReader.hasNextLine()) {
				data = myReader.nextLine();
			} else {
				System.out.println("Le fichier de la carte est incomplet.");
				myReader.close();
				return;
			}

			// Lire les différents types de cases
			while (!data.equals("==Graph==")) {
				String[] parts = data.split("=");
				if (parts.length < 2) {
					System.out.println("Format de type de sol incorrect: " + data);
					myReader.close();
					return;
				}
				String name = parts[0].trim();
				int time = Integer.parseInt(parts[1].trim());
				if (myReader.hasNextLine()) {
					String color = myReader.nextLine().trim();
					groundTypes.put(name, time);
					groundColor.put(time, color);
				} else {
					System.out.println("Le fichier de la carte est incomplet.");
					myReader.close();
					return;
				}
				if (myReader.hasNextLine()) {
					data = myReader.nextLine();
				} else {
					System.out.println("Le fichier de la carte est incomplet.");
					myReader.close();
					return;
				}
			}

			// On ajoute les sommets dans le graphe (avec le bon type)
			for (int line = 0; line < nlines; line++) {
				if (myReader.hasNextLine()) {
					data = myReader.nextLine();
				} else {
					System.out.println("Le fichier de la carte est incomplet.");
					myReader.close();
					return;
				}
				for (int col = 0; col < ncols; col++) {
					if (col >= data.length()) {
						System.out.println("Ligne " + line + " trop courte.");
						myReader.close();
						return;
					}
					char groundChar = data.charAt(col);
					String groundName = String.valueOf(groundChar);
					if (!groundTypes.containsKey(groundName)) {
						System.out.println("Type de sol inconnu: " + groundName);
						myReader.close();
						return;
					}
					graph.addVertex(groundTypes.get(groundName));
				}
			}

			// On ajoute les arêtes au graphe
			for (int line = 0; line < nlines; line++) {
				for (int col = 0; col < ncols; col++) {
					int source = line * ncols + col;
					double weight = graph.vertexlist.get(source).indivTime; // Poids de la case actuelle

					// Connexions avec les voisins (8 directions)
					for (int dLine = -1; dLine <= 1; dLine++) {
						for (int dCol = -1; dCol <= 1; dCol++) {
							if (dLine == 0 && dCol == 0)
								continue; // Ignorer la case elle-même
							int newLine = line + dLine;
							int newCol = col + dCol;

							// Vérifier si le voisin est dans les limites
							if (newLine >= 0 && newLine < nlines && newCol >= 0 && newCol < ncols) {
								int dest = newLine * ncols + newCol;
								double neighborWeight = graph.vertexlist.get(dest).indivTime;

								// Calculer le poids de l'arête
								if (dLine != 0 && dCol != 0) { // Diagonale
									weight = (graph.vertexlist.get(source).indivTime + neighborWeight) / 2 * Math.sqrt(2); // Poids diagonal
								} else {
									weight = graph.vertexlist.get(source).indivTime + neighborWeight; // Poids horizontal/vertical
								}
								graph.addEgde(source, dest, weight);
							}
						}
					}
				}
			}

			// On obtient les noeuds de départ et d'arrivée
			if (myReader.hasNextLine()) {
				data = myReader.nextLine(); // Supposons que c'est "==StartEnd=="
			} else {
				System.out.println("Le fichier de la carte est incomplet.");
				myReader.close();
				return;
			}

			if (myReader.hasNextLine()) {
				data = myReader.nextLine(); // Start
				String[] startParts = data.split("=")[1].trim().split(",");
				if (startParts.length < 2) {
					System.out.println("Format de départ incorrect: " + data);
					myReader.close();
					return;
				}
				int startRow = Integer.parseInt(startParts[0].trim());
				int startCol = Integer.parseInt(startParts[1].trim());
				int startV = startRow * ncols + startCol;

				if (myReader.hasNextLine()) {
					data = myReader.nextLine(); // End
					String[] endParts = data.split("=")[1].trim().split(",");
					if (endParts.length < 2) {
						System.out.println("Format d'arrivée incorrect: " + data);
						myReader.close();
						return;
					}
					int endRow = Integer.parseInt(endParts[0].trim());
					int endCol = Integer.parseInt(endParts[1].trim());
					int endV = endRow * ncols + endCol;

					myReader.close();

					// Définir la taille des pixels pour l'affichage
					int pixelSize = 10; // Ajustez selon vos préférences
					Board board = new Board(graph, pixelSize, ncols, nlines, groundColor, startV, endV);
					drawBoard(board, nlines, ncols, pixelSize);
					board.repaint();

					// Petite pause pour s'assurer que l'affichage est prêt
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						System.out.println("Interruption du thread.");
					}

					// Demander à l'utilisateur de choisir l'algorithme
					Scanner input = new Scanner(System.in);
					String algorithm = "";
					while (true) {
						System.out.println("Pour trouver le plus court chemin : Veuillez choisir l'algorithme à utiliser :");
						System.out.println("1. Algorithme de Dijkstra");
						System.out.println("2. Algorithme A*");
						System.out.print("Entrez le numéro de l'algorithme (1 ou 2) : ");
						String choice = input.nextLine().trim();
						if (choice.equals("1")) {
							algorithm = "Dijkstra";
							break;
						} else if (choice.equals("2")) {
							algorithm = "AStar";
							break;
						} else {
							System.out.println("Choix invalide. Veuillez entrer 1 ou 2.");
						}
					}
					input.close();

					// On appelle l'algorithme choisi
					LinkedList<Integer> path;
					if (algorithm.equals("Dijkstra")) {
						path = Dijkstra(graph, startV, endV, nlines * ncols, board);
					} else {
						path = AStar(graph, startV, endV, ncols, nlines * ncols, board);
					}

					// Écriture du chemin dans un fichier de sortie
					try {
						File file = new File("out.txt");
						if (!file.exists()) {
							file.createNewFile();
						}
						FileWriter fw = new FileWriter(file.getAbsoluteFile());
						BufferedWriter bw = new BufferedWriter(fw);

						for (int i : path) {
							int row = i / ncols;
							int col = i % ncols;
							bw.write(row + "," + col);
							bw.newLine();
						}
						bw.close();
						System.out.println("Le chemin a bien été enregistré dans le fichier out.txt");
						return;
					} catch (IOException e) {
						e.printStackTrace();
					}

				} else {
					System.out.println("Le fichier de la carte est incomplet.");
					myReader.close();
					return;
				}
			} else {
				System.out.println("Le fichier de la carte est incomplet.");
				myReader.close();
				return;
			}
		} catch (FileNotFoundException e) {
			System.out.println("Une erreur s'est produite.");
			e.printStackTrace();
		}
	}

}
