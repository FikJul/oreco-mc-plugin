#!/bin/bash
# Build script for Oreco MC Plugins

echo "════════════════════════════════════════════"
echo "  Building Oreco MC Plugins..."
echo "════════════════════════════════════════════"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven not found! Please install Maven first."
    exit 1
fi

# Clean previous builds
echo "🧹 Cleaning previous builds..."
mvn clean

# Build project
echo "🔨 Building with Maven..."
mvn package

# Check build status
if [ $? -eq 0 ]; then
    echo ""
    echo "════════════════════════════════════════════"
    echo "  ✅ Build successful!"
    echo "════════════════════════════════════════════"
    echo ""
    echo "📦 Output files:"
    ls -lh target/*.jar
    echo ""
else
    echo ""
    echo "════════════════════════════════════════════"
    echo "  ❌ Build failed!"
    echo "════════════════════════════════════════════"
    exit 1
fi
