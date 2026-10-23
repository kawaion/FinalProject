package tests;
import model.Book;

public class BookEqualsTest {
    public static void main(String[] args) {
        Book book1 = new Book.Builder().setTitle("Книга").setAuthor("Автор").setYear(2020).build();
        Book book2 = new Book.Builder().setTitle("Книга").setAuthor("Автор").setYear(2020).build();
        Book book3 = new Book.Builder().setTitle("Другая").setAuthor("Автор").setYear(2020).build();

        if (book1.equals(book2) && book1.hashCode() == book2.hashCode()) {
            System.out.println("ПРОЙДЕН");
        } else {
            System.out.println("ПРОВАЛЕН");
        }

        if (!book1.equals(book3)) {
            System.out.println("ПРОЙДЕН");
        } else {
            System.out.println("ПРОВАЛЕН");
        }
    }
}
