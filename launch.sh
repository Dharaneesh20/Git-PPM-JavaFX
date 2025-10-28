#!/bin/bash

# Git PPM Launch Script
# Made by Dharaneesh R S

echo "================================"
echo "   Git PPM - Launch Script"
echo "   Made by Dharaneesh R S"
echo "================================"
echo ""

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Error: Maven is not installed!"
    echo "Please install Maven and try again."
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Error: Java is not installed!"
    echo "Please install Java 17 or higher and try again."
    exit 1
fi

echo "✓ Maven found: $(mvn --version | head -n 1)"
echo "✓ Java found: $(java -version 2>&1 | head -n 1)"
echo ""
echo "🚀 Starting Git PPM..."
echo ""

# Run the application
mvn javafx:run
