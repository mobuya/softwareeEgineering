package client.main;

import data.GameData;
import visualization.GameVisualizer;

public class MainClient {

	public static void main(String[] args) {
		if (args.length < 3) {
			System.err.println(
					"Wrong start parameters! They should be like this: TR https://swe1.wst.univie.ac.at/ <GameID>");
		}

		String serverBaseUrl = args[1];
		String gameId = args[2];

		GameData model = new GameData();
		GameProcess newGame = new GameProcess(model);
		GameVisualizer visuals = new GameVisualizer(newGame, model);
		newGame.startClient(serverBaseUrl, gameId);
	}

}
