drop database if exists os;
create database os;
use os;
drop table if exists orders;

drop table if exists products;

/*==============================================================*/
/* Table: orders                                                */
/*==============================================================*/
create table orders
(
   id                   char(6) not null,
   products_id          char(6) not null,
   number               int,
   price                double,
   create_time          datetime,
   send_time            datetime,
   confirm_time         datetime,
   consignee            varchar(20),
   consignee_phone      char(11),
   consignee_address    varchar(100),
   status               varchar(10),
   primary key (id)
);

/*==============================================================*/
/* Table: products                                              */
/*==============================================================*/
create table products
(
   id                   char(6) not null,
   title                varchar(20),
   price                double,
   stock                int,
   status               varchar(10),
   primary key (id)
);

alter table orders add constraint FK_Reference_1 foreign key (products_id)
      references products (id) on delete restrict on update restrict;

insert into products values('100001','小米8',2699,100,'正常');
insert into products values('100002','小米8SE',1799,100,'正常');
insert into products values('100003','小米MIX2S',3299,100,'正常');
insert into products values('100004','小米手环3',199,100,'正常');

