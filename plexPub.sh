#! /bin/bash

# Cron job script for republishing local plex server.

RESPONSE=$(curl -X GET http://localhost:32400/myplex/account )

if [[ $RESPONSE != *"publicPort"* ]]
then
  curl -v -X PUT http://localhost:32400/:/prefs?PublishServerOnPlexOnlineKey=true
fi

