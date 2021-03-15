FROM nginx

WORKDIR /usr/share/nginx/html
COPY default.conf /etc/nginx/conf.d/default.conf
COPY dist .
CMD ["/bin/sh",  "-c",  "envsubst < assets/env.template.js > assets/env.js && exec nginx -g 'daemon off;'"]
