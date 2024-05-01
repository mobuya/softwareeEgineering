package game.data.map;

import java.util.LinkedHashMap;
import java.util.Map;

import game.data.map.constants.EField;

public class ServerHalfMap {

	private Map<Position, EField> baseMap = new LinkedHashMap<>();
	private Position castlePosition = new Position();
	// castle position is the players start position

	public ServerHalfMap(Map<Position, EField> baseMap, Position castlePosition) {
		this.baseMap = baseMap;
		this.castlePosition = castlePosition;
	}

	public Map<Position, EField> getBaseMap() {
		return baseMap;
	}

	public Position getCastlePosition() {
		return castlePosition;
	}

	public int getMapSize() {
		return this.baseMap.size();
	}

}
