package library.transactions;

import java.util.Date;

public abstract class FineTransaction {

    protected Date   creationDate;
    protected double amount;

    public FineTransaction(double amount) {
        this.amount       = amount;
        this.creationDate = new Date();
    }

    public abstract boolean initiateTransaction();

    public double getAmount()       { return amount; }
    public Date   getCreationDate() { return creationDate; }
}

class CreditCardTransaction extends FineTransaction {

    private String nameOnCard;

    public CreditCardTransaction(double amount, String nameOnCard) {
        super(amount);
        this.nameOnCard = nameOnCard;
    }

    @Override
    public boolean initiateTransaction() {
        System.out.println("[TO'LOV] Kredit karta: " + nameOnCard
                           + " | Summa: $" + amount);
        return true;
    }

    public String getNameOnCard() { return nameOnCard; }
}

class CheckTransaction extends FineTransaction {

    private String bankName;
    private String checkNumber;

    public CheckTransaction(double amount, String bankName, String checkNumber) {
        super(amount);
        this.bankName    = bankName;
        this.checkNumber = checkNumber;
    }

    @Override
    public boolean initiateTransaction() {
        System.out.println("[TO'LOV] Chek: " + bankName + " | Chek raqami: " + checkNumber
                           + " | Summa: $" + amount);
        return true;
    }
}

class CashTransaction extends FineTransaction {

    private double cashTendered; 

    public CashTransaction(double amount, double cashTendered) {
        super(amount);
        this.cashTendered = cashTendered;
    }

    @Override
    public boolean initiateTransaction() {
        if (cashTendered < amount) {
            System.out.println("[XATO] Yetarli pul yo'q. Kerak: $" + amount
                               + " | Berildi: $" + cashTendered);
            return false;
        }
        double change = cashTendered - amount;
        System.out.println("[TO'LOV] Naqd pul: $" + amount
                           + " | Qaytim: $" + change);
        return true;
    }

    public double getCashTendered() { return cashTendered; }
}
