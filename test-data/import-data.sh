#mkdir original-data
## download data files
#wget http://api.nobelprize.org/v1/laureate.json -O original-data/laureate.json
#wget http://api.nobelprize.org/v1/prize.json -O original-data/prize.json

# generate bulk source files
## laureates(complex version)
jq -c '.laureates[] |
if .born == "0000-00-00" then del(.born) else . end |
if .died == "0000-00-00" then del(.died) else . end |
.id |= tonumber |
.prizes |= map(
               .year |= tonumber |
               .share |= tonumber)' < original-data/laureate.json | \
jq -c '{ index: { _index: "laureate", _type: (if .gender == "org" then "org-laureates" else "laureates" end)}}, .' > data-laureates.json

## laureates(flatten version)
cat < data-laureates.json | \
jq 'select(has("index") | not)' | \
jq '. + .prizes[] | del(.prizes)' | \
jq 'select(.year < 2014)' | \
jq -c '{ index: { _id: ((.id | tostring) + "_" + .category + "_" + (.year | tostring)), _index: "flat-laureate", _type: (if .gender == "org" then "org-laureates" else "laureates" end)}}, .' > data-flat-laureates.json

## prizes
cat original-data/prize.json | \
jq -c '.prizes[] |
.composite_id = (.category + "_" + .year) |
.year |= tonumber |
.laureates |= map(
                  .id |= tonumber |
                  .share |= tonumber)' | \
jq -c 'select(.year < 2014)' | \
jq -c '{ index: { _index: (if .year >= 1950 then "new-prize" else "old-prize" end), _type: (if .category == "economics" then "economics-prizes" else "prizes" end)}}, .' > data-prizes.json

# delete and create indices
curl -X DELETE http://localhost:9200/laureate ; curl -XPUT http://localhost:9200/laureate -d @mappings/laureates-mapping.json
curl -X DELETE http://localhost:9200/flat-laureate ; curl -XPUT http://localhost:9200/flat-laureate -d @mappings/flat-laureates-mapping.json
curl -X DELETE http://localhost:9200/new-prize ; curl -XPUT http://localhost:9200/new-prize -d @mappings/prizes-mapping.json
curl -X DELETE http://localhost:9200/old-prize ; curl -XPUT http://localhost:9200/old-prize -d @mappings/prizes-mapping.json

# post data
curl -XPOST "http://localhost:9200/_bulk?pretty" --data-binary @data-flat-laureates.json
curl -XPOST "http://localhost:9200/_bulk?pretty" --data-binary @data-laureates.json
curl -XPOST "http://localhost:9200/_bulk?pretty" --data-binary @data-prizes.json
