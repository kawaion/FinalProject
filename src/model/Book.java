package model;

import validation.BookValidator;
import java.util.Objects;

public class Book {
    private final String title;
    private final String author;
    private final int year;

    private Book(String title, String author, int year) {
        this.title = title;
        this.author = author;
        this.year = year;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getYear() { return year; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return year == book.year &&
                Objects.equals(title, book.title) &&
                Objects.equals(author, book.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, year);
    }

    @Override
    public String toString() {
        return String.format("Книга: '%s', автор: %s, год: %d", title, author, year);
    }

    public static class Builder {
        private String title;
        private String author;
        private int year;

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setAuthor(String author) {
            this.author = author;
            return this;
        }

        public Builder setYear(int year) {
            this.year = year;
            return this;
        }

        public Book build() {
            boolean isValid = BookValidator.validate(title, author, year);

            if (!isValid) {
                throw new IllegalArgumentException("Неверные данные для создания книги (название: '"
                        + title + "', автор: '" + author + "', год: " + year + ")");
            }

            return new Book(title, author, year);
        }
    }
}