package library.search;

import library.models.BookItem;

import java.util.*;

/**
 * Barcha kitoblar katalogi.
 * Search interfeysini implement qiladi.
 * HashMap yordamida tez qidirish ta'minlanadi.
 */
public class Catalog implements Search {

    private Date creationDate;
    private int  totalBooks;

    // Har xil indekslar - tez qidirish uchun
    private Map<String, List<BookItem>> bookTitles;
    private Map<String, List<BookItem>> bookAuthors;
    private Map<String, List<BookItem>> bookSubjects;
    private Map<Date,   List<BookItem>> bookPublicationDates;

    // Barcha kitoblar ro'yxati
    private List<BookItem> allBooks;

    public Catalog() {
        this.creationDate        = new Date();
        this.totalBooks          = 0;
        this.bookTitles          = new HashMap<>();
        this.bookAuthors         = new HashMap<>();
        this.bookSubjects        = new HashMap<>();
        this.bookPublicationDates = new HashMap<>();
        this.allBooks            = new ArrayList<>();
    }

    /**
     * Kitob nomini qidirish (katta-kichik harf farqi yo'q)
     */
    @Override
    public List<BookItem> searchByTitle(String title) {
        if (title == null || title.isBlank()) return new ArrayList<>();
        String key = title.toLowerCase().trim();
        List<BookItem> result = bookTitles.getOrDefault(key, new ArrayList<>());
        System.out.println("[QIDIRUV] Nom bo'yicha: '" + title + "' - " + result.size() + " ta topildi.");
        return result;
    }

    /**
     * Muallif bo'yicha qidirish
     */
    @Override
    public List<BookItem> searchByAuthor(String author) {
        if (author == null || author.isBlank()) return new ArrayList<>();
        String key = author.toLowerCase().trim();
        List<BookItem> result = bookAuthors.getOrDefault(key, new ArrayList<>());
        System.out.println("[QIDIRUV] Muallif bo'yicha: '" + author + "' - " + result.size() + " ta topildi.");
        return result;
    }

    /**
     * Mavzu bo'yicha qidirish
     */
    @Override
    public List<BookItem> searchBySubject(String subject) {
        if (subject == null || subject.isBlank()) return new ArrayList<>();
        String key = subject.toLowerCase().trim();
        List<BookItem> result = bookSubjects.getOrDefault(key, new ArrayList<>());
        System.out.println("[QIDIRUV] Mavzu bo'yicha: '" + subject + "' - " + result.size() + " ta topildi.");
        return result;
    }

    /**
     * Nashr sanasi bo'yicha qidirish
     */
    @Override
    public List<BookItem> searchByPubDate(Date publishDate) {
        if (publishDate == null) return new ArrayList<>();
        return bookPublicationDates.getOrDefault(publishDate, new ArrayList<>());
    }

    /**
     * Katalogga kitob qo'shish
     */
    public boolean updateCatalog(BookItem bookItem) {
        if (bookItem == null) return false;

        String titleKey   = bookItem.getTitle().toLowerCase().trim();
        String authorKey  = (bookItem.getAuthor() != null)
                            ? bookItem.getAuthor().getName().toLowerCase().trim() : "unknown";
        String subjectKey = bookItem.getSubject().toLowerCase().trim();

        bookTitles .computeIfAbsent(titleKey,   k -> new ArrayList<>()).add(bookItem);
        bookAuthors.computeIfAbsent(authorKey,  k -> new ArrayList<>()).add(bookItem);
        bookSubjects.computeIfAbsent(subjectKey, k -> new ArrayList<>()).add(bookItem);

        if (bookItem.getPublicationDate() != null) {
            bookPublicationDates
                .computeIfAbsent(bookItem.getPublicationDate(), k -> new ArrayList<>())
                .add(bookItem);
        }

        allBooks.add(bookItem);
        totalBooks++;
        return true;
    }

    /**
     * Katalogdan kitob o'chirish
     */
    public boolean removeFromCatalog(BookItem bookItem) {
        if (bookItem == null || !allBooks.contains(bookItem)) return false;

        String titleKey = bookItem.getTitle().toLowerCase().trim();
        bookTitles.getOrDefault(titleKey, new ArrayList<>()).remove(bookItem);
        allBooks.remove(bookItem);
        totalBooks--;
        return true;
    }

    /**
     * Barcha kitoblarni ko'rish
     */
    public List<BookItem> getAllBooks() { return Collections.unmodifiableList(allBooks); }
    public int            getTotalBooks() { return totalBooks; }
    public Date           getCreationDate() { return creationDate; }
}
