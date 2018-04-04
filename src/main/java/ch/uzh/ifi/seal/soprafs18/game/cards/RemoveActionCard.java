package ch.uzh.ifi.seal.soprafs18.game.cards;

import ch.uzh.ifi.seal.soprafs18.game.player.Player;

public class RemoveActionCard extends ActionCard {

    public RemoveActionCard(String name, float value, int cost, SpecialActions actions){
        super(name, value, cost, actions);
    }

    /*
   Calls the parents performAction to use Player.remove(Card) instead of Player.discard(Card).
     */

    @Override
    public SpecialActions performAction(Player player){
        player.remove(this);
        return actions;
    }
}
