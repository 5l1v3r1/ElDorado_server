package ch.uzh.ifi.seal.soprafs18.game.main;

import ch.uzh.ifi.seal.soprafs18.game.hexspace.BlockadeSpace;
import ch.uzh.ifi.seal.soprafs18.game.hexspace.HexSpace;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Embeddable
public class Blockade  implements Serializable {

    public Blockade(List<HexSpace> blockadeSpaces){
        this.spaces = blockadeSpaces;
        this.cost = blockadeSpaces.get(0).getStrength();
    }

    /*
    List of BlockadeSpaces/HexSpaces the Blockade consists of.
     */
    //@Embedded
    //@ElementCollection
    @JsonIgnore
    @Transient
    private List<HexSpace> spaces;

    /*
    Cost to remove this blockade. The cost is the same for all
    BlockadSpaces in the same Blockade. The same cost is also set as
    “strength” in the BlockadeSpace. When a barricade is deactivated,
    the BlockadeSpace strength is set to 0, but not the Blockades cost since the
    Blockade gets assigned to the Player that removed it and its cost factor
    has to be remembered for the endgame.
     */
    private int cost;

    /*
    Deactivates a blockade by settings its BlockadeSpace strength to 0.
     */
    public void deactivate(){
        this.cost = 0;
    }


}
