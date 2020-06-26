package com.interview.hangman.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.interview.hangman.model.GameData;

@Repository
public interface GameRepository extends CrudRepository<GameData, String> {

}
