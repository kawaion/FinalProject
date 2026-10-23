package tests;
import model.Book;

public class BookTitleValidationTest {
    public static void main(String[] args) {
        try {
            new Book.Builder()
                    .setTitle("")
                    .setAuthor("Пушкин")
                    .setYear(1830)
                    .build();
            System.out.println("ПРОВАЛЕН");
        } catch (IllegalArgumentException e) {
            System.out.println("ПРОЙДЕН");
        }

        try {
            new Book.Builder()
                    .setTitle(null)
                    .setAuthor("Пушкин")
                    .setYear(1830)
                    .build();
            System.out.println("ПРОВАЛЕН");
        } catch (IllegalArgumentException e) {
            System.out.println("ПРОЙДЕН");
        }
    }
}
