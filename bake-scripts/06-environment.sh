#!/bin/bash

# This script has the filename of the JSON configuration (which is only readable at bake-time) passed to it,
# as detailed on this Confluence page:
# https://confluence.dev.bbc.co.uk/display/platform/Environment+-+specific+service+configuration

cat $1 > /etc/vivo-collaboration/vivo-collaboration.json

