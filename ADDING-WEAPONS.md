# Adding New Weapons to Custom Weapon Gacha

Step-by-step guide untuk menambah custom weapons baru ke plugin.

## Prerequisites

- Text editor (VS Code, Notepad++, Sublime, dll)
- Basic knowledge YAML syntax
- (Optional) Blockbench untuk custom models

## Step 1: Define Weapon di weapons.yml

Edit `plugins/CustomWeaponGacha/weapons.yml`:

```yaml
weapons:
  # ID weapon Anda (lowercase, no spaces)
  mysword:
    display_name: "&c&lMy Custom Sword"
    lore:
      - "&7This is my custom sword"
      - "&7With special abilities!"
      - ""
      - "&6Rarity: &eEPIC"
    
    # Base properties
    base_item: NETHERITE_SWORD
    custom_model_data: 1004  # Unique ID untuk model
    damage_multiplier: 1.15  # +15% damage
    attack_speed: 1.6
    rarity: EPIC
    unbreakable: false
```

## Step 2: Add Abilities (Optional)

### Active Ability Example

```yaml
abilities:
  active:
    name: "My Ability"
    trigger: RIGHT_CLICK
    cooldown: 20  # seconds
    effects:
      - type: DASH_FORWARD
        distance: 5
        particle: FLAME
        sound: ENTITY_BLAZE_SHOOT
```

### Passive Ability Example

```yaml
abilities:
  passive:
    name: "My Passive"
    condition: "health < 50%"
    effect:
      type: DAMAGE_BOOST
      amount: 10  # +10%
```

### On-Hit Effects Example

```yaml
on_hit_effects:
  - type: POTION
    effect: POISON
    level: 2
    duration: 3
    chance: 25  # 25% chance
    particle: VILLAGER_ANGRY
```

## Step 3: Add Enchantments (Optional)

### Vanilla Enchantments

```yaml
enchantments:
  vanilla:
    SHARPNESS: 5
    FIRE_ASPECT: 2
    UNBREAKING: 3
```

### Custom Enchantments

```yaml
enchantments:
  custom:
    my_enchant:
      name: "&dMy Enchant"
      lore:
        - "&7Special effect description"
      # Custom mechanics (implement in code)
```

## Step 4: Add to Gacha Banner

Edit `plugins/CustomWeaponGacha/banners.yml`:

```yaml
banners:
  sword_banner:
    drop_pool:
      epic:  # Sesuaikan dengan rarity weapon
        # ... existing items ...
        - weapon: mysword  # ID dari weapons.yml
          weight: 5  # Drop weight (higher = more common)
```

## Step 5: Create Resource Pack Model (Optional)

Jika menggunakan custom model:

### 1. Create Model JSON

File: `resource-pack-template/assets/minecraft/models/item/mysword.json`

```json
{
  "parent": "item/handheld",
  "textures": {
    "layer0": "item/mysword"
  }
}
```

### 2. Update Netherite Sword Override

File: `resource-pack-template/assets/minecraft/models/item/netherite_sword.json`

```json
{
  "overrides": [
    // ... existing overrides ...
    {
      "predicate": {
        "custom_model_data": 1004
      },
      "model": "item/mysword"
    }
  ]
}
```

### 3. Create Texture

- Create PNG texture (16x16 or 32x32)
- Save to: `resource-pack-template/assets/minecraft/textures/item/mysword.png`

## Step 6: Reload Plugin

In-game sebagai admin:

```bash
/gacha admin reload
```

Atau restart server.

## Step 7: Test Weapon

### Give via Admin Command (requires implementation)

```bash
# For testing, manually add to gacha drops then roll
/gacha roll sword_banner
```

### Test Abilities

- Right-click untuk active ability
- Hit enemy untuk passive/on-hit effects
- Check damage multiplier works

## Complete Template

```yaml
weapons:
  weapon_id:
    # Display Properties
    display_name: "&6&lWeapon Name"
    lore:
      - "&7Description line 1"
      - "&7Description line 2"
      - ""
      - "&6Rarity: &eRARITY_NAME"
    
    # Base Properties
    base_item: NETHERITE_SWORD  # DIAMOND_SWORD, NETHERITE_SWORD, etc
    custom_model_data: 1000  # Unique integer
    damage_multiplier: 1.0  # 1.0 = normal, 1.2 = +20%, etc
    attack_speed: 1.6  # Vanilla netherite sword = 1.6
    rarity: COMMON  # COMMON, RARE, EPIC, LEGEND, MYTHIC, GOD
    unbreakable: false  # true = weapon never breaks
    
    # Soulbound (Optional)
    soulbound:
      enabled: false
      type: PERMANENT
      bind_on: FIRST_PICKUP
      other_player_damage_penalty: 50  # %
      lore_bound: "&cBound to: {player}"
    
    # Abilities (Optional)
    abilities:
      # Active ability (triggered by right-click)
      active:
        name: "Ability Name"
        trigger: RIGHT_CLICK
        cooldown: 15  # seconds
        cost:  # Optional
          type: HEALTH_PERCENT
          amount: 10
        effects:
          - type: DASH_FORWARD
            distance: 6
            particle: FIREWORK
            sound: ENTITY_ENDER_DRAGON_FLAP
      
      # Passive ability (always active when condition met)
      passive:
        name: "Passive Name"
        condition: "health > 80%"  # or "health < 40%", etc
        effect:
          type: DAMAGE_BOOST
          amount: 5  # %
    
    # On-Hit Effects (Optional)
    on_hit_effects:
      - type: POTION
        effect: WEAKNESS
        level: 1
        duration: 2  # seconds
        chance: 15  # %
        particle: ENCHANTMENT_TABLE
        sound: ENTITY_PLAYER_HURT
    
    # Enchantments (Optional)
    enchantments:
      vanilla:
        SHARPNESS: 5
        UNBREAKING: 3
      custom:
        custom_enchant_id:
          name: "&5Custom Enchant"
          lore:
            - "&7Effect description"
```

## Available Effect Types

### Ability Effects

- `DASH_FORWARD` - Dash forward X blocks
- `TRUE_DAMAGE_BOOST` - Add true damage (bypasses armor)
- `POTION_SELF` - Apply potion effect to self
- `LIFESTEAL` - Heal % of damage dealt
- `AOE_POTION` - Apply potion in radius
- `AOE_COOLDOWN_EXTEND` - Extend enemy cooldowns

### On-Hit Effects

- `POTION` - Apply potion effect to enemy
- `CUSTOM_DOT` - Custom damage over time
- `TEMPORAL_ECHO` - Repeat attack after delay

## Tips & Best Practices

### Balancing

- **Common weapons**: 1.0x - 1.05x damage
- **Rare weapons**: 1.05x - 1.10x damage
- **Epic weapons**: 1.10x - 1.15x damage
- **Legend weapons**: 1.15x - 1.25x damage
- **Mythic weapons**: 1.25x - 1.35x damage
- **God weapons**: 1.35x - 1.50x damage

### Abilities

- Short cooldowns (5-10s): Weak effects
- Medium cooldowns (15-25s): Moderate effects
- Long cooldowns (30-60s): Strong effects

### Drop Rates

Adjust weight in banners.yml:
- Higher weight = more common
- Lower weight = more rare
- Weight 1 = baseline

## Troubleshooting

### Weapon tidak muncul di gacha

- Check weapons.yml syntax (YAML valid?)
- Check weapon ID di banners.yml matches
- Reload plugin: `/gacha admin reload`

### Custom model tidak muncul

- Verify custom_model_data unique
- Check resource pack installed on client
- Verify JSON model syntax correct

### Abilities tidak bekerja

- Check ability framework implemented in code
- Some abilities require additional code
- See WeaponListener.java for implementation

## Next Steps

- **TUTORIAL-BLOCKBENCH.md** - Create 3D models
- **TUTORIAL-RESOURCE-PACK.md** - Setup resource pack
- **WEAPON-GACHA-README.md** - Plugin documentation

---

Happy weapon creating! ⚔️✨
