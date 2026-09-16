-- ============================================================================
--  DON CHAMBITAS
--  92_usuarios_prueba.sql  ·  Las cuatro cuentas del paso 2c de S1-T03
-- ============================================================================
--  Pegalo COMPLETO en el SQL Editor de Supabase y corre. Devuelve 4 renglones.
--  Despues corre:  python3 basedatos/92_prueba_rls_anon.py
--
--  POR QUE ESTE ARCHIVO EXISTE, Y NO LAS CREA EL GUION
--
--    El guion no puede: el alta por la API rechaza estos correos con
--    "email_address_invalid". Supabase valida que el dominio del correo
--    exista, y @prueba.donchambitas.mx es ficticio: donchambitas.mx no tiene
--    registro A ni MX. El dominio se eligio cuando solo se usaba desde SQL,
--    en 91_prueba_funcional.sql, que inserta directo en auth.users y por eso
--    nunca se topo con la validacion.
--
--    Insertar aqui y entrar por la API desde el guion deja intacto lo que el
--    paso 2c tiene que probar: las lecturas van con un JWT de verdad, contra
--    PostgREST, con la anon key. Lo unico que se saltan estas cuentas es el
--    formulario de registro, que no es lo que se esta probando.
--
--  QUE ESCRIBE. Cuatro filas en auth.users; public.usuarios las crea sola el
--  trigger tg_auth_usuario_creado. El primer DELETE limpia una corrida previa,
--  y solo alcanza correos que terminan en @prueba.donchambitas.mx.
--
--  ESTAS CUENTAS NO SON PARA PRODUCCION. Llevan una contrasena escrita en
--  claro en este archivo, a proposito: son de un dominio que no existe y no
--  puede recibir correo, asi que nadie recupera nada con ellas. Borralas al
--  terminar con el DELETE del final.
-- ============================================================================

delete from auth.users where email like '%@prueba.donchambitas.mx';

insert into auth.users (
    instance_id, id, aud, role, email, encrypted_password, email_confirmed_at,
    raw_app_meta_data, raw_user_meta_data, created_at, updated_at,
    -- GoTrue lee estas cuatro como texto al iniciar sesion y revienta si
    -- estan en NULL. Cadena vacia, no nulo.
    confirmation_token, recovery_token, email_change_token_new, email_change
)
select
    '00000000-0000-0000-0000-000000000000',
    d.id, 'authenticated', 'authenticated', d.correo,
    extensions.crypt('prueba-S1T03-no-usar-en-produccion', extensions.gen_salt('bf')),
    now(),
    '{"provider":"email","providers":["email"]}'::jsonb,
    jsonb_build_object('nombre', d.nombre, 'apellidos', d.apellidos,
                       'telefono', '5550000000', 'rol', d.rol),
    now(), now(), '', '', '', ''
from (values
    ('aaaaaaaa-0000-4000-8000-000000000001'::uuid, 'ana.rls@prueba.donchambitas.mx',   'Ana',   'Cliente',    'cliente'),
    ('aaaaaaaa-0000-4000-8000-000000000002'::uuid, 'beto.rls@prueba.donchambitas.mx',  'Beto',  'Plomero',    'trabajador'),
    ('aaaaaaaa-0000-4000-8000-000000000003'::uuid, 'cesar.rls@prueba.donchambitas.mx', 'Cesar', 'Competidor', 'trabajador'),
    ('aaaaaaaa-0000-4000-8000-000000000004'::uuid, 'dora.rls@prueba.donchambitas.mx',  'Dora',  'Ajena',      'cliente')
) as d(id, correo, nombre, apellidos, rol);

-- Las cuatro tienen que aparecer, con su rol y ya confirmadas.
select u.email,
       p.rol::text                                   as rol,
       (u.email_confirmed_at is not null)            as confirmada,
       (u.encrypted_password is not null)            as con_contrasena,
       (p.id is not null)                            as perfil_creado_por_el_trigger
  from auth.users u
  left join public.usuarios p on p.id = u.id
 where u.email like '%@prueba.donchambitas.mx'
 order by u.email;

-- ============================================================================
--  AL TERMINAR, para borrarlas:
--      delete from auth.users where email like '%@prueba.donchambitas.mx';
-- ============================================================================
