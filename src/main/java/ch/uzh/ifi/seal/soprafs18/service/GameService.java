package ch.uzh.ifi.seal.soprafs18.service;

import ch.uzh.ifi.seal.soprafs18.entity.GameEntity;
import ch.uzh.ifi.seal.soprafs18.entity.PlayerEntity;
import ch.uzh.ifi.seal.soprafs18.entity.RoomEntity;
import ch.uzh.ifi.seal.soprafs18.entity.UserEntity;
import ch.uzh.ifi.seal.soprafs18.game.board.entity.HexSpaceEntity;
import ch.uzh.ifi.seal.soprafs18.game.hexspace.HexSpace;
import ch.uzh.ifi.seal.soprafs18.game.hexspace.Matrix;
import ch.uzh.ifi.seal.soprafs18.game.main.Game;
import ch.uzh.ifi.seal.soprafs18.game.player.Player;
import ch.uzh.ifi.seal.soprafs18.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs18.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@Service
public class GameService implements Serializable{

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    private final Logger LOGGER = Logger.getLogger(RoomService.class.getName());
    private FileHandler filehandler;

    public GameService() {
        try {
            filehandler = new FileHandler("Serverlog.log", 1024 * 8, 1, true);
            LOGGER.addHandler(filehandler);
            SimpleFormatter formatter = new SimpleFormatter();
            filehandler.setFormatter(formatter);
            LOGGER.setLevel(Level.ALL);
            filehandler.setLevel(Level.ALL);
        } catch (IOException io) {
            System.out.println("ERROR: Could not set logging handler to file");
        }
    }

    public List<GameEntity> getAll() {
        List<GameEntity> games = new ArrayList<>();
        gameRepository.findAll().forEach(games::add);
        LOGGER.info("Returning all games");
        return games;
    }

    public void newGame(RoomEntity room) {
        List<UserEntity> users = room.getUsers();
        LOGGER.info("Shuffling users");
        Collections.shuffle(users);
        String gameName = room.getName();
        int board = room.getBoardnumber();
        LOGGER.info("Creating empty list of players and player entities");
        List<Player> players = new ArrayList<>();
        List<PlayerEntity> playerEntities = new ArrayList<>();
        LOGGER.info("Create game with board " + board + " with no players.");
        Game game = new Game(board, null, room.getRoomID());

        int i = 0;
        for (UserEntity user : users) {
            LOGGER.info("Creating new player "+user.getUserID()+" with name "+user.getName()+" and position "+i);
            Player player = new Player(user.getUserID(), user.getName(), game, i);
            LOGGER.info("Added player to players");
            players.add(player);
            LOGGER.info("Create new playerEntity "+user.getUserID()+" with player "+player.getId()+" and gameentity "+gameName+" and token "+user.getToken());
            PlayerEntity playerEntity = new PlayerEntity(user.getUserID(), player, user.getToken(), null);
            LOGGER.info("Adding player entity to list of playerEntities");
            playerEntities.add(playerEntity);
            LOGGER.info("Save playerEntity to database");
            playerRepository.save(playerEntity);
            playerRepository.findByPlayerID(user.getUserID()).get(0).setPlayer(player);
            i++;
            LOGGER.info("Converted " + user.getUserID() + " to playerEntity and created new Player");
        }

        LOGGER.info("Setting players of game");
        game.setPlayers(players);
        LOGGER.info("Creating gameEntity");
        GameEntity gameEntity = new GameEntity(game, room.getRoomID(), playerEntities);
        LOGGER.info("Setting playerEntities to gameEntity and saving it to database");
        System.out.println("***PLAYER ID IS: "+playerRepository.findAll().iterator().next().getPlayerID());
        gameRepository.save(gameEntity);
        gameEntity.setGame(game);
        gameRepository.save(gameEntity);
        LOGGER.info("Save game " + gameEntity.getGameID() + " to the database");
        for(int n = 0; n<playerEntities.size(); n++){
            playerEntities.get(n).setGame(gameEntity);
            playerEntities.get(n).setPlayer((players.get(n)));
            playerRepository.save(playerEntities.get(n));
        }
    }

    public GameEntity getGame(int gameID) {
        LOGGER.info("Returning game with ID " + gameID);
        return gameRepository.findByGameID(gameID).get(0);
    }


    public PlayerEntity getCurrentPlayer(GameEntity game) {
        LOGGER.info("Returning current player of game " + game.getGameID());
        return playerRepository.findByPlayerID(game.getGame().getCurrent().getPlayerID()).get(0);
    }


    public List<PlayerEntity> getPlayers(GameEntity game) {
        LOGGER.info("Returning Players of game " + game.getGameID());
        return gameRepository.findByGameID(game.getGameID()).get(0).getPlayers();
    }


    public void stop(GameEntity game) {
        LOGGER.info("Stoppping game " + game.getGameID());
        game.getGame().setRunning(false);
    }


    public List<PlayerEntity> getWinners(GameEntity game) {
        LOGGER.info("Returning winners of game " + game.getGameID());
        List<Player> winners = game.getGame().getWinners();
        List<PlayerEntity> players = new ArrayList<>();
        for(Player winner:winners){
            players.add(playerRepository.findByPlayerID(winner.getPlayerID()).get(0));
        }
        return players;
    }

    public Matrix getBoard(GameEntity game) {
        LOGGER.info("Returning Board of game " + game.getGameID());
        return game.getGame().getPathMatrix();
    }

}
