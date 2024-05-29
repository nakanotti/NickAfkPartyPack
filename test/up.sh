#!/bin/sh
cp -u ../target/NickAfkPartyPack-*.jar ./plugins/
trap 'docker compose down' 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15
docker compose up
docker compose down
