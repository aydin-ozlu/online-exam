# Online Sınav Sistemi

## Proje Hakkında

Bu proje, Java HTTP Server kullanarak geliştirilmiş bir **Online Sınav Sistemi**dir. Kullanıcılar JWT tabanlı kimlik doğrulama ile sisteme giriş yaparak; sınavları listeleyip sınav sorularını görebilir, cevapları gönderebilir ve sonuçlarını anında alabilir.

---

## Özellikler

- **Kendi HTTP Server'ı:** Java `HttpServer` kullanılarak minimalist şekilde oluşturulmuştur.
- **JWT ile Kimlik Doğrulama:** `/login` endpoint’inden Basic Auth ile giriş yapılarak JWT token alınır.
- **Yetkilendirme:** Sınav sorgulama ve cevap gönderme işlemleri JWT token ile doğrulanır.
- **Çoklu Soru Tipi:** Çoktan seçmeli ve klasik (metin girişi) soru tipleri desteklenir.
- **Veritabanı:** MySQL veritabanı kullanılır.
- **Thread Pool:** ThreadPoolExecutor ile minimum ve maksimum thread sayısı ayarlanarak eşzamanlı istekler yönetilir. Gerçek yük durumuna göre konfigürasyon esnek şekilde değiştirilebilir.
- **JSON İşlemleri:** Jackson kütüphanesi kullanılarak JSON serialization ve deserialization  yapılır.

---

## Teknolojiler

- Java 17
- Java HTTP Server (com.sun.net.httpserver.HttpServer)
- JWT (JSON Web Token)
- Jackson (JSON işleme)
- JDBC (MySQL)
- Maven (proje yönetimi ve bağımlılıklar)
- SLF4J + Logback (loglama)

---

## Proje Yapısı

```
src/
 ├─ com.aydin.exam
 │    ├─ model                 (Soru, Sınav, Cevap modelleri)
 │    ├─ dao                   (Veritabanı erişim katmanı)
 │    ├─ handler               (HTTP handler sınıfları: LoginHandler, ExamHandler, SubmitHandler, ...)
 │    ├─ auth     	           (JWT token oluşturma ve doğrulama)
 │    ├─ scoring               (Sınav sonucu hesaplama)
 │    ├─ config                (Veritabanı konfigürasyonları)
 │    ├─ OnlineExamServer.java (Ana server başlatıcı)
 ├─ resources
 │    ├─ frontend              (statik içerik, html)
 │    ├─ logback.xml           (Logback konfigürasyonu)
```

---

## Kullanım

### 1. Veritabanı

- Veritabanı bağlantısı Database.java sınıfında yapılandırılmalıdır. 
- `exams`, `questions` ve `choices` tabloları ile gerekli veri yapısı oluşturulmuştur. Tablolar yoksa sunucu başlarken yaratılır.
- Örnek veriler `DatabaseInitializer` sınıfında oluşturulmaktadır.

### 2. Sunucuyu Başlatma

```bash
mvn clean compile exec:java -Dexec.mainClass=com.aydin.exam.OnlineExamServer
```

Sunucu `http://localhost:8079` adresinde çalışır.
Kullanıcı adı: user   Şifre: 12345 olarak girilebilir.

### 3. API Endpointleri

| Yöntem | Yol        | Açıklama                                  | Yetkilendirme          |
|--------|------------|------------------------------------------|-----------------------|
| POST   | /login     | Basic Auth ile giriş, JWT token döner    | Yok                   |
| GET    | /exams/{id}| Belirtilen sınavın sorularını döner      | JWT Token (Bearer)     |
| POST   | /submit    | Sınav cevaplarını gönderir, puan döner   | JWT Token (Bearer)     |

---

## JWT Kimlik Doğrulama Akışı

- Kullanıcı `/login` endpoint’ine Basic Auth header ile POST yapar.
- Sunucu doğrulama yapar, başarılı ise JWT token oluşturur ve JSON içinde döner.
- Frontend, token’ı `localStorage`’da saklar.
- Diğer API isteklerinde `Authorization: Bearer <token>` header'ı eklenir.
- Sunucu token’ı doğrular, geçerli ise isteği işler.

---

## Frontend Özellikleri

- Basit HTML + JavaScript ile hazırlanmıştır.
- Giriş formu, sınav yükleme ve cevap gönderme işlemleri desteklenir.
- JWT token kontrolü yapar; giriş olmadan sınav yükleme ve gönderme engellenir.
- 401 hatası alındığında kullanıcıya login olması gerektiği bildirilir.

---

## Loglama

- SLF4J + Logback kullanılır.
- `logback.xml` ile dosyaya ve konsola loglama konfigüre edilebilir.

---

## Testler

- JUnit + Mockito kullanılarak bazı birim testleri yazılmıştır.
- Testlerin Java 17 JVM altında çalıştırılması önerilir (Mockito sürüm kısıtlaması nedeniyle).

---

## Geliştirme Notları

- Thread Pool: ThreadPoolExecutor gerçek yük durumuna göre konfigüre edilebilir.
- Çoklu kullanıcı aynı anda işlem yapabilir; JDBC bağlantıları thread-safe olarak yönetilir.
- Metin bazlı soru puanlama otomatik değil, manuel veya belirlenen kriterlerle yapılabilir.
- Güvenlik için HTTPS ve token yenileme eklenebilir.

---

