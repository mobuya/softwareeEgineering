package server.main;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import game.GameRegistry;
import game.data.DataConverter;
import game.data.map.ServerHalfMap;
import messagesbase.ResponseEnvelope;
import messagesbase.UniqueGameIdentifier;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerRegistration;
import messagesbase.messagesfromserver.GameState;
import rules.CastlePositionIsGrassRule;
import rules.HalfMapCanBeSentOnceRule;
import rules.HalfMapContainsNoUnreachableFieldsRule;
import rules.HalfMapContainsOneCastleRule;
import rules.IRule;
import rules.PlayersNumberLimitRule;
import rules.ValidGameIDRule;
import rules.ValidHalfMapCoordinatesRule;
import rules.ValidHalfMapSizeRule;
import rules.ValidMapFieldTypeCountRule;
import rules.ValidPlayerIDRule;
import rules.WaterFieldsOnBorderCountRule;
import server.exceptions.GenericExampleException;

@RestController
@RequestMapping(value = "/games")
public class ServerEndpoints {
	private final Logger logger = LoggerFactory.getLogger(ServerEndpoints.class);
	private final GameRegistry registry = new GameRegistry();
	private final DataConverter converter = new DataConverter();

	private final List<IRule> gameRules = List.of(new ValidGameIDRule(registry), new PlayersNumberLimitRule(registry),
			new ValidPlayerIDRule(registry), new HalfMapCanBeSentOnceRule(registry), new ValidHalfMapSizeRule(),
			new ValidMapFieldTypeCountRule(), new WaterFieldsOnBorderCountRule(), new HalfMapContainsOneCastleRule(),
			new CastlePositionIsGrassRule(), new ValidHalfMapCoordinatesRule(),
			new HalfMapContainsNoUnreachableFieldsRule());

	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody UniqueGameIdentifier newGame(
			@RequestParam(required = false, defaultValue = "false", value = "enableDebugMode") boolean enableDebugMode,
			@RequestParam(required = false, defaultValue = "false", value = "enableDummyCompetition") boolean enableDummyCompetition) {

		return converter.extractUniqueGameID(registry.createNewGame());
	}

	@RequestMapping(value = "/{gameID}/players", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<UniquePlayerIdentifier> registerPlayer(
			@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @RequestBody PlayerRegistration playerRegistration) {

		for (IRule rule : gameRules) {
			try {
				rule.validateRegister(gameID, playerRegistration);
			} catch (GenericExampleException exception) {
				logger.warn("Exception was thrown after validating the Register information.");
				throw exception;
			}
		}

		String newPlayerID = registry.registerPlayerForGame(gameID.getUniqueGameID(),
				converter.extractPlayerInfoFromRegistration(playerRegistration));
		ResponseEnvelope<UniquePlayerIdentifier> playerIDMessage = new ResponseEnvelope<>(
				new UniquePlayerIdentifier(newPlayerID));

		registry.preparePlayerToSendMap(gameID.getUniqueGameID());

		return playerIDMessage;
	}

	@RequestMapping(value = "/{gameID}/halfmaps", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<?> recieveHalfMap(@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @RequestBody PlayerHalfMap halfMap) {

		for (IRule rule : gameRules) {
			try {
				rule.validateMap(gameID, halfMap);
			} catch (GenericExampleException exception) {
				registry.handlePlayerRuleViolation(gameID.getUniqueGameID(), halfMap.getUniquePlayerID());
				logger.warn("Exception was thrown after Client sent a Map Half.");
				throw exception;
			}
		}

		String playerID = halfMap.getUniquePlayerID();
		ServerHalfMap sHalfMap = converter.convertClientMapToServerMap(halfMap);
		registry.processRecievedMapHalf(gameID.getUniqueGameID(), sHalfMap, playerID);

		ResponseEnvelope<?> responseFromServer = new ResponseEnvelope<Object>();

		return responseFromServer;
	}

	@RequestMapping(value = "/{gameID}/states/{playerID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<GameState> returnGameState(
			@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @PathVariable UniquePlayerIdentifier playerID) {

		for (IRule rule : gameRules) {
			try {
				rule.validateGameState(gameID, playerID);
			} catch (GenericExampleException exception) {
				logger.warn("Exception was thrown after Client requested a GameState.");
				throw exception;
			}
		}

		GameState currentGameState = converter.convertGameToGameState(registry.getGame(gameID.getUniqueGameID()),
				playerID);
		return new ResponseEnvelope<>(currentGameState);
	}

	/*
	 * 
	 * 
	 * 
	 * 
	 */

	@ExceptionHandler({ GenericExampleException.class })
	public @ResponseBody ResponseEnvelope<?> handleException(GenericExampleException ex, HttpServletResponse response) {
		ResponseEnvelope<?> result = new ResponseEnvelope<>(ex.getErrorName(), ex.getMessage());

		response.setStatus(HttpServletResponse.SC_OK);
		return result;
	}
}
