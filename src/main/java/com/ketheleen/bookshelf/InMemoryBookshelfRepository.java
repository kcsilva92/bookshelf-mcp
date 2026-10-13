package com.ketheleen.bookshelf;

import java.util.ArrayList;
import java.util.List;

public class InMemoryBookshelfRepository implements BookshelfRepository {
  private final List<ShelfEntry> entries = new ArrayList<>();

  @Override
  public void add(ShelfEntry entry){
    entries.add(entry);
  }

  @Override
  public List<ShelfEntry> findAll(){
    return new ArrayList<>(entries);
  }

  @Override
  public ShelfEntry findByBookId(String bookId) {
    for (ShelfEntry entry : entries) {
        if (entry.getBook().getId().equals(bookId)) {
            return entry;
        }
    }
    return null;
  }
}
