create table aktivitetsplan_melding
(
    id            uuid primary key,
    avtale_id     uuid,
    avtale_status varchar,
    opprettet_tidspunkt     timestamp,
    hendelse_type varchar,
    mottatt_json  varchar,
    sending_json  varchar,
    sendt         boolean not null default false
);
