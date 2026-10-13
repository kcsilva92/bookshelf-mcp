# Java basics through your bookshelf examples

Read this beside Main.java. The goal is to recognise the code you have already used, rather than memorise every Java keyword.

## A program follows instructions

Within a method, ordinary statements execute in order unless a loop, condition, return, or exception changes the flow. Creating an object and printing its information are separate instructions.

```java
Book book = new Book("sample-id", "The Hobbit", "J. R. R. Tolkien", 1937);
System.out.println(book.getTitle());
```

First create the object; then print its title. `sample-id` here is demonstration data, not an actual Open Library identifier.

## Variables: named places to keep values

```java
String title = "The Hobbit";
int publicationYear = 1937;
```

| Piece | Meaning |
|---|---|
| `String` | The type: text. |
| `title` | The variable's name. |
| `=` | Assign the value on the right to the variable on the left. |
| `"The Hobbit"` | The text value. |
| `;` | Ends the statement. |

`int` stores whole numbers. Text uses double quotes; integers do not. `boolean` stores true or false, which you used through methods such as isEmpty().

A name is not a type: publicationYear is a name, and int is its type.

## Declaration and reassignment

```java
String json = client.searchBooks("The Hobbit");
json = client.searchBooks("Pride and Prejudice");
```

This was an earlier exercise, not a recommendation to add the second search again. The first line declares json. The second replaces its value. It does not keep both responses automatically. Printing only after the second line shows the second value.

Your current example searches only for The Hobbit to keep things clear.

## Methods: named actions

```java
public String getTitle() {
    return this.title;
}
```

A method has a name, parameters in parentheses, and a body inside braces. This getter returns a String. `return` supplies a result and ends that method call.

```java
public void setStatus(ReadingStatus status) {
    this.status = status;
}
```

`void` means this action returns no value. It can still change an object.

A parameter is a named input in the method declaration. An argument is the actual value passed in a call:

```java
entry.setStatus(ReadingStatus.READING);
```

Here status is the parameter, and ReadingStatus.READING is the argument.

## Parentheses mean a method call

```java
entry.getStatus()
```

The parentheses call the method, even if there are no arguments.

```java
entry.getStatus
```

Without parentheses, Java looks for a field with that name. This caused the error you saw because getStatus is a method, not a field.

## Main: the starting method

```java
public static void main(String[] args) {
    System.out.println("Hello!");
}
```

For this application's launch, Java starts at main.

- public allows the method to be accessed.
- static means it belongs to the class and can be called without creating a Main object.
- void means there is no returned result.
- String[] args is an array of text arguments provided at launch. We have not used those arguments in this project.

Your current main also declares throws IOException, InterruptedException because it calls the HTTP client. See the HTTP guide for those words.

## Printing and new lines

```java
System.out.print("Hello");
System.out.print("World");
```

Produces `HelloWorld`: print adds no newline of its own.

```java
System.out.println("Hello");
System.out.println("World");
```

Prints each value on its own line. println adds a newline after the value.

```java
System.out.print("Hello\n\n");
System.out.println("World");
```

Two `\n` escape sequences move to the next line twice, leaving one blank line between the words. `System.out.println();` also prints an empty line.

```java
System.out.println("Title: " + book.getTitle());
```

When used with text, + joins values into a String. It also converts the publication year to text when you print `"Year: " + publicationYear`.

The editor's sysout shortcut generates a print statement. Pretty-printing JSON does not remove normal print statements.

## Braces and scope

```java
if (docs.isArray() && !docs.isEmpty()) {
    String title = "The Hobbit";
    System.out.println(title);
}
```

A local variable declared inside this block is available within that block, not after its closing brace. That is its scope.

Similarly, a request declared inside one method is not available inside another method. Moving the building and sending code into one searchBooks method fixed that earlier issue.

Indentation helps you see which block contains each instruction. Java uses braces to define the blocks, rather than indentation alone.

## Conditions and operators

```java
if (entry == null) {
    System.out.println("Book not found.");
} else {
    System.out.println(entry.getBook().getTitle());
}
```

if runs its block when a condition is true. else runs the alternative.

| Symbol | Meaning in our examples |
|---|---|
| `=` | Assign a value. |
| `== null` | Check that a reference is missing. |
| `!= null` | Check that a reference exists. |
| `!` | Not; reverses a boolean result. |
| `&&` | And; both conditions must hold. |
| `||` | Or; at least one condition must hold. |

For String contents, use `.equals(...)`, not `==`. The repository uses this to compare book IDs.

## Comments

```java
// This comment explains the next operation.
```

Comments help people understand the code. Java ignores them when executing the program. A useful comment explains purpose rather than merely repeating a method name.

## Tiny exercise

Explain `System.out.println("Books on shelf: " + shelf.size());` aloud. It asks the list for its count, joins that count with the label, and prints the resulting text with a newline.

Next: [Classes and objects](03-classes-and-objects-explained.md).
