#!/bin/bash

# Check Maven
if ! command -v mvn &> /dev/null; then
    echo "Maven is not installed."
    exit 1
fi

# Check Java
if ! command -v java &> /dev/null; then
    echo "Java is not installed."
    exit 1
fi

# Check Python
if ! command -v python3 &> /dev/null; then
    echo "Python 3 is not installed."
    exit 1
fi

# Build project
mvn clean package

# Run project
java -jar target/concurrent-matrix-multiplication-1.0-jar-with-dependencies.jar

# Analyse results
python3 analyze.py
