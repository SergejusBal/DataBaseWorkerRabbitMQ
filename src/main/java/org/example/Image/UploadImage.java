package org.example.Image;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.example.DataBaseInfo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class UploadImage {

    public String uploadImage(byte[] imageBytes) {

        String encodedImage = Base64.getEncoder().encodeToString(imageBytes);

        try {
            encodedImage = URLEncoder.encode(encodedImage, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        String requestBody = "key=" + DataBaseInfo.IMAGEBB + "&image=" + encodedImage;


        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.imgbb.com/1/upload"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }

        Gson gson = new Gson();
        JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
        String imageUrl = jsonResponse.getAsJsonObject("data").get("url").getAsString();

        if (response != null) return imageUrl;

        return "Image upload failed!";
    }

}
