package tests;
import model.Book;

public class BookCreationTest {
    public static void main(String[] args) {
        Book book = new Book.Builder()
                .setTitle("Война и мир")
                .setAuthor("Лев Толстой")
                .setYear(1869)
                .build();

        if (book.getTitle().equals("Война и мир") &&
                book.getAuthor().equals("Лев Толстой") &&
                book.getYear() == 1869) {
            System.out.println("ПРОЙДЕН");
        } else {
            System.out.println("ПРОВАЛЕН");
        }
    }
}