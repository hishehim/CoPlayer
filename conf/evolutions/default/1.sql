# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table playlist (
  id                            bigserial not null,
  owner_id                      bigint,
  title                         varchar(255),
  uid                           varchar(255),
  is_private                    boolean,
  size                          integer,
  create_time                   timestamp not null,
  constraint uq_playlist_owner_id_title unique (owner_id,title),
  constraint pk_playlist primary key (id)
);

create table playlist_item (
  id                            bigserial not null,
  parent_list_id                bigint,
  source_type_id                bigint,
  link                          varchar(255),
  constraint uq_playlist_item_parent_list_id_id unique (parent_list_id,id),
  constraint pk_playlist_item primary key (id)
);

create table source_type (
  id                            bigserial not null,
  source_type                   varchar(255),
  constraint uq_source_type_source_type unique (source_type),
  constraint pk_source_type primary key (id)
);

create table users (
  id                            bigserial not null,
  email                         varchar(255),
  username                      varchar(255),
  password_hash                 varchar(255),
  constraint uq_users_email unique (email),
  constraint uq_users_username unique (username),
  constraint uq_users_password_hash unique (password_hash),
  constraint pk_users primary key (id)
);

alter table playlist add constraint fk_playlist_owner_id foreign key (owner_id) references users (id) on delete restrict on update restrict;
create index ix_playlist_owner_id on playlist (owner_id);

alter table playlist_item add constraint fk_playlist_item_parent_list_id foreign key (parent_list_id) references playlist (id) on delete restrict on update restrict;
create index ix_playlist_item_parent_list_id on playlist_item (parent_list_id);

alter table playlist_item add constraint fk_playlist_item_source_type_id foreign key (source_type_id) references source_type (id) on delete restrict on update restrict;
create index ix_playlist_item_source_type_id on playlist_item (source_type_id);


# --- !Downs

alter table if exists playlist drop constraint if exists fk_playlist_owner_id;
drop index if exists ix_playlist_owner_id;

alter table if exists playlist_item drop constraint if exists fk_playlist_item_parent_list_id;
drop index if exists ix_playlist_item_parent_list_id;

alter table if exists playlist_item drop constraint if exists fk_playlist_item_source_type_id;
drop index if exists ix_playlist_item_source_type_id;

drop table if exists playlist cascade;

drop table if exists playlist_item cascade;

drop table if exists source_type cascade;

drop table if exists users cascade;

