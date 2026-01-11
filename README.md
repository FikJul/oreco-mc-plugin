# Oreco MC Plugin

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21-green)
![Paper](https://img.shields.io/badge/Paper-Required-blue)
![Java](https://img.shields.io/badge/Java-21-orange)

Plugin Paper untuk Minecraft 1.21 yang mengimplementasikan sistem mata uang berbasis ore/ingot dengan shop system yang lengkap.

## 📋 Deskripsi

Oreco MC Plugin adalah sistem ekonomi unik untuk server Minecraft yang menggunakan ore dan ingot sebagai mata uang. Balance player disimpan di **Enderchest** mereka secara real-time, tanpa perlu database eksternal. Plugin ini juga menyediakan shop system dengan GUI yang mudah digunakan dan konfigurasi yang fleksibel.

## ✨ Fitur Utama

### Sistem Mata Uang
- **6 Tingkat Mata Uang**: Copper, Iron, Gold, Emerald, Diamond, Netherite
- **Enderchest sebagai Vault**: Balance dihitung langsung dari isi Enderchest
- **Virtual Balance**: Real-time calculation tanpa database
- **Konversi Mata Uang**: Convert antar currency dengan aturan khusus
- **Smart Deduction**: Sistem otomatis mengambil kombinasi currency yang tepat saat transaksi

### Shop System
- **Infinite Stock**: Item tidak terbatas dari admin
- **GUI Chest Shop**: Interface inventory yang intuitif
- **8 Kategori**: Weapons, Armor, Tools, Blocks, Items, Food, Redstone, Misc
- **Multiple Currency Pricing**: Harga bisa kombinasi beberapa currency
- **Auto-categorization**: Item otomatis masuk kategori yang sesuai

### Commands & Permissions
- `/balance` - Cek saldo currency
- `/currency convert` - Convert mata uang
- `/shop` - Buka shop GUI
- Admin commands untuk mengelola shop

## 🔧 Requirements

- **Minecraft Version**: 1.21.x
- **Server Software**: Paper (atau fork-nya seperti Purpur)
- **Java Version**: 21 atau lebih tinggi

## 📦 Instalasi

1. **Download** file `oreco-mc-plugin-1.0.0.jar` dari [Releases](https://github.com/FikJul/oreco-mc-plugin/releases)

2. **Copy** file .jar ke folder `plugins/` server Anda

3. **Restart** server

4. **Edit** file konfigurasi di `plugins/OrecoCurrency/`:
   - `config.yml` - Konfigurasi umum dan pesan
   - `shops.yml` - Item shop dan kategori

5. **Reload** plugin dengan `/shop admin reload` atau restart server

## 💰 Sistem Mata Uang

### Hierarki & Nilai Konversi

| Mata Uang | Material | Nilai (Copper Equivalent) | Konversi |
|-----------|----------|---------------------------|----------|
| Copper | `COPPER_INGOT` | 1 | ✅ Bisa convert |
| Iron | `IRON_INGOT` | 64 | ✅ Bisa convert |
| Gold | `GOLD_INGOT` | 6,144 (96 × 64) | ✅ Bisa convert |
| Emerald | `EMERALD` | 393,216 (64 × 6,144) | ✅ Bisa convert |
| Diamond | `DIAMOND` | 100,663,296 (256 × 393,216) | ❌ **TIDAK bisa dari Emerald** |
| Netherite | `NETHERITE_INGOT` | 25,769,803,776 (256 × Diamond) | ✅ Hanya dari Diamond |

### Aturan Konversi

**✅ Konversi Dua Arah (Bisa Bolak-Balik)**:
- Copper ↔ Iron
- Iron ↔ Gold
- Gold ↔ Emerald

**⚠️ Konversi Satu Arah**:
- Diamond → Netherite (hanya bisa convert dari Diamond ke Netherite)

**❌ Tidak Bisa Convert**:
- Emerald → Diamond (Diamond hanya didapat dari pickup ingot asli)
- Netherite → Diamond (tidak bisa downgrade)

### Cara Kerja Balance

1. Plugin scan Enderchest player secara real-time
2. Menghitung jumlah setiap currency (COPPER_INGOT, IRON_INGOT, dll)
3. Menghitung total nilai dalam copper equivalent

**Contoh**:
```
Enderchest berisi:
- 128 Copper Ingot
- 5 Iron Ingot
- 2 Gold Ingot

Total Balance:
128 + (5 × 64) + (2 × 6,144) = 128 + 320 + 12,288 = 12,736 copper equivalent
```

## 🛒 Tutorial Menambah Item ke Shop

### Cara 1: Menggunakan Command (In-Game)

```bash
/shop admin add <material> <currency1:amount1> [currency2:amount2] ...
```

**Contoh**:
```bash
/shop admin add DIAMOND_SWORD diamond:2 emerald:10
/shop admin add ENCHANTED_GOLDEN_APPLE emerald:15 gold:32
/shop admin add ELYTRA diamond:10 emerald:64
```

**Tips**:
- Material name harus exact (gunakan `/shop admin list` untuk referensi)
- Bisa menggunakan 1 atau lebih currency untuk harga
- Item akan otomatis masuk kategori yang sesuai

### Cara 2: Edit `shops.yml` (Manual)

1. **Buka** file `plugins/OrecoCurrency/shops.yml`

2. **Tambahkan** item baru di section `items:`:

```yaml
items:
  NAMA_MATERIAL:
    display_name: "&bNama Display Item"
    category: "Kategori"
    price:
      currency1: jumlah1
      currency2: jumlah2
    lore:
      - "&7Deskripsi line 1"
      - "&7Deskripsi line 2"
```

3. **Contoh Lengkap**:

```yaml
items:
  # ... item yang sudah ada ...
  
  DIAMOND_PICKAXE:
    display_name: "&bDiamond Pickaxe"
    category: "Tools"
    price:
      diamond: 3
      iron: 10
    lore:
      - "&7Mine faster with this tool"
      - "&7Durability: High"
      
  ENCHANTING_TABLE:
    display_name: "&dEnchanting Table"
    category: "Blocks"
    price:
      diamond: 4
      emerald: 20
    lore:
      - "&7Enchant your gear"
```

4. **Save** file

5. **Reload** dengan `/shop admin reload` atau restart server

### Material Names Reference

Gunakan nama material dari [Spigot Material List](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html) atau lihat contoh di `shops.yml`.

**Contoh Material yang Umum**:
- **Weapons**: `DIAMOND_SWORD`, `IRON_SWORD`, `BOW`, `CROSSBOW`, `TRIDENT`
- **Armor**: `DIAMOND_HELMET`, `NETHERITE_CHESTPLATE`, `IRON_LEGGINGS`
- **Tools**: `DIAMOND_PICKAXE`, `IRON_AXE`, `GOLDEN_SHOVEL`
- **Blocks**: `DIAMOND_BLOCK`, `BEACON`, `SHULKER_BOX`, `END_PORTAL_FRAME`
- **Food**: `GOLDEN_APPLE`, `ENCHANTED_GOLDEN_APPLE`, `COOKED_BEEF`
- **Items**: `ELYTRA`, `TOTEM_OF_UNDYING`, `ENDER_PEARL`, `NETHER_STAR`

### Valid Currency Types

Untuk bagian `price:`, gunakan salah satu atau kombinasi:
- `copper` - Copper Ingot
- `iron` - Iron Ingot
- `gold` - Gold Ingot
- `emerald` - Emerald
- `diamond` - Diamond
- `netherite` - Netherite Ingot

### Format Display Name & Lore

Gunakan color codes dengan `&`:
- `&0` - Black
- `&1` - Dark Blue
- `&2` - Dark Green
- `&3` - Dark Aqua
- `&4` - Dark Red
- `&5` - Dark Purple
- `&6` - Gold
- `&7` - Gray
- `&8` - Dark Gray
- `&9` - Blue
- `&a` - Green
- `&b` - Aqua
- `&c` - Red
- `&d` - Light Purple
- `&e` - Yellow
- `&f` - White
- `&l` - **Bold**
- `&o` - *Italic*
- `&n` - Underline
- `&m` - ~~Strikethrough~~
- `&r` - Reset

## 📝 Commands

### Player Commands

| Command | Aliases | Deskripsi | Permission |
|---------|---------|-----------|------------|
| `/balance` | `/bal`, `/money` | Tampilkan balance dari Enderchest | `oreco.balance` |
| `/currency convert <amount> <from> <to>` | - | Convert mata uang | `oreco.currency` |
| `/shop` | - | Buka shop GUI | `oreco.shop` |

### Admin Commands

| Command | Deskripsi | Permission |
|---------|-----------|------------|
| `/shop admin add <material> <currency:amount> [...]` | Tambah item ke shop | `oreco.shop.admin` |
| `/shop admin remove <material>` | Hapus item dari shop | `oreco.shop.admin` |
| `/shop admin reload` | Reload shops.yml | `oreco.shop.admin` |
| `/shop admin setcategory <material> <category>` | Set kategori item | `oreco.shop.admin` |
| `/shop admin list` | List semua item di shop | `oreco.shop.admin` |

### Contoh Penggunaan

```bash
# Cek balance
/balance

# Convert 64 copper menjadi iron
/currency convert 64 copper iron

# Convert 96 iron menjadi gold
/currency convert 96 iron gold

# Buka shop
/shop

# Admin: Tambah diamond sword ke shop
/shop admin add DIAMOND_SWORD diamond:2 emerald:10

# Admin: Hapus item dari shop
/shop admin remove DIAMOND_SWORD

# Admin: Reload konfigurasi
/shop admin reload

# Admin: Ubah kategori item
/shop admin setcategory DIAMOND_SWORD Weapons

# Admin: List semua item
/shop admin list
```

## 🔐 Permissions

| Permission | Deskripsi | Default |
|------------|-----------|---------|
| `oreco.balance` | Akses command /balance | `true` (semua player) |
| `oreco.currency` | Akses command /currency | `true` (semua player) |
| `oreco.shop` | Akses shop GUI | `true` (semua player) |
| `oreco.shop.admin` | Akses admin commands | `op` (operator only) |

## ⚙️ Konfigurasi

### config.yml

```yaml
messages:
  prefix: "&8[&6Oreco&8] &r"
  insufficient_balance: "&cBalance tidak cukup!"
  purchase_success: "&aItem berhasil dibeli!"
  conversion_success: "&aKonversi berhasil!"
  conversion_failed: "&cKonversi gagal! Periksa balance Anda."
  no_permission: "&cAnda tidak punya permission!"

currency:
  values:
    copper: 1
    iron: 64
    gold: 6144
    emerald: 393216
    diamond: 100663296
    netherite: 25769803776
    
  conversion_rules:
    copper_iron: true
    iron_gold: true
    gold_emerald: true
    emerald_diamond: false  # Diamond tidak bisa dari emerald!
    diamond_netherite: true

shop:
  gui_title: "&6&lOreco Shop"
  category_gui_title: "&6&lShop - {category}"
  gui_size: 54
```

## 🎮 Cara Bermain

### Untuk Player

1. **Kumpulkan Ore/Ingot**: Mine atau farming untuk mendapatkan copper, iron, gold, emerald, diamond
2. **Simpan di Enderchest**: Masukkan currency ke Enderchest (otomatis terhitung sebagai balance)
3. **Cek Balance**: Gunakan `/balance` untuk melihat total currency
4. **Convert Currency**: Gunakan `/currency convert` untuk mengubah currency
5. **Belanja**: Buka `/shop` dan pilih item yang ingin dibeli
6. **Auto-Deduction**: Sistem otomatis ambil currency dari Enderchest saat membeli

### Untuk Admin

1. **Tambah Item**: Gunakan `/shop admin add` atau edit `shops.yml`
2. **Atur Harga**: Set harga dengan satu atau lebih currency
3. **Kelola Kategori**: Organisir item ke dalam 8 kategori
4. **Reload**: Gunakan `/shop admin reload` setelah edit config

## ❓ FAQ

### Q: Bagaimana cara mendapatkan Diamond?
**A**: Diamond tidak bisa di-convert dari Emerald. Player harus mining atau pickup diamond asli. Ini untuk menjaga balance ekonomi server.

### Q: Apakah bisa downgrade dari Netherite ke Diamond?
**A**: Tidak. Konversi Netherite → Diamond tidak diperbolehkan. Hanya Diamond → Netherite yang bisa.

### Q: Apakah balance hilang jika Enderchest kosong?
**A**: Ya, balance dihitung real-time dari Enderchest. Jika item diambil dari Enderchest, balance akan berkurang.

### Q: Bagaimana jika inventory penuh saat membeli?
**A**: Transaksi akan dibatalkan dan currency tidak akan dideduct. Pastikan ada space di inventory sebelum membeli.

### Q: Bisa custom kategori baru?
**A**: Ya! Edit `shops.yml` di section `categories` dan tambahkan kategori baru dengan icon, display name, dan slot.

### Q: Apakah support multi-world?
**A**: Ya, Enderchest shared across worlds, jadi balance sama di semua world.

### Q: Bagaimana cara backup data shop?
**A**: Cukup backup file `plugins/OrecoCurrency/shops.yml`. Tidak ada database yang perlu di-backup.

## 🔨 Build dari Source

Jika ingin compile plugin sendiri:

```bash
# Clone repository
git clone https://github.com/FikJul/oreco-mc-plugin.git
cd oreco-mc-plugin

# Build dengan Maven
mvn clean package

# File .jar akan ada di target/oreco-mc-plugin-1.0.0.jar
```

**Requirements untuk Build**:
- Java 21 JDK
- Maven 3.6+

## 🐛 Bug Reports & Feature Requests

Jika menemukan bug atau ingin request fitur:
1. Buka [Issues](https://github.com/FikJul/oreco-mc-plugin/issues)
2. Gunakan template yang sesuai
3. Berikan detail yang lengkap

## 📄 License

Plugin ini dilisensikan di bawah [MIT License](LICENSE).

## 👨‍💻 Author

**FikJul**

## 🙏 Credits

- Paper Team untuk Paper API
- Spigot Community untuk dokumentasi
- Semua contributor yang telah membantu

---

**Made with ❤️ for Minecraft Indonesia Community**