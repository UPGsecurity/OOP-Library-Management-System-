package library.accounts;

import library.enums.AccountStatus;
import library.enums.BookStatus;
import library.models.*;
import library.notifications.EmailNotification;
import library.search.Catalog;
import library.transactions.BookLending;
import library.transactions.BookReservation;
import library.enums.ReservationStatus;

import java.util.List;

/**
 * Kutubxonachi.
 * Account klassidan meros oladi.
 * Katalog, a'zolik va kitob berish/qabul qilishni boshqaradi.
 */
public class Librarian extends Account {

    public Librarian(String id, String password, Person person) {
        super(id, password, person);
    }

    /**
     * Katalogga yangi kitob nusxasi qo'shish.
     */
    public boolean addBookItem(BookItem bookItem, Catalog catalog) {
        boolean result = catalog.updateCatalog(bookItem);
        if (result) System.out.println("[OK] Kitob katalogga qo'shildi: " + bookItem.getTitle());
        return result;
    }

    /**
     * Katalogdan kitob nusxasini o'chirish.
     */
    public boolean removeBookItem(BookItem bookItem, Catalog catalog) {
        boolean result = catalog.removeFromCatalog(bookItem);
        if (result) System.out.println("[OK] Kitob katalogdan o'chirildi: " + bookItem.getTitle());
        return result;
    }

    /**
     * A'zoni bloklash.
     */
    public boolean blockMember(Member member) {
        member.setStatus(AccountStatus.BLACKLISTED);
        System.out.println("[OK] A'zo bloklandi: " + member.getId());
        return true;
    }

    /**
     * A'zo blokini ochish.
     */
    public boolean unblockMember(Member member) {
        member.setStatus(AccountStatus.ACTIVE);
        System.out.println("[OK] A'zo blokdan chiqarildi: " + member.getId());
        return true;
    }

    /**
     * A'zoga rasman kitob berish (Issue book).
     * Use Case: "Issue book" - Librarian bajaradi.
     */
    public BookLending issueBook(BookItem bookItem, Member member) {
        if (member.checkoutBookItem(bookItem)) {
            System.out.println("[OK] Kutubxonachi kitob berdi: " + bookItem.getTitle()
                               + " -> " + member.getPerson().getName());
            return new BookLending(bookItem, member);
        }
        System.out.println("[XATO] Kitob berib bo'lmadi.");
        return null;
    }

    /**
     * Yangi a'zo ro'yxatdan o'tkazish va kutubxona kartasi berish.
     * Use Case: "Register new account" + "Issue library card"
     */
    public LibraryCard registerNewMember(Member member) {
        String cardNumber = "LIB-" + System.currentTimeMillis();
        String barcode    = "BAR-" + member.getId();
        LibraryCard card  = new LibraryCard(cardNumber, barcode);
        member.setLibraryCard(card);
        System.out.println("[OK] Yangi a'zo ro'yxatga olindi: " + member.getPerson().getName());
        System.out.println("     Kutubxona kartasi: " + cardNumber);
        return card;
    }

    /**
     * A'zo a'zoligini bekor qilish.
     */
    public boolean cancelMembership(Member member) {
        if (!member.getActiveLendings().isEmpty()) {
            System.out.println("[XATO] A'zoda qaytarilmagan kitoblar bor.");
            return false;
        }
        member.setStatus(AccountStatus.CANCELED);
        System.out.println("[OK] A'zolik bekor qilindi: " + member.getId());
        return true;
    }

    /**
     * Muddati o'tgan kitoblar uchun bildirishnoma yuborish.
     * Use Case: System -> "Send overdue notification"
     */
    public void sendOverdueNotifications(List<BookLending> overdueList) {
        for (BookLending lending : overdueList) {
            String memberEmail = lending.getMember().getPerson().getEmail();
            String content     = "Hurmatli " + lending.getMember().getPerson().getName()
                               + ", \"" + lending.getBookItem().getTitle()
                               + "\" kitobining qaytarish muddati o'tdi!";
            EmailNotification notif = new EmailNotification(
                (int)(Math.random() * 9999), content, memberEmail
            );
            notif.sendNotification();
        }
    }

    /**
     * Kitob qaytarilganda zahira egasiga xabar yuborish.
     */
    public void notifyReservationAvailable(BookReservation reservation) {
        String email   = reservation.getMember().getPerson().getEmail();
        String content = "\"" + reservation.getBookItem().getTitle()
                       + "\" kitobingiz endi mavjud! Iltimos, kelib oling.";
        EmailNotification notif = new EmailNotification(
            (int)(Math.random() * 9999), content, email
        );
        notif.sendNotification();

        // Kitob holatini "Reserved" ga o'zgartirish
        reservation.getBookItem().setStatus(BookStatus.RESERVED);
        reservation.setStatus(ReservationStatus.WAITING);
    }

    @Override
    public String toString() {
        return "Librarian{id='" + id + "', name='" + person.getName() + "'}";
    }
}
