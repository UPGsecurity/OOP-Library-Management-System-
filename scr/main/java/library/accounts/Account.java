package library.accounts;

import library.enums.AccountStatus;
import library.models.Person;

public abstract class Account {
    protected String        id;
    protected String        password;
    protected AccountStatus status;
    protected Person        person;

    public Account(String id, String password, Person person) {
        this.id       = id;
        this.password = password;
        this.person   = person;
        this.status   = AccountStatus.ACTIVE;
    }

    public boolean resetPassword(String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            System.out.println("[XATO] Parol kamida 6 ta belgidan iborat bo'lishi kerak.");
            return false;
        }
        this.password = newPassword;
        System.out.println("[INFO] Parol muvaffaqiyatli yangilandi.");
        return true;
    }

   
    public boolean login(String inputPassword) {
        if (status != AccountStatus.ACTIVE) {
            System.out.println("[XATO] Hisob faol emas. Holati: " + status);
            return false;
        }
        boolean ok = this.password.equals(inputPassword);
        if (ok) System.out.println("[INFO] Kirish muvaffaqiyatli: " + id);
        else    System.out.println("[XATO] Noto'g'ri parol.");
        return ok;
    }

    public void logout() {
        System.out.println("[INFO] Chiqildi: " + id);
    }
    public String        getId()     { return id; }
    public AccountStatus getStatus() { return status; }
    public Person        getPerson() { return person; }
    public void setStatus(AccountStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "Account{id='" + id + "', status=" + status +
               ", person=" + (person != null ? person.getName() : "null") + "}";
    }
}
