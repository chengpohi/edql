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

ELASTIC_SHELL_HOME=`dirname "$SCRIPT"`/..

ELASTIC_SHELL_HOME=`cd "$ELASTIC_SHELL_HOME"; pwd`

java -cp $ELASTIC_SHELL_HOME/conf/:$ELASTIC_SHELL_HOME/lib/elasticshell-assembly-1.0.jar com.github.chengpohi.repl.ELKRepl
