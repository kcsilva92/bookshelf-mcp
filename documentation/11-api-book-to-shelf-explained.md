# From an Open Library result to a book on your shelf

This lesson explains the change made in `Main.java`: adding the book created from Open Library data to your in-memory shelf. Read one section at a time with `Main.java` open beside this guide.

## 1. What you had already done

Your program requested The Hobbit from Open Library and read these values from the first search result:

| Java variable | Value in your successful response | Type |
|---|---|---|
| `id` | `/works/OL27482W` | `String` |
| `title` | `The Hobbit` | `String` |
| `author` | `J.R.R. Tolkien` | `String` |
| `publicationYear` | `1937` | `int` |

These variables hold the data that your `Book` constructor needs.

## 2. Reading the author

The response contains an array of author names:

```json
"author_name": ["J.R.R. Tolkien"]
```

An array is a list of values enclosed in square brackets. Your code reads its first item:

```java
// Get the list of authors.
JsonNode authors = firstBook.path("author_name");

// Use this value if the API provides no authors.
String author = "Unknown author";

// Read the first author if the list contains one.
if (authors.isArray() && !authors.isEmpty()) {
    author = authors.get(0).asText();
}
```

`path("author_name")` retrieves that field from the result. `isArray()` checks its structure. `!isEmpty()` checks that it contains at least one item. The `&&` means both conditions must be true before the code inside the block runs.

Positions start at zero, so `get(0)` selects the first author. `asText()` reads the selected value as a Java string. If the field is missing or the array is empty, the initial value, `"Unknown author"`, remains in place.

This exercise uses only the first author, even if the response contains several.

## 3. Creating the Book object

You then wrote:

```java
// Create a Book using the values read from Open Library.
Book apiBook = new Book(id, title, author, publicationYear);
```

Read this line in small pieces:

- `Book` is the type of object the variable will refer to.
- `apiBook` is the variable name you chose.
- `new Book(...)` creates an object by calling the `Book` constructor.
- The four arguments supply its ID, title, author, and publication year, in that order.

Earlier, you supplied values directly, such as `"The Hobbit"`. Here, the variables supply values extracted from the API response.

At this point, the object exists in memory, but creating it does not automatically add it to the repository. Printing `apiBook.getTitle()` confirms its title; printing also does not store it on the shelf.

## 4. What was added for you

Immediately after creating `apiBook`, these lines were added:

```java
// Add the book from Open Library to our shelf.
service.addBook(apiBook, ReadingStatus.TO_READ);

// Retrieve the current shelf and print its updated size.
System.out.println("Books after API addition: " + service.getBooks().size());
```

### Adding the book

```java
service.addBook(apiBook, ReadingStatus.TO_READ);
```

`service` is the `BookshelfService` you created earlier in `main`. Its `addBook` method accepts two arguments:

| Argument | Purpose |
|---|---|
| `apiBook` | The book you want to store. |
| `ReadingStatus.TO_READ` | Its initial reading status. |

The method checks that the book and status are present, that the ID is not blank, and that the repository does not already contain that ID. It then creates a `ShelfEntry` that combines the book with its status and passes that entry to the repository for storage.

The service already existed. This change calls it with the new API book; it does not introduce a new storage method.

### Printing the updated count

```java
System.out.println("Books after API addition: " + service.getBooks().size());
```

Java evaluates the expression to obtain the value to print:

1. `service.getBooks()` retrieves the shelf entries currently stored.
2. `.size()` counts the entries in that returned list.
3. `+` joins the label and number into a string.
4. `System.out.println(...)` prints it in the terminal.

Your earlier `shelf` variable contains a list copy retrieved before the API addition. Calling `service.getBooks()` again gets the updated list.

## 5. Why the expected count is three

Your program first adds two manually created demonstration books. It then requests Open Library data, creates `apiBook`, and adds it as a third entry.

The expected output includes:

```text
Books on shelf: 2
Books after API addition: 3
```

Both counts are correct at the moment they print. The first is printed before the API addition; the second is printed afterwards. The original loop also runs before the addition, so that loop displays only the two demonstration entries.

The manually created Hobbit has the sample ID `OL27448W`, whereas the API result has `/works/OL27482W`. The service compares IDs, so it currently treats these as different books even though their titles match. The sample ID is not the correct ID for the returned Hobbit work. Cleaning up the demonstration data and choosing a consistent ID format remain later tasks.

## 6. Where the data is stored

The repository is an `InMemoryBookshelfRepository`. It stores shelf entries in a Java list while the program runs.

This addition does not write to a database or file. When the program ends, that in-memory shelf is lost. Running `Main` again creates a fresh repository and repeats the additions.

The code is inside the condition that checks for a nonempty search-results array. If there are no results, this block does not create or add an API book.

## 7. What was verified

After the addition, `mvn test` completed with `BUILD SUCCESS`: five tests ran with zero failures, errors, or skipped tests.

Those existing tests check service behaviour. They do not make a live Open Library request or verify this entire `Main` flow. Run `Main` in VS Code to check the live response and the updated count yourself.

If VS Code has an older unsaved copy of `Main.java`, compare or reload it before saving. Otherwise, saving that older copy can overwrite the change on disk.

## 8. The next small coding step

At the time this change was made, `OpenLibraryClient.searchBooks(String title)` still used a hardcoded Hobbit URL. Accepting a `title` parameter does not make the URL use it automatically.

The next exercise is to replace that hardcoded URL with one built from the parameter:

```java
// Encode the title so spaces and special characters work in a URL.
String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);

// Build the search URL using the title passed to this method.
URI uri = URI.create(
    "https://openlibrary.org/search.json?title=" + encodedTitle
    + "&limit=1&fields=key,title,author_name,first_publish_year"
);
```

This snippet is the next exercise, not part of the shelf-addition change. Keep the Hobbit example while making that change so you can compare the output with your previous run.

## Check your understanding

Before moving on, try answering these in your own words:

1. What does `new Book(...)` create?
2. Which line actually adds that book to the shelf?
3. Why does the first shelf count remain two?
4. What happens to the shelf when the program stops?

For more detail about the existing service, read [Bookshelf service](07-bookshelf-service-explained.md). For the API data structure, read [JSON and Jackson](10-json-and-jackson-explained.md).
