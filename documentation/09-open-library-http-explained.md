# Open Library and your first Java HTTP request

You have already made a successful request in the browser and from Java. This guide explains those steps. It does not add a new feature.

## API: asking another program for information

Imagine asking a librarian for books with a particular title. Your application sends a request to Open Library and receives a response containing book information.

```text
Your Java code → request → Open Library
Your Java code ← response ← Open Library
```

Open Library already supplies this API. Writing its URL did not create an API in your project. Your OpenLibraryClient is code that uses the existing API.

Searching does not automatically add a result to your shelf. Your existing shelf and external search are still separate in Main.

## The URL you used

```text
https://openlibrary.org/search.json?title=the+hobbit&limit=1&fields=key,title,author_name,first_publish_year
```

| Part | Meaning |
|---|---|
| https://openlibrary.org | The destination website using HTTPS. |
| /search.json | The search endpoint that returns JSON. |
| ? | Begins the query parameters. |
| title=the+hobbit | Search by title; + represents a space in this query encoding. |
| & | Separates query parameters. |
| limit=1 | Return at most one matching result. |
| fields=... | Select the book fields requested. |

An endpoint is an address for a particular API operation. Query parameters are extra instructions carried in the URL.

Your response reported 224 matches, but docs contained one result because limit was 1. Match count and number returned are different.

## HTTP and GET

HTTP is the communication protocol used for web requests. GET is an HTTP method used to retrieve information. HTTPS adds a secure transport connection.

The response has more than a body: it also includes a status code and headers. Our current example returns only the body; it does not yet check HTTP status codes.

## The imports

```java
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
```

These types are provided by Java, so we did not add an external Maven library just to send HTTP requests. Jackson is a separate dependency for reading and formatting JSON.

## Create the client

```java
private final HttpClient httpClient = HttpClient.newHttpClient();
```

HttpClient is the type. httpClient is your field name. newHttpClient creates the object that can send requests. Creating it does not contact Open Library yet.

final means the field cannot be reassigned to another client object after initialisation.

## One method builds and sends the request

The current saved method accepts a parameter:

```java
public String searchBooks(String title) throws IOException, InterruptedException {
```

It returns a String: the response body as text. The title parameter is meant to select the searched title.

**Current-file distinction:** the saved OpenLibraryClient still builds a hardcoded Hobbit URL. Therefore the parameter is not currently used to choose the title. This was recorded in continue-here.md after a save conflict. The following sections distinguish the saved code from the earlier encoding exercise.

The saved request address is created with URI.create:

```java
URI uri = URI.create(
    "https://openlibrary.org/search.json?title=the+hobbit&limit=1&fields=key,title,author_name,first_publish_year"
);
```

URI represents the address. It does not send anything itself.

## Build the request

```java
HttpRequest request = HttpRequest.newBuilder()
    .uri(uri)
    .GET()
    .build();
```

This builder constructs a request in steps:

1. Start the builder.
2. Set the destination URI.
3. Select GET.
4. Build the finished request object.

The leading dots continue the same expression across lines. They are method calls, not separate requests.

## Send it and receive text

```java
HttpResponse<String> response = httpClient.send(
    request,
    HttpResponse.BodyHandlers.ofString()
);
return response.body();
```

send receives two arguments: the request and a body handler. The handler tells Java to read the body as a String. HttpResponse<String> describes the body type held in the response.

send waits for the response here. body() extracts its content. For this API, that content is JSON text.

Printing response.body is different from interpreting its book data: at this point it is just text.

## Why throws appears in two places

IOException signals an input/output problem, such as a network failure. InterruptedException signals interruption while the thread waits for the operation.

These are checked exceptions: Java requires the calling code to catch them or declare that they may pass to its caller.

Our first version declares them on searchBooks, then on main because main calls searchBooks:

```java
public static void main(String[] args) throws IOException, InterruptedException {
```

throws does not recover from a failure. In this simple application an uncaught exception can terminate the program and display an error. We have not implemented a recovery flow.

## What went wrong with two searchBooks methods?

An earlier version created the request in one method and tried to send it in another with the same name and parameters. Java does not treat a different throws clause as a different method signature. A local request variable also stays within its method's scope.

Combining preparation and sending into one method fixed both issues. The send call needs request as its first argument, not only the body handler.

## Encoding: the exercise already discussed

To build a URL from arbitrary supplied text, the earlier lesson introduced:

```java
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
```

```java
String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
URI uri = URI.create(
    "https://openlibrary.org/search.json?title=" + encodedTitle
    + "&limit=1&fields=key,title,author_name,first_publish_year"
);
```

This is the intended dynamic-title version from that lesson, not the current saved method body. Encoding safely represents spaces and query-sensitive characters. For example, & in a title must not accidentally begin another query parameter.

UTF-8 is the character encoding used for this conversion. The imports alone do not activate encoding; the method must call encode.

## The Main call

```java
OpenLibraryClient client = new OpenLibraryClient();
String json = client.searchBooks("The Hobbit");
```

The first statement creates a client. The second sends the request and stores the returned String. It does not create a Book or add one to the repository.

Next: [JSON and Jackson](10-json-and-jackson-explained.md).

Reference: [Open Library Search API documentation](https://openlibrary.org/dev/docs/api/search).
