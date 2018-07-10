[![Build Status](https://travis-ci.org/chengpohi/elasticdsl.svg)](https://travis-ci.org/chengpohi/elasticdsl)
[![JProfiler Support](
https://www.ej-technologies.com/images/product_banners/jprofiler_small.png)](https://www.ej-technologies.com/products/jprofiler/overview.html)

# Elastic DSL with REPL

## Introduction
It is a **DSL** for elasticsearch. Have fun :)


## Use it

### conf

In the ***conf/elasticdsl.conf***, you can configure your ***elasticsearch host, port and clustername***.

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
| index             | `index into "index-name" / "index-type" fields {"name1": value1, "name2": value2} ` | index doc |
| bulkIndex             | `bulk index "index-name" "index-type" [{"name1": value1}, {"name2": value2}, {"name3": value3}] ` | bulk index doc |
| reindex | `reindex into "source-index-name" / "target-index-name" from "source-index-type" fields ["field1", "field2"]` | reindex index to another indexes with fields |
| count             | `count "index-name" ` | count the size of index |
| query | `search in "index-name"` | query all by index-name |
| query | `search in "index-name" / "index-type" ` | query all data by index-name  and index-type|
| aggs avg | `"aggs in "index-name" / "index-type" avg "field"` | query all data by index-name  and index-type|
| hist aggs | `"aggs in "index-name" / "index-type" hist "field" interval "day" field "created_at"`| hist aggs |
| term query | `term query "index-name" "index-type" {"name": value}` | term query by index-name  and index-type|
| get | `get from "index-name" / "idnex-type" id "doc_id"` | get doc by id |
| analysis | `analysis "foo,bar" by "standard"` | analysis text by the specific analyzer |
| create analyzer | `create analyzer "index-name" {"analyzer":{"myAnalyzer":{"type":"pattern","pattern":"\\s+"}}}` | create analyzer |
| delete | `delete "index-name"` | delete all by index-name |
| update | `update on "index-name" / "index-type" fields (field_name, field_value)` | update or append field by field_name and field_value |
| mapping | `"test-parser-name" mapping` | get the index's mapping |
| mapping | `mapping "index-name" "index-type" "[(field_name,field_type,analyzer),(field_name2,field_type)]"` | set the mapping of based on fields type analyzer |
| alias | `alias "index" "index-1"` | alias index to index-1|
| extract | `search in "index-name" \\ "name"`| extract field from json |
| create repository | `create repository "repository_name" "fs" {"compress": "true", "location": "/my/path} `| create repository for snapshot|
| create snapshot | `create snapshot "snapshot1" "repository_name"`| create snapshot in repository|
| get snapshot | `get snapshot "snapshot1" "repository_name"`| retrieve snapshot in repository|
| close index | `close index "index-name"`| close index by index-name|
| open index | `open index "index-name"`| open index by index-name|
| restore snapshot | `restore snapshot "snapshot1" "repository_name"`| restore snapshot from repository by snapshot name|
| delete snapshot | `delete snapshot "snapshot1" "test_snapshot"`| delete snapshot in repository by snapshot name |

