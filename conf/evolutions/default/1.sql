# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table playlist (
  id                        bigint not null,
  owner_id                  bigint,
  title                     varchar(255),
  uuid                      varchar(255),
  is_private                boolean,
  size                      integer,
  create_time               timestamp not null,
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
  id                        bigint not null,
  email                     varchar(255),
  username                  varchar(255),
  password_hash             varchar(255),
  constraint uq_users_email unique (email),
  constraint uq_users_username unique (username),
  constraint uq_users_password_hash unique (password_hash),
  constraint pk_users primary key (id))
;

create sequence playlist_seq;

create sequence source_type_seq;

create sequence users_seq;

alter table playlist add constraint fk_playlist_owner_1 foreign key (owner_id) references users (id) on delete restrict on update restrict;
create index ix_playlist_owner_1 on playlist (owner_id);
alter table playlist_item add constraint fk_playlist_item_parentList_2 foreign key (parent_list_id) references playlist (id) on delete restrict on update restrict;
create index ix_playlist_item_parentList_2 on playlist_item (parent_list_id);
alter table playlist_item add constraint fk_playlist_item_sourceType_3 foreign key (source_type_source_type) references source_type (source_type) on delete restrict on update restrict;
create index ix_playlist_item_sourceType_3 on playlist_item (source_type_source_type);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists playlist;

drop table if exists playlist_item;

drop table if exists source_type;

drop table if exists users;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists playlist_seq;

drop sequence if exists source_type_seq;

drop sequence if exists users_seq;

