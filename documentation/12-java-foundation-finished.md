# Java foundation: cleanup before MCP

This is the current guide as of 12 October 2026. Lessons 9–11 explain how we built the earlier version; some of their snippets show code that has now moved or changed. Read this guide with `Main.java` and `OpenLibraryClient.java` beside it. Use Cmd + Shift + V to preview Markdown in VS Code.

## 1. What is finished

The Java foundation can search for a title, convert the first result into a `Book`, add that book to an in-memory shelf, list entries, and update a selected book's reading status. Duplicate IDs and missing service inputs are rejected by the existing service.

We have finished the small preparation stage discussed before MCP. That does not mean all future Java development is finished: the MCP layer will also be Java code, and persistence and hosting are separate future tasks.

The cleanup uses ordinary classes, methods, variables, loops, and conditions. There are no new frameworks or dependencies. The improvement is clearer responsibility and less repeated work, rather than a measured speed improvement.

## 2. The flow through the program

```text
Main
  -> OpenLibraryClient.searchBooks("The Hobbit")
       -> buildSearchRequest(title)
       -> send HTTP request
       -> readSearchResponse(statusCode, body)
       -> parseFirstBook(body)
       -> return a Book, or null if there are no matches
  -> BookshelfService.addBook(book, TO_READ)
       -> validate inputs and reject duplicate IDs
       -> create a ShelfEntry
       -> store it in InMemoryBookshelfRepository
  -> printShelf(service.getBooks())
```

Think of `Main` as the coordinator. The client knows about Open Library. The service knows the bookshelf rules. The repository stores entries. Keeping these jobs separate lets the future MCP tools reuse the client and service.

## 3. Main is now shorter

The central part is:

```java
Book book = client.searchBooks("The Hobbit");
if (book == null) {
  System.out.println("No matching book found.");
  return;
}

service.addBook(book, ReadingStatus.TO_READ);
printShelf(service.getBooks());
```

Previously, `Main` received a JSON string, parsed it, read each field, created a book, and printed several intermediate values. The client now performs that conversion once. `Main` receives a normal `Book` and uses it directly.

The return type of `searchBooks` changed from `String` to `Book`. This is an intentional change to its contract: callers must now expect a book rather than raw JSON.

`null` means no book was found. The condition checks for that before adding anything. `return` exits `main` early. This keeps the successful path easy to read without another nested block.

The two manually created demonstration books were removed, including the incorrect sample Hobbit ID. The shelf begins empty, and a successful search adds one book.

Expected output for the response you previously received:

```text
Books on shelf: 1
The Hobbit, J.R.R. Tolkien (1937) - TO_READ
ID: /works/OL27482W
```

Actual search results may change. We no longer print the entire JSON response or the old intermediate debugging lines.

## 4. Building a search request

The client checks the input first:

```java
if (title == null || title.isBlank()) {
  throw new IllegalArgumentException("Search title is required.");
}
```

`null` means no value was supplied. `isBlank()` means the string is empty or only whitespace. `||` means either condition is enough to reject the input. Validation happens before sending a request.

Then it prepares the title:

```java
String encodedTitle = URLEncoder.encode(title.trim(), StandardCharsets.UTF_8);
```

`trim()` removes spaces at the beginning and end. Encoding makes the title suitable for a URL query parameter. For example, `A & B` becomes `A+%26+B`. Without encoding, the ampersand could be interpreted as separating URL parameters.

The URL uses that encoded variable, followed by `limit=1` and the four fields we need. The search now uses the supplied title instead of always using a fixed Hobbit URL. Open Library documents title searches and field selection in its [Search API guide](https://openlibrary.org/dev/docs/api/search).

`buildSearchRequest` returns an `HttpRequest`. It has no `public` keyword, which means other classes in the same package can use it. Our tests are in that package and can inspect the request without sending it.

## 5. Timeouts and headers

The reusable `HttpClient` has a ten-second connection timeout. The individual request has a twenty-second request timeout. These are bounds on waiting for the network operation; they do not guarantee that every search takes that long or succeeds.

The request also sends:

- `Accept: application/json`, indicating the expected response format.
- `User-Agent: BookshelfMCP/1.0 (Java learning project)`, identifying the application.

The user-agent currently has no contact address. Before a hosted deployment, review Open Library's [API usage guidance](https://openlibrary.org/developers/api) and configure suitable identification and request rates. The current demo makes one request per run; it does not implement caching, retries, or a rate limiter.

## 6. HTTP success and errors

`readSearchResponse` checks the status code before parsing:

```java
if (statusCode != 200) {
  throw new IOException("Open Library returned HTTP " + statusCode + ".");
}
return parseFirstBook(body);
```

For this search endpoint, we expect HTTP 200. A 503, for example, is treated as a failed request rather than as an empty search. `IOException` represents an external data or communication problem.

A successful response with an empty `docs` array means no matches. A response missing the expected array is an error. Those cases must stay distinct: a broken response does not tell us that a book does not exist.

## 7. Turning JSON into a Book

`parseFirstBook` does the extraction previously performed in `Main`:

1. Read JSON into a `JsonNode` structure.
2. Check that the top level is an object and `docs` is an array.
3. Return `null` if that array is empty.
4. Read the first result's ID and title.
5. Read the first author and publication year using the fallback rules below.
6. Call the existing `Book` constructor.

Malformed JSON causes an `IOException` through Jackson. Empty input, an invalid response structure, or a result missing a usable ID or title also produces an error.

The client still selects only the first search result. It does not yet offer a list of alternatives for the user to choose between.

## 8. Consistent work IDs

The client accepts either of these forms from Open Library:

```text
OL27482W
/works/OL27482W
```

It stores both as:

```text
/works/OL27482W
```

If a `/works/` prefix exists, the code temporarily removes it, checks the remaining ID, then adds the standard prefix back. The expression `OL[0-9]+W` is a regular expression: it means `OL`, one or more digits, and `W`. This checks that the result has the work ID form our application expects.

This normalization happens at the API boundary. The service still compares the exact ID strings it receives and does not normalize arbitrary manually created books. Future MCP inputs must follow the documented format or be normalized at their boundary too.

## 9. Missing optional details

| Field | Rule |
|---|---|
| ID | Required, with a valid work ID format. |
| Title | Required and not blank. |
| Author | Use the first author; missing, empty, or blank becomes `Unknown author`. |
| Publication year | Accept a positive integer; otherwise use `0` to represent unknown. |

We kept the existing `int` year field to avoid introducing another type right now. Zero is a documented sentinel value: a special value meaning the year is unknown, rather than the book being published in year zero.

`printShelf` converts this zero value into the text `Unknown year` when displaying it. A string such as `"1937"` is not an integer JSON value and receives the fallback under these rules.

## 10. Display code lives in one method

`printShelf` handles the count and each entry's title, author, year, status, and ID. Inside the loop, it stores `entry.getBook()` in a local `book` variable so it does not repeat that method call on every field access.

Separating this method keeps the main flow readable. It is still ordinary terminal display code, not an MCP response format. The future MCP server will return structured results instead of depending on these printed lines.

## 11. Handling failures in Main

The `try` block contains the normal search-and-add flow. Each `catch` handles a different failure:

| Exception | Main's response |
|---|---|
| `IOException` | Print a search/data failure message. |
| `InterruptedException` | Restore the thread interruption flag and print an interrupted-search message. |
| `IllegalArgumentException` | Print the invalid bookshelf-operation message. |

`System.err.println` prints error messages to the error output stream. Normal shelf output uses `System.out.println`.

`Thread.currentThread().interrupt()` preserves the signal that the current task was asked to stop. For now, understand its purpose without needing to study threading in detail. We do not catch every possible programming error and silently hide it.

## 12. Small changes to existing classes

The fields in `Book` are now `final`. You already had no setters for them; `final` makes Java enforce that their values are assigned once. Their getters and constructor work as before.

The `book` reference in `ShelfEntry` is also `final`. Its `status` remains changeable, because `updateStatus` needs to change it.

The repository's `entries` list reference is `final`. This prevents assigning a different list to that field. It still allows adding entries to the existing list. A final list reference does not make the list contents immutable.

The service and repository continue to use the straightforward rules and list lookup you already learned. No database, new framework, or extra service layer was introduced.

## 13. Tests and what they prove

Run:

```bash
mvn clean test
```

`clean` removes old build output. Maven then recompiles the application and runs the tests. Java 21 must be active in your terminal.

The verified result was **14 tests, zero failures, zero errors, zero skipped**:

- Five existing service tests cover an empty shelf, adding, duplicate rejection, targeted status updates, and missing-book updates.
- Nine client tests cover field extraction, ID normalization, empty results, fallbacks, malformed responses, blank search input, URL encoding and request timeout, non-200 status handling, and successful response parsing.

The client tests use fixed JSON and inspect constructed requests. They do not contact Open Library, so they are not dependent on Wi-Fi or changing search results. An initial local-server test could not bind a socket in this restricted environment; it was replaced with tests of request construction and response handling that need no sockets.

A live end-to-end run of the cleaned-up `Main` has not been verified in this environment. Run `Main` using VS Code to check it on your machine. The existing tests do not cover every validation branch or simulate an actual network timeout.

## 14. What remains outside this stage

Storage is still in memory and disappears when the program stops. The repository is designed for the simple current demo; concurrent access will need review when we choose the MCP transport and server model. There is no MCP server, hosting, database, or Salesforce connection yet.

The next stage is to verify the current Java MCP options and Agentforce connection requirements against official documentation, then add a minimal server around these reusable operations. We will not assume that the console `Main` itself is an MCP server.

## Suggested reading order

First read sections 2 and 3 and compare them with `Main.java`. Then read the client sections while following its methods from top to bottom. Finally, read the tests one at a time and predict the result before running them.

Before saving an editor buffer that was open during these changes, reload or compare it with the disk version. An older unsaved buffer can overwrite the new code.
