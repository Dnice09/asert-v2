#!/bin/bash

# Function to print in green
print_green() {
  tput setaf 2
  echo "***** $1 *****"
  tput sgr0
}

# Function to print in red
print_red() {
  tput setaf 1
  echo "***** $1 *****"
  tput sgr0
}

# Function to print in red
print_yellow() {
  tput setaf 3
  echo "***** $1 *****"
  tput sgr0
}

# Checkout develop branch
print_yellow "Checking out develop branch..."
git checkout develop --quiet
if [ $? -eq 0 ]; then
  print_green "Checkout successful."
else
  print_red "Checkout failed. Please check the errors above."
  exit 1
fi

# Fetch changes from develop branch
print_yellow "Fetching from develop branch..."
git fetch
if [ $? -eq 0 ]; then
  print_green "Fetching successful."
else
  print_red "Fetching failed. Please check the errors above."
  exit 1
fi

# Merge with origin changes
print_yellow "Merging with origin develop branch..."
git merge origin/develop -m "CI/CD Auto-merge"
if [ $? -eq 0 ]; then
  print_green "Merging successful."
else
  print_red "Merging failed. Please check the errors above."
  exit 1
fi

# Fetch changes from develop branch
print_yellow "Shutting down docker image..."
docker compose down
if [ $? -eq 0 ]; then
  print_green "Docker shutdown successful."
else
  print_red "Docker image shutdown failed. Please check the errors above."
  exit 1
fi

# Fetch changes from develop branch
print_yellow "Rebuilding and starting docker image..."
docker compose up -d --build
if [ $? -eq 0 ]; then
  print_green "Docker rebuild and restart successful."
else
  print_red "Docker image rebuilding failed. Please check the errors above."
  exit 1
fi
