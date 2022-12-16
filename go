#!/bin/bash


case "$1" in
  "build")
    ;;
  "assembly")
    ;;
  "pb")
    ./gradlew :modules:script:publishToMavenLocal
    ;;
  "fmt")
    ;;
  *)
    ;;
esac
