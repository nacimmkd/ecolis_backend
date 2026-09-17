#!/usr/bin/env bash

# deploying the new version
# use : deploy.sh <git-sha>

set -e
cd "$(dirname "$0")"

TAG=$1
PREVIOUS=$(grep '^TAG=' .env | cut -d= -f2)

sed -i "s/^TAG=.*/TAG=$TAG/" .env
docker compose pull app

# rollback if something went wrong
# if ! docker compose up -d --wait app; then
#   echo "FAILED, rolling back to $PREVIOUS"
#   sed -i "s/^TAG=.*/TAG=$PREVIOUS/" .env
#   docker compose up -d --wait app
#   exit 1
# fi

echo "RUNNING VERSION : $TAG"