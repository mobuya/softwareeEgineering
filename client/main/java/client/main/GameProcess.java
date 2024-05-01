package client.main;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.EPlayerState;
import data.GameData;
import exceptions.InvalidNetworkActionException;
import map.EField;
import map.EMapCharacter;
import map.FullMap;
import map.MapGenerator;
import map.MountainScanner;
import map.Position;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromserver.GameState;
import move.EDirection;
import move.GoalFinder;
import move.PathFinder;
import network.NetworkDataConverter;
import network.NetworkHandler;

public class GameProcess {
	private String playerID;
	private GameData model;
	private NetworkHandler network;
	private NetworkDataConverter converter = new NetworkDataConverter();

	private final Logger logger = LoggerFactory.getLogger(GameProcess.class);

	public GameProcess(GameData gameModel) {
		this.model = gameModel;
	}

	public void startClient(String serverBaseUrl, String gameID) {
		model.updateGameID(gameID);
		createNetworkHandler(serverBaseUrl, gameID);
		startGame();
	}

	private void createNetworkHandler(String serverBaseUrl, String gameID) {
		this.network = new NetworkHandler(serverBaseUrl, gameID);
	}

	private void startGame() {
		registerMyPlayer();
		sendHalfMap();
		prepareGameData();
		findTreasure();
		findEnemyCastle();
		logger.info("Method startGame() ended; Game ended.");
	}

	private void registerMyPlayer() {
		try {
			UniquePlayerIdentifier uniquePlayerID = network.registerPlayer();
			// my register info is hard coded !!!
			this.playerID = uniquePlayerID.getUniquePlayerID();
			model.updatePlayerID(this.playerID);
		} catch (InvalidNetworkActionException e) {
			System.err.println(e.getMessage());
		}
	}

	private void sendHalfMap() {
		waitForTurn();
		MapGenerator generator = new MapGenerator();
		LinkedHashMap<Position, EField> halfMap = generator.createValidMap();
		try {
			network.sendMap(converter.converMyMapToServerMap(halfMap, playerID));
			model.updatePlayerHalfMap(halfMap);
		} catch (InvalidNetworkActionException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	private GameState getGameState() {
		GameState gameState = new GameState();
		try {
			gameState = network.getGameState();
		} catch (InvalidNetworkActionException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		if (gameState.getGameStateId().isEmpty()) {
			logger.error("GameState is empty, but should be defined with the network method!");
		}
		var nodeWithPlayer = gameState.getMap().stream()
				.filter(node -> node.getPlayerPositionState().representsMyPlayer()).findFirst();
		if (nodeWithPlayer.isPresent()) {
			logger.debug("Server (synchronization check): MyPlayerPosition is x{}, y{}", nodeWithPlayer.get().getX(),
					nodeWithPlayer.get().getY());
		}

		return gameState;
	}

	private EPlayerState getPlayerState(GameState serverGameState) {
		EPlayerState playerState = converter.extractPlayerState(serverGameState, playerID);
		model.updatePlayerState(playerState);
		return playerState;
	}

	private FullMap getFullMap(GameState serverGameState) {
		FullMap fullMap = converter.extractFullMap(serverGameState);
		return fullMap;
	}

	private void sendMove(EDirection direction) {
		waitForTurn();
		try {
			network.sendMove(converter.convertDirectionToServerMove(playerID, direction));
		} catch (InvalidNetworkActionException e) {
			logger.error("Issue with sending a move to the server.");
			System.err.println(e.getMessage());
		}
		waitForTurn();
		model.updateFullMap(getFullMap(this.getGameState()));
	}

	private void waitForTurn() {
		GameState gState = this.getGameState();
		EPlayerState state = getPlayerState(gState);
		while (state.equals(EPlayerState.WAIT)) {
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				System.err.println("Thread was interrupted!");
				e.printStackTrace();
			}
			GameState newGameState = this.getGameState();
			state = getPlayerState(newGameState);
			if (state.equals(EPlayerState.WON)) {
				model.updateFullMap(getFullMap(newGameState));
				model.updatePlayerState(state);
				System.exit(0);
			}
			model.updatePlayerState(state);
		}
	}

	private void prepareGameData() {
		waitForTurn();
		model.updateFullMap(getFullMap(this.getGameState()));
	}

	private void findTreasure() {
		boolean foundTreasure = false;
		List<Position> visitedFields = new ArrayList<>();
		FullMap map = model.getFullMap();
		Position myPlayerPosition = map.getPlayerPosition(EMapCharacter.MY_PLAYER);
		if (myPlayerPosition == null) {
			myPlayerPosition = map.getPlayerPosition(EMapCharacter.BOTH);
		}

		while (!foundTreasure) {
			GoalFinder goalFinder = new GoalFinder(myPlayerPosition);
			MountainScanner mountainScanner = new MountainScanner();
			Position randomGoal = goalFinder.findRandomGoal(map, visitedFields);
			PathFinder pathFinder = new PathFinder(map);
			List<Position> shortesPathToRandomgoal = pathFinder.getPathToGoal(myPlayerPosition, randomGoal);
			LinkedList<EDirection> directionToGoal = pathFinder.convertToMoves(shortesPathToRandomgoal);
			visitedFields.addAll(shortesPathToRandomgoal);

			shortesPathToRandomgoal.remove(0); // remove my starting position
			while (!myPlayerPosition.equals(randomGoal)) {
				EDirection nextMove = directionToGoal.get(0);
				while (!myPlayerPosition.equals(shortesPathToRandomgoal.get(0))) {
					waitForTurn();
					if (converter.extractTreasureState(getGameState(), playerID)) {
						model.updateTreasureState(true);
						foundTreasure = true;
						return;
					}
					sendMove(nextMove);
					myPlayerPosition = model.getFullMap().getPlayerPosition(EMapCharacter.MY_PLAYER);
					logger.debug("My player Position after send move: x{}, y{}", myPlayerPosition.getXCoordinate(),
							myPlayerPosition.getYCoordinate());
				}
				if (isTreasureVisible(model.getFullMap())) {
					Position treasurePosition = model.getFullMap().getTreasurePosition();
					moveToGoal(map, myPlayerPosition, treasurePosition);
					model.updateTreasureState(true);
					foundTreasure = true;
					return;
				}
				if (map.getPositionField(myPlayerPosition).equals(EField.MOUNTAIN)) {
					List<Position> surroundingEmptyGrassFields = mountainScanner.getSurroundingGrassFields(map,
							myPlayerPosition);
					visitedFields.addAll(surroundingEmptyGrassFields);
					if (surroundingEmptyGrassFields.contains(randomGoal)) {
						break;
					}
				}
				shortesPathToRandomgoal.remove(0);
				directionToGoal.remove(0);
			}
		}
	}

	private boolean isTreasureVisible(FullMap map) {
		if (map.getTreasurePosition() != null) {
			return true;
		}
		return false;
	}

	private void moveToGoal(FullMap map, Position myPosition, Position goal) {
		PathFinder pathFinder = new PathFinder(map);
		List<Position> shortesPathToRandomgoal = pathFinder.getPathToGoal(myPosition, goal);
		LinkedList<EDirection> directionToGoal = pathFinder.convertToMoves(shortesPathToRandomgoal);

		shortesPathToRandomgoal.remove(0); // remove my starting position
		while (!myPosition.equals(goal)) {
			EDirection nextMove = directionToGoal.get(0);
			while (!myPosition.equals(shortesPathToRandomgoal.get(0))) {
				sendMove(nextMove);
				myPosition = model.getFullMap().getPlayerPosition(EMapCharacter.MY_PLAYER);
				logger.debug("My player Position after send move: x{}, y{}", myPosition.getXCoordinate(),
						myPosition.getYCoordinate());
			}
			shortesPathToRandomgoal.remove(0);
			directionToGoal.remove(0);
		}
	}

	private List<Position> pathToEnemySide() {
		FullMap map = model.getFullMap();
		Position myPosition = map.getPlayerPosition(EMapCharacter.MY_PLAYER);
		GoalFinder enemySide = new GoalFinder(myPosition);
		Position randomEnemySidePosition = enemySide.getRandomEnemySideMountainField(map);
		logger.debug("Walking to enemy side, position is : x {}, y{}", randomEnemySidePosition.getXCoordinate(),
				randomEnemySidePosition.getYCoordinate());
		PathFinder pathFinderToEnemySide = new PathFinder(map);
		List<Position> pathToEnemy = pathFinderToEnemySide.getPathToGoal(myPosition, randomEnemySidePosition);
		List<EDirection> directionToPosition = pathFinderToEnemySide.convertToMoves(pathToEnemy);

		pathToEnemy.remove(0);

		while (!myPosition.equals(randomEnemySidePosition)) {

			EDirection nextMove = directionToPosition.get(0);
			while (!myPosition.equals(pathToEnemy.get(0))) {
				sendMove(nextMove);
				myPosition = model.getFullMap().getPlayerPosition(EMapCharacter.MY_PLAYER);
			}
			if (model.getFullMap().getCastlePosition(EMapCharacter.ENEMY) != null) {
				Position castlePosition = model.getFullMap().getCastlePosition(EMapCharacter.ENEMY);
				if (myPosition.equals(castlePosition)) {
					System.exit(0);
				}
				moveToGoal(map, myPosition, castlePosition);
				System.exit(0);
			}
			pathToEnemy.remove(0);
			directionToPosition.remove(0);
		}
		logger.debug("I finished moving to random enemy mountain");
		return pathToEnemy;
	}

	private void findEnemyCastle() {
		boolean gameEnded = false;
		List<Position> visitedFields = pathToEnemySide();
		FullMap map = model.getFullMap();
		Position myPlayerPosition = map.getPlayerPosition(EMapCharacter.MY_PLAYER);
		if (myPlayerPosition == null) {
			myPlayerPosition = map.getPlayerPosition(EMapCharacter.BOTH);
		}
		GoalFinder goalFinder = new GoalFinder(myPlayerPosition);
		MountainScanner mountainScanner = new MountainScanner();
		PathFinder pathFinder = new PathFinder(map);

		while (!gameEnded) {
			Position randomGoal = goalFinder.findRandomGoal(map, visitedFields);
			List<Position> shortesPathToRandomgoal = pathFinder.getPathToGoal(myPlayerPosition, randomGoal);
			LinkedList<EDirection> directionToGoal = pathFinder.convertToMoves(shortesPathToRandomgoal);

			visitedFields.addAll(shortesPathToRandomgoal);
			shortesPathToRandomgoal.remove(0); // remove my starting positions

			while (!myPlayerPosition.equals(randomGoal)) {
				EDirection nextMove = directionToGoal.get(0);
				while (!myPlayerPosition.equals(shortesPathToRandomgoal.get(0))) {
					sendMove(nextMove);
					myPlayerPosition = model.getFullMap().getPlayerPosition(EMapCharacter.MY_PLAYER);
				}
				if (model.getFullMap().getCastlePosition(EMapCharacter.ENEMY) != null) {
					Position castlePosition = model.getFullMap().getCastlePosition(EMapCharacter.ENEMY);
					if (myPlayerPosition.equals(castlePosition)) {
						model.updatePlayerState(getPlayerState(getGameState()));
						System.exit(0);
						return;
					}
					moveToGoal(map, myPlayerPosition, castlePosition);
					gameEnded = true;
					return;
				}
				if (map.getPositionField(myPlayerPosition).equals(EField.MOUNTAIN)) {
					List<Position> surroundingEmptyGrassFields = mountainScanner.getSurroundingGrassFields(map,
							myPlayerPosition);
					visitedFields.addAll(surroundingEmptyGrassFields);
					if (surroundingEmptyGrassFields.contains(randomGoal)) {
						break;
					}
				}
				shortesPathToRandomgoal.remove(0);
				directionToGoal.remove(0);
			}
		}
	}

}
