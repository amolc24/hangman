package com.interview.hangman.graphql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.interview.hangman.model.GameData;
import com.interview.hangman.model.Guess;
import com.interview.hangman.service.GameService;

import graphql.schema.DataFetcher;

@Component
public class GraphQLDataFetcher {
	
	
	@Autowired
	GameService service;
	
	

    public DataFetcher<GameData> joinByIdDataFetcher() {
        return dataFetchingEnvironment -> {
            String gameId = dataFetchingEnvironment.getArgument("gameId");
            return service.getGame(gameId);
        };
    }

    public DataFetcher<GameData> newGameDataFetcher() {
        return dataFetchingEnvironment -> {
            return service.newGame();
        };
    }
    
    
    public DataFetcher<GameData> guess() {
    	return dataFetchingEnvironment -> {
    		String gameId = dataFetchingEnvironment.getArgument("gameId");
    		Guess guess = dataFetchingEnvironment.getArgument("guess");
    		GameData game = service.getGame(gameId);
    		return service.addGuess(game, guess);
    	};
    }
}