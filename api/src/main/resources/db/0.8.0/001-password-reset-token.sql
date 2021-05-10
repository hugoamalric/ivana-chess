CREATE TABLE password_reset_token
(
    id              uuid                     NOT NULL PRIMARY KEY,
    creation_date   timestamp WITH TIME ZONE NOT NULL,
    "user"          uuid                     NOT NULL UNIQUE REFERENCES "user" ON DELETE CASCADE,
    expiration_date timestamp WITH TIME ZONE NOT NULL
);

CREATE FUNCTION delete_expired_password_reset_tokens() RETURNS trigger
    LANGUAGE plpgsql
AS
$$
BEGIN
    DELETE
    FROM password_reset_token
    WHERE expiration_date <= NOW();
    RETURN new;
END;
$$;

CREATE TRIGGER delete_expired_password_reset_tokens_trigger
    AFTER INSERT
    ON password_reset_token
EXECUTE PROCEDURE delete_expired_password_reset_tokens();
