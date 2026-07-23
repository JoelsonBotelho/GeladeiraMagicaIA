create table food_item (
    id bigint not null auto_increment primary key,
    nome varchar(255) not null,
    categoria varchar(255) not null,
    quantidade int not null default 1,
    data_validade date not null default current_date + interval 15 day,
);