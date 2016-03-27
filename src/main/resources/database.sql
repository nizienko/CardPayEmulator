CREATE DATABASE card_pay
  WITH OWNER = postgres
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'Russian_Russia.1251'
       LC_CTYPE = 'Russian_Russia.1251'
       CONNECTION LIMIT = -1;


CREATE TABLE "order"
(
  id integer NOT NULL DEFAULT nextval('order_id_seq'::regclass),
  changed_date timestamp with time zone,
  created_date timestamp with time zone NOT NULL,
  status integer,
  status_message text,
  order_n text NOT NULL,
  CONSTRAINT order_pk PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "order"
  OWNER TO postgres;


CREATE TABLE operation
  (
    id integer NOT NULL DEFAULT nextval('operation_id_seq'::regclass),
    order_id integer NOT NULL DEFAULT nextval('operation_order_id_seq'::regclass),
    operation_type integer,
    status integer,
    bank_acquire_id integer,
    request_params text,
    response_params text,
    changed_date timestamp with time zone,
    created_date timestamp with time zone,
    CONSTRAINT operation_pk PRIMARY KEY (id)
  )
  WITH (
    OIDS=FALSE
  );
  ALTER TABLE operation
    OWNER TO postgres;