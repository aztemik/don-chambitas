-- ============================================================================
--  DON CHAMBITAS
--  03_almacenamiento.sql  ·  Cubetas de Supabase Storage y sus politicas
-- ============================================================================
--  Ejecutar DESPUES de 02_politicas_rls.sql.
--
--  DOS CUBETAS, Y LA CONVENCION DE RUTAS QUE LAS HACE SEGURAS
--
--    perfiles/<uid>/<archivo>    foto de perfil del usuario (P-18)
--    servicios/<uid>/<archivo>   fotos de los servicios del trabajador (P-13)
--
--  La PRIMERA CARPETA es siempre el id del usuario dueno del archivo. De ahi
--  salen las politicas: se compara auth.uid() contra esa carpeta. Si alguien
--  sube a la carpeta de otro, la politica lo rechaza.
--
--  Si la aplicacion no respeta esa convencion de rutas, las politicas de
--  abajo no protegen nada. Es la parte que hay que respetar al pie de la
--  letra en S2-T13 y S2-T14.
--
--  Las dos cubetas son de LECTURA PUBLICA: las fotos se pintan con Coil desde
--  una URL directa, y poner URLs firmadas a cada foto de cada tarjeta de
--  resultados seria pagar mucho por nada. Lo publico es la foto; lo que sigue
--  protegido es quien puede subirla, cambiarla y borrarla.
-- ============================================================================

begin;

-- ----------------------------------------------------------------------------
--  Cubetas
--  5 MB por archivo, solo imagenes. El recorte y la compresion los hace la
--  aplicacion antes de subir (S2-T13).
-- ----------------------------------------------------------------------------
insert into storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
values
    ('perfiles',  'perfiles',  true, 5242880,
     array['image/jpeg', 'image/png', 'image/webp']),
    ('servicios', 'servicios', true, 5242880,
     array['image/jpeg', 'image/png', 'image/webp'])
on conflict (id) do update
    set public             = excluded.public,
        file_size_limit    = excluded.file_size_limit,
        allowed_mime_types = excluded.allowed_mime_types;

-- ----------------------------------------------------------------------------
--  Politicas
--  storage.foldername(name) parte la ruta: [1] es la primera carpeta.
-- ----------------------------------------------------------------------------

-- Este archivo tambien se puede volver a correr. Se borran solo LAS NUESTRAS,
-- por nombre: storage.objects puede tener politicas de otras cosas.
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

-- Lectura: cualquiera, incluso sin sesion. Son cubetas publicas.
create policy "fotos de perfil visibles para todos"
    on storage.objects for select to anon, authenticated
    using (bucket_id = 'perfiles');

create policy "fotos de servicio visibles para todos"
    on storage.objects for select to anon, authenticated
    using (bucket_id = 'servicios');

-- Escritura: solo dentro de tu propia carpeta.
create policy "usuario sube su foto de perfil"
    on storage.objects for insert to authenticated
    with check (bucket_id = 'perfiles'
                and (storage.foldername(name))[1] = auth.uid()::text);

create policy "usuario reemplaza su foto de perfil"
    on storage.objects for update to authenticated
    using (bucket_id = 'perfiles'
           and (storage.foldername(name))[1] = auth.uid()::text)
    with check (bucket_id = 'perfiles'
                and (storage.foldername(name))[1] = auth.uid()::text);

create policy "usuario borra su foto de perfil"
    on storage.objects for delete to authenticated
    using (bucket_id = 'perfiles'
           and (storage.foldername(name))[1] = auth.uid()::text);

create policy "trabajador sube fotos de sus servicios"
    on storage.objects for insert to authenticated
    with check (bucket_id = 'servicios'
                and (storage.foldername(name))[1] = auth.uid()::text);

create policy "trabajador reemplaza fotos de sus servicios"
    on storage.objects for update to authenticated
    using (bucket_id = 'servicios'
           and (storage.foldername(name))[1] = auth.uid()::text)
    with check (bucket_id = 'servicios'
                and (storage.foldername(name))[1] = auth.uid()::text);

create policy "trabajador borra fotos de sus servicios"
    on storage.objects for delete to authenticated
    using (bucket_id = 'servicios'
           and (storage.foldername(name))[1] = auth.uid()::text);

commit;

-- ============================================================================
--  NOTA SOBRE EL LIMITE DE 3 FOTOS POR SERVICIO
--
--  El tope de 3 lo impone la tabla servicio_fotos, no el almacenamiento:
--  ck_foto_posicion mas uq_foto_posicion_unica. Storage no sabe contar fotos
--  por servicio.
--
--  Consecuencia practica para S2-T14: se sube el archivo PRIMERO y se inserta
--  la fila DESPUES. Si el insert falla por el tope, hay que borrar el archivo
--  que se acaba de subir, o quedan huerfanos acumulandose en la cubeta.
-- ============================================================================
