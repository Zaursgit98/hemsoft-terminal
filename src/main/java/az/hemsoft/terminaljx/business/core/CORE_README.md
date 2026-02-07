# HEMsoft Custom ORM Engine Guide

Bu sənəd layihədə istifadə olunan xüsusi Mini-ORM (Object-Relational Mapping) mühərrikinin necə işlədiyini və ondan necə istifadə olunacağını izah edir.

## 1. Memarlıq Baxışı
Mini-ORM, Spring Data JPA-ya bənzər bir strukturda qurulub, lakin tamamilə yüngülvaznli (lightweight) JDBC və Reflection (əksolunma) üzərində işləyir.

### Əsas Komponentlər:
- **Annotations**: Modellərin verilənlər bazası xəritəsini təyin edir.
- **SchemaGenerator**: Modelləri skan edərək bazada avtomatik cədvəllər, sütunlar, indekslər və xarici açarlar (Foreign Keys) yaradır.
- **BaseRepository**: Generik CRUD (Create, Read, Update, Delete) əməliyyatlarını və əlaqəli obyektlərin yüklənməsini təmin edir.

---

## 2. Annotasiyalar

| Annotasiya | Təsviri | Sahələri |
| :--- | :--- | :--- |
| `@Table` | Klassı bazada bir cədvələ bağlayır. | `value` (cədvəl adı) |
| `@Column` | Sahəni sütuna bağlayır. | `name`, `length`, `nullable`, `unique` |
| `@Id` | Sahənin Primary Key olduğunu göstərir. | - |
| `@Index` | Sütun üçün bazada indeks yaradır. | `name` (isteğe bağlı) |
| `@ManyToOne` | N-in 1-ə əlaqəsini təyin edir. | `targetEntity`, `fetch` (EAGER/LAZY) |
| `@OneToMany` | 1-in N-ə əlaqəsini təyin edir (Məntiqi). | `mappedBy`, `fetch` |
| `@JoinColumn` | Xarici açar (FK) sütun adını təyin edir. | `name` |

---

## 3. Schema Generator (Avtomatik Skanlama)
Proqram işə düşəndə `SchemaGenerator` göstərilən paketi skan edir:
1. **Cədvəl yaradılması**: `@Table` olan klasslar üçün yoxdursa cədvəl yaradır.
2. **Sütun yenilənməsi**: Modelə yeni sahə əlavə etsəniz, bazaya avtomatik `ALTER TABLE ADD COLUMN` əmri göndərilir.
3. **Unique & Index**: `@Column(unique=true)` və ya `@Index` qoyulduqda bazada indekslər yaradılır.
4. **UTF-8**: Bütün cədvəllər avtomatik `utf8mb4` (Azərbaycan şriftləri üçün) formatına çevrilir.

---

## 4. BaseRepository və Performans
`BaseRepository` bütün SQL əməliyyatlarını mərkəzləşdirir.

### Fetch Strategies (Yükləmə Strategiyaları):
- **EAGER (Default ManyToOne)**: Obyekt yüklənəndə onun bütün bağlı olduğu obyektlər də bazadan dərhal çəkilir.
- **LAZY (Default OneToMany)**: Obyekt yüklənəndə bağlı olduğu obyektin yalnız **ID**-si doldurulur, digər detallar üçün bazaya əlavə sorğu getmir. Bu, performansı artırır.

### İstifadə Nümunəsi:
```java
@Data
@Table("products")
@JsonInclude(JsonInclude.Include.NON_NULL) // Null sahələri JSON-da gizlədir
public class Product {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", unique = true)
    @Index
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private ProductGroup group;
}
```

---

## 5. Developer üçün qeydlər
- **Yeni Model**: Sadəcə klassı yaradın və annotasiyaları əlavə edin. Bazada heç nə etməyə ehtiyac yoxdur.
- **Performans**: Çoxlu sayda məlumat olan cədvəllərdə axtarış sahələrinə `@Index` əlavə edin.
- **Foreign Keys**: `@ManyToOne` istifadə edərkən həm də `@JoinColumn` qoymağı unutmayın.

---
*HEMsoft Terminal - Core Engineering Team*
