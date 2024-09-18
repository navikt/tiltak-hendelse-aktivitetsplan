create table aktivitetsplan_id
(
    avtale_id         uuid primary key,
    aktivitetsplan_id uuid unique not null
);


insert into aktivitetsplan_id (avtale_id, aktivitetsplan_id)
select distinct avtale_id, avtale_id from aktivitetsplan_melding;
