#!/bin/bash

rm -rf jacoco
./gradlew jacocoCombinedTestReport

rm -rf dokka
./gradlew dokkaHtml

