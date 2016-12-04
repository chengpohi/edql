[![Build Status](https://travis-ci.org/chengpohi/elasticshell.svg)](https://travis-ci.org/chengpohi/elasticshell)

# Elastic DSL with REPL

## Introduction
It is a **DSL** for elasticsearch. Have fun :)

## Install

Go to the [release folder](https://github.com/chengpohi/elasticshell/tree/master/release), choose a version and download.

| Version | Elasticsearch Version |
|-------------------------------------------|----------------|
| 0.2.1             | 5.0.0 |


## Use it

### conf

In the ***conf/elastic.conf***, you can configure your ***elasticsearch host, port and clustername***.

### Start

```
bin/es
```

### DSL

```
val result: Future[SearchResponse] = DSL {
  search in "*"
}
val result: SearchResponse = DSL {
  search in "*"
}.await
val result: String = DSL {
  search in "*"
}.toJson
```

### Syntax


| Operation                                 | Syntax | Description |
|-------------------------------------------|----------------|----------|
| health             | `health` | Get Cluster Health |
| index             | `index "index-name" "index-type" {"name1": value1, "name2": value2} ` | index doc |
| bulkIndex             | `bulk index "index-name" "index-type" [{"name1": value1}, {"name2": value2}, {"name3": value3}] ` | bulk index doc |
| reindex | `reindex "source-index-name" "target-index-name" "source-index-type" "field_name"` | reindex index to another indexes with fields |
| count             | `count "index-name" ` | count the size of index |
| query | `query "index-name"` | query all by index-name |
| query | `query "index-name" "index-type" ` | query all data by index-name  and index-type|
| aggsCount | `"aggs in "index-name" "index-type" {"agg_name":{"terms": {"field": "field_name"}}}` | query all data by index-name  and index-type|
| term query | `term query "index-name" "index-type" {"name": value}` | term query by index-name  and index-type|
| get | `get "index-name" "idnex-type" "doc_id"` | get doc by id |
| analysis | `analysis "standard" "foo,bar"` | analysis text by the specific analyzer |
| create analyzer | `create analyzer "index-name" {"analyzer":{"myAnalyzer":{"type":"pattern","pattern":"\\s+"}}}` | create analyzer |
| delete | `delete "index-name"` | delete all by index-name |
| update | `update "index-name" "index-type" "(field_name, field_value)"` | update or append field by field_name and field_value |
| mapping | `"test-parser-name" mapping` | get the index's mapping |
| mapping | `mapping "index-name" "index-type" "[(field_name,field_type,analyzer),(field_name2,field_type)]"` | set the mapping of based on fields type analyzer |
| alias | `alias "index" "index-1"` | alias index to index-1|
| extract | `query "index-name" \\ "name"`| extract field from json |
| create repository | `create repository "repository_name" "fs" {"compress": "true", "location": "/my/path} `| create repository for snapshot|
| create snapshot | `create snapshot "snapshot1" "repository_name"`| create snapshot in repository|
| get snapshot | `get snapshot "snapshot1" "repository_name"`| retrieve snapshot in repository|
| close index | `close index "index-name"`| close index by index-name|
| open index | `open index "index-name"`| open index by index-name|
| restore snapshot | `restore snapshot "snapshot1" "repository_name"`| restore snapshot from repository by snapshot name|
| delete snapshot | `delete snapshot "snapshot1" "test_snapshot"`| delete snapshot in repository by snapshot name |

