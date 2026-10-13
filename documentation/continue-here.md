# Bookshelf MCP — continue here

Updated 12 October 2026 after finishing the Java preparation stage.

## Learning preferences

The user is learning Java for eventual Agentforce integration. Use small steps and plain language. They prefer to write code themselves, but explicitly authorized finishing and simplifying the Java foundation in this session. That authorization does not automatically request an MCP implementation. Avoid building ahead of the current request.

Keep ordinary classes, methods, loops, and conditions. Explain unfamiliar concepts and add useful comments. The user requested detailed Markdown documentation to study the completed changes.

When directly editing, remind them to reload/compare open VS Code buffers: an older unsaved buffer previously overwrote newer changes.

## Current state

- Project targets Java 21. Homebrew Java 21 was installed and terminal/Maven checks showed Java 21. The user was guided to persist JAVA_HOME and PATH in ~/.zshrc; persistence in a new terminal was not confirmed.
- Book, ReadingStatus, ShelfEntry, repository, and BookshelfService exist. Service supports adding, listing, and updating status, and rejects duplicate IDs and missing inputs.
- Book fields, ShelfEntry's book reference, and the repository list reference are final. Status and list contents remain mutable.
- OpenLibraryClient.searchBooks(String title) now returns Book, or null for no matches, rather than raw JSON.
- Client uses the supplied, trimmed and URL-encoded title, requests one result and four fields, and handles HTTP and JSON parsing itself.
- Connection timeout: 10 seconds. Request timeout: 20 seconds. Accept and a basic application User-Agent header are present; no contact address, retries, rate limiting, or caching.
- Package-visible buildSearchRequest, readSearchResponse, and parseFirstBook helpers allow offline tests.
- Required result fields: valid work ID and nonblank title. ID normalized to /works/OL…W. Missing/blank first author becomes Unknown author. Missing/invalid/nonpositive year becomes 0, displayed as Unknown year.
- Main now searches only The Hobbit, adds it with TO_READ, and prints the shelf once. Manually created demonstration books and raw JSON/debug output removed. Expected shelf count is now 1, not 3.
- Main handles IO errors, invalid operations, and interruption, preserving the interrupt flag.
- mvn clean test passed: 14 tests, zero failures/errors/skipped. Five service tests and nine offline client tests. A live run of the cleaned-up Main has not been verified here. Local socket binding was denied, so tests require no sockets or external network.

## Documentation

Read 12-java-foundation-finished.md for the current implementation and reasoning. Lessons 9–11 describe earlier learning steps and may show the previous structure. The README links the new guide.

## Next stage

The Java preparation stage discussed with the user is finished. Next, verify current official Java MCP options and Agentforce transport/connection requirements, then guide the user through a minimal MCP layer around search, add, list, and update status.

No MCP dependency or server has been added yet. Storage is in memory, no database or hosting exists, and concurrent access needs review when choosing the server model. Service compares exact IDs; API normalization does not normalize arbitrary manual inputs. Not all service validation branches have tests.

Do not introduce all these future topics at once. A useful first check is having the user run the simplified Main in VS Code, then introduce the MCP communication layer.
