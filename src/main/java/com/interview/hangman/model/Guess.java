package com.interview.hangman.model;

public class Guess {
	
	Character letter;
	String clientId;

	public Character getLetter() {
		return letter;
	}

	public void setLetter(Character c) {
		this.letter = c;
	}
}
