#!/bin/sh
cp ../target/NickAfkPartyPack-*.jar ./plugins/
docker compose up
docker wait test
docker compose down
