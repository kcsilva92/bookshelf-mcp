package com.ketheleen.bookshelf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class OpenLibraryClient {
  private final HttpClient httpClient = HttpClient.newBuilder()
      .connectTimeout(Duration.ofSeconds(10))
      .build();
  private final ObjectMapper mapper = new ObjectMapper();
  // Return the first matching book, or null when there are no matches.
  public Book searchBooks(String title) throws IOException, InterruptedException {
    HttpRequest request = buildSearchRequest(title);
    HttpResponse<String> response = httpClient.send(
        request, HttpResponse.BodyHandlers.ofString());
    return readSearchResponse(response.statusCode(), response.body());
  }

  // Separate request construction so encoding can be checked without a network.
  HttpRequest buildSearchRequest(String title) {
    if (title == null || title.isBlank()) {
      throw new IllegalArgumentException("Search title is required.");
    }

    String encodedTitle = URLEncoder.encode(title.trim(), StandardCharsets.UTF_8);
    URI uri = URI.create("https://openlibrary.org/search.json?title=" + encodedTitle
        + "&limit=1&fields=key,title,author_name,first_publish_year");

    return HttpRequest.newBuilder()
        .uri(uri)
        .timeout(Duration.ofSeconds(20))
        .header("Accept", "application/json")
        .header("User-Agent", "BookshelfMCP/1.0 (Java learning project)")
        .GET()
        .build();

  }

  // Check HTTP success before trying to interpret the JSON body.
  Book readSearchResponse(int statusCode, String body) throws IOException {
    // A failed HTTP request must not be treated as a successful search.
    if (statusCode != 200) {
      throw new IOException("Open Library returned HTTP " + statusCode + ".");
    }
    return parseFirstBook(body);
  }

  // Keep JSON details here so callers can work with ordinary Book objects.
  Book parseFirstBook(String json) throws IOException {
    JsonNode root = mapper.readTree(json);
    if (root == null || !root.isObject()) {
      throw new IOException("Open Library returned an invalid search response.");
    }
    JsonNode docs = root.path("docs");
    if (!docs.isArray()) {
      throw new IOException("Open Library response is missing the results array.");
    }
    if (docs.isEmpty()) {
      return null;
    }

    JsonNode firstBook = docs.get(0);
    String id = firstBook.path("key").asText("").trim();
    String title = firstBook.path("title").asText("").trim();

    // Open Library examples use both bare IDs and /works/ paths.
    // Store one consistent form so duplicate checks compare the same value.
    if (id.startsWith("/works/")) {
      id = id.substring("/works/".length());
    }
    if (!id.matches("OL[0-9]+W") || title.isBlank()) {
      throw new IOException("Open Library result is missing a valid work ID or title.");
    }
    id = "/works/" + id;

    String author = "Unknown author";
    JsonNode authors = firstBook.path("author_name");
    if (authors.isArray() && !authors.isEmpty()) {
      String firstAuthor = authors.get(0).asText("").trim();
      if (!firstAuthor.isBlank()) {
        author = firstAuthor;
      }
    }

    // Zero means unknown; only accept a positive integer from the response.
    int publicationYear = 0;
    JsonNode year = firstBook.path("first_publish_year");
    if (year.isIntegralNumber() && year.canConvertToInt() && year.asInt() > 0) {
      publicationYear = year.asInt();
    }
    return new Book(id, title, author, publicationYear);
  }
}
