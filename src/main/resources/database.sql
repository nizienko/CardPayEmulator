CREATE SEQUENCE order_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE public.order_id_seq
  OWNER TO postgres;


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

CREATE SEQUENCE public.operation_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE public.operation_id_seq
  OWNER TO postgres;

CREATE TABLE operation
  (
    id integer NOT NULL DEFAULT nextval('operation_id_seq'::regclass),
    order_id integer NOT NULL,
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
    OWNER TO postgres;