package game.data;

import java.util.Random;
import java.util.UUID;

public class IdGenerator {

	public String generateRandomGameID() {
		Random random = new Random();
		final int REQUIRED_GAMEID_LENGHT = 5;

		String elementsForGameID = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder gameIdBuilder = new StringBuilder();

		for (int gameIDLength = 0; gameIDLength < REQUIRED_GAMEID_LENGHT; gameIDLength++) {
			int randomElementIndex = random.nextInt(elementsForGameID.length());
			char randomCharacter = elementsForGameID.charAt(randomElementIndex);
			gameIdBuilder.append(randomCharacter);
		}

		return gameIdBuilder.toString();
	}

	public String generateRandomPlayerID() {
		return UUID.randomUUID().toString();

	}

	public String generateRandomGameStateId() {
		return UUID.randomUUID().toString();
	}
}
