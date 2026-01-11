# Build & Deployment Notes

## Project Structure

This repository contains **TWO** separate plugins:

1. **OrecoCurrency** - Currency system plugin (base plugin)
2. **CustomWeaponGacha** - Weapon gacha plugin (depends on OrecoCurrency)

Both plugins are in the same repository but will be built as **separate JAR files**.

## Build Process

### Current Setup (Single JAR)

The current `pom.xml` builds both plugins into a single JAR. To deploy:

```bash
mvn clean package
```

This creates: `target/oreco-mc-plugin-1.0.0.jar` (contains both plugins)

### Recommended: Multi-Module Maven Setup

For proper separation, convert to multi-module project:

```
oreco-mc-plugin/
├── pom.xml (parent)
├── OrecoCurrency/
│   ├── pom.xml
│   └── src/...
└── CustomWeaponGacha/
    ├── pom.xml (depends on OrecoCurrency)
    └── src/...
```

### Alternative: Manual Build

1. **Build OrecoCurrency first:**
   ```bash
   # Temporarily remove CustomWeapon sources
   mvn clean package
   # Output: OrecoCurrency-1.0.0.jar
   ```

2. **Install OrecoCurrency to local Maven:**
   ```bash
   mvn install:install-file \
     -Dfile=target/OrecoCurrency-1.0.0.jar \
     -DgroupId=com.fikjul \
     -DartifactId=orecurrency \
     -Dversion=1.0.0 \
     -Dpackaging=jar
   ```

3. **Update CustomWeaponGacha pom.xml:**
   ```xml
   <dependency>
       <groupId>com.fikjul</groupId>
       <artifactId>orecurrency</artifactId>
       <version>1.0.0</version>
       <scope>provided</scope>
   </dependency>
   ```

4. **Build CustomWeaponGacha:**
   ```bash
   mvn clean package
   # Output: CustomWeaponGacha-1.0.0.jar
   ```

## Deployment

### Server Setup

1. **Install OrecoCurrency:**
   ```bash
   cp OrecoCurrency-1.0.0.jar plugins/
   ```

2. **Start server** to load OrecoCurrency

3. **Install CustomWeaponGacha:**
   ```bash
   cp CustomWeaponGacha-1.0.0.jar plugins/
   ```

4. **Restart server**

### Verification

Check that both plugins loaded:
```bash
plugins
```

Should show:
- OrecoCurrency v1.0.0
- CustomWeaponGacha v1.0.0

## Development Notes

- CustomWeaponGacha **requires** OrecoCurrency to be loaded first
- OrecoCurrency must be in `plugins/` folder before CustomWeaponGacha
- Both plugins share the same Java version requirement (21)
- Both plugins target Paper 1.21+

## Integration Points

CustomWeaponGacha accesses OrecoCurrency via:

```java
// Get OrecoCurrency plugin instance
Plugin orecoPlugin = Bukkit.getPluginManager().getPlugin("OrecoCurrency");
OrecoPlugin oreco = (OrecoPlugin) orecoPlugin;

// Use CurrencyManager
CurrencyManager cm = oreco.getCurrencyManager();
cm.deductCurrency(player, prices);
```

Make sure OrecoCurrency API remains stable between versions!

## Troubleshooting

### CustomWeaponGacha won't load

**Error:** "OrecoCurrency plugin not found!"

**Fix:**
- Ensure OrecoCurrency is installed
- Check OrecoCurrency loaded successfully
- Verify plugin.yml `depend: [OrecoCurrency]`

### Compilation errors

**Error:** Cannot find OrecoCurrency classes

**Fix:**
- OrecoCurrency must be in classpath
- Use Maven dependency or install locally
- Or use multi-module Maven structure

---

**For production deployment, recommend converting to multi-module Maven project!**
