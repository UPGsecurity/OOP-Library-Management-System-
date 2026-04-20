package library.transactions;

import library.accounts.Member;
import library.models.BookItem;

import java.util.Date;

/**
 * Kitob berish tranzaksiyasi (lending).
 * Activity Diagram: "Check-out book" va "Renew book" jarayonlari.
 * Fine (jarima) shu klass bilan bog'liq - Composition munosabati.
 */
public class BookLending {

    private Date     creationDate;
    private Date     dueDate;
    private Date     returnDate;
    private BookItem bookItem;
    private Member   member;

    // Standart muddat: 14 kun
    private static final int LOAN_DAYS = 14;

    public BookLending(BookItem bookItem, Member member) {
        this.bookItem     = bookItem;
        this.member       = member;
        this.creationDate = new Date();

        // Qaytarish muddatini hisoblash
        long dueMs = System.currentTimeMillis() + (long) LOAN_DAYS * 24 * 60 * 60 * 1000;
        this.dueDate = new Date(dueMs);
        bookItem.setDueDate(this.dueDate);
    }

    /**
     * Kitobni qaytarish.
     * Activity Diagram: "Return book"
     */
    public boolean returnBook() {
        this.returnDate = new Date();
        System.out.println("[INFO] Kitob qaytarildi: " + bookItem.getTitle());
        System.out.println("       Qaytarish sanasi: " + returnDate);
        return true;
    }

    /**
     * Kitobni yangilash (renew) - muddatni uzaytirish.
     * Activity Diagram: "Renew book"
     * Agar boshqa a'zo zahiralagan bo'lsa - yangilab bo'lmaydi.
     */
    public boolean renewBook(boolean isReservedByOther) {
        if (isReservedByOther) {
            System.out.println("[XATO] Kitob boshqa a'zo tomonidan zahiralangan. Yangilab bo'lmaydi.");
            return false;
        }
        // Muddatni 14 kun uzaytirish
        long newDueMs = System.currentTimeMillis() + (long) LOAN_DAYS * 24 * 60 * 60 * 1000;
        this.dueDate = new Date(newDueMs);
        bookItem.setDueDate(this.dueDate);
        System.out.println("[OK] Kitob yangilandi. Yangi muddat: " + dueDate);
        return true;
    }

    /**
     * Muddati o'tganmi tekshirish.
     */
    public boolean isOverdue() {
        if (returnDate != null) return false; // qaytarilgan
        return new Date().after(dueDate);
    }

    /**
     * Necha kun kechiktirilganligini hisoblash.
     */
    public long getOverdueDays() {
        if (!isOverdue()) return 0;
        long diffMs = System.currentTimeMillis() - dueDate.getTime();
        return diffMs / (24L * 60 * 60 * 1000);
    }

    // Getters
    public Date     getCreationDate() { return creationDate; }
    public Date     getDueDate()      { return dueDate; }
    public Date     getReturnDate()   { return returnDate; }
    public BookItem getBookItem()     { return bookItem; }
    public Member   getMember()       { return member; }

    @Override
    public String toString() {
        return "BookLending{book='" + bookItem.getTitle()
               + "', member='" + member.getPerson().getName()
               + "', dueDate=" + dueDate
               + ", overdue=" + isOverdue() + "}";
    }
}
