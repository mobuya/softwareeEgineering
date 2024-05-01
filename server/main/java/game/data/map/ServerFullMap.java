package game.data.map;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.data.map.constants.EField;
import game.data.map.constants.EMapEntities;
import game.data.map.constants.EMapHalfPosition;
import game.data.map.constants.EMapLayout;

public class ServerFullMap {
	private Map<Position, EField> baseMap = new LinkedHashMap<>();
	private EMapLayout layout;
	private Map<EMapEntities, Position> entitiesPosition = new LinkedHashMap<>();
	private Map<EMapEntities, Position> castlePosition = new LinkedHashMap<>();
	private Map<ServerHalfMap, EMapHalfPosition> mapHalvesPositions = new LinkedHashMap<>();
	private Position treasurePosition = new Position();
	private final Logger logger = LoggerFactory.getLogger(ServerFullMap.class);

	public ServerFullMap(ServerHalfMap oneHalf, ServerHalfMap secondHalf) {
		combineMapHalves(oneHalf, secondHalf);
	}

	public ServerFullMap() {
		// default map -> empty map
	}

	public int getMapSize() {
		return this.baseMap.size();
	}

	private ServerFullMap(Map<Position, EField> baseMap, EMapLayout layout) {
		this.layout = layout;
		this.baseMap = baseMap;
	}

	public Map<Position, EField> getBaseMap() {
		return this.baseMap;
	}

	public Position getEntityPosition(EMapEntities entity) {
		return this.entitiesPosition.get(entity);
	}

	public EMapEntities findEntityOnPosition(Position position) {
		EMapEntities entity = EMapEntities.UNKNOWN;
		for (var eachEntity : entitiesPosition.entrySet()) {
			if (eachEntity.getValue().equals(position)) {
				entity = eachEntity.getKey();
			}
		}
		return entity;
	}

	public EMapEntities findCastleOnPosition(Position position) {
		EMapEntities entity = EMapEntities.UNKNOWN;
		for (var eachEntity : castlePosition.entrySet()) {
			if (eachEntity.getValue().equals(position)) {
				entity = eachEntity.getKey();
				break;
			}
		}
		return entity;
	}

	public void combineMapHalves(ServerHalfMap firstHalf, ServerHalfMap secondHalf) {
		final int VERTICAL_MAP_GAP_VALUE = 5;
		final int HORIZONTAL_MAP_GAP = 10;

		EMapLayout layout = EMapLayout.values()[new Random().nextInt(EMapLayout.values().length)];
		this.layout = layout;

		Map<Position, EField> baseMap = new LinkedHashMap<>();
		Map<Position, EField> addedHalf = new LinkedHashMap<>();

		int xCoordinateAdapter = 0;
		int yCoordinateAdapter = 0;

		if (layout.equals(EMapLayout.HORIZONTAL)) {
			baseMap = secondHalf.getBaseMap();
			addedHalf = firstHalf.getBaseMap();
			this.mapHalvesPositions.put(firstHalf, EMapHalfPosition.SHIFTED);
			this.mapHalvesPositions.put(secondHalf, EMapHalfPosition.ORIGINAL);
			xCoordinateAdapter = HORIZONTAL_MAP_GAP;

		} else {
			baseMap = firstHalf.getBaseMap();
			addedHalf = secondHalf.getBaseMap();
			this.mapHalvesPositions.put(secondHalf, EMapHalfPosition.SHIFTED);
			this.mapHalvesPositions.put(firstHalf, EMapHalfPosition.ORIGINAL);
			yCoordinateAdapter = VERTICAL_MAP_GAP_VALUE;
		}

		for (var eachTile : addedHalf.entrySet()) {
			int newXValue = eachTile.getKey().getXCoordinate() + xCoordinateAdapter;
			int nexYValue = eachTile.getKey().getYCoordinate() + yCoordinateAdapter;
			Position position = new Position(newXValue, nexYValue);
			EField field = eachTile.getValue();
			baseMap.put(position, field);
		}

		this.baseMap = baseMap;
	}

	public Position getTreasurePosition() {
		// TODO place treasure
		return this.treasurePosition;
	}

	private void placeTreasure(EMapHalfPosition mapHalfPosition) {
		Position emptyPosition = new Position();

		if (this.treasurePosition.equals(emptyPosition)) {
			Position randomTreasurePosition = new Position();

			if (this.layout.equals(EMapLayout.HORIZONTAL)) {
				randomTreasurePosition = randomPositionOnHorizontalLayout(mapHalfPosition);
			} else {
				randomTreasurePosition = randomPositionOnVerticalLayout(mapHalfPosition);
			}

			EField treasureField = this.baseMap.get(randomTreasurePosition);

			while (!treasureField.equals(EField.GRASS)) {
				if (this.layout.equals(EMapLayout.HORIZONTAL)) {
					randomTreasurePosition = randomPositionOnHorizontalLayout(mapHalfPosition);
				} else {
					randomTreasurePosition = randomPositionOnVerticalLayout(mapHalfPosition);
				}
				treasureField = this.baseMap.get(randomTreasurePosition);
			}

			logger.info("The treasure Position is : x {} and y {}", randomTreasurePosition.getXCoordinate(),
					randomTreasurePosition.getYCoordinate());
			this.treasurePosition = randomTreasurePosition;

		}
	}

	public ServerFullMap makeFullMapForPlayer(ServerHalfMap oneHalf) {
		if (!this.baseMap.isEmpty()) {
			ServerFullMap playerMap = new ServerFullMap(this.baseMap, this.layout);
			Position playerStartPosition = oneHalf.getCastlePosition();

			EMapHalfPosition mapHalfPosition = this.mapHalvesPositions.get(oneHalf);

			int xCoordinateShift = 0;
			int yCoordinateShift = 0;

			final int HORIZONTAL_X_COORDINATE_SHIFT = 10;
			final int VERTICAL_Y_COORDINATE_SHIFT = 5;

			if (mapHalfPosition.equals(EMapHalfPosition.SHIFTED)) {
				if (layout.equals(EMapLayout.HORIZONTAL)) {
					xCoordinateShift = HORIZONTAL_X_COORDINATE_SHIFT;
				} else {
					yCoordinateShift = VERTICAL_Y_COORDINATE_SHIFT;
				}
			}

			Position playerPosition = new Position(playerStartPosition.getXCoordinate() + xCoordinateShift,
					playerStartPosition.getYCoordinate() + yCoordinateShift);

			playerMap.entitiesPosition.put(EMapEntities.MY_PLAYER, playerPosition);
			playerMap.castlePosition.put(EMapEntities.MY_PLAYER, playerPosition);

			Position enemyPosition = new Position();
			if (layout.equals(EMapLayout.HORIZONTAL)) {
				enemyPosition = randomPositionOnHorizontalLayout(mapHalfPosition.getOpposite());
			} else {
				enemyPosition = randomPositionOnVerticalLayout(mapHalfPosition.getOpposite());
			}

			playerMap.entitiesPosition.put(EMapEntities.ENEMY, enemyPosition);
			playerMap.placeTreasure(mapHalfPosition);

			return playerMap;
		} else {
			logger.info("The maps are not combined; can't show full map data for players.");
			return this;
		}
	}

	private Position randomPositionOnHorizontalLayout(EMapHalfPosition mapHalfPosition) {
		final Random random = new Random();
		final int HORIZONTAL_Y_LIMIT = 5;

		final int SHIFTED_MAP_X_LIMIT = 20;
		final int ORIGINAL_MAP_X_LIMIT = 5;

		int randomXCoordinate = 0;
		int randomYCoordinate = random.nextInt(0, HORIZONTAL_Y_LIMIT);

		if (mapHalfPosition.equals(EMapHalfPosition.ORIGINAL)) {
			randomXCoordinate = random.nextInt(0, ORIGINAL_MAP_X_LIMIT);
		} else {
			final int SHIFTED_MAP_X_START = 10;
			randomXCoordinate = random.nextInt(SHIFTED_MAP_X_START, SHIFTED_MAP_X_LIMIT);
		}

		return new Position(randomXCoordinate, randomYCoordinate);
	}

	private Position randomPositionOnVerticalLayout(EMapHalfPosition mapHalfPosition) {
		final Random random = new Random();
		final int VERTICAL_X_LIMIT = 10;

		final int SHIFTED_MAP_Y_LIMIT = 10;
		final int ORIGINAL_MAP_Y_LIMIT = 5;

		int randomXCoordinate = random.nextInt(0, VERTICAL_X_LIMIT);
		int randomYCoordinate = 0;

		if (mapHalfPosition.equals(EMapHalfPosition.ORIGINAL)) {
			randomYCoordinate = random.nextInt(0, ORIGINAL_MAP_Y_LIMIT);
		} else {
			final int SHIFTED_MAP_Y_START = 5;
			randomYCoordinate = random.nextInt(SHIFTED_MAP_Y_START, SHIFTED_MAP_Y_LIMIT);
		}

		return new Position(randomXCoordinate, randomYCoordinate);
	}

}
