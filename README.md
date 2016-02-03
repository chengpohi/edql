# ElasticShell

## Preface
This a ***REPL*** for elasticsearch, it uses ***elk*** syntax to interact with ***elasticsearch***, it's more ***BDD***, more easy to use it.
Also you can use ***elk*** file to batch execute ***elasticsearch*** commands like shell file.

## Version
For now support ***elasticsearch-1.7.1***

## Installation

## conf

In the ***conf/application.conf***, you can configure your ***elasticsearch host, port and clustername***.

## Start

```
bin/elasticshell.sh
```

## Syntax


| Operation                                 | Syntax | Description |
|-------------------------------------------|----------------|----------|
| health             | `health` | Get Cluster Health |
| index             | `index "index-name" "index-type" "(field_name, fieldvalue)" ` | index doc |
| count             | `count "index-name" ` | the size of index |
| reindex | `reindex "source-index-name" "target-index-name" "source-index-type" "field_name"` | reindex index to another indexes with fields |
| query | `query "index-name"` | query all by index-name |
| delete | `delete "index-name"` | delete all by index-name |
| update | `update "index-name" "index-type" "(field_name, field_value)"` | update or append field by field_name and field_value |
| analysis | `analysis "standard" "foo,bar"` | analysis text by the specific analyzer |
| mapping | `"test-parser-name" mapping` | get the index's mapping |
| mapping | `mapping "index-name" "index-type" "[(field_name,field_type,analyzer),(field_name2,field_type)]"` | set the mapping of based on fields type analyzer |
| get | `get "index-name" "idnex-type" "DOC-ID-"` | get doc by specific id |
| extract | `query "index-name" \\ "name"`| extract field from json |
