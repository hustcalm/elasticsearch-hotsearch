
# delete and create indices
curl -X DELETE http://localhost:9200/laureate ; curl -XPUT http://localhost:9200/laureate -d @mappings/laureates-mapping.json
curl -X DELETE http://localhost:9200/flat-laureate ; curl -XPUT http://localhost:9200/flat-laureate -d @mappings/flat-laureates-mapping.json
curl -X DELETE http://localhost:9200/new-prize ; curl -XPUT http://localhost:9200/new-prize -d @mappings/prizes-mapping.json
curl -X DELETE http://localhost:9200/old-prize ; curl -XPUT http://localhost:9200/old-prize -d @mappings/prizes-mapping.json

# post data
curl -XPOST "http://localhost:9200/_bulk?pretty" --data-binary @data-flat-laureates.json
curl -XPOST "http://localhost:9200/_bulk?pretty" --data-binary @data-laureates.json
curl -XPOST "http://localhost:9200/_bulk?pretty" --data-binary @data-prizes.json
