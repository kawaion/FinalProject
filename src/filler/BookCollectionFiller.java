package filler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import model.Book;

public class BookCollectionFiller {
	private final long size;
	private final Consumer<String> output;

	private static final String[] SAMPLE_TITLES = { "Война и мир", "1984", "Мастер и Маргарита",
			"Преступление и наказание", "Гарри Поттер", "Властелин колец", "Дюна", "Три товарища",
			"Автостопом по галактике", "Шерлок Холмс", "Мертвые души", "Обломов" };

	private static final String[] SAMPLE_AUTHORS = { "Лев Толстой", "Джордж Оруэлл", "Михаил Булгаков",
			"Федор Достоевский", "Джоан Роулинг", "Джон Толкин", "Фрэнк Герберт", "Эрих Мария Ремарк", "Дуглас Адамс",
			"Артур Конан Дойл", "Николай Гоголь", "Иван Гончаров" };

	public BookCollectionFiller(long size, Consumer<String> output) {
		if (size <= 0) {
			throw new IllegalArgumentException("Размер массива не может быть меньше 1");
		}

		this.size = size;
		this.output = output;
	}

	public List<Book> fillFromFile(String fileName) {
		try (Stream<String> lines = Files.lines(Paths.get(fileName), StandardCharsets.UTF_8)) {
			return lines.filter(line -> !line.trim().isEmpty()).map(this::parseLine).filter(book -> book != null)
					.limit(size).collect(Collectors.toList());
		} catch (IOException | UncheckedIOException e) {
			output.accept("Ошибка чтения файла: " + e.getMessage());
			return new ArrayList<Book>();
		}
	}

	private Book parseLine(String line) {
		String[] parts = line.split(";");
		if (parts.length != 3) {
			output.accept("Неверный формат строки, ожидалось 3 поля через ';': " + line);
			return null;
		}
		try {
			return new Book.Builder().setTitle(parts[0].trim()).setAuthor(parts[1].trim())
					.setYear(Integer.parseInt(parts[2].trim())).build();
		} catch (Exception e) {
			output.accept("Ошибка парсинга строки \"" + line + "\": " + e.getMessage());
			return null;
		}
	}

	public List<Book> randomFill() {
		int maxYear = Year.now().getValue();

		return Stream.generate(() -> {
			ThreadLocalRandom rnd = ThreadLocalRandom.current();

			String randomTitle = SAMPLE_TITLES[rnd.nextInt(SAMPLE_TITLES.length)] + ", издание " + rnd.nextInt(1, 10);
			String randomAuthor = SAMPLE_AUTHORS[rnd.nextInt(SAMPLE_AUTHORS.length)];
			int randomYear = rnd.nextInt(1800, maxYear + 1);

			return new Book.Builder().setTitle(randomTitle).setAuthor(randomAuthor).setYear(randomYear).build();
		}).limit(size).collect(Collectors.toList());
	}

	public List<Book> fillFromScanner(Scanner scanner) {
		if (scanner == null) {
			throw new IllegalArgumentException("Scanner не может быть null");
		}

		return LongStream.rangeClosed(1, size).mapToObj(i -> readBook(scanner, i)).collect(Collectors.toList());
	}

	private Book readBook(Scanner scanner, long number) {
		while (true) {
			output.accept("Книга " + number + " из " + size + ":");

			String title = readNonEmpty(scanner, "Название: ");
			String author = readNonEmpty(scanner, "Автор: ");
			int year = readYear(scanner);

			try {
				return new Book.Builder().setTitle(title).setAuthor(author).setYear(year).build();
			} catch (Exception e) {
				output.accept("Не удалось создать книгу: " + e.getMessage() + ". Введите книгу заново");
			}
		}
	}

	private String readNonEmpty(Scanner scanner, String prompt) {
		while (true) {
			output.accept(prompt);
			String input = scanner.nextLine().trim();
			if (!input.isEmpty()) {
				return input;
			}
			output.accept("Значение не может быть пустым, повторите ввод");
		}
	}

	private int readYear(Scanner scanner) {
		while (true) {
			output.accept("Год издания: ");
			String input = scanner.nextLine().trim();
			try {
				return Integer.parseInt(input);
			} catch (NumberFormatException e) {
				output.accept("Некорректный год: \"" + input + "\", повторите ввод");
			}
		}
	}
}
