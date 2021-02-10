FROM nginx

WORKDIR /usr/share/nginx/html
COPY dist .
CMD ["/bin/sh",  "-c",  "envsubst < assets/env.template.js > assets/env.js && exec nginx -g 'daemon off;'"]
