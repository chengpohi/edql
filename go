#!/bin/bash


case "$1" in
  "build")
    sbt build
    ;;
  "assembly")
    sbt assembly
    ;;
  "pb")
    sbt publishLocal
    ;;
  *)
    sbt build
    ;;
esac
