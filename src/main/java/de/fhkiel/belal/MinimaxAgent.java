package de.fhkiel.belal;

import de.fhkiel.ki.cathedral.ai.Agent;
import de.fhkiel.ki.cathedral.game.*;

import java.util.*;

/**
 * Implementierung eines Minimax-Agenten für das Spiel Cathedral.
 */
public class MinimaxAgent implements Agent {

    /**
     * Berechnet den besten Zug für den aktuellen Spielzustand.
     *
     * @param game        Das Spielobjekt, das den aktuellen Zustand des Spiels repräsentiert.
     * @param timeForTurn Die verbleibende Zeit für den aktuellen Zug.
     * @param timeBonus   Ein Bonus für die verbleibende Zeit.
     * @return Die berechnete Placement-Instanz für den besten Zug.
     */
    @Override
    public Optional<Placement> calculateTurn(Game game, int timeForTurn, int timeBonus) {
        List<Placement> placements = generatePossiblePlacements(game, game.getCurrentPlayer());
        System.out.println("Generated Possible Placements Count: " + placements.size());  // Hinzugefügt

        Placement bestMove = null;
        int tenSeconds = 10000;
        if (game.lastTurn().getTurnNumber() == 0) {
            bestMove = new Placement(4, 4, Direction._90, Building.Blue_Cathedral);
        } else {
            long startTime = System.currentTimeMillis();
            long deadline = startTime + tenSeconds;
            // Iterative Deepening Depth-First Search (IDDFS)
       //
            //   while (System.currentTimeMillis() < deadline) {

                int depth = 1;


                int bestScore = Integer.MIN_VALUE;


                for (Placement placement : placements) {
                    System.out.println("[calculateTurn] Testing placement: " + placement);
                    game.takeTurn(placement);
                    int score = minimax(game, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, false, game.getCurrentPlayer(), placements);
                    game.undoLastTurn();
                    System.out.println("[calculateTurn] Score for placement: " + score);
                    if (score > bestScore) {
                        System.out.println("[calculateTurn] Found better placement: " + placement + " with score: " + score);  // Log if a better placement was found
                        bestMove = placement;
                        bestScore = score;
                    }
                    System.out.println("Current placement: " + placement);
                    System.out.println("Score: " + score);
                    System.out.println("Current best score: " + bestScore);
                    System.out.println("Current best move: " + bestMove);

                }
// depth++ wird nie benutzt
                depth++;
            System.out.println("[calculateTurn] Final best move: " + bestMove + ", with score: "+bestScore);  // Log the final best move and score
            }
     //   }
        if (bestMove != null && (game.lastTurn().getAction() == null || !game.lastTurn().getAction().equals(bestMove))) {
            game.takeTurn(bestMove);  // Führt den besten berechneten Zug auf dem Spiel aus
        }

        System.out.println("Game Score: " + game.score());
        System.out.println("Generated Possible Placements Count: " + placements.size());  // Hinzugefügt
        return Optional.ofNullable(bestMove);
    }

    /**
     * Die Hauptmethode des Minimax-Algorithmus mit Alpha-Beta-Pruning.
     *
     * @param game             Das Spielobjekt, das den aktuellen Zustand des Spiels repräsentiert.
     * @param depth            Die aktuelle Suchtiefe.
     * @param alpha            Der Alpha-Wert für Alpha-Beta-Pruning.
     * @param beta             Der Beta-Wert für Alpha-Beta-Pruning.
     * @param maximizingPlayer Ein boolescher Wert, der angibt, ob der Spieler maximiert oder minimiert.
     * @param playerColor      Die Farbe des aktuellen Spielers.
     * @return Der berechnete Score für den aktuellen Spielzustand.
     */
    private int minimax(Game game, int depth, int alpha, int beta, boolean maximizingPlayer, Color playerColor, List<Placement> placements) {
        if (depth == 0 || game.isFinished()) {
            int evaluation = evaluate(game, playerColor, placements);
            System.out.println("Evaluated Score: " + evaluation);  // Hinzugefügt
            return evaluate(game, playerColor, placements);
        }

        int bestScore;

        if (maximizingPlayer) {
            bestScore = Integer.MIN_VALUE;

            for (Placement placement :placements) {
                game.takeTurn(placement);
                int score = minimax(game, depth - 1, alpha, beta, false, playerColor, placements);
                game.undoLastTurn();
                bestScore = Math.max(score, bestScore);
                alpha = Math.max(alpha, bestScore);

                if (beta <= alpha) {
                    break;
                }
            }
        } else {
            bestScore = Integer.MAX_VALUE;

            for (Placement placement : placements) {
                game.takeTurn(placement);
                int score = minimax(game, depth - 1, alpha, beta, true, playerColor,placements);
                game.undoLastTurn();
                bestScore = Math.min(score, bestScore);
                beta = Math.min(beta, bestScore);

                if (beta <= alpha) {
                    break;
                }
            }
        }
        System.out.println("[minimax] Entered minimax with depth: " + depth + ", alpha: " + alpha + ", beta: " + beta + ", maximizingPlayer: " + maximizingPlayer + ", playerColor: " + playerColor);


        System.out.println("[minimax] Returning best score: " + bestScore);

        return bestScore;
    }

    /**
     * Bewertet den aktuellen Spielzustand basierend auf verschiedenen Kriterien.
     *
     * @param game        Das Spielobjekt, das den aktuellen Zustand des Spiels repräsentiert.
     * @param playerColor Die Farbe des aktuellen Spielers.
     * @return Die Bewertung des Spielzustands.
     */

    // Änderung der Parameter w1, w2, w3 hat keine Auswirkung auf die Bewertung
    private int evaluate(Game game, Color playerColor, List<Placement> placements) {
        int w1 = 1;
        int w2 = 1;
        int w3 = 1;
        System.out.println("[evaluate] Evaluating game. Player color: " + playerColor);
        int evaluation = 0;

        evaluation += highestScore(placements);

        // Die Größe des besetzten Gebietes berechnen
     //  int occupiedAreaSize = calculateOccupiedAreaSize(game);

        // Die Größe des besetzten Gebietes gewichten
     //   evaluation += w2 * occupiedAreaSize;

        // Potenzielle Verluste
       // evaluation -= w3 * potentialLossesWithoutCopyGame(game, playerColor);
   //     System.out.println("[evaluate] Final evaluation score: " + evaluation); // Log the final evaluation score before returning it
        return evaluation;
    }

    /**
     * Berechnet die Größe des besetzten Gebiets des aktuellen Spielers.
     *
     * @param game        Das Spielobjekt, das den aktuellen Zustand des Spiels repräsentiert.
     * @return Die Größe des besetzten Gebiets.
     */
    private int calculateOccupiedAreaSize(Game game) {
        Color playerColor = game.getCurrentPlayer();
        Color ownedColor = playerColor == Color.Black ? Color.Black_Owned : Color.White_Owned;

        int occupiedAreaSize = 0;
        Color[][] field = game.getBoard().getField();

        for (int y = 0; y < 10; ++y) {
            for (int x = 0; x < 10; ++x) {
                if (field[y][x] == ownedColor) {
                    occupiedAreaSize++;
                }
            }
        }
       // System.out.println("Occupied Area Size: " + occupiedAreaSize);  // Hinzugefügt
        return occupiedAreaSize;
    }

    /**
     * Berechnet die Differenz zwischen dem Score des letzten Zugs und dem aktuellen Score des Spielers.
     *
     * @return Die Differenz im Score.

    private int calculateScoreDifference(Game game) {
        Color playerColor = game.getCurrentPlayer();
        Map<Color, Integer> lastTurnScore = game.lastTurn().score();
        Map<Color, Integer> gameScore = game.score();

        Integer lastGameScore = lastTurnScore.get(playerColor);
        Integer currentGameScore = gameScore.get(playerColor);

        if (lastGameScore == null) {
            lastGameScore = 0; // Wenn der Spieler beim letzten Zug keine Punkte hatte, setze den Score auf 0
        }

        if (currentGameScore == null) {
            currentGameScore = 0; // Wenn der Spieler in der aktuellen Situation keine Punkte hat, setze den Score auf 0
        }

        System.out.println("Last Game Score: " + lastGameScore);
        System.out.println("Current Game Score: " + currentGameScore);
        System.out.println("Current Player Color: " + playerColor);

        int difference = currentGameScore - lastGameScore;
        System.out.println("Score Difference: " + difference);

        return difference;
    }
     */
    private int highestScore(List<Placement> placements) {
        int highestScore = -1;

        for (Placement placement : placements) {
            int currentScore = placement.building().score();

            if (currentScore > highestScore) {
                highestScore = currentScore;
            }
        }

        return highestScore;
    }

    /**
     * Berechnet potenzielle Verluste ohne die Kopie des Spiels zu verwenden.
     *
     * @param game        Das Spielobjekt, das den aktuellen Zustand des Spiels repräsentiert.
     * @param playerColor Die Farbe
     */
    private int potentialLossesWithoutCopyGame(Game game, Color playerColor) {
        int lastGameScore = game.lastTurn().score().get(playerColor);
        int gameScore = game.score().get(playerColor);
        if (lastGameScore < gameScore) {
            int losses = gameScore - lastGameScore;
            System.out.println("Potential Losses Without Copy Game: " + losses);  // Hinzugefügt
            return gameScore - lastGameScore;
        } else {
            return 0;
        }
    }

    /**
     * Berechnet potenzielle Verluste mit einer Kopie des Spiels.
     *
     * @param game        Das Spielobjekt, das den aktuellen Zustand des Spiels repräsentiert.
     * @param playerColor Die Farbe des aktuellen Spielers.
     * @return Die potenziellen Verluste.
     */
    /*private int potentialLosses(Game game, Color playerColor) {
        int potentialLosses = 0;

        // Get the set of current placable buildings
        List<Building> currentBuildings = game.getPlacableBuildings(playerColor);

        // Suche nach jedem möglichen Zug des Gegners
        for (Placement placement : generatePossiblePlacements(game, getOpponentColor(playerColor))) {
            Game hypotheticalGame = game.copy();  //Simulate the enemy's move
            hypotheticalGame.takeTurn(placement);

            // Zählt die Anzahl der eigenen Gebäude vor und nach dem Zug des Gegners
            List<Building> numberOfBuildingsAfter = hypotheticalGame.getPlacableBuildings(playerColor);

            // Create a new set from the current buildings and removes all buildings still placable after opponent's turn
            Set<Building> lostBuildings = new HashSet<>(currentBuildings);
            lostBuildings.removeAll(numberOfBuildingsAfter);

            // Gehe durch alle Gebäude, die potenziell verloren gehen könnten
            for (Building building : lostBuildings) {
                // Addiere die Größe des Gebäudes zu den potenziellen Verlusten hinzu
                potentialLosses += building.score();
            }
        }

        return potentialLosses;
    }*/

    /**
     * Gibt die Farbe des Gegners zurück.
     *
     * @param playerColor Die Farbe des aktuellen Spielers.
     * @return Die Farbe des Gegners.
     */
  /*  private Color getOpponentColor(Color playerColor) {
        if (playerColor == Color.Black) {
            return Color.White;
        } else {
            return Color.Black;
        }
    }*/


    private List<Placement> generatePossiblePlacements(Game game, Color playerColor) {
        // Maximale Anzahl der Platzierungen definieren
        int maxPlacements = 550;

        // Variablen initialisieren
        Color ownedColor = playerColor == Color.Black ? Color.Black_Owned : Color.White_Owned;
        LinkedList<Placement> placements = new LinkedList<>();
        List<Building> buildings = new ArrayList<>(game.getPlacableBuildings(playerColor));

        // Sortiere die Gebäude nach Größe in absteigender Reihenfolge
        buildings.sort((building1, building2) -> Integer.compare(building2.score(), building1.score()));

        for (Building building : buildings) {
            for (int y = 0; y < 10; ++y) {
                for (int x = 0; x < 10; ++x) {

                    if (placements.size() >= maxPlacements) {
                        return placements;
                    }

                    for (Direction direction : Direction.values()) {
                        Placement potentialPlacement = new Placement(new Position(x, y), direction, building);
                        Board gameBoardCopy = game.getBoard().copy();

                        if (gameBoardCopy.placeBuilding(potentialPlacement, true)) {
                            if (!(game.getBoard().getField()[y][x] == playerColor || game.getBoard().getField()[y][x] == ownedColor)) {
                                placements.addFirst(potentialPlacement); // Füge zur Anfang der Liste hinzu
                            } else {
                                placements.addLast(potentialPlacement); // Füge zur Ende der Liste hinzu
                            }
                        }
                    }
                }
            }
        }
        return placements;
    }
}
