package library.models;

import java.util.Date;

/**
 * Barcode o'quvchi qurilma
 */
public class BarcodeReader {
    private String  id;
    private Date    registeredAt;
    private boolean active;

    public BarcodeReader(String id) {
        this.id           = id;
        this.registeredAt = new Date();
        this.active       = true;
    }

    /**
     * Barcodni skanerlash
     * @param barcode skanerlash uchun barcode
     * @return barcode string yoki null (qurilma o'chiq bo'lsa)
     */
    public String scan(String barcode) {
        if (!active) {
            System.out.println("[XATO] Barcode o'quvchi faol emas.");
            return null;
        }
        System.out.println("[SCAN] Barcode skanerlandi: " + barcode);
        return barcode;
    }

    public boolean isActive()     { return active; }
    public String  getId()        { return id; }
    public Date    getRegisteredAt() { return registeredAt; }

    public void setActive(boolean active) { this.active = active; }
}
