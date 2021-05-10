FROM nginx

WORKDIR /usr/share/nginx/html

COPY default.conf /etc/nginx/conf.d/default.conf
COPY entrypoint.sh /usr/local/bin/entrypoint
COPY dist .

RUN rm *.html && chmod a+x /usr/local/bin/entrypoint

ENTRYPOINT ["/usr/local/bin/entrypoint"]
