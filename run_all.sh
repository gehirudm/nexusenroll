#!/bin/bash
set -e
rm -rf out
mkdir -p out
find . -name "*.java" > sources.txt
javac -d out @sources.txt
echo "Running StudentServiceMain..."
java -cp out student.StudentServiceMain
echo
echo "Running FacultyServiceMain..."
java -cp out faculty.FacultyServiceMain
echo
echo "Running AdminServiceMain..."
java -cp out admin.AdminServiceMain
