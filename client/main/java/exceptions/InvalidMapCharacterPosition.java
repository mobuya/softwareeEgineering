package exceptions;

import map.EMapCharacter;
import map.Position;

public class InvalidMapCharacterPosition extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidMapCharacterPosition(String message) {
		super(message);
	}

	public InvalidMapCharacterPosition(EMapCharacter character, Position position) {
		super("Position x = " + position.getXCoordinate() + " , y = " + position.getYCoordinate()
				+ " for the character " + character.toString() + " is out of the map. ");
	}
}
