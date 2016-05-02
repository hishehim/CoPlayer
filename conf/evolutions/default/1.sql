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
  parent_id                     bigint,
  source_type                   varchar(255),
  link                          varchar(255),
  title                         varchar(255),
  constraint uq_playlist_item_parent_id_id unique (parent_id,id),
  constraint pk_playlist_item primary key (id)
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

alter table playlist_item add constraint fk_playlist_item_parent_id foreign key (parent_id) references playlist (id) on delete restrict on update restrict;
create index ix_playlist_item_parent_id on playlist_item (parent_id);


# --- !Downs

alter table if exists playlist drop constraint if exists fk_playlist_owner_id;
drop index if exists ix_playlist_owner_id;

alter table if exists playlist_item drop constraint if exists fk_playlist_item_parent_id;
drop index if exists ix_playlist_item_parent_id;

drop table if exists playlist cascade;

drop table if exists playlist_item cascade;

drop table if exists users cascade;

