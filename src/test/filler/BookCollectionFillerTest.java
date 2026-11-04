package test.filler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Year;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.OptionalInt;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import collection.SortableList;
import filler.BookCollectionFiller;
import model.Book;

class BookCollectionFillerTest {
	private final List<String> messages = new ArrayList<>();

	private BookCollectionFiller filler(long size) {
		return new BookCollectionFiller(size, messages::add);
	}

	private static Book book(String title, String author, int year) {
		return new Book.Builder().setTitle(title).setAuthor(author).setYear(year).build();
	}

	private static List<Book> toList(SortableList<Book> list) {
		return list.stream().collect(Collectors.toList());
	}

	private static Path write(Path dir, String content) throws IOException {
		Path file = dir.resolve("books.txt");
		Files.write(file, content.getBytes(StandardCharsets.UTF_8));
		return file;
	}

	private boolean anyMessageContains(String part) {
		return messages.stream().anyMatch(m -> m.contains(part));
	}

	private static boolean isValid(String title, String author, int year) {
		try {
			book(title, author, year);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	private static OptionalInt findInvalidYear() {
		return IntStream.of(-1, 0, 1, 99999, Integer.MAX_VALUE).filter(y -> !isValid("Title", "Author", y)).findFirst();
	}

	@ParameterizedTest
	@ValueSource(longs = { 0, -1, Long.MIN_VALUE })
	void constructor_rejectsNonPositiveSize(long size) {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> filler(size));
		assertEquals("Размер массива не может быть меньше 1", ex.getMessage());
	}

	@ParameterizedTest
	@ValueSource(longs = { 1, 100, Long.MAX_VALUE })
	void constructor_acceptsPositiveSize(long size) {
		assertDoesNotThrow(() -> filler(size));
	}

	@Test
	void fillFromFile_parsesValidLines_trimsFields_keepsOrder(@TempDir Path dir) throws IOException {
		Path file = write(dir, "  Война и мир ; Лев Толстой ; 1869 \n1984;Джордж Оруэлл;1949\n");

		SortableList<Book> result = filler(10).fillFromFile(file.toString());

		assertEquals(List.of(book("Война и мир", "Лев Толстой", 1869), book("1984", "Джордж Оруэлл", 1949)),
				toList(result));
		assertTrue(messages.isEmpty());
	}

	@Test
	void fillFromFile_skipsBlankLinesSilently(@TempDir Path dir) throws IOException {
		Path file = write(dir, "\nA;B;2000\n   \n\t\nC;D;2010\n\n");

		SortableList<Book> result = filler(10).fillFromFile(file.toString());

		assertEquals(2, result.size());
		assertTrue(messages.isEmpty());
	}

	@Test
	void fillFromFile_fewerLinesThanSize_returnsAllAvailable(@TempDir Path dir) throws IOException {
		Path file = write(dir, "A;B;2000\nC;D;2010\n");

		assertEquals(2, filler(100).fillFromFile(file.toString()).size());
	}

	@Test
	void fillFromFile_moreLinesThanSize_returnsExactlySize(@TempDir Path dir) throws IOException {
		Path file = write(dir, "A;B;2000\nC;D;2010\nE;F;2020\n");

		SortableList<Book> result = filler(2).fillFromFile(file.toString());

		assertEquals(List.of(book("A", "B", 2000), book("C", "D", 2010)), toList(result));
	}

	@Test
	void fillFromFile_emptyFile_returnsEmptyList(@TempDir Path dir) throws IOException {
		Path file = write(dir, "");

		SortableList<Book> result = filler(5).fillFromFile(file.toString());

		assertTrue(result.isEmpty());
		assertTrue(messages.isEmpty());
	}

	@ParameterizedTest
	@ValueSource(strings = { "A;B", "A;B;2000;X", "no semicolons at all", "A;B;" })
	void fillFromFile_wrongFieldCount_reportsAndSkips(String badLine, @TempDir Path dir) throws IOException {
		Path file = write(dir, badLine + "\nGood;Author;2000\n");

		SortableList<Book> result = filler(10).fillFromFile(file.toString());

		assertEquals(List.of(book("Good", "Author", 2000)), toList(result));
		assertEquals(1, messages.size());
		assertTrue(messages.get(0).contains("Неверный формат строки"));
		assertTrue(messages.get(0).contains(badLine));
	}

	@ParameterizedTest
	@ValueSource(strings = { "abc", "20.5", "99999999999", "" })
	void fillFromFile_nonNumericYear_reportsAndSkips(String year, @TempDir Path dir) throws IOException {
		Path file = write(dir, "Title;Author;" + year + "\nGood;Author;2000\n");

		SortableList<Book> result = filler(10).fillFromFile(file.toString());

		assertEquals(List.of(book("Good", "Author", 2000)), toList(result));
		assertEquals(1, messages.size());
	}

	@Test
	void fillFromFile_nonNumericYear_messageMentionsParsingError(@TempDir Path dir) throws IOException {
		Path file = write(dir, "Title;Author;abc\n");

		filler(10).fillFromFile(file.toString());

		assertEquals(1, messages.size());
		assertTrue(messages.get(0).startsWith("Ошибка парсинга строки"));
	}

	@Test
	void fillFromFile_yearRejectedByValidator_reportsAndSkips(@TempDir Path dir) throws IOException {
		OptionalInt badYear = findInvalidYear();
		assumeTrue(badYear.isPresent(), "не удалось подобрать год, отвергаемый BookValidator");
		Path file = write(dir, "Title;Author;" + badYear.getAsInt() + "\nGood;Author;2000\n");

		SortableList<Book> result = filler(10).fillFromFile(file.toString());

		assertEquals(List.of(book("Good", "Author", 2000)), toList(result));
		assertEquals(1, messages.size());
		assertTrue(messages.get(0).startsWith("Ошибка парсинга строки"));
	}

	@Test
	void fillFromFile_emptyMiddleField_followsValidatorRules(@TempDir Path dir) throws IOException {
		Path file = write(dir, "Title;;2000\n");

		SortableList<Book> result = filler(10).fillFromFile(file.toString());

		if (isValid("Title", "", 2000)) {
			assertEquals(1, result.size());
			assertTrue(messages.isEmpty());
		} else {
			assertTrue(result.isEmpty());
			assertEquals(1, messages.size());
		}
	}

	@Test
	void fillFromFile_invalidLinesDoNotConsumeLimit(@TempDir Path dir) throws IOException {
		Path file = write(dir, "bad line\nA;B;2000\n\nC;D;abc\nE;F;2010\nG;H;2020\n");

		SortableList<Book> result = filler(2).fillFromFile(file.toString());

		assertEquals(List.of(book("A", "B", 2000), book("E", "F", 2010)), toList(result));
		assertEquals(2, messages.size());
	}

	@Test
	void fillFromFile_linesAfterLimitAreNotParsed(@TempDir Path dir) throws IOException {
		Path file = write(dir, "A;B;2000\nthis is bad\n");

		SortableList<Book> result = filler(1).fillFromFile(file.toString());

		assertEquals(1, result.size());
		assertTrue(messages.isEmpty(), "строка после достижения лимита не должна парситься");
	}

	@Test
	void fillFromFile_missingFile_returnsEmptyAndReports(@TempDir Path dir) {
		SortableList<Book> result = filler(5).fillFromFile(dir.resolve("nope.txt").toString());

		assertTrue(result.isEmpty());
		assertEquals(1, messages.size());
		assertTrue(messages.get(0).startsWith("Ошибка чтения файла"));
	}

	@Test
	void fillFromFile_wrongEncoding_returnsEmptyAndReports(@TempDir Path dir) throws IOException {
		Path file = dir.resolve("cp1251.txt");
		Files.write(file, "Война;Автор;2000\n".getBytes(Charset.forName("windows-1251")));

		SortableList<Book> result = filler(5).fillFromFile(file.toString());

		assertTrue(result.isEmpty());
		assertTrue(anyMessageContains("Ошибка чтения файла"));
	}

	@Test
	void fillFromFile_malformedBytesInMiddle_discardsPartialResult(@TempDir Path dir) throws IOException {
		Path file = dir.resolve("mixed.txt");
		byte[] good = "A;B;2000\n".getBytes(StandardCharsets.UTF_8);
		byte[] bad = "Война;Автор;2000\n".getBytes(Charset.forName("windows-1251"));
		byte[] all = new byte[good.length + bad.length];
		System.arraycopy(good, 0, all, 0, good.length);
		System.arraycopy(bad, 0, all, good.length, bad.length);
		Files.write(file, all);

		SortableList<Book> result = filler(5).fillFromFile(file.toString());

		assertTrue(result.isEmpty());
		assertTrue(anyMessageContains("Ошибка чтения файла"));
	}

	@Test
	void fillFromFile_nullFileName_throwsNpe() {
		assertThrows(NullPointerException.class, () -> filler(5).fillFromFile(null));
	}

	@RepeatedTest(5)
	void randomFill_producesValidBooksOfRequestedSize() {
		int size = 300;
		int maxYear = Year.now().getValue();

		SortableList<Book> result = filler(size).randomFill();

		assertEquals(size, result.size());
		for (Book b : toList(result)) {
			assertNotNull(b);
			assertTrue(b.getTitle().matches(".+, издание [1-9]"), "title: " + b.getTitle());
			assertFalse(b.getAuthor().trim().isEmpty());
			assertTrue(b.getYear() >= 1800 && b.getYear() <= maxYear, "year: " + b.getYear());
		}
		assertTrue(messages.isEmpty());
	}

	@Test
	void randomFill_isNotDegenerate() {
		Set<Book> unique = new HashSet<>(toList(filler(1000).randomFill()));

		assertTrue(unique.size() > 1);
	}

	@Test
	void randomFill_sizeOne() {
		assertEquals(1, filler(1).randomFill().size());
	}

	@Test
	void fillFromScanner_nullScanner_throws() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> filler(1).fillFromScanner(null));
		assertEquals("Scanner не может быть null", ex.getMessage());
	}

	@Test
	void fillFromScanner_readsBooksInOrder_andTrims() {
		Scanner sc = new Scanner("  T1 \n A1\n 2001 \nT2\nA2\n2002\nT3\nA3\n2003\n");

		SortableList<Book> result = filler(3).fillFromScanner(sc);

		assertEquals(List.of(book("T1", "A1", 2001), book("T2", "A2", 2002), book("T3", "A3", 2003)), toList(result));
	}

	@Test
	void fillFromScanner_handlesWindowsLineEndings() {
		Scanner sc = new Scanner("T\r\nA\r\n2000\r\n");

		assertEquals(List.of(book("T", "A", 2000)), toList(filler(1).fillFromScanner(sc)));
	}

	@Test
	void fillFromScanner_promptSequence() {
		filler(1).fillFromScanner(new Scanner("T\nA\n2000\n"));

		assertEquals(List.of("Книга 1 из 1:", "Название: ", "Автор: ", "Год издания: "), messages);
	}

	@Test
	void fillFromScanner_emptyTitle_repromptsUntilNonEmpty() {
		Scanner sc = new Scanner("\n   \nTitle\nAuthor\n2000\n");

		SortableList<Book> result = filler(1).fillFromScanner(sc);

		assertEquals(List.of(book("Title", "Author", 2000)), toList(result));
		long errors = messages.stream().filter(m -> m.contains("Значение не может быть пустым")).count();
		assertEquals(2, errors);
	}

	@Test
	void fillFromScanner_emptyAuthor_repromptsUntilNonEmpty() {
		Scanner sc = new Scanner("Title\n\nAuthor\n2000\n");

		SortableList<Book> result = filler(1).fillFromScanner(sc);

		assertEquals(List.of(book("Title", "Author", 2000)), toList(result));
		assertTrue(anyMessageContains("Значение не может быть пустым"));
	}

	@Test
	void fillFromScanner_nonNumericYear_repromptsAndEchoesInput() {
		Scanner sc = new Scanner("Title\nAuthor\nabc\n20.5\n1999\n");

		SortableList<Book> result = filler(1).fillFromScanner(sc);

		assertEquals(List.of(book("Title", "Author", 1999)), toList(result));
		assertTrue(anyMessageContains("Некорректный год: \"abc\""));
		assertTrue(anyMessageContains("Некорректный год: \"20.5\""));
	}

	@Test
	void fillFromScanner_plusSignYear_isAccepted() {
		Scanner sc = new Scanner("T\nA\n+2000\n");

		assertEquals(List.of(book("T", "A", 2000)), toList(filler(1).fillFromScanner(sc)));
	}

	@Test
	void fillFromScanner_bookRejectedByValidator_asksWholeBookAgain() {
		OptionalInt badYear = findInvalidYear();
		assumeTrue(badYear.isPresent(), "не удалось подобрать год, отвергаемый BookValidator");
		Scanner sc = new Scanner("Bad\nBad\n" + badYear.getAsInt() + "\nGood\nAuthor\n2000\n");

		SortableList<Book> result = filler(1).fillFromScanner(sc);

		assertEquals(List.of(book("Good", "Author", 2000)), toList(result));
		assertTrue(anyMessageContains("Не удалось создать книгу"));
		assertTrue(anyMessageContains("Введите книгу заново"));
	}

	@Test
	void fillFromScanner_inputEndsEarly_throwsNoSuchElement() {
		Scanner sc = new Scanner("T1\nA1\n2000\nT2\n");

		assertThrows(NoSuchElementException.class, () -> filler(2).fillFromScanner(sc));
	}

	@Test
	void filledList_canBeSortedByYear(@TempDir Path dir) throws IOException {
		Path file = write(dir, "C;X;2020\nA;X;1990\nB;X;2005\n");
		SortableList<Book> result = filler(3).fillFromFile(file.toString());

		result.sort(Comparator.comparingInt(Book::getYear));

		assertEquals(List.of(1990, 2005, 2020),
				toList(result).stream().map(Book::getYear).collect(Collectors.toList()));
	}
}