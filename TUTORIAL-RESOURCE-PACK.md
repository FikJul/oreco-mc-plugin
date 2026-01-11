# Tutorial: Setup Resource Pack untuk Custom Weapons

Tutorial lengkap untuk membuat dan menggunakan resource pack dengan custom weapon models.

## 📋 Prerequisites

- Blockbench (download dari https://www.blockbench.net/)
- Image editor (GIMP, Photoshop, Paint.NET, atau Pixlr)
- File hosting (GitHub Releases, Dropbox, Google Drive, dll)
- Akses ke server.properties

## 🎨 Step 1: Create Weapon Textures

### Menggunakan Image Editor

1. **Buat file PNG baru**
   - Size: 16x16 pixels (atau 32x32 untuk detail lebih)
   - Background: Transparent (alpha channel)

2. **Design weapon texture**
   - Gambar sword dari samping (side view)
   - Gunakan warna yang sesuai dengan tema weapon:
     - Astraedge: Biru/putih (tema bintang)
     - Blood Oath: Merah/hitam (tema darah)
     - Chronoslicer: Ungu/hitam (tema waktu)

3. **Export sebagai PNG**
   - Astraedge → `astraedge.png`
   - Blood Oath Saber → `blood_oath.png`
   - Chronoslicer → `chronoslicer.png`

4. **Copy ke resource pack**
   ```
   resource-pack-template/assets/minecraft/textures/item/
   ```

### Tips Texturing

- Gunakan referensi dari vanilla Minecraft items
- Jangan terlalu detail untuk 16x16
- Pastikan outline jelas dan kontras tinggi
- Test di game untuk melihat hasilnya

## 🔨 Step 2: Create 3D Models (Optional)

Jika ingin model 3D instead of flat texture:

### Menggunakan Blockbench

1. **Open Blockbench**
   - New → Java Block/Item

2. **Create sword model**
   - Add cubes untuk blade, handle, guard
   - Adjust posisi dan size
   - Apply textures

3. **Export**
   - File → Export → Java Item Model
   - Save ke `resource-pack-template/assets/minecraft/models/item/`

4. **Update texture path**
   - Edit JSON file
   - Set texture path sesuai nama file

Untuk tutorial detail Blockbench, lihat **TUTORIAL-BLOCKBENCH.md**.

## 📦 Step 3: Package Resource Pack

### Create ZIP File

1. **Navigate ke folder resource-pack-template**

2. **Select semua files**
   - pack.mcmeta
   - assets/ folder
   - pack.png (optional - icon untuk resource pack)

3. **Create ZIP archive**
   
   **Windows:**
   - Select all → Right-click → Send to → Compressed folder
   
   **Mac:**
   - Select all → Right-click → Compress
   
   **Linux:**
   ```bash
   cd resource-pack-template
   zip -r ../oreco-weapons-pack.zip *
   ```

4. **Rename ZIP** (optional)
   - `oreco-weapons-pack.zip`

### Verify ZIP Structure

Extract dan pastikan struktur seperti ini:
```
oreco-weapons-pack.zip
├── pack.mcmeta
├── assets/
│   └── minecraft/
│       ├── models/
│       │   └── item/
│       └── textures/
│           └── item/
```

**PENTING:** Jangan ada folder wrapper! `pack.mcmeta` harus ada di root ZIP.

## ☁️ Step 4: Upload to Hosting

### Option A: GitHub Releases (Recommended)

1. **Create GitHub Repository**
   - Atau gunakan existing repo

2. **Create Release**
   - Go to Releases → Create new release
   - Tag: `v1.0` (atau version apapun)
   - Title: "Oreco Custom Weapons Resource Pack v1.0"
   - Upload `oreco-weapons-pack.zip`

3. **Get Direct Download URL**
   - Right-click ZIP → Copy link address
   - URL format: `https://github.com/USER/REPO/releases/download/TAG/FILENAME.zip`

### Option B: Dropbox

1. Upload ZIP ke Dropbox
2. Create share link
3. Change `?dl=0` to `?dl=1` di URL untuk direct download

### Option C: Google Drive

1. Upload ZIP ke Google Drive
2. Set sharing to "Anyone with the link"
3. Use: `https://drive.google.com/uc?export=download&id=FILE_ID`

### Option D: Self-hosted

Upload ke web server Anda sendiri dengan direct access.

## 🔐 Step 5: Generate SHA1 Hash

SHA1 hash diperlukan untuk verify resource pack.

### Using Provided Script

```bash
./generate-sha1.sh oreco-weapons-pack.zip
```

### Manual (Linux/Mac)

```bash
sha1sum oreco-weapons-pack.zip
```

### Manual (Windows)

```powershell
CertUtil -hashfile oreco-weapons-pack.zip SHA1
```

**Copy hash yang dihasilkan** - akan digunakan di server.properties.

## ⚙️ Step 6: Configure Server

Edit `server.properties`:

```properties
# Resource Pack Configuration
resource-pack=https://github.com/USER/REPO/releases/download/v1.0/oreco-weapons-pack.zip
resource-pack-sha1=PASTE_SHA1_HASH_HERE
require-resource-pack=true
resource-pack-prompt=§6Custom Weapons Resource Pack\n§7Download untuk melihat custom models!
```

**Contoh lengkap:**
```properties
resource-pack=https://github.com/FikJul/oreco-weapons/releases/download/v1.0/oreco-weapons-pack.zip
resource-pack-sha1=a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0
require-resource-pack=true
resource-pack-prompt=§6Oreco Custom Weapons\n§7Required for weapon models
```

## 🔄 Step 7: Restart Server

```bash
stop
# Wait for server to stop
# Start server
```

## ✅ Step 8: Test In-Game

1. **Join server**
   - Player akan diminta download resource pack

2. **Accept download**

3. **Test weapon**
   - Get weapon via `/gacha` atau admin command
   - Check apakah custom model muncul

4. **Troubleshooting**
   - Jika tidak muncul: Check F3 debug (resource pack loaded?)
   - Redownload: `/reload confirm` atau reconnect

## 🔧 Troubleshooting

### Resource pack tidak auto-download

**Problem:** Player tidak diminta download

**Fix:**
- Cek URL accessible dari browser
- Verify SHA1 hash correct
- Pastikan `require-resource-pack=true`

### Texture tidak muncul

**Problem:** Weapon masih pakai vanilla texture

**Fix:**
- Verify custom_model_data di weapons.yml
- Check texture path di model JSON
- Pastikan PNG files ada di textures/item/

### "Invalid resource pack" error

**Problem:** Server reject resource pack

**Fix:**
- Verify pack.mcmeta format correct (JSON valid)
- Check pack_format = 34 (Minecraft 1.21)
- Pastikan ZIP structure benar (no wrapper folder)

### Model terbalik atau aneh

**Problem:** Weapon model orientation salah

**Fix:**
- Edit model JSON
- Adjust rotation/display settings
- Re-export dari Blockbench

## 📚 Next Steps

- **TUTORIAL-BLOCKBENCH.md** - Tutorial lengkap Blockbench modeling
- **ADDING-WEAPONS.md** - Cara tambah weapon baru
- **WEAPON-GACHA-README.md** - Plugin documentation lengkap

## 🎓 Resources

- Blockbench: https://www.blockbench.net/
- Minecraft Wiki - Resource Pack: https://minecraft.wiki/w/Resource_pack
- Custom Model Data: https://minecraft.wiki/w/Tutorials/Models
- GIMP (Free Image Editor): https://www.gimp.org/

---

**Good luck creating custom weapons! 🎨⚔️**
