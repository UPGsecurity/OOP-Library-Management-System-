package library.models;

import library.enums.BookFormat;
import library.enums.BookStatus;

import java.util.Date;

/**
 * Kutubxonadagi kitobning aniq nusxasi.
 * Book klassidan meros oladi.
 */
public class BookItem extends Book {

    private String     barcode;
    private boolean    isReferenceOnly;  // Faqat kutubxonada o'qish uchun
    private Date       borrowed;
    private Date       dueDate;
    private double     price;
    private BookFormat format;
    private BookStatus status;
    private Date       dateOfPurchase;
    private Date       publicationDate;
    private Rack       placedAt;

    public BookItem(String ISBN, String title, String subject,
                    String publisher, String language, int numberOfPages,
                    Author author, String barcode, BookFormat format, double price) {
        super(ISBN, title, subject, publisher, language, numberOfPages, author);
        this.barcode         = barcode;
        this.format          = format;
        this.price           = price;
        this.status          = BookStatus.AVAILABLE;
        this.isReferenceOnly = false;
        this.dateOfPurchase  = new Date();
    }

    /**
     * Kitobni chiqarish (checkout) metodi.
     * Agar kitob reference-only yoki mavjud emas bo'lsa - false qaytaradi.
     */
    public boolean checkout() {
        if (isReferenceOnly) {
            System.out.println("[XATO] Bu kitob faqat kutubxonada o'qish uchun.");
            return false;
        }
        if (status != BookStatus.AVAILABLE) {
            System.out.println("[XATO] Kitob hozirda mavjud emas. Holati: " + status);
            return false;
        }
        this.status   = BookStatus.LOANED;
        this.borrowed = new Date();
        return true;
    }

    // --- Getters ---
    public String     getBarcode()         { return barcode; }
    public boolean    isReferenceOnly()    { return isReferenceOnly; }
    public Date       getBorrowed()        { return borrowed; }
    public Date       getDueDate()         { return dueDate; }
    public double     getPrice()           { return price; }
    public BookFormat getFormat()          { return format; }
    public BookStatus getStatus()          { return status; }
    public Date       getDateOfPurchase()  { return dateOfPurchase; }
    public Date       getPublicationDate() { return publicationDate; }
    public Rack       getPlacedAt()        { return placedAt; }

    // --- Setters ---
    public void setStatus(BookStatus status)             { this.status = status; }
    public void setDueDate(Date dueDate)                 { this.dueDate = dueDate; }
    public void setReferenceOnly(boolean referenceOnly)  { isReferenceOnly = referenceOnly; }
    public void setPlacedAt(Rack placedAt)               { this.placedAt = placedAt; }
    public void setPublicationDate(Date publicationDate) { this.publicationDate = publicationDate; }
    public void setBorrowed(Date borrowed)               { this.borrowed = borrowed; }

    @Override
    public String toString() {
        return "BookItem{barcode='" + barcode + "', title='" + getTitle() +
               "', status=" + status + "}";
    }
}
