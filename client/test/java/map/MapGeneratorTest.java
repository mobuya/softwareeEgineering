package map;

import java.util.LinkedHashMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;

class MapGeneratorTest {

	@RepeatedTest(value = 5)
	public void withMapGenerator_createValidMap_SizeOfMapShouldBe50() {
		MapGenerator generator = new MapGenerator();

		LinkedHashMap<Position, EField> newMapHalf = generator.createValidMap();

		Assertions.assertEquals(50, newMapHalf.size());
	}
}
