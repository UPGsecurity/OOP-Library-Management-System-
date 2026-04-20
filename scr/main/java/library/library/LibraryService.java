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

public class LibraryService {

    private Catalog    catalog;
    private Library    library;

    private List<BookLending> allLendings = new ArrayList<>();

    public LibraryService(Library library) {
        this.library = library;
        this.catalog = new Catalog();
    }

    public boolean checkoutBook(Member member, BookItem bookItem,
                                BarcodeReader reader) {
        System.out.println("\n===== KITOB OLISH JARAYONI =====");

        if (member.getLibraryCard() == null || !member.getLibraryCard().isActive()) {
            System.out.println("[XATO] Faol kutubxona kartasi yo'q.");
            return false;
        }

        String scanned = reader.scan(bookItem.getBarcode());
        if (scanned == null) return false;

        boolean success = member.checkoutBookItem(bookItem);

        if (success) {
            BookLending lending = new BookLending(bookItem, member);
            allLendings.add(lending);
        }

        System.out.println("================================\n");
        return success;
    }

  
    public boolean returnBook(Member member, BookItem bookItem,
                              BarcodeReader reader, Librarian librarian) {
        System.out.println("\n===== KITOB QAYTARISH JARAYONI =====");

        reader.scan(bookItem.getBarcode());

        BookLending lending = allLendings.stream()
            .filter(l -> l.getBookItem().equals(bookItem)
                      && l.getMember().equals(member)
                      && l.getReturnDate() == null)
            .findFirst().orElse(null);

        if (lending == null) {
            System.out.println("[XATO] Bu kitob sizda chiqarilmagan.");
            return false;
        }

        if (lending.isOverdue()) {
            Fine fine = new Fine(lending);
            System.out.println("[JARIMA] To'lash kerak: $" + fine.getAmount());

            CashTransaction payment = new CashTransaction(fine.getAmount(), fine.getAmount());
            payment.initiateTransaction();
        }

        lending.returnBook();
        member.returnBookItem(bookItem);
        allLendings.remove(lending);

        boolean hasReservation = checkAndHandleReservation(bookItem, librarian);
        if (!hasReservation) {
            bookItem.setStatus(BookStatus.AVAILABLE);
            System.out.println("[INFO] Kitob holati: AVAILABLE");
        }

        System.out.println("====================================\n");
        return true;
    }

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

        if (lending.isOverdue()) {
            Fine fine = new Fine(lending);
            CashTransaction payment = new CashTransaction(fine.getAmount(), fine.getAmount());
            payment.initiateTransaction();
        }

        boolean isReservedByOther = allLendings.stream()
            .noneMatch(l -> l.getBookItem().equals(bookItem)) &&
            bookItem.getStatus() == BookStatus.RESERVED;

        boolean renewed = lending.renewBook(isReservedByOther);

        if (!renewed) {
  
            System.out.println("[INFO] Kitob zahira egasiga bildiriladi.");
            bookItem.setStatus(BookStatus.RESERVED);
        }

        System.out.println("======================================\n");
        return renewed;
    }

  
    private boolean checkAndHandleReservation(BookItem bookItem, Librarian librarian) {
  
        if (bookItem.getStatus() == BookStatus.RESERVED) {
            bookItem.setStatus(BookStatus.RESERVED);
            System.out.println("[INFO] Kitob zahiralangan. Holat: RESERVED. Bildirishnoma yuborildi.");
            return true;
        }
        return false;
    }

   
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


    public Catalog          getCatalog()    { return catalog; }
    public List<BookLending> getAllLendings(){ return allLendings; }
}
