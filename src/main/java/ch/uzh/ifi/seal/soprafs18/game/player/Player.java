package ch.uzh.ifi.seal.soprafs18.game.player;

import ch.uzh.ifi.seal.soprafs18.game.cards.ActionCard;
import ch.uzh.ifi.seal.soprafs18.game.cards.Card;
import ch.uzh.ifi.seal.soprafs18.game.cards.Slot;
import ch.uzh.ifi.seal.soprafs18.game.cards.SpecialActions;
import ch.uzh.ifi.seal.soprafs18.game.hexspace.HexSpace;
import ch.uzh.ifi.seal.soprafs18.game.main.Blockade;
import ch.uzh.ifi.seal.soprafs18.game.main.Game;
import ch.uzh.ifi.seal.soprafs18.game.main.Pathfinder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jdk.nashorn.internal.ir.Block;

import javax.persistence.*;
import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Boolean.FALSE;

@Embeddable
public class Player  implements Serializable {

    public Player(int PlayerID, String name, Game game, int id){
        this();
        this.name = name;
        this.playerID = PlayerID;
        this.id = id;
        this.board = game;

    }

    public Player(){
        this.name = "Unknown";
        this.playerID = -1;
        this.id = -1;
        this.board = new Game();
        this.coins = (float) 0;
        this.pathFinder = new Pathfinder();
        this.playingPieces = new ArrayList<PlayingPiece>();
        this.specialAction = new SpecialActions();
        this.history = new ArrayList<CardAction>();
        this.drawPile  = new ArrayList<Card>();
        this.handPile = new ArrayList<Card>();
        this.discardPile = new ArrayList<Card>();
        this.bought = FALSE;
    }

    /*
    Globally unique ID
     */
    private int playerID;

    /*
    Players  name, set by the User. Has to be unique in the Game
     */
    private String name;

    /*
    Globally unique ID to recognize a Player within the Game.
     */
    private Integer id;

    /*
    Global unique token that identifies a User to its player.
    The token is communicated via SSL and randomized. For each game changing move,
    the user has to validate itself with this token in order to perform the move.
     */
    //Propably not needed since already in database
    //private String token;

    /*
    Number of coins the Player has in his wallet. Is reset to 0 when he ends his round or bought one card.
     */
    @Transient
    @JsonIgnore
    private Float coins;

    /*
    Instance of Game on which the Player is performing his action.
     */
    @Transient
    @JsonIgnore
    private Game board;

    /*
    Instance of PATHFINDER the player uses to find the possible paths.
     */
    @Transient
    @JsonIgnore
    private Pathfinder pathFinder;

    /*
    List of playing pieces the player controls.
     */
    @Transient
    @JsonIgnore
    private ArrayList<PlayingPiece> playingPieces;

    /*
    List of blockades the Player has collected so far.
     */
    @Transient
    @JsonIgnore
    private List<Blockade> blockades;

    /*
    The budget the user has for the current round.
    Is set from the action cards and reset either at the end of the game or
    value-by-value each time the corresponding method (draw, remove, steal) is called.
     */
    @Transient
    @JsonIgnore
    private SpecialActions specialAction;

    /*
    Each time the user plays a Card of any type, its history is appended with the corresponding CardAction.
     */
    @Transient
    @JsonIgnore
    private ArrayList<CardAction> history;

    /*
    List of cards the user has in his drawPile.
     */
    @Transient
    @JsonIgnore
    private ArrayList<Card> drawPile;

    /*
    List of cards the user has in his handPile.
     */
    @Transient
    @JsonIgnore
    private ArrayList<Card> handPile;

    /*
    List of cards the user has in his discardPile.
     */
    @Transient
    @JsonIgnore
    private ArrayList<Card> discardPile;

    /*
    Indicates whether the user has already bought a Card in the current round.
     */
    @Transient
    @JsonIgnore
    private Boolean bought;

    /*
    Calls PathFinder with the cards and the selected playingPiece. Returns the same arrayList the PathFinder returns.
     */
    public ArrayList<HexSpace> findPath(List<Card> activeCards, PlayingPiece playingPiece) {
        return null;
    }

    /*
    Call this.pathFinder with the first playingPiece and the list of Cards.
    Returns the same arrayList this.pathFinder returns.
     */
    public ArrayList<HexSpace> findPath(List<Card> activeCards) {
        return null;
    }

    /*
    Checks in the memento whether cards corresponds to SelectedCards,
    placingPiece to playingPiece and if to-HexSpace is in reachables.
    If so, the players PlayingPiece is moved to the to-HexSpace location.
    If in the history of the to-HexSpaces previous an active barricadeSpace is located,
    deactive is called on the corresponding blockade and the blockade is added to the users barricades-array.
    After a move is done, call cards.move on all cards in the Cards array.
    Finally, we check whether a deactivated barricade is now next to the to-HexSpaces neighbours.
    If this is the case, check whether the barricade is of the same type as the to-HexSpace and if the difference
    between the cards value and the to-HexSpaces minimalCosts allows the removal of the barricade.
    The user can then decide whether he wants to take that barricade or not.
    When the move is done, the Player checks whether his PlayingPiece stands on a HexSpace of colour ElDoardo.
    If this is the case, he adds himself to the Games winning Player array.
     */
    public void move(PlayingPiece playingPiece, List<Card> cards, HexSpace moveTo) {
    }

    /*
    Calls action on the corresponding card and sets the returned SpecialAction to its own budget.
    Adds instance of CardAction with dedicated name to the history array. It returns the budget back to the Frontend.
     */

    public void action(ActionCard card) {
        specialAction = card.performAction(this);

        CardAction cardAct = new CardAction(card, "Play: " + card.getName());
        history.add(cardAct);
    }

    /*
    Moves the corresponding Card from the handPile to the discardPile.
    Adds instance of CardAction with dedicated name to the history array.
     */
    public void discard(Card card) {

        CardAction cardAct = new CardAction(card,"Discard: " + card.getName());
        history.add(cardAct);

        if (handPile.contains(card)) {
            discardPile.add(card);
            handPile.remove(card);
        } else {
            discardPile.add(card);
        }
    }

    /*
    calls Card.sell(self: Player)
     */
    public void sell(Card card) {

        history.add(new CardAction(card,"Sell: " + card.getName()));
        card.sellAction(this);
    }

    /*
    Calls buy on the market and adds the returned card to the discardPile if the user has the coins to do so and
    not yet bought anything. Adds instance of CardAction with dedicated name to the history array.
     */
    public void buy(Slot slot) {

        history.add(new CardAction(slot.getCard(), "Sell: " + slot.getCard().getName()));

        if (slot.getCard().getCoinCost() <= coins && !bought) {
            this.discard(slot.buy());
        }
    }

    /*
    Calls draw(amount) with the amount being 4 - length of HandPile.
     */
    public void draw() {
        draw(4 - handPile.size());
    }

    /*
    Takes amount-cards from the drawpile, regardless of how many cards there are in the Handpile.
    If the drawPile is empty, the discardPiles order is randomized and all cards are
    moved from the discardPile to the drawPile.
     */
    public void draw(Integer amount) {
        int amountTmp = amount;
        CardAction cardAct = new CardAction("Draw " + amountTmp + " cards.");
        while (drawPile.size() > 0 && amount > 0) {
            cardAct.addCard(drawPile.get(0));
            handPile.add(drawPile.remove(0));
            amount--;
        }
        history.add(cardAct);

       /* while (drawPile.size() > 0 && specialAction.getDraw() > 0) {
            handPile.add(drawPile.remove(0));
            amount--;
        }*/

        if (drawPile.size() < 1) {
            for (int i = discardPile.size(); i == 0; i--) {
                int rnd = new Random().nextInt(discardPile.size());
                drawPile.add(discardPile.remove(rnd));
            }
            draw(amount);
        }
    }

    /*
    Calls market.steal and adds the returned card to the discardpile. Does neither take the amount of
    coins nor the bought-boolean into consideration.
     */
    public void steal(Slot slot) {
        history.add(new CardAction(slot.getCard(), "Take: " + slot.getCard().getName()));
        discardPile.add(slot.buy());
    }

    /*
    Moves the card from the handPile to the removePile.
     */
    public void remove(Card card) {
        history.add(new CardAction(card,"Remove: " + card.getName()));
        handPile.remove(card);
    }

    /*
    Calls draw() and resets the coins to 0 and the bought-boolean to False.
     */
    public void endRound() {
        draw();
        coins = (float) 0;
        bought = false;
    }

    public void addCoins(Float amount) {
        coins = coins + amount;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /*
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    */

    public Float getCoins() {
        return coins;
    }

    public void setCoins(Float coins) {
        this.coins = coins;
    }

    public Game getBoard() {
        return board;
    }

    public void setBoard(Game board) {
        this.board = board;
    }

    public Pathfinder getPathFinder() {
        return pathFinder;
    }

    public void setPathFinder(Pathfinder pathFinder) {
        this.pathFinder = pathFinder;
    }

    public List<PlayingPiece> getPlayingPieces() {
        return playingPieces;
    }

    public void setPlayingPieces(ArrayList<PlayingPiece> playingPieces) {
        this.playingPieces = playingPieces;
    }

    public SpecialActions getSpecialAction() {
        return specialAction;
    }

    public void setSpecialAction(SpecialActions specialAction) {
        this.specialAction = specialAction;
    }

    public List<CardAction> getHistory() {
        return history;
    }

    public void setHistory(ArrayList<CardAction> history) {
        this.history = history;
    }

    public List<Card> getDrawPile() {
        return drawPile;
    }

    public void setDrawPile(ArrayList<Card> drawPile) {
        this.drawPile = drawPile;
    }

    public List<Card> getHandPile() {
        return handPile;
    }

    public void setHandPile(ArrayList<Card> handPile) {
        this.handPile = handPile;
    }

    public List<Card> getDiscardPile() {
        return discardPile;
    }

    public void setDiscardPile(ArrayList<Card> discardPile) {
        this.discardPile = discardPile;
    }

    public Boolean getBought() {
        return bought;
    }

    public void setBought(Boolean bought) {
        this.bought = bought;
    }

    public List<Blockade> getBlockades() {
        return blockades;
    }

    public void setBlockades(List<Blockade> blockades) {
        this.blockades = blockades;
    }

    public int getPlayerID() {
        return playerID;
    }

}
