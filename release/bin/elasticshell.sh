#!/bin/sh
CDPATH=""
SCRIPT="$0"

while [ -h "$SCRIPT" ] ; do
  ls=`ls -ld "$SCRIPT"`
  # Drop everything prior to ->
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    SCRIPT="$link"
  else
    SCRIPT=`dirname "$SCRIPT"`/"$link"
  fi
done

SINY_HOME=`dirname "$SCRIPT"`/..

SINY_HOME=`cd "$SINY_HOME"; pwd`


java -cp conf/:lib/elasticshell-assembly-1.0.jar com.github.chengpohi.ELKRepl
