#!/bin/bash

# Exit the script if any command fails
set -e

# Variables
JAR_URL="https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-j-9.1.0.tar.gz"
JAR_DIR="lib/mysql-connector-j-9.1.0"
JAR_FILE="$JAR_DIR/mysql-connector-j-9.1.0.jar"
SRC_DIR="src"
BIN_DIR="bin"
MAIN_CLASS="runner"

# Function to determine the classpath separator based on the OS
get_classpath_separator() {
  if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" || "$OSTYPE" == "win32" ]]; then
    echo ";"
  else
    echo ":"
  fi
}

# Function to set up the project
setup() {
  echo "Starting the setup process..."

  # 1. Start Docker Containers
  echo "Starting Docker containers..."
  docker-compose up -d

  # Wait for MySQL container to initialize
  echo "Waiting for MySQL to initialize..."
  sleep 10  # Adjust as needed

  # 2. Download MySQL Connector JAR
  if [ ! -f "$JAR_FILE" ]; then
    echo "Downloading MySQL Connector J..."
    curl -L $JAR_URL -o mysql-connector.tar.gz
    mkdir -p lib
    tar -xzf mysql-connector.tar.gz -C lib
    echo "MySQL Connector J downloaded and extracted."
  else
    echo "MySQL Connector J already exists. Skipping download."
  fi

  # 3. Ensure Bin Directory Exists
  echo "Creating bin directory if it doesn't exist..."
  mkdir -p "$BIN_DIR"

  # 4. Compile Java Project
  CLASSPATH=".$(get_classpath_separator)$JAR_FILE"
  echo "Compiling Java project with CLASSPATH: $CLASSPATH"
  javac -cp "$CLASSPATH" -d "$BIN_DIR" "$SRC_DIR"/*.java

  echo "Setup complete. Use './setup.sh start' to run the application."
}

# Function to start the application
start() {
  echo "Starting the Java application..."
  if [ ! -f "$JAR_FILE" ]; then
    echo "Error: MySQL Connector JAR not found. Please run './setup.sh setup' first."
    exit 1
  fi

  CLASSPATH="bin$(get_classpath_separator)$JAR_FILE"
  echo "Starting Java application with CLASSPATH: $CLASSPATH"
  java -cp "$CLASSPATH" "$MAIN_CLASS"
}

# Main script logic
if [ "$1" == "setup" ]; then
  setup
elif [ "$1" == "start" ]; then
  start
else
  echo "Usage: $0 {setup|start}"
  exit 1
fi