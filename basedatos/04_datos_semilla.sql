-- ============================================================================
--  DON CHAMBITAS
--  04_datos_semilla.sql  ·  Catalogos base
--  Ejecutar al final: 01 -> 02 -> 03 -> 04. Ver basedatos/README.md
-- ============================================================================
--  Solo carga catalogos: 16 categorias, 32 estados y 26 municipios.
--
--  No incluye usuarios, servicios ni solicitudes de prueba. Los usuarios no
--  se pueden sembrar con un INSERT: nacen en auth.users cuando alguien se
--  registra, y el trigger tg_auth_usuario_creado crea su fila de perfil.
--  Para datos de prueba, registra cuentas desde la aplicacion.
--
--  Es idempotente: se puede volver a correr sin duplicar nada. Las categorias
--  ademas se RECONCILIAN (descripcion, icono y orden), para que corregirlas
--  sea volver a correr este archivo y nada mas.
-- ============================================================================

BEGIN;

-- ----------------------------------------------------------------------------
--  Categorias de oficios
--
--  Iconos de Material Icons variante Outlined (definidos en S1-T08 y DISENO.md),
--  sin librerias externas.
--
--  El ON CONFLICT es DO UPDATE para reconciliar icono, descripcion y orden
--  si se vuelve a correr el archivo.
-- ----------------------------------------------------------------------------
INSERT INTO categorias (nombre, descripcion, icono, orden) VALUES
    ('Plomeria',        'Fugas, instalaciones hidraulicas y drenajes',        'plumbing',          10),
    ('Electricidad',    'Instalaciones electricas, cortos y luminarias',      'bolt',              20),
    ('Albanileria',     'Muros, pisos, castillos y acabados',                 'construction',      30),
    ('Carpinteria',     'Muebles a medida, puertas y reparaciones en madera', 'carpenter',         40),
    ('Pintura',         'Interiores, exteriores e impermeabilizacion',        'format_paint',      50),
    ('Herreria',        'Rejas, portones, barandales y soldadura',            'hardware',          60),
    ('Limpieza',        'Limpieza de casas, oficinas y mudanzas',             'cleaning_services', 70),
    ('Jardineria',      'Poda, mantenimiento de jardines y riego',            'yard',              80),
    ('Mudanzas y carga','Fletes, cargadores y traslados',                     'local_shipping',    90),
    ('Aire y refrigeracion','Minisplits, refrigeradores y ventilacion',       'ac_unit',          100),
    ('Mecanica',        'Reparacion automotriz y servicio a domicilio',       'car_repair',       110),
    ('Computo',         'Reparacion de equipos, redes y respaldo de datos',   'computer',         120),
    ('Cerrajeria',      'Aperturas, cambio de chapas y duplicado de llaves',  'key',              130),
    ('Costura',         'Arreglos de ropa, confeccion y tapiceria',           'content_cut',      140),
    ('Cocina y eventos','Banquetes, meseros y servicio para eventos',         'restaurant',       150),
    ('Otros',           'Oficios que no encajan en las categorias anteriores','more_horiz',       999)
on conflict (nombre) do update
    set descripcion = excluded.descripcion,
        icono       = excluded.icono,
        orden       = excluded.orden;

-- ----------------------------------------------------------------------------
--  Estados de la Republica Mexicana
-- ----------------------------------------------------------------------------
INSERT INTO estados (nombre, clave) VALUES
    ('Aguascalientes','AGU'), ('Baja California','BCN'), ('Baja California Sur','BCS'),
    ('Campeche','CAM'), ('Coahuila','COA'), ('Colima','COL'),
    ('Chiapas','CHP'), ('Chihuahua','CHH'), ('Ciudad de Mexico','CMX'),
    ('Durango','DUR'), ('Guanajuato','GUA'), ('Guerrero','GRO'),
    ('Hidalgo','HID'), ('Jalisco','JAL'), ('Estado de Mexico','MEX'),
    ('Michoacan','MIC'), ('Morelos','MOR'), ('Nayarit','NAY'),
    ('Nuevo Leon','NLE'), ('Oaxaca','OAX'), ('Puebla','PUE'),
    ('Queretaro','QUE'), ('Quintana Roo','ROO'), ('San Luis Potosi','SLP'),
    ('Sinaloa','SIN'), ('Sonora','SON'), ('Tabasco','TAB'),
    ('Tamaulipas','TAM'), ('Tlaxcala','TLA'), ('Veracruz','VER'),
    ('Yucatan','YUC'), ('Zacatecas','ZAC')
on conflict (nombre) do nothing;

-- ----------------------------------------------------------------------------
--  Municipios
--
--  PENDIENTE: falta la carga completa de los 2,469 municipios del pais.
--  Se toman del catalogo abierto del INEGI y se cargan con COPY desde CSV.
--  Por ahora se siembran solo los de la zona donde se hara la prueba, para
--  no bloquear el desarrollo de la interfaz.
-- ----------------------------------------------------------------------------
INSERT INTO municipios (estado_id, nombre)
SELECT e.id, m.nombre
  FROM estados e
  JOIN (VALUES
        ('CMX','Alvaro Obregon'),   ('CMX','Azcapotzalco'),      ('CMX','Benito Juarez'),
        ('CMX','Coyoacan'),         ('CMX','Cuauhtemoc'),        ('CMX','Gustavo A. Madero'),
        ('CMX','Iztacalco'),        ('CMX','Iztapalapa'),        ('CMX','Miguel Hidalgo'),
        ('CMX','Tlalpan'),          ('CMX','Venustiano Carranza'),
        ('MEX','Ecatepec de Morelos'), ('MEX','Naucalpan de Juarez'), ('MEX','Nezahualcoyotl'),
        ('MEX','Tlalnepantla de Baz'), ('MEX','Toluca'),         ('MEX','Chalco'),
        ('PUE','Puebla'),           ('PUE','Cholula'),           ('PUE','Atlixco'),
        ('JAL','Guadalajara'),      ('JAL','Zapopan'),           ('JAL','Tlaquepaque'),
        ('NLE','Monterrey'),        ('NLE','San Nicolas de los Garza'), ('NLE','Guadalupe')
       ) AS m(clave, nombre) ON m.clave = e.clave
on conflict (estado_id, nombre) do nothing;

COMMIT;
