#!/bin/sh

# Replace it with what we prefer
cat /etc/bake-scripts/td-agent.conf >> /etc/td-agent/td-agent.conf
echo "Written custom td-agent configuration"
