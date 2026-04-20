package library.notifications;

/**
 * Email orqali bildirishnoma yuborish.
 */
public class EmailNotification extends Notification {

    private String email;

    public EmailNotification(int notificationId, String content, String email) {
        super(notificationId, content);
        this.email = email;
    }

    @Override
    public boolean sendNotification() {
        if (email == null || !email.contains("@")) {
            System.out.println("[XATO] Email manzil noto'g'ri: " + email);
            return false;
        }
        // Haqiqiy loyihada JavaMail API ishlatiladi
        System.out.println("[EMAIL] Yuborildi -> " + email);
        System.out.println("        Mazmun: " + content);
        return true;
    }

    public String getEmail() { return email; }
}
