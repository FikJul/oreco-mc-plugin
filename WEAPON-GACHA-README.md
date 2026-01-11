# Custom Weapon Gacha Plugin

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21-green)
![Paper](https://img.shields.io/badge/Paper-Required-blue)
![Java](https://img.shields.io/badge/Java-21-orange)
![Dependency](https://img.shields.io/badge/Dependency-OrecoCurrency-purple)

Plugin Paper untuk Minecraft 1.21 yang mengimplementasikan sistem gacha untuk custom weapons dengan abilities, enchantments, dan resource pack support.

## 📋 Deskripsi

Custom Weapon Gacha adalah sistem gacha lengkap yang memungkinkan players mendapatkan custom weapons dengan abilities unik melalui sistem roll. Plugin ini terintegrasi penuh dengan OrecoCurrency untuk pembelian token gacha menggunakan diamonds.

## ✨ Fitur Utama

### Custom Weapons (3 Tier Legendary)
- **Astraedge** (LEGEND) - Pedang bintang dengan Stellar Dash ability
- **Blood Oath Saber** (MYTHIC) - Pedang soulbound dengan Crimson Pact & lifesteal
- **Chronoslicer** (GOD) - Pedang waktu dengan Time Fracture & Temporal Echo

### Gacha System
- **Multi-Banner System** - Berbagai banner gacha (Sword, Bow, Special)
- **Pity System** - Soft pity (70 rolls) & Hard pity (90 rolls)
- **Rate-Up Events** - Admin dapat mengaktifkan rate-up untuk rarity tertentu
- **10x Roll Discount** - Roll 10x dengan 9 token (hemat 1 token)

### Token System
- **Pembelian Token** - Beli dengan 64 diamond per token via OrecoCurrency
- **Boss Drops** - Ender Dragon (20% chance, 2-5 token) & Wither (40% chance, 1-3 token)
- **Physical Tokens** - Token drop sebagai item yang bisa di-pickup

### Collection System
- **Weapon Tracking** - Catat semua weapons yang sudah didapat
- **Progress Tracking** - Lihat persentase koleksi completed
- **Roll History** - Lacak pity counter per banner

## 🔧 Requirements

- **Minecraft**: 1.21.x
- **Server**: Paper (atau fork seperti Purpur)
- **Java**: 21 atau lebih tinggi
- **Hard Dependency**: OrecoCurrency plugin (harus terinstall)

## 📦 Instalasi

### 1. Install OrecoCurrency

Download dan install OrecoCurrency plugin terlebih dahulu dari [Releases](https://github.com/FikJul/oreco-mc-plugin/releases).

```bash
# Copy ke folder plugins
cp OrecoCurrency-1.0.0.jar plugins/
```

### 2. Install CustomWeaponGacha

Download file `CustomWeaponGacha-1.0.0.jar` dari [Releases](https://github.com/FikJul/oreco-mc-plugin/releases).

```bash
# Copy ke folder plugins
cp CustomWeaponGacha-1.0.0.jar plugins/
```

### 3. Restart Server

```bash
# Restart server untuk load plugins
stop
# Start server again
```

### 4. Konfigurasi (Opsional)

Edit file konfigurasi di `plugins/CustomWeaponGacha/`:
- `config.yml` - Pesan, token cost, boss drops, pity settings
- `weapons.yml` - Definisi custom weapons
- `banners.yml` - Banner gacha dan drop rates
- `drops.yml` - Item pools untuk gacha

### 5. Resource Pack (Opsional)

Untuk menampilkan custom model weapons, setup resource pack:
1. Lihat tutorial di **TUTORIAL-RESOURCE-PACK.md**
2. Upload resource pack ke hosting
3. Konfigurasi di `server.properties`

## 💰 Sistem Token

### Cara Mendapatkan Token

#### 1. Beli dengan Diamond (OrecoCurrency)

```bash
/gacha buytoken <amount>
```

**Contoh:**
```bash
/gacha buytoken 10  # Beli 10 token dengan 640 diamond
```

**Catatan:**
- 1 token = 64 diamond
- Diamond diambil dari **Enderchest** via OrecoCurrency
- Pastikan ada cukup diamond di enderchest sebelum membeli

#### 2. Boss Drops

**Ender Dragon:**
- Drop chance: 20%
- Token amount: 2-5
- Broadcast ke server saat drop

**Wither:**
- Drop chance: 40%
- Token amount: 1-3
- Broadcast ke server saat drop

**Cara menggunakan:**
- Token drop sebagai Nether Star dengan glow
- Right-click untuk menambahkan ke balance

## 🎮 Commands

### Player Commands

| Command | Deskripsi | Permission |
|---------|-----------|------------|
| `/gacha` | Buka menu gacha | `customweapon.gacha` |
| `/gacha balance` | Cek token balance | `customweapon.gacha` |
| `/gacha buytoken <amount>` | Beli token dengan diamond | `customweapon.buytoken` |
| `/gacha roll <banner>` | Roll single gacha | `customweapon.gacha` |
| `/gacha roll <banner> 10x` | Roll 10x gacha | `customweapon.gacha` |
| `/gacha collection` | Lihat weapon collection | `customweapon.collection` |

### Admin Commands

| Command | Deskripsi | Permission |
|---------|-----------|------------|
| `/gacha admin reload` | Reload konfigurasi | `customweapon.admin` |
| `/gacha admin rateup <banner> <rarity> <mult> <dur>` | Aktifkan rate-up event | `customweapon.admin` |

### Contoh Penggunaan

```bash
# Cek token balance
/gacha balance

# Beli 5 token
/gacha buytoken 5

# Roll single di sword_banner
/gacha roll sword_banner

# Roll 10x di sword_banner
/gacha roll sword_banner 10x

# Lihat koleksi weapon
/gacha collection

# Admin: Aktifkan rate-up GOD tier x2 selama 7 hari
/gacha admin rateup sword_banner god 2.0 7d

# Admin: Reload config
/gacha admin reload
```

## ⚔️ Custom Weapons

### Astraedge (LEGEND)
- **Damage**: +20% dari Netherite Sword
- **Attack Speed**: 1.7
- **Active Ability - Stellar Dash**: Dash forward 6 blocks + true damage boost
- **Passive Ability - Astral Focus**: +5% damage saat HP > 80%
- **Custom Enchant - Astral Mark III**: Hit 3x berturut = +10% damage 4 detik

### Blood Oath Saber (MYTHIC)
- **Damage**: +30% dari Netherite Sword
- **Attack Speed**: 1.5
- **Soulbound**: Hanya owner yang bisa pakai efektif (player lain -50% damage)
- **Active Ability - Crimson Pact**: Korban 10% HP untuk Strength II + 15% lifesteal
- **Passive Ability - Pain Conversion**: Lifesteal 5% saat HP < 40%
- **On-Hit**: Bleeding DOT 0.5 damage/second selama 3 detik

### Chronoslicer (GOD)
- **Damage**: +40% dari Netherite Sword
- **Attack Speed**: 1.6
- **Active Ability - Time Fracture**: AOE Slowness III radius 4 blocks
- **Passive Ability - Momentum Shift**: Stack +3% attack speed per hit (max 5 stacks)
- **On-Hit - Temporal Echo**: 10% chance serangan repeat dengan 50% damage (0.5s delay)

## 🎲 Gacha Drop Rates

### Sword Banner

| Rarity | Rate | Drops |
|--------|------|-------|
| COMMON | 50% | Cobblestone, Iron Ore, Coal, Stone/Iron Sword |
| RARE | 25% | Gold Ore, Diamond, Enchanted Diamond Sword |
| EPIC | 10% | Emerald, Epic Enchanted Swords, Netherite Sword |
| LEGEND | 10% | **Astraedge** |
| MYTHIC | 4% | **Blood Oath Saber** |
| GOD | 1% | **Chronoslicer** |

### Pity System

**Soft Pity:**
- Starts at roll 70
- +5% chance per roll untuk GOD tier

**Hard Pity:**
- Roll 50: Guaranteed MYTHIC atau GOD
- Roll 90: Guaranteed GOD tier

**Reset:**
- Pity reset saat dapat GOD tier
- Pity TIDAK carry between banners

## 🔐 Permissions

| Permission | Deskripsi | Default |
|------------|-----------|---------|
| `customweapon.gacha` | Akses gacha system | `true` |
| `customweapon.buytoken` | Beli token | `true` |
| `customweapon.collection` | Akses collection | `true` |
| `customweapon.admin` | Admin commands | `op` |

## ⚙️ Konfigurasi

Lihat file konfigurasi untuk customization lengkap:

- **config.yml** - Messages, token cost, boss drops, pity
- **weapons.yml** - Weapon definitions, abilities, enchants
- **banners.yml** - Gacha banners, drop pools, rates
- **drops.yml** - Common/rare/epic item pools

## 🎨 Resource Pack

Plugin ini support custom model data untuk weapons. Untuk menggunakan custom 3D models:

1. Lihat **TUTORIAL-RESOURCE-PACK.md** untuk setup lengkap
2. Gunakan template di folder `resource-pack-template/`
3. Create textures dengan Blockbench (lihat **TUTORIAL-BLOCKBENCH.md**)
4. Upload resource pack ke hosting
5. Set di `server.properties`

**Custom Model Data IDs:**
- Astraedge: 1001
- Blood Oath Saber: 1002
- Chronoslicer: 1003

## ❓ FAQ

### Q: Apakah harus punya OrecoCurrency?
**A**: Ya! CustomWeaponGacha membutuhkan OrecoCurrency sebagai hard dependency. Plugin tidak akan load tanpa OrecoCurrency.

### Q: Bagaimana cara mendapat token gratis?
**A**: Kill Ender Dragon (20% chance 2-5 token) atau Wither (40% chance 1-3 token).

### Q: Apakah bisa downgrade Blood Oath Saber ke player lain?
**A**: Bisa trade, tapi player lain akan dapat -50% damage penalty karena soulbound.

### Q: Apa yang terjadi jika inventory full saat gacha?
**A**: Item akan masuk ke inventory. Jika full, item akan drop di ground.

### Q: Apakah pity carry antar banner?
**A**: Tidak (default). Pity counter terpisah per banner dan tidak carry over.

### Q: Bisa tambah weapon baru?
**A**: Ya! Lihat **ADDING-WEAPONS.md** untuk tutorial lengkap.

## 🔨 Build dari Source

```bash
# Clone repository
git clone https://github.com/FikJul/oreco-mc-plugin.git
cd oreco-mc-plugin

# Build dengan Maven
mvn clean package

# File .jar ada di target/
ls target/*.jar
```

## 🐛 Bug Reports

Jika menemukan bug atau ingin request fitur:
1. Buka [Issues](https://github.com/FikJul/oreco-mc-plugin/issues)
2. Gunakan template yang sesuai
3. Berikan detail lengkap

## 📄 License

MIT License - Lihat [LICENSE](LICENSE) untuk detail.

## 👨‍💻 Author

**FikJul**

---

**Made with ❤️ for Minecraft Indonesia Community**
