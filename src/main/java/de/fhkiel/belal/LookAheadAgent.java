package de.fhkiel.belal;
import de.fhkiel.ki.cathedral.ai.Agent;
import de.fhkiel.ki.cathedral.game.Board;
import de.fhkiel.ki.cathedral.game.Building;
import de.fhkiel.ki.cathedral.game.Color;
import de.fhkiel.ki.cathedral.game.Direction;
import de.fhkiel.ki.cathedral.game.Game;
import de.fhkiel.ki.cathedral.game.Placement;
import de.fhkiel.ki.cathedral.game.Position;
import de.fhkiel.ki.cathedral.gui.Settings;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LookAheadAgent implements Agent {
    @Override
    public Optional<Placement> calculateTurn(Game game, int timeForTurn, int timeBonus) {
        Board current = game.getBoard().copy();

        List<PlacementWithBoard> opp = new ArrayList<>();
        for(Building free : current.getAllUnplacedBuildings()){
            if(free.getColor() == game.getCurrentPlayer()){
                for (Direction d : free.getTurnable().getPossibleDirections()){
                    for(int y = 0; y < 10; ++y){
                        for(int x = 0; x < 10; ++x){
                            Placement poss = new Placement(x, y, d, free);
                            if(current.placeBuilding(poss)){
                                opp.add(new PlacementWithBoard(poss, current));
                                current = game.getBoard().copy();
                            }
                        }
                    }
                }
            }
        }

        int currentOwnedArea = getAreaCount(current, game.getCurrentPlayer());
        int bestAreaGain = 0;
        Placement best = null;

        for(PlacementWithBoard pWb : opp){
            int currentAreaGain = getAreaCount(pWb.board, game.getCurrentPlayer()) - currentOwnedArea;
            if(currentAreaGain > bestAreaGain){
                bestAreaGain = currentAreaGain;
                best = pWb.placement;
            }
        }

        if(best != null){
            return Optional.of(best);
        }
        return Optional.empty();
    }

    private int getAreaCount(Board board, Color player) {
        int currentOwnedArea = 0;
        for(int y = 0; y < 10; ++y){
            for(int x = 0; x < 10; ++x){
                if(board.getField()[y][x] == player.subColor()){
                    currentOwnedArea += 1;
                }
            }
        }
        return currentOwnedArea;
    }

    private record PlacementWithBoard(Placement placement, Board board){};
}
