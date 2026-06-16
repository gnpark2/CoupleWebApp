#!/bin/bash
KAFKA=kafka:9092
create() {
  kafka-topics --create --bootstrap-server $KAFKA --topic $1 --partitions ${2:-3} --replication-factor 1 --if-not-exists
  echo "  created: $1"
}
echo "Creating Kafka topics..."
create "presence.update" 3
create "feeling.shared" 3
create "diary.created" 3
create "setlog.created" 3
create "character.interaction" 3
create "character.xp.gained" 3
create "calendar.event.created" 3
create "anniversary.trigger" 1
create "couple.formed" 1
create "notification.push" 1
create "notification.email" 1
echo "Done."
