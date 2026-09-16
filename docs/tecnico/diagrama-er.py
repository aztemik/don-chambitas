# -*- coding: utf-8 -*-
"""Genera docs/tecnico/diagrama-er.png desde el modelo de basedatos/01_esquema.sql.
Las rutas se declaran a mano y un verificador comprueba que ningun tramo cruce
una caja antes de dibujar nada."""
from PIL import Image, ImageDraw, ImageFont

F  = "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"
FB = "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf"
f_t, f_c, f_k = ImageFont.truetype(FB,19), ImageFont.truetype(F,15), ImageFont.truetype(FB,15)
f_l, f_h, f_s, f_g = (ImageFont.truetype(F,14), ImageFont.truetype(FB,34),
                      ImageFont.truetype(F,17), ImageFont.truetype(FB,17))

CARBON, GRIS, GRIS_CL = (23,23,23), (110,110,110), (188,188,188)
BLANCO, FONDO = (255,255,255), (250,249,246)
MOSTAZA, AZUL, VERDE, MORADO = (191,140,40), (38,84,124), (45,106,79), (106,61,130)

ANCHO, ENC, FILA, PAD = 300, 38, 25, 12
T = {}
def tabla(k, titulo, x, y, color, cols):
    T[k] = dict(t=titulo, x=x, y=y, c=color, cols=cols,
                w=ANCHO, h=ENC + len(cols)*FILA + PAD)

tabla("auth","auth.users",60,160,MORADO,[("id","pk"),("email",""),("raw_user_meta_data","")])
tabla("estados","estados",60,360,GRIS,[("id","pk"),("nombre",""),("clave","")])
tabla("municipios","municipios",60,540,GRIS,[("id","pk"),("estado_id","fk"),("nombre","")])
tabla("categorias","categorias",60,720,GRIS,[("id","pk"),("nombre",""),("icono",""),("activa","")])
tabla("usuarios","usuarios",470,300,AZUL,[("id","pk fk"),("correo",""),("nombre",""),
    ("apellidos",""),("telefono",""),("rol",""),("foto_url",""),("activo","")])
tabla("perfiles","perfiles_trabajador",900,160,VERDE,[("usuario_id","pk fk"),("rol",""),
    ("titulo",""),("descripcion",""),("experiencia_anios",""),("telefono_contacto",""),
    ("estado_id","fk"),("municipio_id","fk"),("disponible","")])
tabla("habilidades","perfil_habilidades",900,490,VERDE,[("perfil_id","pk fk"),("habilidad","pk")])
tabla("servicios","servicios",900,645,VERDE,[("id","pk"),("perfil_id","fk"),
    ("categoria_id","fk"),("titulo",""),("descripcion",""),("precio_desde",""),
    ("precio_hasta",""),("activo","")])
tabla("fotos","servicio_fotos",900,950,VERDE,[("id","pk"),("servicio_id","fk"),
    ("url",""),("posicion","")])
tabla("solicitudes","solicitudes",1380,160,CARBON,[("id","pk"),("cliente_id","fk"),
    ("categoria_id","fk"),("titulo",""),("descripcion",""),("presupuesto",""),
    ("estado_id","fk"),("municipio_id","fk"),("estatus",""),("trabajador_id","fk"),
    ("cerrada_en","")])
tabla("postulaciones","postulaciones",1380,540,CARBON,[("id","pk"),("solicitud_id","fk"),
    ("trabajador_id","fk"),("mensaje",""),("precio_propuesto",""),("estatus","")])
tabla("resenas","resenas",1380,795,CARBON,[("id","pk"),("solicitud_id","fk"),
    ("cliente_id","fk"),("trabajador_id","fk"),("calificacion",""),("comentario","")])
tabla("conversaciones","conversaciones",1820,160,AZUL,[("id","pk"),("cliente_id","fk"),
    ("trabajador_id","fk"),("solicitud_id","fk"),("ultimo_mensaje_en","")])
tabla("mensajes","mensajes",1820,390,AZUL,[("id","pk"),("conversacion_id","fk"),
    ("emisor_id","fk"),("contenido",""),("leido_en","")])
tabla("ia_consumo","ia_consumo",1820,640,MOSTAZA,[("usuario_id","pk fk"),("fecha","pk"),
    ("funcion","pk"),("llamadas",""),("tokens","")])
tabla("ia_cache","ia_cache",1820,870,MOSTAZA,[("funcion","pk"),("entrada_hash","pk"),("respuesta","")])

# (waypoints, etiqueta, color, punteada)
R = [
 ([(210,285),(210,320),(470,320)], "1 : 1", MORADO, False),
 ([(770,340),(790,340),(790,300),(900,300)], "1 : 0..1", VERDE, False),
 ([(1050,435),(1050,490)], "1 : N", VERDE, False),
 ([(1200,320),(1225,320),(1225,700),(1200,700)], "1 : N", VERDE, False),
 ([(1050,895),(1050,950)], "1 : N (max 3)", VERDE, False),
 ([(770,380),(800,380),(800,140),(1330,140),(1330,200),(1380,200)], "1 : N  cliente", CARBON, False),
 ([(1200,250),(1380,250)], "asignado  0..1 : N", CARBON, False),
 ([(1530,485),(1530,540)], "1 : N", CARBON, False),
 ([(1200,380),(1275,380),(1275,620),(1380,620)], "1 : N", CARBON, False),
 ([(1680,420),(1750,420),(1750,880),(1680,880)], "1 : 0..1", CARBON, False),
 ([(1200,410),(1300,410),(1300,900),(1380,900)], "1 : N", CARBON, False),
 ([(770,500),(825,500),(825,1140),(1350,1140),(1350,960),(1380,960)], "1 : N  cliente", CARBON, False),
 ([(770,440),(850,440),(850,1170),(2220,1170),(2220,250),(2120,250)], "1 : N  cliente", AZUL, False),
 ([(770,520),(875,520),(875,1200),(2200,1200),(2200,480),(2120,480)], "1 : N  emisor", AZUL, False),
 ([(1200,190),(1340,190),(1340,140),(1780,140),(1780,200),(1820,200)], "1 : N", AZUL, False),
 ([(1680,250),(1750,250),(1750,290),(1820,290)], "0..1 : N", AZUL, False),
 ([(1970,335),(1970,390)], "1 : N", AZUL, False),
 ([(770,530),(890,530),(890,1230),(2180,1230),(2180,700),(2120,700)], "1 : N", MOSTAZA, False),
 ([(210,485),(210,540)], "1 : N", GRIS_CL, True),
 ([(360,600),(415,600),(415,230),(900,230)], "estado_id, municipio_id", GRIS_CL, True),
 ([(360,630),(395,630),(395,1260),(1250,1260),(1250,450),(1380,450)], "estado_id, municipio_id", GRIS_CL, True),
 ([(360,795),(440,795),(440,770),(900,770)], "1 : N", GRIS_CL, True),
 ([(360,830),(375,830),(375,1290),(1325,1290),(1325,470),(1380,470)], "1 : N", GRIS_CL, True),
]

# --- verificador: ningun tramo puede entrar en una caja ---------------------
M = 6
def caja_en(pt):
    """Que caja tiene ese punto sobre su borde: es el anclaje de la ruta."""
    for k,b in T.items():
        if (b["x"]-1 <= pt[0] <= b["x"]+b["w"]+1) and (b["y"]-1 <= pt[1] <= b["y"]+b["h"]+1):
            return k
    return None

fallas = []
for idx, (pts, lab, col, pun) in enumerate(R):
    anclas = {caja_en(pts[0]), caja_en(pts[-1])}
    assert None not in anclas, (idx, "extremo sin caja", pts[0], pts[-1])
    for i in range(len(pts)-1):
        (x1,y1),(x2,y2) = pts[i], pts[i+1]
        assert x1==x2 or y1==y2, (idx, i, "tramo no ortogonal")
        for k,b in T.items():
            if k in anclas: continue
            bx1,by1,bx2,by2 = b["x"]-M, b["y"]-M, b["x"]+b["w"]+M, b["y"]+b["h"]+M
            if max(x1,x2) < bx1 or min(x1,x2) > bx2: continue
            if max(y1,y2) < by1 or min(y1,y2) > by2: continue
            fallas.append((idx, i, k, (x1,y1,x2,y2)))
if fallas:
    for f in fallas: print("CRUCE:", f)
    raise SystemExit("hay %d tramos que cruzan una caja" % len(fallas))
print("verificador: ninguna linea cruza una caja")

W, H = 2280, 1510
img = Image.new("RGB",(W,H),FONDO); d = ImageDraw.Draw(img)

def punteado(a,b,col):
    n = max(int((abs(b[0]-a[0])+abs(b[1]-a[1]))/10),1)
    for j in range(0,n,2):
        p = (a[0]+(b[0]-a[0])*j/n, a[1]+(b[1]-a[1])*j/n)
        q = (a[0]+(b[0]-a[0])*min(j+1,n)/n, a[1]+(b[1]-a[1])*min(j+1,n)/n)
        d.line([p,q], fill=col, width=2)

for pts, lab, col, pun in R:
    for i in range(len(pts)-1):
        (punteado if pun else (lambda a,b,c: d.line([a,b],fill=c,width=2)))(pts[i],pts[i+1],col)
    ex,ey = pts[-1]
    d.ellipse([ex-5,ey-5,ex+5,ey+5], fill=col)
    # etiqueta sobre el tramo mas largo
    tr = max(range(len(pts)-1), key=lambda i: abs(pts[i][0]-pts[i+1][0])+abs(pts[i][1]-pts[i+1][1]))
    lx, ly = (pts[tr][0]+pts[tr+1][0])//2, (pts[tr][1]+pts[tr+1][1])//2
    w = d.textlength(lab, font=f_l)
    d.rectangle([lx-w/2-5, ly-11, lx+w/2+5, ly+11], fill=FONDO)
    d.text((lx-w/2, ly-8), lab, font=f_l, fill=col)

for k,b in T.items():
    x,y,w,h = b["x"],b["y"],b["w"],b["h"]
    d.rectangle([x+3,y+3,x+w+3,y+h+3], fill=(234,232,228))
    d.rectangle([x,y,x+w,y+h], fill=BLANCO, outline=b["c"], width=2)
    d.rectangle([x,y,x+w,y+ENC], fill=b["c"])
    d.text((x+PAD,y+9), b["t"], font=f_t, fill=BLANCO)
    for i,(nom,tipo) in enumerate(b["cols"]):
        cy = y+ENC+5+i*FILA
        marca = ("PK " if "pk" in tipo else "") + ("FK " if "fk" in tipo else "")
        if marca: d.text((x+PAD,cy), marca, font=f_k, fill=b["c"])
        d.text((x+PAD+(d.textlength(marca,font=f_k) if marca else 0), cy), nom,
               font=(f_k if marca else f_c), fill=(CARBON if marca else (95,95,95)))

d.text((60,50), "Don Chambitas · modelo entidad-relacion", font=f_h, fill=CARBON)
d.text((62,100), "Generado desde basedatos/01_esquema.sql · S1-T03 · 15 tablas de public mas auth.users",
       font=f_s, fill=GRIS)

ly = 1350
d.text((60,ly-34), "Como se lee", font=f_g, fill=CARBON)
for i,(c,t) in enumerate([
    (MORADO, "auth.users lo administra Supabase Auth. usuarios.id ES ese mismo uuid, y la fila la crea el trigger tg_auth_usuario_creado"),
    (VERDE,  "Todo lo del trabajador cuelga de perfiles_trabajador, nunca de usuarios: asi no existe un servicio de alguien sin perfil"),
    (CARBON, "Flujo del trabajo: solicitud, postulaciones, resena. Una resena por solicitud, y solo si quedo cerrada"),
    (AZUL,   "Chat: un hilo por cliente-trabajador-solicitud, mas uno suelto por pareja. Lo abre siempre el cliente (DEC-20)"),
    (MOSTAZA,"ia_consumo e ia_cache tienen RLS activo y CERO politicas: solo las toca la Edge Function con la service_role key"),
    (GRIS_CL,"Punteado: catalogos. estatus (flujo) NO es estado_id (entidad federativa)"),
]):
    yy = ly+i*24
    (punteado((60,yy+8),(100,yy+8),c) if c is GRIS_CL else d.line([(60,yy+8),(100,yy+8)],fill=c,width=3))
    d.text((112,yy), t, font=f_l, fill=(70,70,70))

d.text((1500,ly-34), "PK  llave primaria        FK  llave foranea", font=f_g, fill=CARBON)
for i,t in enumerate([
  "perfiles_trabajador y solicitudes llevan una llave foranea COMPUESTA",
  "contra usuarios(id, rol): los roles son excluyentes y no se cambian (DEC-22).",
  "solicitudes.trabajador_id es ON DELETE SET NULL, y el CHECK que exige",
  "trabajador solo aplica a 'asignada': por eso un trabajador con historial",
  "puede darse de baja sin que la transaccion aborte.",
]):
    d.text((1500,ly+i*24), t, font=f_l, fill=(70,70,70))

img.save("docs/tecnico/diagrama-er.png", optimize=True)
print("listo", img.size)
