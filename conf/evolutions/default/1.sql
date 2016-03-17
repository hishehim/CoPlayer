# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table playlist (
  id                        bigserial not null,
  owner_id                  bigint,
  title                     varchar(255),
  uuid                      varchar(255),
  create_time               bigint,
  is_private                boolean,
  size                      integer,
  constraint uq_playlist_uuid unique (uuid),
  constraint uq_playlist_1 unique (owner_id,title),
  constraint pk_playlist primary key (id))
;

create table playlist_item (
  parent_list_id            bigint,
  id                        bigint,
  source_type_source_type   varchar(255),
  link                      varchar(255),
  constraint uq_playlist_item_1 unique (parent_list_id,id))
;

create table source_type (
  source_type               varchar(255) not null,
  constraint pk_source_type primary key (source_type))
;

create table users (
  id                        bigserial not null,
  email                     varchar(255),
  username                  varchar(255),
  password_hash             varchar(255),
  constraint uq_users_email unique (email),
  constraint uq_users_username unique (username),
  constraint uq_users_password_hash unique (password_hash),
  constraint pk_users primary key (id))
;

alter table playlist add constraint fk_playlist_owner_1 foreign key (owner_id) references users (id);
create index ix_playlist_owner_1 on playlist (owner_id);
alter table playlist_item add constraint fk_playlist_item_parentList_2 foreign key (parent_list_id) references playlist (id);
create index ix_playlist_item_parentList_2 on playlist_item (parent_list_id);
alter table playlist_item add constraint fk_playlist_item_sourceType_3 foreign key (source_type_source_type) references source_type (source_type);
create index ix_playlist_item_sourceType_3 on playlist_item (source_type_source_type);



# --- !Downs

drop table if exists playlist cascade;

drop table if exists playlist_item cascade;

drop table if exists source_type cascade;

drop table if exists users cascade;

