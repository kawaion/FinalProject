package ui;

import collection.SortableList;
import filler.BookCollectionFiller;
import model.Book;
import sorters.EvenFieldSort;
import sorters.MergeSort;
import sorters.QuickSort;
import sorters.SortStrategy;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Menu {
    public static void run(Scanner scanner) {
        SortableList<Book> collection = new SortableList<>();
        while(true) {
            System.out.printf("Здравствуйте, выберите действие: %n" +
                    "1. Заполнить коллекцию %n" +
                    "2. Отсортировать коллекцию %n" +
                    "3. Подсчитать число вхождений элемента N в коллекции \n" +
                    "0. Закончить работу %n");
            int choice = scanner.nextInt();
            System.out.println();
            switch (choice) {
                case 1:
                    collection = fillCollection(scanner);
                    System.out.println("Коллекция заполнена. \n");
                    break;
                case 2:
                    assert collection != null;
                    if(!collection.isEmpty()) {
                        collection = sortCollection(collection, scanner);
                        System.out.println("Коллекция отсортирована. \n");
                    }else {
                        System.out.println("Вы еще не заполнили коллекцию. \n");
                    }
                    break;
                case 3:
                    assert collection != null;
                    if (!collection.isEmpty()) {
                        countOccurrencesMultithreaded(collection, scanner);
                    } else {
                        System.out.println("Вы еще не заполнили коллекцию. \n");
                    }
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Неверный ввод, попробуйте еще раз. \n");
            }
        }
    }

    private static SortableList<Book> fillCollection(Scanner scanner) {
        System.out.println("Укажите число элементов: ");
        int size = scanner.nextInt();
        System.out.println();
        SortableList<Book> collection;
        BookCollectionFiller filler = new BookCollectionFiller(size, System.out::println);
        System.out.printf("Выберите способ заполнения: %n" +
                "1. Заполнить вручную %n" +
                "2. Заполнить случайными значениями %n" +
                "3. Заполнить из файла %n");
        int choice = scanner.nextInt();
        System.out.println();
        switch(choice) {
            case 1:
                collection = filler.fillFromScanner(scanner);
                System.out.println("Список полученных книг:");
                System.out.println(collection);
                return collection;
            case 2:
                collection = filler.randomFill();
                System.out.println("Список полученных книг:");
                System.out.println(collection);
                return collection;
            case 3:
                System.out.println("Введите название файла: ");
                String fileName = scanner.next();
                collection = filler.fillFromFile(fileName);
                System.out.println("Список полученных книг:");
                System.out.println(collection);
                return collection;
            default:
                return null;
        }
    }

    private static SortableList<Book> sortCollection(SortableList<Book> collection, Scanner scanner) {
        System.out.println("Тип сортировки:");
        System.out.println("1. Обычная сортировка");
        System.out.println("2. Сортировка только чётных значений");
        int modeChoice = scanner.nextInt();

        boolean evenOnly;
        switch (modeChoice) {
            case 1 -> evenOnly = false;
            case 2 -> evenOnly = true;
            default -> throw new IllegalArgumentException("Неверный ввод");
        }

        System.out.println("Выберите метод сортировки:");
        System.out.println("1. Быстрая сортировка");
        System.out.println("2. Сортировка слиянием");
        int methodChoice = scanner.nextInt();

        SortStrategy<Book> baseStrategy = switch (methodChoice) {
            case 1 -> new QuickSort<>();
            case 2 -> new MergeSort<>();
            default -> throw new IllegalArgumentException("Неверный ввод");
        };

        Comparator<Book> cmp;
        if (evenOnly) {
            System.out.println("Сортировка по году (единственный доступный вариант для чётных значений)");
            cmp = Comparator.comparingInt(Book::getYear);
        } else {
            System.out.println("Сортировать по:");
            System.out.println("1. названию");
            System.out.println("2. автору");
            System.out.println("3. году");
            int fieldChoice = scanner.nextInt();

            cmp = switch (fieldChoice) {
                case 1 -> Comparator.comparing(Book::getTitle);
                case 2 -> Comparator.comparing(Book::getAuthor);
                case 3 -> Comparator.comparingInt(Book::getYear);
                default -> throw new IllegalArgumentException("Неверный ввод");
            };
        }

        SortStrategy<Book> strategy = evenOnly
                ? new EvenFieldSort<>(baseStrategy, Book::getYear)
                : baseStrategy;

        System.out.println();
        SortableList<Book> sortedCollection = collection.customSort(strategy, cmp);
        System.out.println("Отсортированная коллекция: ");
        System.out.println(sortedCollection + "\n");
        System.out.print("""
                Сохранить отсортированную коллекцию в файл?\s
                1. да\s
                0. нет
                """);
        int saveChoice = scanner.nextInt();
        scanner.nextLine();

        if (saveChoice == 1) {
            try {
                saveCollectionToFile(sortedCollection);
                System.out.println("Коллекция успешно добавлена в файл results.txt\n");
            } catch (IOException e) {
                System.out.println("Ошибка при записи в файл: " + e.getMessage() + "\n");
            }
        }

        return sortedCollection;
    }

    private static void saveCollectionToFile(SortableList<Book> collection) throws IOException {
        Path path = Path.of("results.txt");
        try (PrintWriter writer = new PrintWriter(
                Files.newBufferedWriter(path,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND))) {
            for (int i = 0; i < collection.size(); i++) {
                writer.println(collection.get(i));
            }
            writer.println();
        }
    }

    private static void countOccurrencesMultithreaded(SortableList<Book> collection, Scanner scanner) {
        System.out.println("Введите данные книги для поиска:");
        scanner.nextLine();

        System.out.print("Название: ");
        String title = scanner.nextLine().trim();

        System.out.print("Автор: ");
        String author = scanner.nextLine().trim();

        System.out.print("Год издания: ");
        int year = scanner.nextInt();
        System.out.println();

        final int threadsCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadsCount);
        List<Future<Integer>> futures = new ArrayList<>();

        int totalSize = collection.size();
        int chunkSize = (int) Math.ceil((double) totalSize / threadsCount);

        for (int i = 0; i < threadsCount; i++) {
            final int start = i * chunkSize;
            final int end = Math.min(start + chunkSize, totalSize);

            if (start >= end) break;

            Future<Integer> future = executor.submit(() -> {
                int count = 0;
                for (int j = start; j < end; j++) {
                    Book book = collection.get(j);
                    if (book.getTitle().equalsIgnoreCase(title)
                            && book.getAuthor().equalsIgnoreCase(author)
                            && book.getYear() == year) {
                        count++;
                    }
                }
                return count;
            });
            futures.add(future);
        }

        int totalOccurrences = 0;
        try {
            for (Future<Integer> future : futures) {
                totalOccurrences += future.get();
            }
            System.out.printf(
                    "Книга \"%s\" (автор: %s, год: %d) встречается в коллекции %d раз(а).%n%n",
                    title, author, year, totalOccurrences);
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Ошибка при многопоточном поиске: " + e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }
        System.out.print("""
                Сохранить результаты поиска в файл?\s
                1. да\s
                0. нет
                """);
        int saveChoice = scanner.nextInt();
        scanner.nextLine();
        if (saveChoice == 1) {
            try {
                saveSearchResultsToFile(title, author, year, totalOccurrences);
                System.out.println("Результаты успешно записаны в файл \n");
            } catch (IOException e) {
                System.out.println("Ошибка при записи в файл: " + e.getMessage() + "\n");
            }
        }
    }

    private static void saveSearchResultsToFile(String title, String author,
                                                int year, int occurrences) throws IOException{
        Path path = Path.of("results.txt");
        try (PrintWriter writer = new PrintWriter(
                Files.newBufferedWriter(path,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND))) {
            writer.println("Результаты поиска по книге: ");
            writer.printf("Книга \"%s\" (автор: %s, год: %d) встречается в коллекции %d раз(а).%n%n",
                    title, author, year, occurrences);
        }
    }
}
