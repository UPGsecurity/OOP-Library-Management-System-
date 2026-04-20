package library.library;

import library.accounts.Librarian;
import library.accounts.Member;
import library.enums.BookStatus;
import library.enums.ReservationStatus;
import library.models.*;
import library.notifications.EmailNotification;
import library.search.Catalog;
import library.transactions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Kutubxona bosh xizmat klassi.
 * Barcha asosiy operatsiyalarni boshqaradi.
 * Activity Diagramdagi barcha jarayonlar shu yerda.
 */
public class LibraryService {

    private Catalog    catalog;
    private Library    library;

    // Faol lendinglar ro'yxati (muddati o'tganlarni tekshirish uchun)
    private List<BookLending> allLendings = new ArrayList<>();

    public LibraryService(Library library) {
        this.library = library;
        this.catalog = new Catalog();
    }

    // ─────────────────────────────────────────────────────────────
    //  KITOB OLISH (CHECK-OUT) - Activity Diagram
    // ─────────────────────────────────────────────────────────────

    /**
     * Kitob checkout jarayoni.
     * 1. Karta skanerlash
     * 2. Kitob barcode skanerlash
     * 3. Reference-only tekshiruvi
     * 4. Limit tekshiruvi
     * 5. Zahira tekshiruvi
     * 6. Tranzaksiya yaratish
     */
    public boolean checkoutBook(Member member, BookItem bookItem,
                                BarcodeReader reader) {
        System.out.println("\n===== KITOB OLISH JARAYONI =====");

        // 1. Kutubxona kartasini tekshirish
        if (member.getLibraryCard() == null || !member.getLibraryCard().isActive()) {
            System.out.println("[XATO] Faol kutubxona kartasi yo'q.");
            return false;
        }

        // 2. Barcode skanerlash
        String scanned = reader.scan(bookItem.getBarcode());
        if (scanned == null) return false;

        // 3-5. Member o'zi checkout qiladi (ichida barcha tekshiruvlar bor)
        boolean success = member.checkoutBookItem(bookItem);

        if (success) {
            BookLending lending = new BookLending(bookItem, member);
            allLendings.add(lending);
        }

        System.out.println("================================\n");
        return success;
    }

    // ─────────────────────────────────────────────────────────────
    //  KITOB QAYTARISH (RETURN) - Activity Diagram
    // ─────────────────────────────────────────────────────────────

    /**
     * Kitob qaytarish jarayoni.
     * 1. Barcode skanerlash
     * 2. Muddatni tekshirish - jarima hisoblash
     * 3. Zahira tekshiruvi
     * 4. Holat yangilash + bildirishnoma
     */
    public boolean returnBook(Member member, BookItem bookItem,
                              BarcodeReader reader, Librarian librarian) {
        System.out.println("\n===== KITOB QAYTARISH JARAYONI =====");

        // 1. Barcode skanerlash
        reader.scan(bookItem.getBarcode());

        // 2. Lending topish
        BookLending lending = allLendings.stream()
            .filter(l -> l.getBookItem().equals(bookItem)
                      && l.getMember().equals(member)
                      && l.getReturnDate() == null)
            .findFirst().orElse(null);

        if (lending == null) {
            System.out.println("[XATO] Bu kitob sizda chiqarilmagan.");
            return false;
        }

        // 3. Muddati o'tganmi? - Jarima hisoblash
        if (lending.isOverdue()) {
            Fine fine = new Fine(lending);
            System.out.println("[JARIMA] To'lash kerak: $" + fine.getAmount());
            // Naqd pul orqali to'lash (demo)
            CashTransaction payment = new CashTransaction(fine.getAmount(), fine.getAmount());
            payment.initiateTransaction();
        }

        // 4. Qaytarish
        lending.returnBook();
        member.returnBookItem(bookItem);
        allLendings.remove(lending);

        // 5. Zahira bormi tekshirish
        boolean hasReservation = checkAndHandleReservation(bookItem, librarian);
        if (!hasReservation) {
            bookItem.setStatus(BookStatus.AVAILABLE);
            System.out.println("[INFO] Kitob holati: AVAILABLE");
        }

        System.out.println("====================================\n");
        return true;
    }

    // ─────────────────────────────────────────────────────────────
    //  KITOBNI YANGILASH (RENEW) - Activity Diagram
    // ─────────────────────────────────────────────────────────────

    /**
     * Kitob muddatini uzaytirish (renew).
     * 1. Karta va barcode skanerlash
     * 2. Muddatni tekshirish - jarima hisoblash
     * 3. Zahira tekshiruvi
     * 4. Yangi muddat yaratish
     */
    public boolean renewBook(Member member, BookItem bookItem, BarcodeReader reader) {
        System.out.println("\n===== KITOBNI YANGILASH JARAYONI =====");

        reader.scan(bookItem.getBarcode());

        BookLending lending = allLendings.stream()
            .filter(l -> l.getBookItem().equals(bookItem)
                      && l.getMember().equals(member)
                      && l.getReturnDate() == null)
            .findFirst().orElse(null);

        if (lending == null) {
            System.out.println("[XATO] Aktiv lending topilmadi.");
            return false;
        }

        // Muddati o'tganmi? - Avval jarimani to'lash kerak
        if (lending.isOverdue()) {
            Fine fine = new Fine(lending);
            CashTransaction payment = new CashTransaction(fine.getAmount(), fine.getAmount());
            payment.initiateTransaction();
        }

        // Boshqa a'zo zahiralagan?
        boolean isReservedByOther = allLendings.stream()
            .noneMatch(l -> l.getBookItem().equals(bookItem)) &&
            bookItem.getStatus() == BookStatus.RESERVED;

        boolean renewed = lending.renewBook(isReservedByOther);

        if (!renewed) {
            // Bildirishnoma - zahira egasiga
            System.out.println("[INFO] Kitob zahira egasiga bildiriladi.");
            bookItem.setStatus(BookStatus.RESERVED);
        }

        System.out.println("======================================\n");
        return renewed;
    }

    // ─────────────────────────────────────────────────────────────
    //  YORDAMCHI METODLAR
    // ─────────────────────────────────────────────────────────────

    /**
     * Kitob qaytarilganda zahira borligini tekshirish va bildirishnoma yuborish.
     */
    private boolean checkAndHandleReservation(BookItem bookItem, Librarian librarian) {
        // Demo: haqiqiy loyihada DB dan zahira tekshiriladi
        if (bookItem.getStatus() == BookStatus.RESERVED) {
            bookItem.setStatus(BookStatus.RESERVED);
            System.out.println("[INFO] Kitob zahiralangan. Holat: RESERVED. Bildirishnoma yuborildi.");
            return true;
        }
        return false;
    }

    /**
     * Muddati o'tgan barcha kitoblar uchun bildirishnoma yuborish.
     * Use Case: System -> "Send overdue notification"
     */
    public void sendOverdueNotifications() {
        System.out.println("\n===== MUDDATI O'TGAN KITOBLAR =====");
        allLendings.stream()
            .filter(BookLending::isOverdue)
            .forEach(lending -> {
                String email   = lending.getMember().getPerson().getEmail();
                String content = "\"" + lending.getBookItem().getTitle()
                                + "\" kitobingiz " + lending.getOverdueDays()
                                + " kun kechikdi. Jarima: $" + lending.getOverdueDays() + ".";
                EmailNotification notif = new EmailNotification(
                    (int)(Math.random() * 9999), content, email
                );
                notif.sendNotification();
            });
        System.out.println("====================================\n");
    }

    // ─────────────────────────────────────────────────────────────
    //  GETTERS
    // ─────────────────────────────────────────────────────────────

    public Catalog          getCatalog()    { return catalog; }
    public List<BookLending> getAllLendings(){ return allLendings; }
}
