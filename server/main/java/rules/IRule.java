package rules;

import messagesbase.UniqueGameIdentifier;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerRegistration;

public interface IRule {

	public void validateRegister(UniqueGameIdentifier gameID, PlayerRegistration playerRegistration);

	public void validateMap(UniqueGameIdentifier gameID, PlayerHalfMap halfMap);

	public void validateGameState(UniqueGameIdentifier gameID, UniquePlayerIdentifier playerID);
}
