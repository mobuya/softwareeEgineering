package map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PositionTest {

	@Test
	void twoSamePositionCoordinates_comparePositions_ReturnTrue() {
		Position firstPosition = new Position(1, 1);
		Position secondPosition = new Position(1, 1);

		boolean samePositions = firstPosition.equals(secondPosition);

		Assertions.assertEquals(true, samePositions);
	}

	@Test
	void onePositionUnderTheOther_compareThem_ExpectedOtherIsBigger() {
		Position upperPosition = new Position(2, 2);
		Position underPosition = new Position(2, 3);

		int underIsBigger = underPosition.compareTo(upperPosition);

		Assertions.assertEquals(1, underIsBigger);

	}

	@Test
	void onePositionLeftToTheOther_compareThem_ExpectedFirstIsBigger() {
		Position targetposition = new Position(2, 2);
		Position leftPosition = new Position(1, 2);

		int targetIsBigger = targetposition.compareTo(leftPosition);

		Assertions.assertEquals(1, targetIsBigger);

	}

}
