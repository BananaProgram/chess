package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.Request;
import reqres.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResult register(RegisterRequest req) {
        var path = "/user";
        return this.makeRequest("POST", path, req, null, RegisterResult.class);
    }

    public LoginResult login(LoginRequest req) {
        var path = "/session";
        return this.makeRequest("POST", path, req, null, LoginResult.class);
    }

    public ErrorMessage logout(String authToken) {
        var path = "/session";
        return this.makeRequest("DELETE", path, null, "authorization: " + authToken, ErrorMessage.class);
    }

    public NewGameResult create(NewGameRequest req, String authToken) {
        var path = "/game";
        return this.makeRequest("POST", path, req, "authorization: " + authToken, NewGameResult.class);
    }

    public ListGamesResult list(String authToken) {
        var path = "/game";
        return this.makeRequest("GET", path, null, "authorization: " + authToken, ListGamesResult.class);
    }

    public ErrorMessage join(JoinRequest req, String authToken) {
        var path = "/game";
        return this.makeRequest("PUT", path, req, "authorization: " + authToken, ErrorMessage.class);
    }

    private <T> T makeRequest(String method, String path, Object req, String headers, Class<T> responseClass) {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(req, http, headers);
            http.connect();
            return readBody(http, responseClass);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeBody(Object request, HttpURLConnection http, String headers) throws IOException {
        if (headers != null) {
            String[] pairs = headers.split(";");
            for (String pair : pairs) {
                String[] parts = pair.split(":", 2);
                if (parts.length == 2) {
                    http.setRequestProperty(parts[0].trim(), parts[1].trim());
                }
            }
        }
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }
}
