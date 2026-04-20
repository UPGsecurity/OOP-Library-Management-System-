package library.accounts;

import library.enums.AccountStatus;
import library.models.*;
import library.transactions.BookLending;
import library.transactions.BookReservation;
import library.enums.BookStatus;
import library.enums.ReservationStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Member extends Account {

    private Date         dateOfMembership;
    private int          totalBooksCheckedout;
    private LibraryCard  libraryCard;

  
    private static final int MAX_BOOKS_LIMIT = 5;

   
    private List<BookLending>      activeLendings     = new ArrayList<>();
    private List<BookReservation>  activeReservations = new ArrayList<>();

    public Member(String id, String password, Person person) {
        super(id, password, person);
        this.dateOfMembership    = new Date();
        this.totalBooksCheckedout = 0;
    }

  
    public boolean checkoutBookItem(BookItem bookItem) {
       
        if (getStatus() != AccountStatus.ACTIVE) {
            System.out.println("[XATO] Hisobingiz faol emas.");
            return false;
        }
      
        if (totalBooksCheckedout >= MAX_BOOKS_LIMIT) {
            System.out.println("[XATO] Siz " + MAX_BOOKS_LIMIT + " ta kitob limitiga etdingiz.");
            return false;
        }

        if (bookItem.checkout()) {
  
            long fourteenDays = 14L * 24 * 60 * 60 * 1000;
            bookItem.setDueDate(new Date(System.currentTimeMillis() + fourteenDays));

            BookLending lending = new BookLending(bookItem, this);
            activeLendings.add(lending);
            totalBooksCheckedout++;

            activeReservations.stream()
                .filter(r -> r.getBookItem().equals(bookItem))
                .forEach(r -> r.setStatus(ReservationStatus.COMPLETED));

            System.out.println("[OK] Kitob muvaffaqiyatli olindi: " + bookItem.getTitle());
            System.out.println("     Qaytarish muddati: " + bookItem.getDueDate());
            return true;
        }
        return false;
    }

  
    public boolean returnBookItem(BookItem bookItem) {
        BookLending lending = activeLendings.stream()
            .filter(l -> l.getBookItem().equals(bookItem))
            .findFirst()
            .orElse(null);

        if (lending == null) {
            System.out.println("[XATO] Bu kitob sizda yo'q.");
            return false;
        }

        lending.returnBook();
        activeLendings.remove(lending);
        totalBooksCheckedout--;
        System.out.println("[OK] Kitob qaytarildi: " + bookItem.getTitle());
        return true;
    }

    
    public BookReservation reserveBookItem(BookItem bookItem) {
        if (bookItem.getStatus() == BookStatus.AVAILABLE) {
            System.out.println("[INFO] Kitob mavjud, to'g'ridan to'g'ri oling.");
            return null;
        }
        BookReservation reservation = new BookReservation(bookItem, this);
        activeReservations.add(reservation);
        bookItem.setStatus(BookStatus.RESERVED);
        System.out.println("[OK] Kitob zahiralandi: " + bookItem.getTitle());
        return reservation;
    }

   
    public boolean removeReservation(BookReservation reservation) {
        if (!activeReservations.contains(reservation)) {
            System.out.println("[XATO] Bu zahira sizda yo'q.");
            return false;
        }
        reservation.setStatus(ReservationStatus.CANCELED);
        activeReservations.remove(reservation);
        System.out.println("[OK] Zahira bekor qilindi.");
        return true;
    }

    public int          getTotalBooksCheckedout() { return totalBooksCheckedout; }
    public Date         getDateOfMembership()     { return dateOfMembership; }
    public LibraryCard  getLibraryCard()          { return libraryCard; }
    public List<BookLending>     getActiveLendings()     { return activeLendings; }
    public List<BookReservation> getActiveReservations() { return activeReservations; }

    public void setLibraryCard(LibraryCard libraryCard) { this.libraryCard = libraryCard; }

    @Override
    public String toString() {
        return "Member{id='" + id + "', name='" + person.getName() +
               "', booksCheckedout=" + totalBooksCheckedout + "}";
    }
}
