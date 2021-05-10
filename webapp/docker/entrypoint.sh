#!/bin/bash

for DIR in $(ls .); do
  envsubst < "${DIR}/assets/env.template.js" > "${DIR}/assets/env.js"
done
exec nginx -g 'daemon off;'
