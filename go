#!/bin/bash


case "$1" in
  "build")
    sbt compile
    ;;
  "assembly")
    sbt binary
    ;;
  "pb")
    sbt clean pbCore
    ;;
  *)
    sbt compile
    ;;
esac
