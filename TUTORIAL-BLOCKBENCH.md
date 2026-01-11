# Blockbench Tutorial untuk Custom Weapon Models

Tutorial lengkap menggunakan Blockbench untuk membuat 3D models custom weapons.

## Download & Installation

1. **Download Blockbench**
   - Website: https://www.blockbench.net/
   - Platform: Windows, Mac, Linux, Web App
   - Free & Open Source

2. **Install**
   - Windows: Run installer `.exe`
   - Mac: Open `.dmg` and drag to Applications
   - Linux: Use AppImage or package manager
   - Web: Use directly in browser

## Creating a Sword Model

### Step 1: New Project

1. Open Blockbench
2. Click "New" → "Java Block/Item"
3. Settings:
   - File Name: `mysword`
   - Model Identifier: leave default
   - Texture Width: 16
   - Texture Height: 16
4. Click "Confirm"

### Step 2: Build the Blade

1. **Add Cube**
   - Click "Add Cube" button (or press `Ctrl+A`)
   
2. **Shape the Blade**
   - Size: Width 1, Height 16, Depth 0.5
   - Position: X 0, Y 8, Z 0
   - This creates the main blade

3. **Refine Shape**
   - Click cube to select
   - Adjust size in "Element" panel on right
   - Make blade thin and long

### Step 3: Add Detail Elements

1. **Blade Tip** (pointed end)
   - Add new cube
   - Size: Width 1, Height 2, Depth 0.5
   - Position above blade
   - Rotate slightly for sharp tip

2. **Cross Guard**
   - Add cube
   - Size: Width 5, Height 1, Depth 1
   - Position at base of blade
   - Horizontal orientation

3. **Handle**
   - Add cube
   - Size: Width 1, Height 6, Depth 1
   - Position below cross guard
   - Slightly thicker than blade

4. **Pommel** (end of handle)
   - Add cube
   - Size: Width 2, Height 2, Depth 2
   - Position at bottom of handle
   - Decorative end piece

### Step 4: Texturing

#### Paint on Model

1. **Switch to Paint Mode**
   - Click "Paint" tab at top

2. **Select Color**
   - Choose color from palette
   - Or use color picker

3. **Paint Faces**
   - Click on cube faces to paint
   - Use brush tool to paint details
   - Eraser to remove

#### Example Color Schemes

**Astraedge (Star Theme):**
- Blade: Light blue (#87CEEB)
- Edge: White (#FFFFFF)
- Guard: Silver (#C0C0C0)
- Handle: Dark blue (#00008B)

**Blood Oath (Dark Theme):**
- Blade: Dark red (#8B0000)
- Edge: Crimson (#DC143C)
- Guard: Black (#000000)
- Handle: Dark brown (#3B1F1F)

**Chronoslicer (Time Theme):**
- Blade: Purple (#9370DB)
- Edge: Light purple (#DDA0DD)
- Guard: Black (#000000)
- Handle: Dark purple (#4B0082)

### Step 5: Preview

1. **Switch to Preview**
   - Use preview window on right
   - Rotate view: Right-click drag
   - Zoom: Mouse wheel

2. **Test Different Angles**
   - View from front, side, back
   - Ensure proportions look good

3. **Adjust as Needed**
   - Go back and modify cubes
   - Repaint if necessary

### Step 6: Item Display Settings

Important for how item appears in-game!

1. **Open Display Settings**
   - Click "Display" tab at top

2. **Configure Each View**
   
   **Third Person Right Hand:**
   - Translation: X 0, Y 2, Z 0
   - Rotation: X 0, Y -90, Z 55
   - Scale: 0.85, 0.85, 0.85

   **First Person Right Hand:**
   - Translation: X 1.13, Y 3.2, Z 1.13
   - Rotation: X 0, Y -90, Z 25
   - Scale: 0.68, 0.68, 0.68

   **GUI (Inventory):**
   - Translation: X 0, Y 0, Z 0
   - Rotation: X 30, Y 225, Z 0
   - Scale: 0.625, 0.625, 0.625

   **Ground:**
   - Translation: X 0, Y 2, Z 0
   - Rotation: X 0, Y 0, Z 0
   - Scale: 0.25, 0.25, 0.25

3. **Test Preview**
   - Click each display mode
   - Adjust until it looks good

### Step 7: Export

1. **Export Model**
   - File → Export → Java Item Model
   - Save to: `resource-pack-template/assets/minecraft/models/item/mysword.json`

2. **Export Texture** (if painted in Blockbench)
   - File → Export → Export Texture
   - Save to: `resource-pack-template/assets/minecraft/textures/item/mysword.png`

## Advanced Techniques

### Adding Glow Effect

1. Create separate glow texture layer
2. Use emissive texture (requires OptiFine/Shaders)
3. Paint glow areas in bright colors

### Animated Textures

1. Create multiple frames
2. Stack vertically in texture
3. Use Minecraft's texture animation format

### Complex Shapes

1. Use more cubes for detail
2. Rotate cubes for angles
3. Combine shapes creatively

## Tips & Tricks

### Modeling

- **Start Simple**: Build basic shape first
- **Symmetry**: Use mirror/copy for symmetric parts
- **Proportions**: Vanilla sword is 1x16x0.5 for reference
- **Low Poly**: Keep cube count reasonable (< 50)

### Texturing

- **Contrast**: Use light and dark for depth
- **Outlines**: Add dark edges for definition
- **Highlights**: Light spots for shine effect
- **Gradients**: Blend colors smoothly

### Display Settings

- **Test In-Game**: Best way to verify
- **Reference Vanilla**: Check vanilla sword settings
- **Iterate**: Adjust based on how it looks in-game

## Common Issues

### Model too big/small in-hand

**Fix:** Adjust scale in Display Settings (Third Person Right Hand)

### Model rotated wrong way

**Fix:** Adjust rotation in Display Settings

### Texture looks pixelated

**Fix:** 
- Use higher resolution (32x32 instead of 16x16)
- Or embrace pixel art style

### Model doesn't load in-game

**Fix:**
- Verify JSON syntax valid
- Check custom_model_data matches weapons.yml
- Ensure resource pack installed

## Resources

- **Blockbench Website**: https://www.blockbench.net/
- **Blockbench Wiki**: https://www.blockbench.net/wiki/
- **Minecraft Models**: https://minecraft.wiki/w/Model
- **YouTube Tutorials**: Search "Blockbench Minecraft Weapon Tutorial"

## Example Workflow

1. **Plan Design** - Sketch on paper
2. **Build Base Shape** - Main blade & handle
3. **Add Details** - Guard, pommel, decorations
4. **Paint Texture** - Colors and patterns
5. **Configure Display** - Rotation and scale
6. **Export Files** - Model JSON & texture PNG
7. **Test In-Game** - Iterate if needed

---

Happy modeling! 🎨⚔️

**Pro Tip:** Save frequently! Blockbench has auto-save but manual saves are safer.
