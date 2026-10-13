package com.ketheleen.bookshelf;

import java.io.IOException;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    BookshelfRepository repository = new InMemoryBookshelfRepository();
    BookshelfService service = new BookshelfService(repository);
    OpenLibraryClient client = new OpenLibraryClient();

    try {
      // The client handles HTTP and JSON, then returns a Book we can use.
      Book book = client.searchBooks("The Hobbit");
      if (book == null) {
        System.out.println("No matching book found.");
        return;
      }

      service.addBook(book, ReadingStatus.TO_READ);
      printShelf(service.getBooks());
    } catch (IOException exception) {
      System.err.println("Could not search Open Library: " + exception.getMessage());
    } catch (InterruptedException exception) {
      // Preserve the interruption so calling code knows the task was stopped.
      Thread.currentThread().interrupt();
      System.err.println("The search was interrupted.");
    } catch (IllegalArgumentException exception) {
      System.err.println("Could not complete the bookshelf operation: " + exception.getMessage());
    }
  }

  // Keep all shelf display formatting in one place.
  private static void printShelf(List<ShelfEntry> shelf) {
    System.out.println("Books on shelf: " + shelf.size());
    for (ShelfEntry entry : shelf) {
      Book book = entry.getBook();
      String year = "Unknown year";
      if (book.getPublicationYear() > 0) {
        year = Integer.toString(book.getPublicationYear());
      }
      System.out.println(book.getTitle() + ", " + book.getAuthor()
          + " (" + year + ") - " + entry.getStatus());
      System.out.println("ID: " + book.getId());
    }
  }
}
