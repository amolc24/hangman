package com.interview.hangman.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;



@Entity
@JsonInclude(value = Include.NON_EMPTY)
public class GameData {
	
	@Id
	String gameId;
	Date startTime;
	boolean isComplete;
	
	@ElementCollection
	List<Character> decodedWord = new ArrayList<>();
	
	@ElementCollection
	List<Character> incorrectGuess = new ArrayList<>(10);
	

	@Transient
	String errorMessage;
	
	
	public void initializeNewGame(String gameword) {
		int len = gameword.length();
		for(int i =0; i<len;i++) {
			decodedWord.add(i,'$');
		}
	}

	
	public String getGameId() {
		return gameId;
	}


	public void setGameId(String gameId) {
		this.gameId = gameId;
	}


	public Date getStartTime() {
		return startTime;
	}


	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}


	public boolean isComplete() {
		return isComplete;
	}


	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}


	public List<Character> getDecodedWord() {
		return decodedWord;
	}


	public void setDecodedWord(List<Character> decodedWord) {
		this.decodedWord = decodedWord;
	}


	public List<Character> getIncorrectGuess() {
		return incorrectGuess;
	}


	public void setIncorrectGuess(List<Character> incorrectGuess) {
		this.incorrectGuess = incorrectGuess;
	}


	public String getErrorMessage() {
		return errorMessage;
	}


	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
