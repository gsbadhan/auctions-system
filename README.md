## auction system

### Build application

mvn clean install

### Run application

docker-compose up

### Stop application

docker stop <pid>


### swagger UI URL

http://localhost:8080/swagger-ui.html

### APIs and Websocket end-points

POST - /v1/auction/{itemCode}/bid

SUBSCRIBE - ws://localhost:8080/auction and Topic - /bids 


### aerospike configurations

create below namespace in /etc/aerospike/aerospike.conf and restart server.

namespace auctions {
        replication-factor 2
        memory-size 2G
        default-ttl 5d # 5 days, use 0 to never expire/evict.
        nsup-period 120
        # To use file storage backing, comment out the line above and use the
        # following lines instead.
        storage-engine device {
                file /opt/aerospike/data/auctions.dat
                filesize 5G
                data-in-memory true # Store data in memory in addition to file.
        }
}


### queries:

show namespaces

show sets

select * from auctions.auctionable_item

select * from auctions.auction_status

select * from auctions.auctionable_item_lock

