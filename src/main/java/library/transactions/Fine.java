package library.transactions;

/**
 * Jarima (Fine).
 * BookLending bilan Composition munosabatida - lending bo'lmasa Fine ham yo'q.
 * Kunlik jarima: 1.0 dollar.
 */
public class Fine {

    private double      amount;
    private BookLending lending;

    // Kunlik jarima miqdori
    private static final double FINE_PER_DAY = 1.0;

    public Fine(BookLending lending) {
        this.lending = lending;
        this.amount  = calculateAmount();
    }

    /**
     * Jarima miqdorini hisoblash.
     * Kechiktirilgan kunlar * kunlik narx.
     */
    private double calculateAmount() {
        long overdueDays = lending.getOverdueDays();
        if (overdueDays <= 0) return 0.0;
        double calc = overdueDays * FINE_PER_DAY;
        System.out.println("[JARIMA] " + overdueDays + " kun kechiktirildi. "
                           + "Jarima: $" + calc);
        return calc;
    }

    public double getAmount() { return amount; }

    @Override
    public String toString() {
        return "Fine{amount=$" + amount + ", book='" + lending.getBookItem().getTitle() + "'}";
    }
}
