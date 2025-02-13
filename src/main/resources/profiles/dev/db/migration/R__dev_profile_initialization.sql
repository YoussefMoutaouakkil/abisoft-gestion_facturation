insert into menuitem(id, parent, key, value, service, grid_def, expanded)
values(0, -1, 'master', 'System', null, null, 1);

insert into menuitem(id, parent, key, value, service, grid_def, expanded, image)
values(1, 0, 'master.customers', 'Mes clients', 'customerService', 'customerGridDef', 0, 'customers_16x16.png');

insert into menuitem(id, parent, key, value, service, grid_def, expanded, image)
values(2, 0, 'master.categories', 'Categories des produits', null, null, 0, 'category_16x16.png');

insert into menuitem(id, parent, key, value, service, grid_def, expanded, image)
values(3, 0, 'master.products', 'Mes produits', null, null, 0, 'product_16x16.png');

insert into menuitem(id, parent, key, value, service, grid_def, expanded, image)
values(4, 0, 'master.invoices', 'Factures', null, null, 0, 'invoice_16x16.png');

insert into menuitem(id, parent, key, value, service, grid_def, expanded, image)
values(5, 0, 'master.devis', 'Devis', null, null, 0, 'invoice_16x16.png');


insert into customer(id, firstname, lastname, address, email)
values(0, 'soufyane ', 'majdoub', 'Narjis', 'soufyanemajdoub@gmail.com');
