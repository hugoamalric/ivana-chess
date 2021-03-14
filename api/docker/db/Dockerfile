FROM postgres:11

COPY ssl /etc/ssl/postgresql
RUN chown root:postgres /etc/ssl/postgresql/localhost.key && chmod 0640 /etc/ssl/postgresql/localhost.key

CMD ["-c", "ssl=on", "-c", "ssl_cert_file=/etc/ssl/postgresql/localhost.pem", "-c", "ssl_key_file=/etc/ssl/postgresql/localhost.key"]
