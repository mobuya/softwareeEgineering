package data;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.beans.PropertyChangeListener;
import java.util.LinkedHashMap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import map.EField;
import map.FullMap;
import map.Position;

class GameDataTest {
	private static GameData data = new GameData();
	private static PropertyChangeListener mockedListener;

	@BeforeAll
	public static void setUpBeforeClass() {
		mockedListener = Mockito.mock(PropertyChangeListener.class);
		data.addPropertyChangeListener(mockedListener);
	}

	@Test
	void gameIDAdded_updateModel_PropertychangeFired() {
		data.updateGameID("newGameId");
		verify(mockedListener, atLeastOnce()).propertyChange(any());
	}

	@Test
	void playerIDAdded_updateModel_PropertychangeFired() {
		data.updatePlayerID("newPlayerID");
		verify(mockedListener, atLeastOnce()).propertyChange(any());
	}

	@Test
	void playerStateUpdated_updateModel_PropertychangeFired() {
		data.updatePlayerState(EPlayerState.SEND_ACTION);
		verify(mockedListener, atLeastOnce()).propertyChange(any());
	}

	@Test
	void playerHalfMapupdated_updateModel_PropertychangeFired() {
		LinkedHashMap<Position, EField> halfMap = new LinkedHashMap<>();
		data.updatePlayerHalfMap(halfMap);
		verify(mockedListener, atLeastOnce()).propertyChange(any());
	}

	@Test
	@MethodSource("createVericalMapWithGrass")
	void fullMapUpdated_updateModel_PropertychangeFired() {
		FullMap fullMap = createVericalMapWithGrass();
		data.updateFullMap(fullMap);
		verify(mockedListener, atLeastOnce()).propertyChange(any());
	}

	@Test
	void treasureIsCollected_updateModel_PropertychangeFired() {
		data.updateTreasureState(true);
		verify(mockedListener, atLeastOnce()).propertyChange(any());
	}

	private static FullMap createVericalMapWithGrass() {
		LinkedHashMap<Position, EField> map = new LinkedHashMap<>();
		final int X_LIMIT = 10;
		final int Y_LIMIT = 10;

		for (int yCounter = 0; yCounter < Y_LIMIT; yCounter++) {
			for (int xCounter = 0; xCounter < X_LIMIT; xCounter++) {
				Position newPosition = new Position(xCounter, yCounter);
				EField field = EField.GRASS;
				map.put(newPosition, field);
			}
		}
		return new FullMap(map);
	}

}
