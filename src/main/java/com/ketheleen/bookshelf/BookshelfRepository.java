package com.ketheleen.bookshelf;

import java.util.List;

public interface BookshelfRepository {
  void add(ShelfEntry entry);
  List<ShelfEntry> findAll();

  ShelfEntry findByBookId(String bookId);
}
