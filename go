#!/bin/bash


case "$1" in
  "build")
    ;;
  "assembly")
    ;;
  "pb")
    ./gradlew --info :modules:core:publishToMavenLocal
    ./gradlew --info :modules:script:publishToMavenLocal
    ./gradlew --info :publishToMavenLocal
    ;;
  "fmt")
    ;;
  *)
    ;;
esac
