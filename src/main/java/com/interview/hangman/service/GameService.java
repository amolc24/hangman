package com.interview.hangman.service;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.interview.hangman.model.GameData;
import com.interview.hangman.model.Guess;
import com.interview.hangman.repository.GameRepository;

@Service
public class GameService {

	/**
	 * A cache here is a simple Hashmap cache with key as the gameid and the value
	 * as the gamedata object A hashmap instance can be used here in this service to
	 * keep the cache but that is only going to be limiting to this instance of the
	 * service if we have to scale then this hashmap is of no use. So we use a
	 * distributed cache mechanism which is Redis. The following methods make use of
	 * redis cache via spring cache mechanism.
	 */

	@Autowired
	GameRepository gameRepo;

	@CachePut(key = "#result.gameId", value = "game") // this will update in cache (Using spring cache and will use
														// redis
														// implementation if dependency added)
	public GameData newGame() {
		String gameId = generateNewGame();
		GameData game = new GameData();
		game.setGameId(gameId);
		game.setStartTime(new Date());
		String gameword = selectNewWord(gameId);
		game.initializeNewGame(gameword);
		game = gameRepo.save(game);
		updateGameWordCache(gameId, gameword);
		return game;

	}

	@Cacheable(key = "#gameId", value = "game") // this will update in cache (Using spring cache and will use redis
												// implementation if dependency added)
	public GameData getGame(String gameId) {
		return gameRepo.findById(gameId).orElse(null);

	}

	@CachePut(key = "#game.gameId", value = "game") // this will update in cache (Using spring cache and will use redis
													// implementation if dependency added)
	public GameData updateGame(GameData game) {
		return gameRepo.save(game); // This will update in database
		// Alternatively, this can also be a asynchronous call to send the game data in
		// a distributed queue
		// and a subscribing service can update the data from queue in database
		// asynchronously.
	}

	@CacheEvict(beforeInvocation = false, key = "#game.gameId") // this will update in cache to remove the element
																// (Using spring cache and will use redis implementation
																// if dependency added)
	public GameData deleteGame(GameData game) {
		// game.setCompleted(true);
		// updateGameWordCache(game.getGameId());
		return updateGame(game);
	}

	private String generateNewGame() {
		return UUID.randomUUID().toString();
	}

	@CachePut(key = "#gameId", value = "gameword")
	public String selectNewWord(String gameId) {
		// ramdomize word selection from a list of words
		// This list of word can be fetched from a separate cache if the word list is
		// huge
		String randomWord = "";
		Random rd = new Random();
		int randomNumber = rd.nextInt(100000); // considering the words dictionary has 100k words for our application
		// considering we have hashed the words we can use the random number to get the
		// word in the cache

		// randomWord = getRandomWord(randomNumber / MAX_CACHE_SIZE);
		
		return getGameWord(gameId);
	}

	@CacheEvict(key = "#gameId", value = "gameword")
	public String updateGameWordCache(String gameId, String gameword) {
		// game to word repository can update it in database in separate table.
		return gameword;
	}

	@Cacheable(key = "#gameId", value = "gameword")
	public String getGameWord(String gameId) {
		// repository.getGameWord(gameId);
		return "hangman"; // this is hardcoded to compile and make the program run, but the actual logic
							// will be getting the word from the repository which can get it either from
							// file or s3 bucket or stream of words
	}

	public GameData addGuess(GameData game, Guess guess) throws GameException {
		// check with game word if the character passed matches with any character in
		// the word
		// if matches then update the decoded word with the character

		// get the word for the game from the cache
		String gameWord = getGameWord(game.getGameId());

		// before we start processing the guess, to avoid concurrency we can add the
		// guesses in a redis FIFO queue and process
		// taking the advantage of distributed nature of the redis queue we can solve
		// concurrency at some extend.

		// iterate over gameword to match the guess character
		// if it matches grab the index
		// update the character in the decodedWord list at the same index. O(n) solution
		List<Character> decodedWord = game.getDecodedWord();
		boolean found = false;
		for (int i = 0; i < gameWord.length(); i++) {
			if (gameWord.charAt(i) == guess.getLetter().charValue()) {
				decodedWord.set(i, guess.getLetter());
				found = true;
			}
		}

		if (!found) {

			List<Character> incorrectGuess = game.getIncorrectGuess();
			incorrectGuess.add(guess.getLetter());
			if (incorrectGuess.size() >= 10) {
				throw new GameException("No of attemps are exhausted.. Please try a new game");
			}
		}

		// other thread might have decoded the word. so checking here irrespective of
		// any condition

		if (isWordDecoded(game)) {
			game.setComplete(true);
			game.setErrorMessage("You have successfully decoded the word!!");
		}
		updateGame(game); // update in cache and database
		return game;

	}

	private boolean isWordDecoded(GameData game) {

		return !game.getDecodedWord().contains('$');

	}

}
