package ch.uzh.ifi.seal.soprafs18.game.board.repository;

import ch.uzh.ifi.seal.soprafs18.game.board.entity.TileEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TileRepository extends CrudRepository<TileEntity, Character>{
    TileEntity findByTileID(Character id);
}
