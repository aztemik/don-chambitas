-- ============================================================================
--  DON CHAMBITAS
--  00_reinicio.sql  ·  Borra TODO y deja el proyecto como recien creado
-- ============================================================================
--
--   #####  ESTO BORRA DATOS DE VERDAD Y NO SE PUEDE DESHACER  #####
--
--  Borra, en este orden:
--    - el trigger que cuelga de auth.users
--    - las politicas de almacenamiento del proyecto
--    - los archivos y las DOS CUBETAS de Storage
--    - TODAS las tablas, vistas, funciones y tipos del esquema public
--    - TODAS las cuentas de auth.users
--
--  No deja nada del proyecto en pie. Despues de esto, correr 01 a 04 es
--  exactamente lo mismo que correrlos en un proyecto recien creado.
--
--  LO DE auth.users NO ES UN DESCUIDO, ES NECESARIO. public.usuarios nace de
--  un trigger que solo dispara al INSERTAR en auth.users. Si borras las tablas
--  y dejas las cuentas, esas cuentas quedan huerfanas para siempre: pueden
--  iniciar sesion pero no tienen perfil, y nada las va a crear. Un reinicio a
--  medias es peor que no reiniciar.
--
--  HOY ESO NO CUESTA NADA porque no hay ni una cuenta real: la aplicacion
--  todavia no existe. EL DIA QUE LA HAYA, ESTE ARCHIVO DEJA DE SER SEGURO.
--  Si en el futuro necesitas reiniciar conservando cuentas, comenta el DELETE
--  de auth.users y despues crea a mano las filas de public.usuarios que
--  falten. No hay atajo.
--
--  Borra el esquema public completo, no solo lo de Don Chambitas. Si alguien
--  guardo algo suyo ahi, se va tambien.
--
--  DESPUES DE ESTE ARCHIVO hay que correr, en orden:
--      01_esquema.sql -> 02_politicas_rls.sql -> 03_almacenamiento.sql
--      -> 04_datos_semilla.sql
--  y luego 90_verificacion.sql y 91_prueba_funcional.sql para comprobar.
--
--  LAS CUBETAS SOBREVIVEN, Y NO PASA NADA. Comprobado el 2026-09-15 contra el
--  proyecto real: Supabase protege storage.objects Y storage.buckets con el
--  trigger storage.protect_delete(), que rechaza SIEMPRE el DELETE por SQL:
--      "Direct deletion from storage tables is not allowed. Use the Storage
--       API instead."  (SQLSTATE 42501)
--
--  No hay forma de saltarselo desde el SQL Editor, ni con este archivo ni con
--  un DELETE a mano, y tampoco conviene: la proteccion existe porque borrar la
--  fila dejaria el archivo real huerfano en el almacenamiento.
--
--  Por eso este archivo NO se detiene por las cubetas. Quedan vacias y sin
--  politicas, que no le sirve a nadie, y 03_almacenamiento.sql las reconcilia
--  con "on conflict do update" al volver a correrlo: el resultado final es
--  identico a haberlas borrado. El ultimo renglon del reporte acepta 0 o 2 y
--  dice cual de los dos paso.
--
--  Si de verdad las quieres en cero, borralas ANTES desde la consola de
--  Supabase > Storage (el panel usa la Storage API, no SQL, por eso ahi si
--  puede), o con la CLI. Por SQL no, da igual como lo intentes.
--
--  Lo que SI detiene este archivo son los ARCHIVOS dentro de las cubetas: eso
--  se arregla con "Empty bucket" en la consola.
--
--  Supabase va a advertir que la consulta es destructiva. Lo es. Esa es toda
--  la funcion de este archivo.
--
--  NOTA: no se hace "drop schema public cascade", que seria mas corto, porque
--  eso se lleva tambien los permisos que Supabase le da al esquema y hay que
--  reconstruirlos a mano. Asi es mas largo y no deja el proyecto cojo.
-- ============================================================================

begin;

-- ----------------------------------------------------------------------------
--  1. El trigger sobre auth.users. Vive fuera de public, hay que quitarlo
--     antes de borrar la funcion de la que depende.
-- ----------------------------------------------------------------------------
drop trigger if exists tg_auth_usuario_creado on auth.users;

-- ----------------------------------------------------------------------------
--  2. Almacenamiento: primero las politicas nuestras, luego los archivos, y
--     al final las cubetas. Una cubeta con archivos dentro no se deja borrar.
-- ----------------------------------------------------------------------------
do $$
declare
    r record;
    k_nuestras text[] := array[
        'fotos de perfil visibles para todos',
        'fotos de servicio visibles para todos',
        'usuario sube su foto de perfil',
        'usuario reemplaza su foto de perfil',
        'usuario borra su foto de perfil',
        'trabajador sube fotos de sus servicios',
        'trabajador reemplaza fotos de sus servicios',
        'trabajador borra fotos de sus servicios'];
begin
    for r in select policyname from pg_policies
              where schemaname = 'storage' and tablename = 'objects'
                and policyname = any(k_nuestras)
    loop
        execute format('drop policy if exists %I on storage.objects', r.policyname);
    end loop;
end
$$;

-- ---------------------------------------------------------------------------
--  2b. Los archivos y las cubetas.
--
--  Se intenta borrarlos. Supabase RECHAZA el DELETE con
--      "Direct deletion from storage tables is not allowed"
--  tanto en storage.objects como en storage.buckets; por eso cada DELETE va
--  dentro de un bloque que atrapa el fallo y guarda el motivo, en vez de
--  tumbar el reinicio de un golpe y dejarte adivinando cual de los dos fue.
--
--  Al final se cuenta lo que quedo. Si quedo algo, este archivo se DETIENE:
--  el raise aborta la transaccion entera, no se pierde nada, y el mensaje
--  dice exactamente que hacer. Vuelve a correrlo despues de vaciar Storage
--  desde la consola.
--
--  El DELETE de las cubetas se queda aqui aunque hoy no pueda funcionar: si
--  algun dia Supabase levanta la proteccion, el reinicio queda completo sin
--  que nadie tenga que acordarse de esto.
-- ---------------------------------------------------------------------------
do $$
declare
    v_archivos int;
    v_cubetas  int;
    v_falla    text := '';
begin
    begin
        delete from storage.objects where bucket_id in ('perfiles', 'servicios');
    exception when others then
        v_falla := v_falla || 'archivos: ' || sqlerrm || ' / ';
    end;

    begin
        delete from storage.buckets where id in ('perfiles', 'servicios');
    exception when others then
        v_falla := v_falla || 'cubetas: ' || sqlerrm;
    end;

    select count(*) into v_archivos
      from storage.objects where bucket_id in ('perfiles', 'servicios');
    select count(*) into v_cubetas
      from storage.buckets where id in ('perfiles', 'servicios');

    -- Que las CUBETAS sobrevivan no detiene nada, y es la unica razon por la
    -- que este bloque sigue aqui intentandolo: si algun dia Supabase levanta
    -- la proteccion, el reinicio queda completo solo. Mientras tanto quedan
    -- vacias y sin politicas, y 03_almacenamiento.sql las reconcilia. El
    -- reporte del final acepta 0 o 2 y dice cual de los dos paso.
    --
    -- Los ARCHIVOS si detienen el reinicio. Un archivo de la vida anterior
    -- sobreviviendo a una base recien creada es una incoherencia de verdad:
    -- ninguna fila lo referencia ya y nadie lo va a volver a encontrar. Ese
    -- si se puede resolver desde la consola, con "Empty bucket".
    if v_archivos > 0 then
        raise exception
            'STORAGE: quedan % archivo(s) dentro de las cubetas, y por SQL no se pueden borrar. Ve a la consola de Supabase > Storage y usa "Empty bucket" en "perfiles" y "servicios", luego vuelve a correr este archivo. NO SE PERDIO NADA: esta transaccion se aborto entera y la base quedo como estaba. Motivo: %',
            v_archivos,
            coalesce(nullif(v_falla, ''), 'el DELETE no dio error pero no borro nada');
    end if;
end
$$;

-- ----------------------------------------------------------------------------
--  3. Vistas. Antes que las tablas, para que no estorben.
-- ----------------------------------------------------------------------------
do $$
declare r record;
begin
    for r in select viewname from pg_views where schemaname = 'public'
    loop
        execute format('drop view if exists public.%I cascade', r.viewname);
    end loop;
end
$$;

-- ----------------------------------------------------------------------------
--  4. Tablas. El cascade se lleva indices, triggers, restricciones, politicas
--     y la membresia en la publicacion de Realtime.
-- ----------------------------------------------------------------------------
do $$
declare r record;
begin
    for r in select tablename from pg_tables where schemaname = 'public'
    loop
        execute format('drop table if exists public.%I cascade', r.tablename);
    end loop;
end
$$;

-- ----------------------------------------------------------------------------
--  5. Funciones. Se leen con su firma completa porque varias reciben
--     argumentos y el nombre solo no basta para identificarlas.
-- ----------------------------------------------------------------------------
do $$
declare r record;
begin
    -- prokind = 'f' deja fuera procedimientos y agregados, que no se borran
    -- con "drop function" y abortarian el barrido. Hoy no hay ninguno; es
    -- para que siga funcionando el dia que alguien agregue uno.
    for r in select p.oid::regprocedure as firma
               from pg_proc p
               join pg_namespace n on n.oid = p.pronamespace
              where n.nspname = 'public' and p.prokind = 'f'
    loop
        execute format('drop function if exists %s cascade', r.firma);
    end loop;
end
$$;

-- ----------------------------------------------------------------------------
--  6. Tipos enumerados.
-- ----------------------------------------------------------------------------
do $$
declare r record;
begin
    for r in select t.oid::regtype as tipo
               from pg_type t
               join pg_namespace n on n.oid = t.typnamespace
              where n.nspname = 'public' and t.typtype = 'e'
    loop
        execute format('drop type if exists %s cascade', r.tipo);
    end loop;
end
$$;

-- ----------------------------------------------------------------------------
--  7. Las cuentas. Lee la advertencia de arriba antes de correr esto.
-- ----------------------------------------------------------------------------
delete from auth.users;

commit;

-- ============================================================================
--  Que quedo. Los 11 renglones tienen que decir OK.
--
--  El ultimo admite 0 o 2 a proposito: 0 si borraste las cubetas desde la
--  consola antes de correr esto, 2 si las dejaste. Las dos cosas estan bien.
-- ============================================================================
select concepto,
       quedan,
       esperado,
       -- El esperado admite varios valores separados por | . Solo lo usa el
       -- renglon de las cubetas, que puede quedar en 0 o en 2 segun si las
       -- borraste a mano en la consola o no. Todos los demas son un valor.
       case when quedan = any(string_to_array(esperado, '|')) then 'OK' else '>>> REVISA' end as resultado
from (values
  ('tablas en public',        (select count(*)::text from pg_tables where schemaname = 'public'), '0'),
  ('vistas en public',        (select count(*)::text from pg_views where schemaname = 'public'), '0'),
  ('funciones en public',     (select count(*)::text from pg_proc p join pg_namespace n on n.oid = p.pronamespace where n.nspname = 'public'), '0'),
  ('tipos enum en public',    (select count(*)::text from pg_type t join pg_namespace n on n.oid = t.typnamespace where n.nspname = 'public' and t.typtype = 'e'), '0'),
  ('politicas en public',     (select count(*)::text from pg_policies where schemaname = 'public'), '0'),
  ('politicas de storage nuestras', (select count(*)::text from pg_policies where schemaname = 'storage' and tablename = 'objects' and policyname like '%foto%'), '0'),
  ('tablas en Realtime',      (select count(*)::text from pg_publication_tables where pubname = 'supabase_realtime' and schemaname = 'public'), '0'),
  ('trigger en auth.users',   (select count(*)::text from pg_trigger where tgname = 'tg_auth_usuario_creado'), '0'),
  ('cuentas en auth.users',   (select count(*)::text from auth.users), '0'),
  ('archivos en las cubetas', (select count(*)::text from storage.objects where bucket_id in ('perfiles','servicios')), '0'),
  ('cubetas de Storage (0 si las borraste en la consola, 2 si Supabase no dejo)',
                              (select count(*)::text from storage.buckets where id in ('perfiles','servicios')), '0|2')
) as t(concepto, quedan, esperado);
