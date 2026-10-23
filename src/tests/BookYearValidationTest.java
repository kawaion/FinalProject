package tests;
import model.Book;

public class BookYearValidationTest {
    public static void main(String[] args) {
        try {
            new Book.Builder().setTitle("A").setAuthor("B").setYear(1000).build();
            System.out.println("ПРОВАЛЕН");
        } catch (IllegalArgumentException e) {
            System.out.println("ПРОЙДЕН");
        }

        try {
            new Book.Builder().setTitle("A").setAuthor("B").setYear(2030).build();
            System.out.println("ПРОВАЛЕН");
        } catch (IllegalArgumentException e) {
            System.out.println("ПРОЙДЕН");
        }

        try {
            new Book.Builder().setTitle("A").setAuthor("B").setYear(1450).build();
            System.out.println("ПРОЙДЕН");
        } catch (IllegalArgumentException e) {
            System.out.println("ПРОВАЛЕН");
        }
    }
}
