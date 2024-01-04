package de.fhkiel.belal;

import de.fhkiel.ki.cathedral.ai.Agent;
import de.fhkiel.ki.cathedral.game.Building;
import de.fhkiel.ki.cathedral.game.Game;
import de.fhkiel.ki.cathedral.game.Placement;

import java.util.*;

public class LargestPieceAgent implements Agent {
    @Override
    public Optional<Placement> calculateTurn(Game game, int timeForTurn, int timeBonus) {
        // Schritt 1: Ermitteln der platzierbaren Gebäude im aktuellen Spielzustand
        List<Building> placableBuildings = game.getPlacableBuildings();

        // Schritt 2: Finden der Gebäude mit dem höchsten Score
        List<Building> highestScoreBuildings = findHighestScoreBuildings(placableBuildings);

        if (!highestScoreBuildings.isEmpty()) {
            // Schritt 3: Simulation von Platzierungen für das Gebäude mit dem höchsten Score
            return simulatePlacements(game, highestScoreBuildings);
        }

        return Optional.empty();
    }

    private Optional<Placement> simulatePlacements(Game game, List<Building> buildings) {
        int simulations = 100;
        int bestScore = Integer.MIN_VALUE;
        Placement bestPlacement = null;

        // Iteriere über die Gebäude mit dem höchsten Score
        for (Building building : buildings) {
            // Schritt 4: Ermitteln aller möglichen Platzierungen für das aktuelle Gebäude
            Set<Placement> possiblePlacements = building.getPossiblePlacements(game);
            List<Placement> possiblePlacementsList = new ArrayList<>(possiblePlacements);

            // Iteriere über alle möglichen Platzierungen
            for (Placement placement : possiblePlacementsList) {
                // Schritt 5: Simulation des Spiels nach der Platzierung
                int score = simulateGame(game, building, placement, simulations);

                // Schritt 6: Auswahl der Platzierung mit dem besten simulierten Score
                if (score > bestScore) {
                    bestScore = score;
                    bestPlacement = placement;
                }
            }
        }

        // Schritt 7: Rückgabe der besten Platzierung
        return Optional.ofNullable(bestPlacement);
    }

    private int simulateGame(Game game, Building building, Placement placement, int simulations) {
        int totalScore = 0;

        // Schritt 8: Durchführung von Simulationen für die ausgewählte Platzierung
        for (int i = 0; i < simulations; i++) {
            // Schritt 9: Kopieren des aktuellen Spielzustands für die Simulation
            Game simulatedGame = game.copy();
            // Schritt 10: Platzieren des Gebäudes in der simulierten Partie
            simulatedGame.takeTurn(placement);


            int score = evaluateSimulatedGame(simulatedGame);
            totalScore += score;
        }


        return totalScore / simulations;
    }

    private int evaluateSimulatedGame(Game simulatedGame) {
      //todo

        return 0;
    }

    private List<Building> findHighestScoreBuildings(List<Building> buildings) {
        int highestScore = -1;

        for (Building building : buildings) {
            int score = building.score();
            if (score > highestScore) {
                highestScore = score;
            }
        }

        final int maxScore = highestScore;
        List<Building> highestScoreBuildings = new ArrayList<>();

        for (Building building : buildings) {
            if (building.score() == maxScore) {
                highestScoreBuildings.add(building);
            }
        }

        return highestScoreBuildings;
    }
}
