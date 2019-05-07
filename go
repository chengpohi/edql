#!/bin/bash


case "$1" in
  "build")
    sbt compile
    ;;
  "assembly")
    sbt assembly
    ;;
  "pb")
    sbt publishLocal
    ;;
  *)
    sbt compile
    ;;
esac
