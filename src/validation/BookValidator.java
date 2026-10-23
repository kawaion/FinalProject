package validation;

public class BookValidator {

    public static boolean validate(String title, String author, int year) {
        if (title == null || title.isBlank()) {
            return false;
        }

        if (author == null || author.isBlank()) {
            return false;
        }

        if (year < 1564 || year > 2026) {
            return false;
        }

        return true;
    }
}