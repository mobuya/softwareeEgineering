package visualization;

import java.beans.PropertyChangeListener;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.main.GameProcess;
import data.EPlayerState;
import data.GameData;
import map.EField;
import map.EMapCharacter;
import map.FullMap;
import map.Position;

public class GameVisualizer {
	private boolean mapDisplayed = false;
	Logger logger = LoggerFactory.getLogger(GameVisualizer.class);

	public GameVisualizer(GameProcess cntr, GameData model) {
		model.addPropertyChangeListener(modelChangedListener);
	}

	final PropertyChangeListener modelChangedListener = event -> {
		String changedValue = event.getPropertyName();

		switch (changedValue) {
		case "gameID":
			showGameID(event.getNewValue());
			break;
		case "playerID":
			showPlayerID(event.getNewValue());
			break;
		case "halfMap":
			showHalfMap(event.getNewValue());
			mapDisplayed = true;
			break;
		case "playerState":
			this.showGameState(event.getNewValue());
			break;
		case "fullMap":
			this.showFullMap(event.getNewValue());
			break;
		case "treasureState":
			this.showTreasurestate(event.getNewValue());
			break;
		}
	};

	private void showTreasurestate(Object newTresureState) {
		if ((boolean) newTresureState) {
			System.out.println("Great! You found the treasure! Run to the other map half!");
		}
	}

	private void showGameID(Object newGameID) {
		if (!(newGameID instanceof String)) {
			logger.error("The object for updating the PlayerID is not the valid type.");
			return;
		}
		String gameID = (String) newGameID;
		System.out.println("The GameID is: " + gameID + '\n');
	}

	private void showPlayerID(Object newPlayerID) {
		if (!(newPlayerID instanceof String)) {
			logger.error("The object for updating the PlayerID is not the valid type.");
			return;
		}
		String playerID = (String) newPlayerID;
		System.out.println("Your PlayerID is: " + playerID + '\n');
	}

	private void showHalfMap(Object newHalfMap) {
		if (!(newHalfMap instanceof LinkedHashMap)) {
			logger.error("The object for updating the HalfMap is not the valid type.");
			return;
		}
		System.out.println("Your map half: \n");
		LinkedHashMap<Position, EField> halfMap = (LinkedHashMap<Position, EField>) newHalfMap;
		final String ANSI_RESET = "\u001B[0m";
		final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
		final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
		final String ANSI_BROWN_BACKGROUND = "\u001B[43m";
		final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";

		for (Map.Entry<Position, EField> field : halfMap.entrySet()) {
			EField terrain = field.getValue();
			if (field.getKey().hasMyCastle()) {
				System.out.print(ANSI_PURPLE_BACKGROUND + " C " + ANSI_RESET);
			} else {
				switch (terrain) {
				case WATER:
					System.out.print(ANSI_BLUE_BACKGROUND + "   " + ANSI_RESET);
					break;
				case GRASS:
					System.out.print(ANSI_GREEN_BACKGROUND + "   " + ANSI_RESET);
					break;
				case MOUNTAIN:
					System.out.print(ANSI_BROWN_BACKGROUND + "   " + ANSI_RESET);
					break;
				}
			}
			if (field.getKey().getXCoordinate() == 9) {
				System.out.print('\n');
			}
		}
		System.out.println("\n");
	}

	private void showGameState(Object gameState) {
		if (!(gameState instanceof EPlayerState)) {
			logger.error("The object for updating the Player State is not the valid type.");
			return;
		}
		EPlayerState state = (EPlayerState) gameState;
		switch (state) {
		case SEND_ACTION:
			if (mapDisplayed == true) {
				System.out.println("It's your turn to send a move! \n");
			} else {
				System.out.println("It's your turn to send a map half! \n");
			}
			break;
		case WAIT:
			System.out.println("Waiting for the other player to send an action... \n");
			break;
		case WON:
			System.out.println("HURRAY! You won!!! :D \n");
			break;
		case LOST:
			System.out.println("Oh no, you lost. Better luck next time! :) \n");
			System.exit(0);
			break;
		}
	}

	private void showFullMap(Object fullMap) {
		if (!(fullMap instanceof FullMap)) {
			logger.error("The FullMap is not the correct Object type, cannot display it.");
		} else {
			FullMap newFullMap = (FullMap) fullMap;
			LinkedHashMap<Position, EField> baseFullMap = newFullMap.getBaseMap();
			TreeMap<Position, EField> helperMap = new TreeMap<Position, EField>(baseFullMap);

			boolean isHorizontal = newFullMap.checkIfMapHotizontal();

			final String ANSI_RESET = "\u001B[0m";
			final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
			final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
			final String ANSI_BROWN_BACKGROUND = "\u001B[43m";
			final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
			final String ANSI_RED_BACKGROUND = "\u001B[41m";
			final String ANSI_YELLOW_TEXT = "\u001B[33m";

			for (Map.Entry<Position, EField> mapTile : helperMap.entrySet()) {
				String output = "";
				EField terrain = mapTile.getValue();
				switch (terrain) {
				case WATER:
					output = ANSI_BLUE_BACKGROUND + "   " + ANSI_RESET;
					break;
				case GRASS:
					output = ANSI_GREEN_BACKGROUND + "   " + ANSI_RESET;
					break;
				case MOUNTAIN:
					output = ANSI_BROWN_BACKGROUND + "   " + ANSI_RESET;
					break;
				}

				if (mapTile.getKey().equals(newFullMap.getCastlePosition(EMapCharacter.MY_PLAYER))) {
					output = ANSI_PURPLE_BACKGROUND + " C " + ANSI_RESET;
				}
				if (mapTile.getKey().equals(newFullMap.getCastlePosition(EMapCharacter.ENEMY))) {
					output = ANSI_RED_BACKGROUND + "[C]" + ANSI_RESET;
				}
				if (mapTile.getKey().equals(newFullMap.getTreasurePosition())) {
					output = ANSI_GREEN_BACKGROUND + ANSI_YELLOW_TEXT + "$$$" + ANSI_RESET;
				}

				if (mapTile.getKey().equals(newFullMap.getPlayerPosition(EMapCharacter.MY_PLAYER))) {
					output = ANSI_PURPLE_BACKGROUND + " P " + ANSI_RESET;
				}
				if (mapTile.getKey().equals(newFullMap.getPlayerPosition(EMapCharacter.ENEMY))) {
					output = ANSI_RED_BACKGROUND + " E " + ANSI_RESET;

				}
				if (mapTile.getKey().equals(newFullMap.getPlayerPosition(EMapCharacter.BOTH))) {
					output = " X ";
				}
				System.out.print(output);
				if (isHorizontal && mapTile.getKey().getXCoordinate() == 19) {
					System.out.print('\n');
				}
				if ((!isHorizontal) && mapTile.getKey().getXCoordinate() == 9) {
					System.out.print('\n');
				}
			}
		}
		System.out.println('\n');
	}

}
