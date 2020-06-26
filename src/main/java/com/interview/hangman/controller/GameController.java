package com.interview.hangman.controller;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.interview.hangman.model.GameData;
import com.interview.hangman.model.Guess;
import com.interview.hangman.service.GameException;
import com.interview.hangman.service.GameService;

@RestController()
public class GameController {

	@Autowired
	GameService service;

	@GetMapping("/v1/hangman/newGame")
	public ResponseEntity<GameData> newGame(@RequestHeader Map<String, String> headers) throws GameException {

		// get cliend id from headers
		// check if any session is in progress for client id?
		// if yes return error that session already in progress
		// else gameservice.newGame();

		return ResponseEntity.ok(service.newGame());

	}

	@GetMapping("/v1/hangman/join/{gameId}")
	public ResponseEntity<GameData> join(@RequestHeader Map<String, String> headers,
			@PathVariable("gameId") Optional<String> gameId) throws GameException{

		ResponseEntity<GameData> validateGameId = validateGameId(gameId);
		if (validateGameId != null) {
			return validateGameId;
		}

		// check if the gameId is valid and is in progress
		// if no gameId in progress then respond 5xx with message as gameId not valid
		// else respond with game Data;
		GameData game = service.getGame(gameId.get());

		if (game == null || game.isComplete()) {
			// no game with given id in progress throw an error
			game = new GameData();
			game.setErrorMessage(String.format("No game with id [%s] in progress.", gameId.get()));
			return ResponseEntity.badRequest().body(game);
		}

		return ResponseEntity.ok(game);

	}

	@PostMapping("/v1/hangman/guess/{gameId}")
	public ResponseEntity<GameData> guess(@RequestHeader Map<String, String> headers,
			@PathVariable("gameId") Optional<String> gameId, @RequestBody Guess guess) throws GameException {

		ResponseEntity<GameData> validateGameId = validateGameId(gameId);
		if (validateGameId != null) {
			return validateGameId;
		}

		// check if the gameId is valid and is in progress
		// if no gameId in progress then respond 5xx with message as gameId not valid
		// else respond with game Data;
		GameData game = service.getGame(gameId.get());

		if (game == null || game.isComplete()) {
			// no game with given id in progress throw an error
			game = new GameData();
			game.setErrorMessage(String.format("No game with id [%s] in progress.", gameId.get()));
			return ResponseEntity.badRequest().body(game);
		} else if(game.isComplete()) {
			return ResponseEntity.ok().body(game);
		}

		// game is still in progress and we should add the guess to continue
		service.addGuess(game, guess);
		
		return ResponseEntity.ok(game);

	}

	private ResponseEntity<GameData> validateGameId(Optional<String> gameId) {

		// validate gameId id for just being a string and not code to avoid issues like
		// sql injection etc.

		if (gameId.isEmpty()) {
			GameData game = new GameData();
			game.setErrorMessage("To join a game please provide a valid game id");
			return ResponseEntity.badRequest().body(game);
		}

		try {
			UUID.fromString(gameId.get());
		} catch (Exception e) {
			GameData game = new GameData();
			game.setErrorMessage("Invalid game id : " + gameId.get());
			return ResponseEntity.badRequest().body(game);
		}

		return null;
	}
	
	
	@ExceptionHandler({ GameException.class, RuntimeException.class })
    public ResponseEntity<GameData> handleException(Exception ex) {
		GameData body = new GameData();
		body.setErrorMessage(ex.getMessage());
		
		if(ex instanceof GameException) {
			return ResponseEntity.status(HttpStatus.OK).body(body);
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
		
    }

}
