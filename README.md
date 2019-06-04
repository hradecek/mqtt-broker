# MQTT Broker

MQTT broker serves as a front controller and entry point for every connected MQTT client.

All MQTT data goes through MQTT broker, which processes it in some form and send to Apache Kafka.

## Kafka topic
Every client has it's own topic by its ID.

## MQTT topics
### `$SYS`
Implements (URL TODO) 

`$SYS/broker/clients/connected`
 - number of currently connected MQTT clients

## Redis
Redis used used for storing broker's meta data.

### Mapping to `$SYS` topics

