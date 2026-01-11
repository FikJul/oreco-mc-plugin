# Build Notes

## Multi-Module Maven Structure

This project uses a multi-module Maven structure to build two separate plugins:
1. **OrecoCurrency** - Currency system
2. **CustomWeaponGacha** - Gacha system (depends on OrecoCurrency)

## Building

### Build Both Plugins

```bash
mvn clean package
```

**Output:**
- `OrecoCurrency/target/OrecoCurrency-1.0.0.jar`
- `CustomWeaponGacha/target/CustomWeaponGacha-1.0.0.jar`

### Build Specific Module

```bash
# OrecoCurrency only
cd OrecoCurrency
mvn clean package

# CustomWeaponGacha only (requires OrecoCurrency built first)
cd CustomWeaponGacha
mvn clean package
```

### Using build.sh (Linux/Mac)

```bash
chmod +x build.sh
./build.sh
```

## Installation

Copy **BOTH** jar files to your server's `plugins/` folder:
```
server/plugins/
├── OrecoCurrency-1.0.0.jar
└── CustomWeaponGacha-1.0.0.jar
```

CustomWeaponGacha **requires** OrecoCurrency to function (hard dependency).

## Development

- Parent POM manages shared configuration
- Modules inherit from parent
- CustomWeaponGacha can access OrecoCurrency classes at compile-time
- Both plugins are independent at runtime (separate jar files)

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
- Build from root directory with `mvn clean package`
- Parent POM will ensure correct build order
