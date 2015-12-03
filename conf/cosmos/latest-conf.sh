#!/bin/sh
# $1 is <path to your certificate>:<password>
curl --cert $1 https://api.live.bbc.co.uk/cosmos/env/int/component/vivo-collaboration/configuration -o conf-vivo-collaboration-int.json
curl --cert $1 https://api.live.bbc.co.uk/cosmos/env/test/component/vivo-collaboration/configuration -o conf-vivo-collaboration-test.json
curl --cert $1 https://api.live.bbc.co.uk/cosmos/env/live/component/vivo-collaboration/configuration -o conf-vivo-collaboration-live.json


