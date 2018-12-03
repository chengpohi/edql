#!/bin/bash


case "$1" in
  "build")
    sbt pb
    ;;
  *)
    sbt build
    ;;
esac
