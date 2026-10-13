# JSON, Jackson, and reading your Open Library result

This lesson ends exactly where your saved code is now: reading the first result's title, ID, and year. Reading the author was explained but has not yet been added to Main. We will identify that distinction below.

## JSON is structured text

JSON stands for JavaScript Object Notation. Java programs can read it too; it is not Java source code.

Imagine a labelled information card:

```json
{
  "title": "The Hobbit",
  "first_publish_year": 1937
}
```

A key is a name on the left of a colon. A value is the information on the right.

| Item | In this example |
|---|---|
| Key | first_publish_year |
| Value | 1937 |
| Java type we used for the value | int |
| Our Java field name | publicationYear |

publicationYear is not the key in this response. It is your Java field name. The API uses a different name for the same kind of information.

## Braces and brackets

Curly braces `{}` group an object: a set of named properties. Square brackets `[]` group an array: an ordered list of values.

```json
{
  "docs": [
    {
      "title": "The Hobbit",
      "author_name": ["J.R.R. Tolkien"]
    }
  ]
}
```

The outside object contains docs. docs is an array of result objects. author_name is another array, containing author names. Its array shape allows multiple authors.

JSON objects do not automatically become Book objects. Jackson first lets us read their structure.

Text uses double quotes; numbers and booleans do not. null means no value, which is different from the text "null" or an empty String.

## Your response

```json
{
  "numFound": 224,
  "start": 0,
  "numFoundExact": true,
  "num_found": 224,
  "documentation_url": "https://openlibrary.org/dev/docs/api/search",
  "q": "",
  "offset": null,
  "docs": [
    {
      "author_name": ["J.R.R. Tolkien"],
      "first_publish_year": 1937,
      "key": "/works/OL27482W",
      "title": "The Hobbit"
    }
  ]
}
```

The search information sits outside docs. Your selected book data sits inside docs. numFound reports matches, not the number of books added to your shelf. This request added none.

The property named key holds the book/work identifier. Do not confuse that literal property name with the general idea of a JSON key: title and first_publish_year are also JSON keys.

## What Jackson does

Jackson is the library we added to read and format JSON. Your pom.xml currently declares jackson-databind version 2.21.2.

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
```

ObjectMapper reads and writes JSON. JsonNode represents a part of the structured JSON tree.

```java
ObjectMapper mapper = new ObjectMapper();
```

Creates the mapper. It does not contact Open Library. Your HTTP client already fetched the text.

## Pretty-printing changes presentation

```java
private static void printJson(ObjectMapper mapper, String json) throws IOException {
    String prettyJson = mapper.writerWithDefaultPrettyPrinter()
        .writeValueAsString(mapper.readTree(json));
    System.out.println(prettyJson);
}
```

Read it in stages:

1. readTree parses the JSON text into a structure.
2. writerWithDefaultPrettyPrinter creates a writer that adds formatting.
3. writeValueAsString writes the structure back to text.
4. println displays the formatted text.

Indentation and line breaks make it easier to read. Pretty-printing does not change the search results or store books.

The helper is private because it is used inside Main. It is static so static main can call it without creating a Main object. It returns void because its job is printing.

Normal System.out.println still works. The helper calls it internally. Only JSON passed through the helper is formatted; unrelated terminal output is not automatically reformatted.

Malformed JSON can fail parsing. Our main currently declares IOException so it can propagate such a problem, rather than presenting a friendly recovery message.

## Reading the root

```java
JsonNode root = mapper.readTree(json);
```

root represents the whole response. Think of it as the top of a tree with branches named docs, numFound, and so on.

Your current Main parses once to pretty-print, then again to read fields. This is understandable practice code, even though one parsed tree could be reused. No refactoring is required to read this lesson.

## Finding the results list

```java
JsonNode docs = root.path("docs");
```

path asks for the property with that name. If the property is absent, it returns a missing node rather than Java null. This makes some navigation safer, but it does not validate that the response has everything we expect.

```java
if (docs.isArray() && !docs.isEmpty()) {
```

Check that docs is an array and contains at least one element. && means both conditions must be true. !isEmpty means not empty.

An absent docs property or an empty array will not enter the block. Our current code does not print a special no-results message.

## Selecting the first book

```java
JsonNode firstBook = docs.get(0);
```

Array positions start at zero, so position 0 is the first result. We are not processing every result here, only the first one.

get(0) is useful after checking the array is not empty. Accessing a missing array position can return null with JsonNode, which could cause a later method call to fail.

## Reading three fields

```java
String title = firstBook.path("title").asText();
String id = firstBook.path("key").asText();
int publicationYear = firstBook.path("first_publish_year").asInt();
```

For each line, find the named property and convert its value into the desired Java type.

| Expression | Result from your successful request |
|---|---|
| path("title").asText() | The Hobbit |
| path("key").asText() | /works/OL27482W |
| path("first_publish_year").asInt() | 1937 |

asText and asInt are convenient conversions, not full validation. A missing title can become empty text, and a missing or unconvertible year defaults to 0. Zero does not mean the book was published in year zero; it can signal unavailable data in this practice code.

These variables exist inside the if block. They are not yet passed to a Book constructor or stored in the repository.

## Your last verified output

```text
Title from API: The Hobbit
ID from API: /works/OL27482W
Year from API: 1937
```

This confirms you successfully navigated the response to obtain these three values. It does not mean the two manually created shelf entries have changed.

## The author step already explained, but not saved yet

The last instruction before stopping was this small addition inside the same if block:

```java
JsonNode authors = firstBook.path("author_name");
String author = "Unknown author";

if (authors.isArray() && !authors.isEmpty()) {
    author = authors.get(0).asText();
}

System.out.println("Author from API: " + author);
```

It asks for the author array, starts with a fallback, and replaces that fallback with the first author if the array contains an item. For your response that is J.R.R. Tolkien.

This snippet records the lesson we reached, not a claim that the code is implemented. The saved Main currently stops at title, ID, and year. It also does not yet check whether the first author string itself is blank, or combine multiple authors.

## JSON text versus Java objects

| Name in our code | What it holds |
|---|---|
| json | The response as a String. |
| root | Parsed representation of the whole response. |
| docs | Node representing the results array. |
| firstBook | Node representing the first JSON result. |
| title | Extracted title as a Java String. |
| publicationYear | Extracted year as a Java int. |
| book in the earlier Main code | A manually created Book object. |

firstBook is a JsonNode, not a Book instance. A similar name does not make two Java types the same.

## Small reading exercises

Which JSON key supplies publicationYear? first_publish_year.

Does numFound equal the shelf size? No; it counts search matches.

Why author_name uses brackets? It is a list of authors.

Does printing JSON add a book? No.

Stop here: this is the current learning point. Use [continue-here.md](continue-here.md) to resume when ready.

References: [Open Library Search API](https://openlibrary.org/dev/docs/api/search) and [Jackson 2.x documentation](https://github.com/FasterXML/jackson-databind/tree/jackson-databind-2.20.0).
