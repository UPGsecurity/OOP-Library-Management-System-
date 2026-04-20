package library.search;

import library.models.BookItem;

import java.util.Date;
import java.util.List;

/**
 * Qidirish interfeysi.
 * Class diagrammada <<interface>> Search sifatida ko'rsatilgan.
 * Catalog bu interfeysi implement qiladi.
 */
public interface Search {
    List<BookItem> searchByTitle(String title);
    List<BookItem> searchByAuthor(String author);
    List<BookItem> searchBySubject(String subject);
    List<BookItem> searchByPubDate(Date publishDate);
}
