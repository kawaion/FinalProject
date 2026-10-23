package tests;
import model.Book;

public class BookAuthorValidationTest {
    public static void main(String[] args) {
        try {
            new Book.Builder()
                    .setTitle("Сказка")
                    .setAuthor("   ")
                    .setYear(2000)
                    .build();
            System.out.println("ПРОВАЛЕН");
        } catch (IllegalArgumentException e) {
            System.out.println("ПРОЙДЕН");
        }
    }
}
