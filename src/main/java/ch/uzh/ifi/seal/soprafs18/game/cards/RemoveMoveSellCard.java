package ch.uzh.ifi.seal.soprafs18.game.cards;

import ch.uzh.ifi.seal.soprafs18.game.player.Player;
import lombok.Data;

import javax.persistence.Entity;

@Entity
@Data
public class RemoveMoveSellCard extends RemoveMoveCard {

    public RemoveMoveSellCard(String name, int coinValue, int coinCost){
        super(name, coinValue, coinCost);
    }

    public RemoveMoveSellCard(){
        super();
    }

    /*
    Overwrites the sellAction method that calls Player.remove(this) instead of Player.discard(this) when a Card is sold.
     */
    @Override
    public void sellAction(Player player){
        super.sellAction(player);
        player.remove(this);
    }
}
