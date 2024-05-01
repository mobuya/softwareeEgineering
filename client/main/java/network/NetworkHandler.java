package network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import exceptions.InvalidNetworkActionException;
import messagesbase.ResponseEnvelope;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.ERequestState;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerMove;
import messagesbase.messagesfromclient.PlayerRegistration;
import messagesbase.messagesfromserver.GameState;
import reactor.core.publisher.Mono;

public class NetworkHandler {
	WebClient baseWebClient;
	private String gameID;
	private String playerID;

	Logger logger = LoggerFactory.getLogger(NetworkHandler.class);

	public NetworkHandler(String serverBaseUrl, String gameID) {
		if (gameID.length() < 5) {
			throw new InvalidNetworkActionException("GameID is not valid; cannoct connect to network.");
		}
		this.gameID = gameID;
		this.baseWebClient = WebClient.builder().baseUrl(serverBaseUrl + "/games")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE).build();
	}

	public UniquePlayerIdentifier registerPlayer() throws InvalidNetworkActionException {
		PlayerRegistration playerReg = new PlayerRegistration("Monika", "Abadzic", "abadzicm73");
		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.POST).uri("/" + this.gameID + "/players")
				.body(BodyInserters.fromValue(playerReg)).retrieve().bodyToMono(ResponseEnvelope.class);

		ResponseEnvelope<UniquePlayerIdentifier> resultReg = webAccess.block();

		if (resultReg.getState() == ERequestState.Error) {
			throw new InvalidNetworkActionException(resultReg.getExceptionMessage());
		}

		UniquePlayerIdentifier uniqueID = resultReg.getData().get();
		this.playerID = uniqueID.getUniquePlayerID();

		return uniqueID;
	}

	public void sendMap(PlayerHalfMap playerHalfMap) throws InvalidNetworkActionException {
		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.POST).uri("/" + this.gameID + "/halfmaps")
				.body(BodyInserters.fromValue(playerHalfMap)).retrieve().bodyToMono(ResponseEnvelope.class);
		ResponseEnvelope<UniquePlayerIdentifier> resultReg = webAccess.block();

		if (resultReg.getState() == ERequestState.Error) {
			throw new InvalidNetworkActionException(resultReg.getExceptionMessage());
		}
	}

	public GameState getGameState() throws InvalidNetworkActionException {
		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.GET)
				.uri("/" + this.gameID + "/states/" + this.playerID).retrieve().bodyToMono(ResponseEnvelope.class);
		ResponseEnvelope<GameState> requestResult = webAccess.block();

		GameState gameState = requestResult.getData().get();

		if (requestResult.getState() == ERequestState.Error) {
			throw new InvalidNetworkActionException(requestResult.getExceptionMessage());
		}
		return gameState;
	}

	public void sendMove(PlayerMove move) throws InvalidNetworkActionException {
		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.POST).uri("/" + this.gameID + "/moves")
				.body(BodyInserters.fromValue(move)).retrieve().bodyToMono(ResponseEnvelope.class);
		ResponseEnvelope<UniquePlayerIdentifier> resultReg = webAccess.block();

		if (resultReg.getState() == ERequestState.Error) {
			throw new InvalidNetworkActionException(resultReg.getExceptionMessage());
		}
	}
}
