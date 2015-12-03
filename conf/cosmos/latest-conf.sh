#!/bin/sh
# $1 is <path to your certificate>:<password>
curl --cert $1 https://api.live.bbc.co.uk/cosmos/env/int/component/scalatraskeleton/configuration -o conf-scalatraskeleton-int.json
curl --cert $1 https://api.live.bbc.co.uk/cosmos/env/test/component/scalatraskeleton/configuration -o conf-scalatraskeleton-test.json
curl --cert $1 https://api.live.bbc.co.uk/cosmos/env/live/component/scalatraskeleton/configuration -o conf-scalatraskeleton-live.json
