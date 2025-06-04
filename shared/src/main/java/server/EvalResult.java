package server;

import model.GameData;

public record EvalResult(String result, String authToken, GameData game) {
}
