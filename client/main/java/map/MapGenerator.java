package map;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.util.internal.ThreadLocalRandom;
import messagesbase.messagesfromclient.ETerrain;

public class MapGenerator {
	private final Logger logger = LoggerFactory.getLogger(MapGenerator.class);

	private LinkedHashMap<Position, EField> createNewHalfMap() {
		LinkedHashMap<Position, EField> halfMap = new LinkedHashMap<>();
		final int X_LIMIT = 10;
		final int Y_LIMIT = 5;

		for (int yCounter = 0; yCounter < Y_LIMIT; yCounter++) {
			for (int xCounter = 0; xCounter < X_LIMIT; xCounter++) {
				Position newPosition = new Position(xCounter, yCounter);
				EField field = EField.values()[new Random().nextInt(EField.values().length)];
				/*
				 * even number y coordinates dont have water fields to prevent unreachable
				 * fields
				 * 
				 */
				while ((yCounter) % 2 != 0 && field.equals(EField.WATER)) {
					field = EField.values()[new Random().nextInt(ETerrain.values().length)];
				}
				halfMap.put(newPosition, field);
			}
		}
		return halfMap;
	}

	private LinkedHashMap<Position, EField> validateMap(LinkedHashMap<Position, EField> halfMap) {
		MapValidator validator = new MapValidator();

		boolean isMapValid = validator.invalidateHalfMap(halfMap);
		LinkedHashMap<Position, EField> newHalfMap = new LinkedHashMap<>();
		int regeneratedCounter = 0;

		while (!isMapValid) {
			newHalfMap = createNewHalfMap();
			isMapValid = validator.invalidateHalfMap(newHalfMap);
			regeneratedCounter++;
		}
		this.placeCastle(newHalfMap);
		logger.info("Map was regenerated " + regeneratedCounter + " times before it was valid.");
		return newHalfMap;

	}

	private void placeCastle(LinkedHashMap<Position, EField> halfMap) {
		int findPosition = 0;
		int randomPosition = ThreadLocalRandom.current().nextInt(0, 49 + 1);
		for (Map.Entry<Position, EField> field : halfMap.entrySet()) {
			if (findPosition == randomPosition) {
				if (field.getValue() == EField.GRASS) {
					field.getKey().putCastle();
				} else {
					randomPosition++;
				}
			}
			findPosition++;
		}
	}

	public LinkedHashMap<Position, EField> createValidMap() {
		MapValidator validator = new MapValidator();
		LinkedHashMap<Position, EField> testMap = this.createNewHalfMap();
		LinkedHashMap<Position, EField> validMap = this.validateMap(testMap);

		boolean fortPresent = validator.checkIfCastlePresent(validMap);
		while (!fortPresent) {
			logger.warn("No castle found on the map!");
			placeCastle(validMap);
			fortPresent = validator.checkIfCastlePresent(validMap);
		}

		return validMap;

	}
}
