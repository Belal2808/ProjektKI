package de.fhkiel.belal;

import de.fhkiel.ki.cathedral.ai.Agent;
import de.fhkiel.ki.cathedral.game.*;

import java.lang.reflect.Field;
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
        //    Game gameCopy = game.copy();
        List<Placement> placements = generatePossiblePlacements(game, game.getCurrentPlayer());
        System.out.println("Generated Possible Placements Count: " + placements.size());  // Hinzugefügt

        Placement bestMove = null;
        int tenSeconds = 10000;
        if (game.lastTurn().getTurnNumber() == 0) {
            bestMove = new Placement(4, 4, Direction._90, Building.Blue_Cathedral);
        } else {
            long startTime = System.currentTimeMillis();
            long deadline = startTime + tenSeconds;
            // ergibt wenig sinn, weil es verrechnet wird, wer wie viele punkte gemacht hat
            int depth = 1;


            int bestScore = Integer.MIN_VALUE;


            for (Placement placement : placements) {
                game.takeTurn(placement);
                int score = minimax(game, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true, game.getCurrentPlayer());
                game.undoLastTurn();
                if (score > bestScore) {
                    bestMove = placement;
                    bestScore = score;
                }


            }
        }

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
    private int minimax(Game game, int depth, int alpha, int beta, boolean maximizingPlayer, Color playerColor) {

        if (depth <= 1 || game.isFinished()) {
            var score = getScore(game, playerColor);
            return score;
        }

        var placements = generatePossiblePlacements(game, playerColor);
        if (maximizingPlayer) {

            for (Placement placement : placements) {
                game.takeTurn(placement);
                // score + beta
                int score = minimax(game, depth - 1, alpha, beta, false, playerColor);
                game.undoLastTurn();
                alpha = Math.max(alpha, score);

                //if (beta < alpha) {
                //    return alpha;
                //}
            }
            return alpha;
        } else {


            for (Placement placement : placements) {
                game.takeTurn(placement);
                int score = minimax(game, depth - 1, alpha, beta, true, playerColor);
                game.undoLastTurn();
                beta = Math.min(beta, score);

                // if (beta > alpha) {
                //      return beta;
                //  }
            }
            return beta;
        }
    }

    private int getScore(Game game, Color playerColor) {
        float ownPointsWeight = 1.0f;
        float ownedPointsWeight = 1.0f;
        float enemyPointsWeight = .5f;
        Evaluation beforePlacementEvaluation = evaluate(game.lastTurn().getBoard(), playerColor);
        Evaluation afterPlacementEvaluation = evaluate(game.getBoard(), playerColor);
        int ownPointsDifference = (int) (ownPointsWeight * (afterPlacementEvaluation.ownPoints - beforePlacementEvaluation.ownPoints));
        int ownedPointsDifference = (int) (ownedPointsWeight * (afterPlacementEvaluation.ownedPoints - beforePlacementEvaluation.ownedPoints));
        int enemyPointsDifference = (int) (enemyPointsWeight * (afterPlacementEvaluation.enemyPoints - beforePlacementEvaluation.enemyPoints));
        //int currentScore = ownPointsDifference + ownedPointsDifference - enemyPointsDifference;
        int currentScore = afterPlacementEvaluation.ownPoints + afterPlacementEvaluation.ownedPoints - afterPlacementEvaluation.enemyPoints;


        return currentScore;
    }


    private class Evaluation {
        public int ownPoints;
        public int ownedPoints;
        public int enemyPoints;

        public Evaluation(int ownPoints, int ownedPoints, int enemyPoints) {
            this.ownPoints = ownPoints;
            this.ownedPoints = ownedPoints;
            this.enemyPoints = enemyPoints;
        }
    }

    /**
     * Bewertet den aktuellen Spielzustand basierend auf verschiedenen Kriterien.
     *
     * @param board       Das Spielobjekt, das den aktuellen Zustand des Spiels repräsentiert.
     * @param playerColor Die Farbe des aktuellen Spielers.
     * @return Die Bewertung des Spielzustands.
     */
    private Evaluation evaluate(Board board, Color playerColor) {
        Color colorOtherPlayer = Color.Black;
        if (playerColor == Color.Black) {
            colorOtherPlayer = Color.White;
        }
        Color colorOwned = Color.Black_Owned;
        if (playerColor == Color.White) {
            colorOwned = Color.White_Owned;
        }
        var field = board.getField();
        int enemyPoints = 0;
        int ownPoints = 0;
        int ownedPoints = 0;
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                if (field[y][x] == playerColor) {
                    ownPoints++;
                } else if (field[y][x] == colorOtherPlayer) {
                    enemyPoints++;
                } else if (field[y][x] == colorOwned) {
                    ownedPoints++;
                }
            }
        }
        Evaluation evaluation = new Evaluation(ownPoints, ownedPoints, enemyPoints);
        return evaluation;
    }

    private List<Placement> generatePossiblePlacements(Game game, Color playerColor) {
        // Variablen initialisieren
        LinkedList<Placement> placements = new LinkedList<>();
        List<Building> buildings = new ArrayList<>(game.getPlacableBuildings(playerColor));


        for (Building building : buildings) {
            for (int y = 0; y < 10; ++y) {
                for (int x = 0; x < 10; ++x) {


                    for (Direction direction : Direction.values()) {
                        Placement potentialPlacement = new Placement(new Position(x, y), direction, building);
                        if (game.takeTurn(potentialPlacement, true)) {
                            placements.add(potentialPlacement);
                            game.undoLastTurn();
                        }
                    }
                }
            }
        }
        return placements;
    }
}
