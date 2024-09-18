# EDQL <a href="https://chengpohi.github.io/support-edql/" alt="Donate shield"><img src="https://img.shields.io/badge/-Donate-red?logo=undertale" /></a>

EDQL is a professional query and management tool for Elasticsearch. It's intelligent and powerful for 
manage Elasticsearch cluster and query from Elasticsearch. also It always follow Elasticsearch the newest features.

It is full compatible with official Query DSL, we can just copy DSL and run on EDQL without any 
extra effort. also EDQL has visual editor for quickly write query conditions with interactive UI.

EDQL also integrate ChatGPT AI, you can use your own AI to help you easily writing query DSL and can directly
ask AI to help query Elasticsearch data without remembering complex query DSL.

It has powerful script engine: function, variable and iteration etc. with smart Intellij you 
can easily write query DSL(live templates, refactor, extract...).

```
# f1 = k1
# f2 in ["k1", "k2", "k3"]
# f3 date field gt now-3d
# f4 number lt 20

POST my-index/_search
{
  "query": {
    "bool": {
      "filter": [
        term("f1", "k1"),
        terms("f2", ["k1", "k2", "k3"]),
        gt("f3", "now-3d"),
        lt("f4", 20)
      ]
    }
  }
}
```

## Use with EDQL Intellij GUI Client
Please view more on:  [EDQL Wiki](https://chengpohi.github.io/) or [Install EDQL](https://plugins.jetbrains.com/plugin/16364-elasticsearch-query--edql/)

### Create a Connection and Query
![Create a Connection and Query](https://chengpohi.github.io/.gitbook/assets/new-connection.gif)

### Chat Query Elasticsearch
![Chat Query](https://chengpohi.github.io/.gitbook/assets/chatquery.gif)
