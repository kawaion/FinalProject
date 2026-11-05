package test.model;

import model.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    void build_createsValidBook() {
        Book book = new Book.Builder()
                .setTitle("Война и мир")
                .setAuthor("Лев Толстой")
                .setYear(1869)
                .build();

        assertNotNull(book);
        assertEquals("Война и мир", book.getTitle());
        assertEquals("Лев Толстой", book.getAuthor());
        assertEquals(1869, book.getYear());
    }

    @Test
    void build_throwsException_whenTitleIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            new Book.Builder()
                    .setTitle(null)
                    .setAuthor("Пушкин")
                    .setYear(1830)
                    .build();
        });
        assertTrue(ex.getMessage().contains("Неверные данные"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    void build_throwsException_whenTitleIsBlank(String blankTitle) {
        assertThrows(IllegalArgumentException.class, () -> {
            new Book.Builder()
                    .setTitle(blankTitle)
                    .setAuthor("Пушкин")
                    .setYear(1830)
                    .build();
        });
    }

    @Test
    void build_throwsException_whenAuthorIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Book.Builder()
                    .setTitle("Сказка")
                    .setAuthor(null)
                    .setYear(2000)
                    .build();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void build_throwsException_whenAuthorIsBlank(String blankAuthor) {
        assertThrows(IllegalArgumentException.class, () -> {
            new Book.Builder()
                    .setTitle("Сказка")
                    .setAuthor(blankAuthor)
                    .setYear(2000)
                    .build();
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {1650, 1869, 2026})
    void build_acceptsValidYears(int year) {
        assertDoesNotThrow(() -> {
            new Book.Builder()
                    .setTitle("Книга")
                    .setAuthor("Автор")
                    .setYear(year)
                    .build();
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {1449, 0, -100, 2027, 3000})
    void build_throwsException_whenYearIsInvalid(int year) {
        assertThrows(IllegalArgumentException.class, () -> {
            new Book.Builder()
                    .setTitle("Книга")
                    .setAuthor("Автор")
                    .setYear(year)
                    .build();
        });
    }

    @Test
    void equalsAndHashCode_workCorrectly() {
        Book book1 = new Book.Builder().setTitle("Книга").setAuthor("Автор").setYear(2020).build();
        Book book2 = new Book.Builder().setTitle("Книга").setAuthor("Автор").setYear(2020).build();
        Book book3 = new Book.Builder().setTitle("Другая").setAuthor("Автор").setYear(2020).build();

        assertEquals(book1, book2);
        assertEquals(book1.hashCode(), book2.hashCode());
        assertNotEquals(book1, book3);
        assertNotEquals(book1, null);
    }
}