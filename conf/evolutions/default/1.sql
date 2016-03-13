# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table m_playlist (
  id                        bigserial not null,
  uuid                      varchar(255),
  title                     varchar(255),
  create_time               bigint,
  is_private                boolean,
  size                      integer,
  constraint uq_m_playlist_uuid unique (uuid),
  constraint pk_m_playlist primary key (id))
;

create table m_playlist_item (
  play_list_id_id           bigint,
  source_type               varchar(11),
  link                      varchar(255),
  constraint ck_m_playlist_item_source_type check (source_type in ('YOUTUBE','DAILYMOTION','VIMEO','SOUNDCLOUD')))
;

alter table m_playlist_item add constraint fk_m_playlist_item_playListID_1 foreign key (play_list_id_id) references m_playlist (id);
create index ix_m_playlist_item_playListID_1 on m_playlist_item (play_list_id_id);



# --- !Downs

drop table if exists m_playlist cascade;

drop table if exists m_playlist_item cascade;

