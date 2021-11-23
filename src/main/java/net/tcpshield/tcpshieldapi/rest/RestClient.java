package net.tcpshield.tcpshieldapi.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.tcpshield.tcpshieldapi.deserializer.DateDeserializer;
import net.tcpshield.tcpshieldapi.exception.APIConnectionException;
import net.tcpshield.tcpshieldapi.exception.status.NoPermissionException;
import net.tcpshield.tcpshieldapi.exception.status.NotFoundException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RestClient {

    private final Pattern HTTP_RESPONSE_CODE_EXCEPTION_PATTERN = Pattern.compile("^Server returned HTTP response code: (\\d+) for URL: .+$");
    private final String apiKey;
    private final ObjectMapper mapper = new ObjectMapper();

    public RestClient(String apiKey) {
        this.apiKey = apiKey;

        SimpleModule module = new SimpleModule();
        module.addDeserializer(Date.class, new DateDeserializer());

        mapper.registerModule(module);
    }

    public <T> T makeRequest(RestRequest<T> restRequest) {
        RestResponse<T> response = null;
        int statusCode = -1;
        try {
            response = internalRequest(restRequest);
        } catch (FileNotFoundException e) { // 404
            statusCode = 404;
        } catch (IOException e) {
            String msg = e.getMessage();
            Matcher matcher = HTTP_RESPONSE_CODE_EXCEPTION_PATTERN.matcher(msg);

            if (!matcher.find()) throw new APIConnectionException(e);

            statusCode = Integer.parseInt(matcher.group(1));
        }

        if (statusCode != 0 && response != null)
            statusCode = response.getStatusCode();

        switch (statusCode) {
            case 403:
                throw new NoPermissionException();
            case 404:
                throw new NotFoundException();
            default:
                if (response == null)
                    throw new APIConnectionException("Response code unknown: " + statusCode + "; for request to " + restRequest.getURL());

                return response.getData();
        }
    }

    private <T> RestResponse<T> internalRequest(RestRequest<T> request) throws IOException {
        String data = toJson(request.getData());
        HttpClient client = HttpClient.newBuilder().build();
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URL(request.getURL()).toURI())
                    .method(request.getRequestType().name(), data == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(data))
                    .header("X-API-Key", apiKey)
                    .header("Content-Type", "application/json")
                    .header("Content-Language", "en-US")
                    .build();
            HttpResponse<?> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            Object responseBody = response.body();
            if (responseBody == null) {
                throw new NullPointerException("Response body is null");
            }
            if (!(responseBody instanceof String responseString)) {
                throw new IllegalArgumentException("Response body is not a string but is instead a " + responseBody.getClass().getSimpleName());
            }
            int statusCode = response.statusCode();
            T parsed = parseJson(responseString, request.getResponseClass());
            return new RestResponse<>(statusCode, parsed);
        } catch (URISyntaxException | InterruptedException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private String toJson(Object data) throws JsonProcessingException {
        if (data == null) return null;

        return mapper.writeValueAsString(data);
    }

    private <T> T parseJson(String data, Class<T> targetClass) throws IOException {
        if (targetClass == null) return null;

        return mapper.readValue(data, targetClass);
    }

}
