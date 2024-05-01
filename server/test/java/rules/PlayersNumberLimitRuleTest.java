package rules;

import static org.mockito.ArgumentMatchers.anyString;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import game.Game;
import game.GameRegistry;
import messagesbase.UniqueGameIdentifier;
import messagesbase.messagesfromclient.PlayerRegistration;
import server.exceptions.ExceededNumberOfRegistrationsException;

class PlayersNumberLimitRuleTest {

	@Test
	@MethodSource("createPlayerRegisterInformation")
	void twoPlayersRegistered_ThirdPlayerWantstoRegister_ThrowExceededNumberOfRegistrationsException() {
		GameRegistry mockedRegistry = Mockito.mock(GameRegistry.class);
		Game mockedGame = Mockito.mock(Game.class);

		PlayerRegistration thirdPlayer = createPlayerRegisterInformation();
		IRule rule = new PlayersNumberLimitRule(mockedRegistry);
		UniqueGameIdentifier gameId = new UniqueGameIdentifier("12345");

		Mockito.when(mockedRegistry.getGame(anyString())).thenReturn(mockedGame);
		Mockito.when(mockedGame.limitOfPlayesExceeded()).thenReturn(true);

		Assertions.assertThrows(ExceededNumberOfRegistrationsException.class,
				() -> rule.validateRegister(gameId, thirdPlayer));
	}

	@Test
	@MethodSource("createPlayerRegisterInformation")
	void onePlayerRegistered_secondPLayerRegister_NoExceptionThrown() {
		GameRegistry mockedRegistry = Mockito.mock(GameRegistry.class);
		Game mockedGame = Mockito.mock(Game.class);

		PlayerRegistration secondPlayer = createPlayerRegisterInformation();
		IRule rule = new PlayersNumberLimitRule(mockedRegistry);
		UniqueGameIdentifier gameId = new UniqueGameIdentifier("12345");

		Mockito.when(mockedRegistry.getGame(anyString())).thenReturn(mockedGame);
		Mockito.when(mockedGame.limitOfPlayesExceeded()).thenReturn(false);

		Executable secondPlayerRegister = () -> {
			rule.validateRegister(gameId, secondPlayer);
		};

		Assertions.assertDoesNotThrow(secondPlayerRegister);
	}

	public static PlayerRegistration createPlayerRegisterInformation() {
		String firstName = "MockedName";
		String lastName = "MockedLastName";
		String uaccount = "mockedUacc";

		return new PlayerRegistration(firstName, lastName, uaccount);
	}

}
