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
  "fmt")
    sbt eqlCore/scalafmt
    sbt eqlRepl/scalafmt
    ;;
  *)
    sbt compile
    ;;
esac
