package rules;

import java.util.Collection;
import java.util.HashSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.MethodSource;

import messagesbase.UniqueGameIdentifier;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import server.exceptions.MapContainsUnreachableFieldException;

class MapContainsNoUnreachableFieldsRuleTest {

	@Test
	@MethodSource("createMapWithIsland")
	void halfMapContaintsIsland_chechIfMapContainsUnreachableField_ThrowMapContainsUnreachableFieldException() {
		PlayerHalfMap invalidMap = createMapWithIsland();
		HalfMapContainsNoUnreachableFieldsRule rule = new HalfMapContainsNoUnreachableFieldsRule();
		UniqueGameIdentifier fakeGameID = new UniqueGameIdentifier("12345");

		Assertions.assertThrows(MapContainsUnreachableFieldException.class,
				() -> rule.validateMap(fakeGameID, invalidMap));
	}

	@Test
	@MethodSource("createMapWithIslandOnBorder")
	void halfMapContaintsIslandOnBorder_chechIfMapContainsUnreachableField_ThrowMapContainsUnreachableFieldException() {
		PlayerHalfMap invalidMap = createMapWithIslandOnBorder();
		HalfMapContainsNoUnreachableFieldsRule rule = new HalfMapContainsNoUnreachableFieldsRule();
		UniqueGameIdentifier fakeGameID = new UniqueGameIdentifier("12345");

		Assertions.assertThrows(MapContainsUnreachableFieldException.class,
				() -> rule.validateMap(fakeGameID, invalidMap));
	}

	@Test
	@MethodSource("createMapWithNoIsland")
	void halfMapContaintsNoIsland_chechIfMapContainsUnreachableField_NoExceptionThrown() {
		PlayerHalfMap halfMap = createMapWithNoIsland();
		HalfMapContainsNoUnreachableFieldsRule rule = new HalfMapContainsNoUnreachableFieldsRule();
		UniqueGameIdentifier fakeGameID = new UniqueGameIdentifier("12345");

		Assertions.assertDoesNotThrow(() -> rule.validateMap(fakeGameID, halfMap));
	}

	private static PlayerHalfMap createMapWithIsland() {
		Collection<PlayerHalfMapNode> nodes = new HashSet<>();
		for (int xCoordinate = 0; xCoordinate < 10; xCoordinate++) {
			for (int yCoordinate = 0; yCoordinate < 5; yCoordinate++) {
				ETerrain terrain = ETerrain.Grass;

				if ((yCoordinate == 1 || yCoordinate == 3)
						&& (xCoordinate == 2 || xCoordinate == 3 || xCoordinate == 4)) {
					terrain = ETerrain.Water;
				}

				if (yCoordinate == 2 && (xCoordinate == 1 || xCoordinate == 5)) {
					terrain = ETerrain.Water;
				}

				PlayerHalfMapNode mapNode = new PlayerHalfMapNode(xCoordinate, yCoordinate, terrain);
				nodes.add(mapNode);
			}
		}
		return new PlayerHalfMap("playerID", nodes);
	}

	private static PlayerHalfMap createMapWithNoIsland() {
		Collection<PlayerHalfMapNode> nodes = new HashSet<>();

		for (int xCoordinate = 0; xCoordinate < 10; xCoordinate++) {
			for (int yCoordinate = 0; yCoordinate < 5; yCoordinate++) {

				PlayerHalfMapNode mapNode = new PlayerHalfMapNode(xCoordinate, yCoordinate, ETerrain.Grass);
				nodes.add(mapNode);
			}
		}
		return new PlayerHalfMap("playerID", nodes);
	}

	private static PlayerHalfMap createMapWithIslandOnBorder() {
		Collection<PlayerHalfMapNode> nodes = new HashSet<>();

		for (int xCoordinate = 0; xCoordinate < 10; xCoordinate++) {
			for (int yCoordinate = 0; yCoordinate < 5; yCoordinate++) {

				ETerrain terrain = ETerrain.Grass;

				if ((yCoordinate == 0) && (xCoordinate == 1 || xCoordinate == 5)) {
					terrain = ETerrain.Water;
				}

				if (yCoordinate == 1 && (xCoordinate == 2 || xCoordinate == 3) || xCoordinate == 4) {
					terrain = ETerrain.Water;
				}

				PlayerHalfMapNode mapNode = new PlayerHalfMapNode(xCoordinate, yCoordinate, terrain);
				nodes.add(mapNode);
			}
		}
		return new PlayerHalfMap("playerID", nodes);
	}

}
