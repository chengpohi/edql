#!/bin/bash


case "$1" in
  "build")
    ;;
  "assembly")
    ;;
  "pb")
    rm -rf /.m2/repository/com/github/chengpohi/
    ./gradlew --info :modules:core:publishToMavenLocal
    ./gradlew --info :modules:script:publishToMavenLocal
    ./gradlew --info :publishToMavenLocal
    ;;
  "fmt")
    ;;
  *)
    ;;
esac
