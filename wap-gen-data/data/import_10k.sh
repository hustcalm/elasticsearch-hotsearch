curl -XDELETE 'http://localhost:9200/query_data_10k/'
curl -XPUT 'http://localhost:9200/query_data_10k/'
curl -XPUT 'http://localhost:9200/query_data_10k/query/_mapping' -d @mapping.json
curl -s -XPOST 'http://localhost:9200/_bulk' --data-binary @data.json
