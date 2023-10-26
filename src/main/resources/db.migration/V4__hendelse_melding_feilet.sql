create table hendelse_melding_feilet
(
    id            uuid primary key,
    avtale_id     varchar,
    mottatt_tidspunkt     timestamp,
    mottatt_json  varchar,
    topic_offset  bigint,
    feilmelding   varchar
);
