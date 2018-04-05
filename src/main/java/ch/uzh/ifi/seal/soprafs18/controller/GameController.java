package ch.uzh.ifi.seal.soprafs18.controller;

import ch.uzh.ifi.seal.soprafs18.entity.GameEntity;
import ch.uzh.ifi.seal.soprafs18.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs18.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs18.service.GameService;
import ch.uzh.ifi.seal.soprafs18.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.List;

@RestController
public class GameController  implements Serializable {
    private final String context = CONSTANTS.APICONTEXT + "/Games";

    @Autowired
    private GameService gameService;

    @Autowired
    private PlayerService playerService;

    //gets all games
    @GetMapping(value = context)
    @ResponseStatus(HttpStatus.OK)
    private List<GameEntity> getGames(){
        return gameService.getAll();
    }

    //gets game id
    @GetMapping(value = context+"/id")
    @ResponseStatus(HttpStatus.OK)
    private int id(){
        return gameService.getAll().get(0).getGame().getID();
    }


}
