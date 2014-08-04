This is sample data from Nobel Prize API.
http://console.apihq.com/nobel-prize-api

It defines four format.
1. laureate
    person-central(has prizes array for multiple times receiver)
2. flat-laureate
    person-central(flatten prizes)
3. prize
    prize-central(generate two indices separated by 1950)

import-data.sh do below(example)

1. download original json files(commented out)
2. generate json files for bulk update(using jq)
3. delete and create indices
4. do bulk update
