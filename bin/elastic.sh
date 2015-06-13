#!/bin/sh

#curl -XPUT 'localhost:9200/chengpohi/bookMarks/_mapping?pretty' -d '{
#	"bookMarks": {
#		"properties": {
#			"created_date": {
#				"type": "date", "store": true}
#		}
#	}
#}'

curl -XPUT 'localhost:9200/chengpohi/' -d '{
	"mappings" : {
		"bookmark" : {
			"properties" : {
				"created_at" : {
					"type" : "date"
				},
					"name" : {
						"type" : "string"
					},
					"url" : {
						"type" : "string"
					}
			}
		}
	}
'}
