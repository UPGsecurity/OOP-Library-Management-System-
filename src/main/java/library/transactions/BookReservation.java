package library.transactions;

import library.accounts.Member;
import library.enums.ReservationStatus;
import library.models.BookItem;

import java.util.Date;

/**
 * Kitob zahirasi (reservation).
 * A'zo mavjud bo'lmagan kitobni zahiralaydi.
 */
public class BookReservation {

    private Date              creationDate;
    private ReservationStatus status;
    private BookItem          bookItem;
    private Member            member;

    public BookReservation(BookItem bookItem, Member member) {
        this.bookItem     = bookItem;
        this.member       = member;
        this.creationDate = new Date();
        this.status       = ReservationStatus.WAITING;
    }

    /**
     * Zahira tafsilotlarini olish
     */
    public BookReservation fetchReservationDetails() {
        System.out.println("[INFO] Zahira: " + bookItem.getTitle()
                           + " | A'zo: " + member.getPerson().getName()
                           + " | Holat: " + status);
        return this;
    }

    // Getters
    public ReservationStatus getStatus()      { return status; }
    public BookItem          getBookItem()    { return bookItem; }
    public Member            getMember()      { return member; }
    public Date              getCreationDate(){ return creationDate; }

    // Setters
    public void setStatus(ReservationStatus status) {
        this.status = status;
        System.out.println("[INFO] Zahira holati o'zgardi: " + status);
    }

    @Override
    public String toString() {
        return "BookReservation{book='" + bookItem.getTitle()
               + "', member='" + member.getPerson().getName()
               + "', status=" + status + "}";
    }
}
