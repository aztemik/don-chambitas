import os
from PIL import Image, ImageDraw, ImageFont

def hex_to_rgb(hex_code):
    hex_code = hex_code.lstrip('#')
    return tuple(int(hex_code[i:i+2], 16) for i in (0, 2, 4))

def wrap_text(text, font, max_width, draw):
    words = text.split(' ')
    lines = []
    current_line = []
    for word in words:
        test_line = ' '.join(current_line + [word])
        bbox = draw.textbbox((0, 0), test_line, font=font)
        if bbox[2] - bbox[0] <= max_width:
            current_line.append(word)
        else:
            if current_line:
                lines.append(' '.join(current_line))
            current_line = [word]
    if current_line:
        lines.append(' '.join(current_line))
    return lines

def create_palette_sheet():
    width = 1360
    height = 980
    bg_color = hex_to_rgb("#FFFDF8") # Crema
    
    img = Image.new("RGBA", (width, height), bg_color)
    draw = ImageDraw.Draw(img)

    try:
        font_title = ImageFont.truetype("arialbd.ttf", 32)
        font_subtitle = ImageFont.truetype("arial.ttf", 17)
        font_section = ImageFont.truetype("arialbd.ttf", 20)
        font_card_title = ImageFont.truetype("arialbd.ttf", 17)
        font_hex = ImageFont.truetype("consola.ttf", 15)
        font_desc = ImageFont.truetype("arial.ttf", 12)
        font_badge = ImageFont.truetype("arialbd.ttf", 11)
    except Exception:
        font_title = ImageFont.load_default()
        font_subtitle = ImageFont.load_default()
        font_section = ImageFont.load_default()
        font_card_title = ImageFont.load_default()
        font_hex = ImageFont.load_default()
        font_desc = ImageFont.load_default()
        font_badge = ImageFont.load_default()

    # Header bar
    draw.rectangle([0, 0, width, 100], fill=hex_to_rgb("#E0A11B"))
    draw.text((48, 22), "DON CHAMBITAS  ·  SISTEMA DE DISEÑO", fill=hex_to_rgb("#2B1F14"), font=font_title)
    draw.text((50, 64), "Paleta Taller — Identidad Visual, Aplicación y Ratios de Contraste (WCAG 2.1)", fill=hex_to_rgb("#2B1F14"), font=font_subtitle)

    # Decorative accent stripe
    draw.rectangle([0, 96, width, 100], fill=hex_to_rgb("#C1440E"))

    # Palette sections
    sections = [
        {
            "title": "COLORES DE IDENTIDAD Y ACCIÓN",
            "y": 125,
            "colors": [
                ("Mostaza", "#E0A11B", "Primario: Botón principal, barra superior y chips activos.", "Carbon: 7.09:1 (AAA)\nBlanco: 2.26:1 (Falla)"),
                ("Mostaza Oscuro", "#B37D08", "Énfasis: Estado presionado, borde de foco y acentos.", "Borde UI: 3.53:1 (AA)"),
                ("Terracota", "#C1440E", "Acento: Botón destacado (postularse, publicar) y estrellas.", "Blanco: 5.12:1 (AA)"),
            ]
        },
        {
            "title": "SUPERFICIE, TEXTO Y DELIMITADORES",
            "y": 395,
            "colors": [
                ("Carbon", "#2B1F14", "Texto principal: Títulos, encabezados y lectura continua.", "Crema: 15.78:1 (AAA)\nArena: 14.16:1 (AAA)"),
                ("Cafe", "#7A6A58", "Texto secundario: Etiquetas, fechas, ayudas y apoyos.", "Crema: 5.13:1 (AA)\nArena: 4.60:1 (AA)"),
                ("Crema", "#FFFDF8", "Fondo: Superficie base cálida de la aplicación móvil.", "Carbon: 15.78:1 (AAA)"),
                ("Arena", "#F7F0E4", "Superficie: Tarjetas, campos de texto y hojas inferiores.", "Separación con fondo"),
                ("Borde", "#E6DAC6", "Delimitador: Contornos de campos en reposo y divisores.", "Grosor estándar 1 dp"),
            ]
        },
        {
            "title": "ESTADOS SEMÁNTICOS (SOLICITUDES Y ALERTAS)",
            "y": 685,
            "colors": [
                ("Éxito", "#2E7D32", "Estado 'Abierta': Solicitud disponible para postulación.", "Blanco: 5.13:1 (AA)"),
                ("Advertencia", "#ED6C02", "Estado 'Asignada': Trabajo en proceso con especialista.", "Carbon: 5.15:1 (AA)\nBlanco: 3.11:1 (Lg)"),
                ("Error", "#C62828", "Estado 'Cancelada': Rechazo o alerta crítica del sistema.", "Blanco: 5.62:1 (AA)"),
            ]
        }
    ]

    for sec in sections:
        draw.text((48, sec["y"]), sec["title"], fill=hex_to_rgb("#7A6A58"), font=font_section)
        
        cards = sec["colors"]
        count = len(cards)
        gap = 18
        total_width = width - 96
        card_w = (total_width - (gap * (count - 1))) // count
        card_h = 195
        card_y = sec["y"] + 32

        for i, (name, hex_val, desc, contrast_info) in enumerate(cards):
            card_x = 48 + i * (card_w + gap)
            
            # Card background
            draw.rounded_rectangle([card_x, card_y, card_x + card_w, card_y + card_h], radius=12, fill=hex_to_rgb("#FFFFFF"), outline=hex_to_rgb("#E6DAC6"), width=1)
            
            # Color Swatch (top part of card)
            draw.rounded_rectangle([card_x, card_y, card_x + card_w, card_y + 70], radius=12, fill=hex_to_rgb(hex_val))
            # Flatten bottom corners of the swatch
            draw.rectangle([card_x, card_y + 58, card_x + card_w, card_y + 70], fill=hex_to_rgb(hex_val))
            # Subtle border around swatch if light
            if hex_val in ["#FFFDF8", "#F7F0E4", "#E6DAC6"]:
                draw.rectangle([card_x, card_y + 69, card_x + card_w, card_y + 70], fill=hex_to_rgb("#E6DAC6"))

            # Card title & hex
            draw.text((card_x + 12, card_y + 78), name, fill=hex_to_rgb("#2B1F14"), font=font_card_title)
            draw.text((card_x + 12, card_y + 100), hex_val, fill=hex_to_rgb("#B37D08") if hex_val != "#B37D08" else hex_to_rgb("#2B1F14"), font=font_hex)
            
            # Description text wrapped
            desc_lines = wrap_text(desc, font_desc, card_w - 24, draw)
            curr_y = card_y + 122
            for dl in desc_lines[:2]:
                draw.text((card_x + 12, curr_y), dl, fill=hex_to_rgb("#7A6A58"), font=font_desc)
                curr_y += 15

            # Contrast badge / footer note
            c_lines = contrast_info.split('\n')
            badge_y = card_y + card_h - (len(c_lines) * 14 + 8)
            for cl in c_lines:
                draw.text((card_x + 12, badge_y), cl, fill=hex_to_rgb("#2B1F14"), font=font_badge)
                badge_y += 14

    output_dir = os.path.join("docs", "tecnico", "recursos")
    os.makedirs(output_dir, exist_ok=True)
    out_path = os.path.join(output_dir, "muestra-paleta.png")
    img.save(out_path, "PNG")
    print(f"Lámina guardada exitosamente en: {out_path}")

if __name__ == "__main__":
    create_palette_sheet()
