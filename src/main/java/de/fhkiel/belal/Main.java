package de.fhkiel.belal;
import de.fhkiel.ki.cathedral.ai.Agent;
import de.fhkiel.ki.cathedral.game.Building;
import de.fhkiel.ki.cathedral.game.Direction;
import de.fhkiel.ki.cathedral.game.Game;
import de.fhkiel.ki.cathedral.game.Placement;
import de.fhkiel.ki.cathedral.game.Position;
import de.fhkiel.ki.cathedral.gui.CathedralGUI;
import de.fhkiel.ki.cathedral.gui.Settings;

import java.util.Optional;


public class Main {
    public static void main(String[] args) {
        CathedralGUI.start(
                new MinimaxAgent(), new LargestPieceAgent());
    }


    }
