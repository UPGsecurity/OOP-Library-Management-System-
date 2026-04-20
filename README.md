# 📚 Library Management System

**Kutubxona Boshqaruv Tizimi** — Java dasturlash tilida yozilgan OOP loyiha.

## 📋 Loyiha haqida

Bu loyiha kutubxonaning asosiy operatsiyalarini — kitob berish, qaytarish, zahiralash, a'zolikni boshqarish — dasturlaydi. UML diagrammalar (Use Case, Class, Activity) asosida qurilgan.

## 🏗️ Loyiha Strukturasi

```
LibraryManagementSystem/
├── src/
│   └── main/
│       └── java/
│           └── library/
│               ├── Main.java                    ← Dasturni ishga tushirish
│               ├── enums/
│               │   ├── BookFormat.java
│               │   ├── BookStatus.java
│               │   ├── ReservationStatus.java
│               │   └── AccountStatus.java
│               ├── models/
│               │   ├── Address.java
│               │   ├── Person.java              ← Abstract
│               │   ├── Author.java
│               │   ├── Book.java
│               │   ├── BookItem.java            ← extends Book
│               │   ├── Library.java             ← Singleton
│               │   ├── LibraryCard.java
│               │   ├── BarcodeReader.java
│               │   └── Rack.java
│               ├── accounts/
│               │   ├── Account.java             ← Abstract
│               │   ├── Member.java              ← extends Account
│               │   └── Librarian.java           ← extends Account
│               ├── transactions/
│               │   ├── BookReservation.java
│               │   ├── BookLending.java
│               │   ├── Fine.java
│               │   └── FineTransaction.java     ← Abstract + 3 subclass
│               ├── search/
│               │   ├── Search.java              ← Interface
│               │   └── Catalog.java             ← implements Search
│               ├── notifications/
│               │   ├── Notification.java        ← Abstract
│               │   ├── EmailNotification.java
│               │   └── PostalNotification.java
│               └── library/
│                   └── LibraryService.java      ← Bosh xizmat klassi
└── README.md
```

## 🎯 OOP Prinsiplari

| Prinsip | Qo'llanish |
|---------|-----------|
| **Inheritance** | `Member` va `Librarian` → `Account`; `BookItem` → `Book` |
| **Abstraction** | `Account`, `Person`, `Notification`, `FineTransaction` — abstract |
| **Interface** | `Search` interfeysi — `Catalog` implement qiladi |
| **Encapsulation** | Barcha maydonlar `private`, getter/setter orqali kirish |
| **Polymorphism** | `sendNotification()`, `initiateTransaction()` — override |
| **Singleton** | `Library` klassi — faqat bitta instance |
| **Composition** | `Fine` → `BookLending` (lending bo'lmasa Fine ham yo'q) |

## 🔄 Asosiy Jarayonlar (Activity Diagrams)

### Kitob Olish (Check-out)
1. Kutubxona kartasi skanerlash
2. Kitob barcodeini skanerlash
3. Reference-only tekshiruvi
4. Limit tekshiruvi (max 5 ta)
5. Zahira tekshiruvi
6. Tranzaksiya yaratish → holat: `LOANED`

### Kitob Qaytarish (Return)
1. Barcode skanerlash
2. Muddatni tekshirish → jarima hisoblash
3. Zahira borligini tekshirish
4. Holat yangilash: `AVAILABLE` yoki `RESERVED`

### Kitobni Yangilash (Renew)
1. Barcode skanerlash
2. Jarima tekshiruvi
3. Zahira tekshiruvi — boshqa a'zo zahiralagan bo'lsa yangilab bo'lmaydi
4. Yangi muddatli tranzaksiya

## ▶️ Ishga Tushirish

```bash
# Kompilyatsiya qilish
javac -d out src/main/java/library/**/*.java src/main/java/library/*.java

# Ishga tushirish
java -cp out library.Main
```

## 👤 Foydalanuvchilar

| Rol | Vazifalari |
|-----|-----------|
| **Member** | Qidirish, olish, qaytarish, zahiralash, hisobni ko'rish |
| **Librarian** | Katalog boshqarish, a'zolik, kitob berish, jarima |
| **System** | Bildirishnomalar, avtomatik holat yangilash |

## 📊 UML Diagrammalar

- **Use Case Diagram** — foydalanuvchilar va tizim vazifalari
- **Class Diagram** — klasslar tuzilishi va munosabatlari
- **Activity Diagrams** — checkout, return, renew jarayonlari

## 🛠️ Texnologiyalar

- **Java** (JDK 11+)
- OOP prinsiplari
- Design Patterns: Singleton, Template Method

---
*Loyiha maqsadi: Dasturiy ta'minot muhandisligi fanidan jamoa loyihasi*
