package library;

import library.accounts.Librarian;
import library.accounts.Member;
import library.enums.BookFormat;
import library.library.LibraryService;
import library.models.*;
import library.search.Catalog;

public class Main {

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║   KUTUBXONA BOSHQARUV TIZIMI         ║");
        System.out.println("║   Library Management System           ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        Address libAddress = new Address("Mustaqillik ko'chasi 1", "Toshkent",
                                         "Toshkent viloyati", "100000", "O'zbekiston");
        Library library   = Library.getInstance("Alisher Navoiy Kutubxonasi", libAddress);
        LibraryService service = new LibraryService(library);
        Catalog catalog   = service.getCatalog();

        System.out.println("Kutubxona: " + library.getName());
        System.out.println("Manzil: " + library.getAddress() + "\n");

        Address librarianAddress = new Address("Chilonzor 5", "Toshkent",
                                                "Toshkent", "100100", "O'zbekiston");
        Person librarianPerson = new Person("Zulfiya Xolmatova", librarianAddress,
                                             "zulfiya@library.uz", "+998901234567") {
        };
        Librarian librarian = new Librarian("LIB-001", "lib123", librarianPerson);
        System.out.println("Kutubxonachi: " + librarian);

        Address memberAddress = new Address("Yunusobod 12", "Toshkent",
                                             "Toshkent", "100200", "O'zbekiston");
        Person memberPerson = new Person("Jasur Toshmatov", memberAddress,
                                          "jasur@email.uz", "+998907654321") {
        };
        Member member = new Member("MEM-001", "mem123", memberPerson);

        LibraryCard card = librarian.registerNewMember(member);
        System.out.println("Karta: " + card + "\n");

        Author author1 = new Author("Robert C. Martin", "Dasturlash bo'yicha ekspert");
        Author author2 = new Author("Asilbek Yusupov", "O'zbek yozuvchisi");

        BookItem book1 = new BookItem("978-0-13-235088-4", "Clean Code",
                "Dasturlash", "Prentice Hall", "Ingliz",
                431, author1, "BAR-1001", BookFormat.PAPERBACK, 45.0);

        BookItem book2 = new BookItem("978-9943-07-123-4", "O'tkan kunlar",
                "Roman", "Sharq nashriyoti", "O'zbek",
                320, author2, "BAR-1002", BookFormat.HARDCOVER, 25.0);

        BookItem book3 = new BookItem("978-0-13-110362-7", "The C Programming Language",
                "Dasturlash", "Prentice Hall", "Ingliz",
                272, author1, "BAR-1003", BookFormat.PAPERBACK, 35.0);

        librarian.addBookItem(book1, catalog);
        librarian.addBookItem(book2, catalog);
        librarian.addBookItem(book3, catalog);
        System.out.println("\nKatalogdagi kitoblar soni: " + catalog.getTotalBooks());

        System.out.println("\n--- Qidirish: 'clean code' ---");
        catalog.searchByTitle("Clean Code");

        System.out.println("--- Qidirish: muallif 'Robert C. Martin' ---");
        catalog.searchByAuthor("Robert C. Martin");

        System.out.println("--- Qidirish: mavzu 'dasturlash' ---");
        catalog.searchBySubject("Dasturlash");

        BarcodeReader reader = new BarcodeReader("READER-01");

        System.out.println("\n--- CHECKOUT ---");
        service.checkoutBook(member, book1, reader);
        service.checkoutBook(member, book3, reader);

        System.out.println("A'zodagi kitoblar: " + member.getTotalBooksCheckedout());

        System.out.println("\n--- RETURN ---");
        service.returnBook(member, book1, reader, librarian);

        System.out.println("A'zodagi kitoblar (qaytarilgandan keyin): "
                            + member.getTotalBooksCheckedout());

        System.out.println("\n--- ZAHIRA ---");
      
        Address member2Address = new Address("Mirzo Ulugbek 3", "Toshkent",
                                              "Toshkent", "100300", "O'zbekiston");
        Person member2Person = new Person("Malika Rahimova", member2Address,
                                           "malika@email.uz", "+998991112233") {
        };
        Member member2 = new Member("MEM-002", "mem456", member2Person);
        librarian.registerNewMember(member2);

        member2.reserveBookItem(book3); 

        service.sendOverdueNotifications();

        System.out.println("\n--- BLOKLASH ---");
        librarian.blockMember(member);
        System.out.println("A'zo holati: " + member.getStatus());

        librarian.unblockMember(member);
        System.out.println("A'zo holati (blokdan keyin): " + member.getStatus());

        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║  Dastur muvaffaqiyatli yakunlandi!   ║");
        System.out.println("╚══════════════════════════════════════╝");
    }
}
