#!/bin/bash


case "$1" in
  "build")
    sbt build
    ;;
  "assembly")
    sbt assembly
    ;;
  *)
    sbt build
    ;;
esac
