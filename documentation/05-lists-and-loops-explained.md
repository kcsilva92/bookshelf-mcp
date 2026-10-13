# Lists, loops, and finding a book

Think of a list as a row of shelf-entry cards. Each card is stored at a numbered position. Our ArrayList can grow as entries are added.

## Creating a list

```java
import java.util.List;
import java.util.ArrayList;
```

```java
List<ShelfEntry> shelf = new ArrayList<>();
```

| Piece | Meaning |
|---|---|
| List | The type describing list operations. |
| <ShelfEntry> | This list stores shelf entries. |
| shelf | Our variable name. |
| new ArrayList<>() | Create an empty ArrayList implementation. |

The angle brackets describe the element type. They help Java reject adding an incompatible type, such as a String, to this list. A list and an array are related collection ideas, but they are not the same Java type. Our current storage uses an ArrayList.

This line now lives in the repository as a field called entries. We originally practised it in Main.

## Adding is what changes the count

```java
shelf.add(entry);
shelf.add(secondEntry);
```

After the first add, the list has one entry. After the second, it has two.

Neither new Book nor ReadingStatus.TO_READ automatically adds an entry. Only the storage operation does that.

ArrayList itself allows duplicates. The service supplies the rule rejecting duplicate book IDs. If callers use repository.add directly, that service rule is bypassed.

## Counting with size()

```java
System.out.println(shelf.size());
```

ArrayList maintains its size, so size() reads the count without visiting every entry. This is constant time, written O(1).

A loop that counts each element performs work for each entry: O(n), where n is the number of entries. With two books the difference is tiny. We use size() because it is clear and efficient for this task.

Do not confuse this with service.getBooks().size(): the current repository first copies the list during getBooks, which requires O(n) work. Calling size() on the returned list is still O(1), but creating that copy is not. Main retrieves the list once and reuses it.

## Empty lists and numbered positions

```java
shelf.isEmpty()
```

Returns true if there are no elements. It returns a boolean rather than printing anything.

```java
shelf.get(0)
```

Gets the first element. Positions start at zero. With two entries, valid positions are 0 and 1. Accessing position 2 would fail. That is why we check an API result list is not empty before using get(0).

## A for-each loop

```java
for (ShelfEntry shelfEntry : shelf) {
    System.out.println(
        shelfEntry.getBook().getTitle() + ", "
        + shelfEntry.getBook().getAuthor() + " - "
        + shelfEntry.getStatus()
    );
}
```

Read it as: for each shelf entry in shelf, run the block.

ShelfEntry is the element type. shelfEntry is a temporary name for the current element. The colon separates that declaration from the list being visited.

On the first iteration it refers to the first entry, then on the next iteration the second entry. The loop does not create another Book or add anything to storage.

Printing every book necessarily involves visiting every entry. Loops are appropriate here; the work is useful rather than an unnecessary count.

## Searching by ID

The repository uses:

```java
for (ShelfEntry entry : entries) {
    if (entry.getBook().getId().equals(bookId)) {
        return entry;
    }
}
return null;
```

For each entry, read its book's ID and compare that ID's text with the requested ID. When a match is found, return immediately; the rest of the loop is skipped.

If all entries are checked without a match, return null. A missing result is different from an empty Book object.

This lookup may check every entry, so it is O(n) in the worst case. We have not built a different lookup structure.

## equals versus ==

Use equals to compare String contents:

```java
"sample-id".equals(bookId)
```

The repository compares the ID returned by getId. `==` for object references checks identity rather than text contents, so it is not the correct choice for this lookup.

Your service validates book IDs before storing them through addBook. The repository itself does not independently validate every input.

## List copy versus object copy

```java
return new ArrayList<>(entries);
```

This creates a new list holding references to the same ShelfEntry objects.

Adding or removing an item in the returned list does not change the repository's list. Changing an entry's status does affect the shared object. It is a shallow copy, not a copy of each entry and book.

Think of making another checklist containing the same cards' addresses, rather than making new cards.

## Check your understanding

Does a loop automatically add entries? No.

Does changing status change size? No.

Does a list need a loop to return its stored size? No.

Next: [Repository and interfaces](06-repository-and-interfaces-explained.md).
