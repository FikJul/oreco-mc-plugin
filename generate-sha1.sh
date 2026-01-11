#!/bin/bash
# Generate SHA1 hash for resource pack
# Usage: ./generate-sha1.sh <resource-pack.zip>

if [ $# -eq 0 ]; then
    echo "Usage: ./generate-sha1.sh <resource-pack.zip>"
    exit 1
fi

FILE=$1

if [ ! -f "$FILE" ]; then
    echo "Error: File '$FILE' not found!"
    exit 1
fi

echo "Generating SHA1 hash for: $FILE"
echo ""

# Generate SHA1 hash
HASH=$(sha1sum "$FILE" | awk '{print $1}')

echo "SHA1 Hash: $HASH"
echo ""
echo "Add this to your server.properties:"
echo "resource-pack-sha1=$HASH"
echo ""
echo "Example server.properties configuration:"
echo "resource-pack=https://github.com/YourName/YourRepo/releases/download/v1.0/resource-pack.zip"
echo "resource-pack-sha1=$HASH"
echo "require-resource-pack=true"
