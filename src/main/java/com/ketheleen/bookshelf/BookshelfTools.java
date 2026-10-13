package com.ketheleen.bookshelf;

import java.util.List;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.stereotype.Component;

// Let Spring discover this class and create its object.
@Component
public class BookshelfTools {

  private final BookshelfService service;

  // Spring supplies the service created by your @Bean method.
  public BookshelfTools(BookshelfService service) {
    this.service = service;
  }

  // Expose this method as an MCP tool.
  @McpTool(
      name = "list_books",
      description = "List the books on the shelf and their reading statuses."
  )
  public List<ShelfEntry> listBooks() {
    return service.getBooks();
  }
}