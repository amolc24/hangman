# Introduction
This is a distributed hangman backend api service which exposes 3 api's to play the game. The documentation of these API's are available in swagger.yaml file in docs folder.

The game starts by calling the newgame api which returns the uuid of the game. Using the UUID of the game the other members can join the game as well.
The game has a limit of 10 guess overall irrespective of each player.