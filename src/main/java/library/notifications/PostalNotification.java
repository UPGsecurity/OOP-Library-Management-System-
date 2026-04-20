package library.notifications;

import library.models.Address;

/**
 * Pochta orqali bildirishnoma yuborish.
 */
public class PostalNotification extends Notification {

    private Address address;

    public PostalNotification(int notificationId, String content, Address address) {
        super(notificationId, content);
        this.address = address;
    }

    @Override
    public boolean sendNotification() {
        if (address == null) {
            System.out.println("[XATO] Manzil ko'rsatilmagan.");
            return false;
        }
        System.out.println("[POCHTA] Yuborildi -> " + address.toString());
        System.out.println("         Mazmun: " + content);
        return true;
    }

    public Address getAddress() { return address; }
}
