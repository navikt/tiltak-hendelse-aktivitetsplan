create table aktivitetsplan_melding
(
    melding_id    uuid primary key,
    avtale_id     uuid references avtale (id),
    avtale_status varchar,
    tidspunkt     timestamp,
    hendelse_type varchar,
    mottatt_json  varchar,
    sending_json  varchar,
    sendt         boolean not null default false
);
