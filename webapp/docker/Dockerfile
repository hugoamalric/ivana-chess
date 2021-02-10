FROM nginx

WORKDIR /usr/share/nginx/html
COPY build .
CMD ["/bin/sh",  "-c",  "envsubst < assets/env.template.js > assets/env.js && exec nginx -g 'daemon off;'"]
