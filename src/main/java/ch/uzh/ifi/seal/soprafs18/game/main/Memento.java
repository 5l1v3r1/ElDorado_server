package ch.uzh.ifi.seal.soprafs18.game.main;

import ch.uzh.ifi.seal.soprafs18.game.cards.Card;
import ch.uzh.ifi.seal.soprafs18.game.hexspace.HexSpace;
import ch.uzh.ifi.seal.soprafs18.game.player.PlayingPiece;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Memento  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int momentoId;

    public Memento(Set<HexSpace> reachables, Set<Card> selectedCards, PlayingPiece playingPiece){
        this.reachables = reachables;
        this.selectedCards = selectedCards;
        this.playingPiece = playingPiece;
    }

    public Memento(){
        this.reachables = new HashSet<>();
        this.selectedCards = new HashSet<>();
        this.playingPiece = null;
    }

    /*
    List of HexSpaces that the PathFinder reached.
     */
    @Embedded
    @ElementCollection
    private Set<HexSpace> reachables;

    /*
    List of Cards that were used to perform the pathfinding-algorithm.
     */
    @OneToMany
    private Set<Card> selectedCards;

    /*
    PlayingPiece from which the PathFinder performed its pathfinder-algorithm.
     */
    @Embedded
    private PlayingPiece playingPiece;

    /*
    Refill Memento with new information from the PathFinder.
     */
    public void reset(){
        for(HexSpace hexSpace: reachables){
            hexSpace.setMinimalCost(1000);
            hexSpace.setMinimalDepth(1000);
            hexSpace.setPrevious(new ArrayList<>());
            this.playingPiece = null;
            this.selectedCards = new HashSet<>();
        }
    }
}
