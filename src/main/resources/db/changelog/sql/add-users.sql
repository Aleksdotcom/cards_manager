INSERT INTO users (id, email, first_name, last_name, password, role)
VALUES
    (nextval('public.users_seq'), 'email@email.com', 'TOM', 'SMITH', '$2a$10$Ghdvdm.A3QmkH2P0uvOGouWovzrKgEvrbJ0KK1hUj8.Lvm5F8sAay', 'ADMIN'),
    (nextval('public.users_seq'), 'email1@email.com', 'KATE', 'BROWN', '$2a$10$ZnXauGh1Qdp2FdHoxy4Mk.EGygt6YgI0uJ3rWuQD0vyn5/.9VvBTi', 'USER')
ON CONFLICT (email) DO NOTHING;