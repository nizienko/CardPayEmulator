CREATE SEQUENCE payment_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE payment_id_seq
  OWNER TO card_pay;


CREATE TABLE payment
(
  id integer NOT NULL DEFAULT nextval('payment_id_seq'::regclass),
  changed_date timestamp with time zone,
  created_date timestamp with time zone NOT NULL,
  status integer,
  status_message text,
  payment_n text NOT NULL,
  CONSTRAINT payment_pk PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE payment
  OWNER TO card_pay;

CREATE SEQUENCE operation_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE operation_id_seq
  OWNER TO card_pay;

CREATE TABLE operation
  (
    id integer NOT NULL DEFAULT nextval('operation_id_seq'::regclass),
    payment_id integer NOT NULL,
    sum numeric(10,2),
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
    OWNER TO card_pay;