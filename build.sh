#!/bin/bash

echo "=========================================="
echo "Building Oreco MC Plugin (Multi-Module)"
echo "=========================================="

# Clean previous builds
echo "Cleaning previous builds..."
mvn clean

# Build all modules
echo "Building all modules..."
mvn package

# Check build results
echo ""
echo "=========================================="
echo "Build Results:"
echo "=========================================="

if [ -f "OrecoCurrency/target/OrecoCurrency-1.0.0.jar" ]; then
    echo "✓ OrecoCurrency-1.0.0.jar built successfully"
    ls -lh OrecoCurrency/target/OrecoCurrency-1.0.0.jar
else
    echo "✗ OrecoCurrency build failed!"
fi

if [ -f "CustomWeaponGacha/target/CustomWeaponGacha-1.0.0.jar" ]; then
    echo "✓ CustomWeaponGacha-1.0.0.jar built successfully"
    ls -lh CustomWeaponGacha/target/CustomWeaponGacha-1.0.0.jar
else
    echo "✗ CustomWeaponGacha build failed!"
fi

echo ""
echo "Copy these files to your server's plugins folder:"
echo "  - OrecoCurrency/target/OrecoCurrency-1.0.0.jar"
echo "  - CustomWeaponGacha/target/CustomWeaponGacha-1.0.0.jar"
echo "=========================================="
