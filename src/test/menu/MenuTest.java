package test.menu;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ui.Menu;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class MenuTest {

    private Scanner scannerFrom(String input) {
        return new Scanner(new ByteArrayInputStream(input.getBytes()));
    }

    @Test
    void testExitImmediately() {
        Scanner scanner = scannerFrom("0\n");
        assertDoesNotThrow(() -> Menu.run(scanner));
    }

    @Test
    void testInvalidInputThenExit() {
        Scanner scanner = scannerFrom("99\n0\n");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));
        try {
            Menu.run(scanner);
        } finally {
            System.setOut(original);
        }
        assertTrue(out.toString().contains("Неверный ввод"));
    }

    @Test
    void testSortEmptyCollection() {
        Scanner scanner = scannerFrom("2\n0\n");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));
        try {
            Menu.run(scanner);
        } finally {
            System.setOut(original);
        }
        assertTrue(out.toString().contains("Вы еще не заполнили коллекцию"));
    }

    @Test
    void testCountOccurrencesEmptyCollection() {
        Scanner scanner = scannerFrom("3\n0\n");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));
        try {
            Menu.run(scanner);
        } finally {
            System.setOut(original);
        }
        assertTrue(out.toString().contains("Вы еще не заполнили коллекцию"));
    }

    @Test
    void testFillRandomThenSort(@TempDir Path tempDir) {
        String input = """
                1
                5
                2
                2
                1
                2
                3
                0
                0
                """;
        Scanner scanner = scannerFrom(input);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));
        try {
            Menu.run(scanner);
        } finally {
            System.setOut(original);
        }
        String output = out.toString();
        assertTrue(output.contains("Коллекция заполнена"));
        assertTrue(output.contains("Коллекция отсортирована"));
        assertTrue(output.contains("Отсортированная коллекция"));
    }

    @Test
    void testFillFromScannerAndCountOccurrences() {
        String input = """
                1
                2
                1
                Книга1
                Автор1
                2000
                Книга1
                Автор1
                2000
                3
                Книга1
                Автор1
                2000
                0
                """;
        Scanner scanner = scannerFrom(input);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));
        try {
            Menu.run(scanner);
        } finally {
            System.setOut(original);
        }
        String output = out.toString();
        assertTrue(output.contains("встречается в коллекции 2 раз"));
    }
}