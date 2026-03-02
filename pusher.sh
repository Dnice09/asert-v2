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

git checkout $1 &&\
git fetch origin $1 &&\
git merge origin/$1 -m $2 &&\
git add . && git commit -m $2 &&\
# git push
print_yellow "Code commited successfully"

print_green "Push completed successfully"
