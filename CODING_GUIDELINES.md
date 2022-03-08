# Coding Guidelines

- All features or bug fixes must be tested by one or more tests.
- Java files must be formatted using Intellij IDEA default code style.
- All files must follow the `.editorconfig` file located at the root of the project.

### Specific coding rules

- No spaces are left inside the parentheses
- Opening brackets goes in the same line as the last statement
```java
    if (condition) {
        stuff;
    }
```
- Put a single space before every "{".
- Separate all binary operators, such as "+", "-", "*", "/", "%", etc., with a single space. The exception is unary operators, such as "++", "--", unary minus "-", etc, which do not need to be separated with a single space.
```java
    // Example
    public static void main(String[] arg) {
      System.out.println("Hello" + " " + "World");
    }
 ```
- Declarations should be made on multiple lines, even if they can be shorten to one line.
```java
    // Example Declaration
    BigDecimal foo = new BigDecimal(0), bar = new BigDecimal(0);
    // Corrected
    BigDecimal foo = new BigDecimal(0);
    BigDecimal bar = new BigDecimal(0);
```
