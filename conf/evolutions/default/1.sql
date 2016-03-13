# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table playlist (
  id                        bigserial not null,
  uuid                      varchar(255),
  title                     varchar(255),
  create_time               bigint,
  is_private                boolean,
  size                      integer,
  constraint uq_playlist_uuid unique (uuid),
  constraint pk_playlist primary key (id))
;

<<<<<<< HEAD
create table playlist_item (
  playlist_id               bigint,
  id                        bigint,
  source_type               varchar(11),
  link                      varchar(255),
  constraint ck_playlist_item_source_type check (source_type in ('NONE','YOUTUBE','DAILYMOTION','VIMEO','SOUNDCLOUD')),
  constraint uq_playlist_item_1 unique (playlist_id,id))
=======
create table m_playlist_item (
  play_list_id_id           bigint,
  source_type               varchar(11),
  link                      varchar(255),
  constraint ck_m_playlist_item_source_type check (source_type in ('YOUTUBE','DAILYMOTION','VIMEO','SOUNDCLOUD')))
>>>>>>> user_model
;

alter table playlist_item add constraint fk_playlist_item_playlist_1 foreign key (playlist_id) references playlist (id);
create index ix_playlist_item_playlist_1 on playlist_item (playlist_id);



# --- !Downs

drop table if exists playlist cascade;

drop table if exists playlist_item cascade;

