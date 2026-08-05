DELETE FROM users WHERE email = 'admin@lers.lb';

INSERT INTO users (id, email, password, role, full_name, status)
VALUES (
           gen_random_uuid(),
           'admin@lers.lb',
           '$2a$10$Dpj.JDMC6k5i/N78S.CnTeBuJa03V5gcWuq1dsSKPLg.xX5PVEksy',
           'SUPER_ADMIN',
           'Super Admin',
           'active'
       );