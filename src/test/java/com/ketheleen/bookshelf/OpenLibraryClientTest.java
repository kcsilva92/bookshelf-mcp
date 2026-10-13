package com.ketheleen.bookshelf;

import java.io.IOException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenLibraryClientTest {
  private final OpenLibraryClient client = new OpenLibraryClient();

  @Test
  void readsBookDetailsAndNormalizesBareWorkId() throws IOException {
    Book book = client.parseFirstBook("""
        {"docs":[{"key":"OL27482W","title":"The Hobbit",
        "author_name":["J.R.R. Tolkien","Another author"],"first_publish_year":1937}]}
        """);
    assertEquals("/works/OL27482W", book.getId());
    assertEquals("The Hobbit", book.getTitle());
    assertEquals("J.R.R. Tolkien", book.getAuthor());
    assertEquals(1937, book.getPublicationYear());
  }

  @Test
  void emptyResultsReturnNull() throws IOException {
    assertNull(client.parseFirstBook("{\"docs\":[]}"));
  }

  @Test
  void missingOptionalDetailsUseFallbacks() throws IOException {
    Book book = client.parseFirstBook("""
        {"docs":[{"key":"/works/OL27482W","title":"The Hobbit"}]}
        """);
    assertEquals("/works/OL27482W", book.getId());
    assertEquals("Unknown author", book.getAuthor());
    assertEquals(0, book.getPublicationYear());
  }

  @Test
  void blankAuthorAndInvalidYearUseFallbacks() throws IOException {
    Book book = client.parseFirstBook("""
        {"docs":[{"key":"OL27482W","title":"The Hobbit",
        "author_name":[" "],"first_publish_year":"not a year"}]}
        """);
    assertEquals("Unknown author", book.getAuthor());
    assertEquals(0, book.getPublicationYear());
  }

  @Test
  void malformedResponsesAreErrorsRatherThanEmptySearches() {
    String[] responses = {"not JSON", "", "null", "{}", "{\"docs\":{}}",
        "{\"docs\":[{\"title\":\"The Hobbit\"}]}",
        "{\"docs\":[{\"key\":\"OL27482W\",\"title\":\" \"}]}"};
    for (String response : responses) {
      assertThrows(IOException.class, () -> client.parseFirstBook(response));
    }
  }

  @Test
  void missingSearchTitleIsRejectedBeforeSendingRequest() {
    assertThrows(IllegalArgumentException.class, () -> client.searchBooks(null));
    assertThrows(IllegalArgumentException.class, () -> client.searchBooks(" "));
  }

  @Test
  void requestUsesEncodedTitleAndTimeout() {
    var request = client.buildSearchRequest(" A & B ");
    assertEquals("title=A+%26+B&limit=1&fields=key,title,author_name,first_publish_year",
        request.uri().getRawQuery());
    assertEquals("GET", request.method());
    assertEquals(20, request.timeout().orElseThrow().toSeconds());
  }

  @Test
  void unsuccessfulHttpResponseIsAnError() {
    IOException error = assertThrows(IOException.class,
        () -> client.readSearchResponse(503, "Service unavailable"));
    assertTrue(error.getMessage().contains("503"));
  }

  @Test
  void successfulResponseIsParsed() throws IOException {
    Book book = client.readSearchResponse(200,
        "{\"docs\":[{\"key\":\"OL1W\",\"title\":\"A book\"}]}");
    assertEquals("A book", book.getTitle());
  }
}
