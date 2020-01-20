## Simple rest api for Kafka

May be useful to inspect local instance topics via REST

### Run

```
docker-compose up
```

Cleanup:

```
docker-compose down --volumes
```

### Available API

List topics:

```
curl -X GET localhost:9093/topics | jq
```

Create topic:

```
curl -X POST localhost:9093/topics/test?partitions=1&replicas=1
```

Delete topic:

```
curl -X DELETE localhost:9093/topics/test
```

Post message to topic:

```
curl -X POST localhost:9093/topics/test/messages -d '
[
  {
    "key": 1,
    "value": {
      "foo": "bar"
    }
  }
]
'
```

Get all messages on topic:

```
curl -X GET localhost:9093/topics/test/messages | jq
```