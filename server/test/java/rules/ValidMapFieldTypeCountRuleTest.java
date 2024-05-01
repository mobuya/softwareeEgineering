package rules;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.MethodSource;

import messagesbase.UniqueGameIdentifier;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import server.exceptions.InvalidMapFieldCountException;

class ValidMapFieldTypeCountRuleTest {

	@Test
	@MethodSource("createMapWith5GrassFields")
	void mapHalfWithLessGrassFields_checkMapFieldCount_throwInvalidMapFieldCountException() {
		PlayerHalfMap halfMap = createMapWith5GrassFields();
		ValidMapFieldTypeCountRule rule = new ValidMapFieldTypeCountRule();
		UniqueGameIdentifier fakeGameID = new UniqueGameIdentifier("12345");

		Assertions.assertThrows(InvalidMapFieldCountException.class, () -> rule.validateMap(fakeGameID, halfMap));

	}

	@Test
	@MethodSource("createMapWith2WaterFields")
	void mapHalfWithLessWaterFields_checkMapFieldCount_throwInvalidMapFieldCountException() {
		PlayerHalfMap halfMap = createMapWith2WaterFields();
		ValidMapFieldTypeCountRule rule = new ValidMapFieldTypeCountRule();
		UniqueGameIdentifier fakeGameID = new UniqueGameIdentifier("12345");

		Assertions.assertThrows(InvalidMapFieldCountException.class, () -> rule.validateMap(fakeGameID, halfMap));

	}

	@Test
	@MethodSource("createMapWith2MountainFields")
	void mapHalfWithLessMountainFields_checkMapFieldCount_throwInvalidMapFieldCountException() {
		PlayerHalfMap halfMap = createMapWith2MountainFields();
		ValidMapFieldTypeCountRule rule = new ValidMapFieldTypeCountRule();
		UniqueGameIdentifier fakeGameID = new UniqueGameIdentifier("12345");

		Assertions.assertThrows(InvalidMapFieldCountException.class, () -> rule.validateMap(fakeGameID, halfMap));

	}

	private static PlayerHalfMap createMapWith5GrassFields() {
		Collection<PlayerHalfMapNode> nodes = new HashSet<>();
		Random random = new Random();
		final int X_LIMIT = 10;
		final int Y_LIMIT = 5;

		int grassCounter = 0;
		final int GRASS_COUNT_GOAL = 5;

		List<ETerrain> waterAndMountain = List.of(ETerrain.Water, ETerrain.Mountain);

		for (int xCoordinate = 0; xCoordinate < X_LIMIT; xCoordinate++) {
			for (int yCoordinate = 0; yCoordinate < Y_LIMIT; yCoordinate++) {
				ETerrain terrain = waterAndMountain.get(random.nextInt(waterAndMountain.size()));
				if (grassCounter < GRASS_COUNT_GOAL) {
					terrain = ETerrain.Grass;
					grassCounter++;
				}
				PlayerHalfMapNode mapNode = new PlayerHalfMapNode(xCoordinate, yCoordinate, terrain);
				nodes.add(mapNode);
			}
		}

		return new PlayerHalfMap("playerID", nodes);
	}

	private static PlayerHalfMap createMapWith2WaterFields() {
		Collection<PlayerHalfMapNode> nodes = new HashSet<>();
		Random random = new Random();
		final int X_LIMIT = 10;
		final int Y_LIMIT = 5;

		List<ETerrain> grassAndMountain = List.of(ETerrain.Grass, ETerrain.Mountain);

		for (int xCoordinate = 0; xCoordinate < X_LIMIT; xCoordinate++) {
			for (int yCoordinate = 0; yCoordinate < Y_LIMIT; yCoordinate++) {
				ETerrain terrain = grassAndMountain.get(random.nextInt(grassAndMountain.size()));

				if ((xCoordinate == 0 && yCoordinate == 0) || (xCoordinate == 2 && yCoordinate == 2)) {
					// random choosen coordinates (no meaning)
					terrain = ETerrain.Water;
				}

				PlayerHalfMapNode mapNode = new PlayerHalfMapNode(xCoordinate, yCoordinate, terrain);
				nodes.add(mapNode);
			}
		}

		return new PlayerHalfMap("playerID", nodes);
	}

	private static PlayerHalfMap createMapWith2MountainFields() {
		Collection<PlayerHalfMapNode> nodes = new HashSet<>();
		Random random = new Random();
		final int X_LIMIT = 10;
		final int Y_LIMIT = 5;

		List<ETerrain> grassAndWater = List.of(ETerrain.Grass, ETerrain.Water);

		for (int xCoordinate = 0; xCoordinate < X_LIMIT; xCoordinate++) {
			for (int yCoordinate = 0; yCoordinate < Y_LIMIT; yCoordinate++) {
				ETerrain terrain = grassAndWater.get(random.nextInt(grassAndWater.size()));

				if ((xCoordinate == 0 && yCoordinate == 0) || (xCoordinate == 2 && yCoordinate == 2)) {
					// random choosen coordinates (no meaning)
					terrain = ETerrain.Mountain;
				}

				PlayerHalfMapNode mapNode = new PlayerHalfMapNode(xCoordinate, yCoordinate, terrain);
				nodes.add(mapNode);
			}
		}

		return new PlayerHalfMap("playerID", nodes);
	}

}
