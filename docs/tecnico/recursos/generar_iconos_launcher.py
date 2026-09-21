import os
from PIL import Image, ImageDraw

def hex_to_rgb(hex_code):
    hex_code = hex_code.lstrip('#')
    return tuple(int(hex_code[i:i+2], 16) for i in (0, 2, 4))

def draw_helmet_icon(size, is_round=False):
    # Render at 4x supersampling for ultra crisp edges
    scale = 4
    canvas_size = size * scale
    
    # Transparent base
    img = Image.new("RGBA", (canvas_size, canvas_size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    carbon = hex_to_rgb("#2B1F14")
    mostaza = hex_to_rgb("#E0A11B")
    mostaza_osc = hex_to_rgb("#B37D08")
    terracota = hex_to_rgb("#C1440E")
    brillo = hex_to_rgb("#FFC857")
    sombra_casco = (26, 18, 10, 180)

    # 1. Background shape (masked to round or squircle/rounded rect)
    if is_round:
        draw.ellipse([0, 0, canvas_size - 1, canvas_size - 1], fill=carbon)
    else:
        radius = int(canvas_size * 0.22)
        draw.rounded_rectangle([0, 0, canvas_size - 1, canvas_size - 1], radius=radius, fill=carbon)

    # Coordinates normalized to 108dp viewport, scaled to canvas_size
    def s(val):
        return (val / 108.0) * canvas_size

    # Sombra base
    draw.ellipse([s(32), s(69), s(76), s(74)], fill=sombra_casco)

    # Cupula del casco (Dome)
    # Using polygon approximation of bezier curve
    cupula_pts = []
    # Left base to right base via top curve
    # M34,64 C34,42 42,34 54,34 C66,34 74,42 74,64 Z
    import math
    steps = 40
    for i in range(steps + 1):
        t = i / steps
        # Bezier curve formula
        # P(t) = (1-t)^3 P0 + 3(1-t)^2 t P1 + 3(1-t) t^2 P2 + t^3 P3
        # Left half: from (34, 64) through control (34, 38) and (44, 34) to (54, 34)
        if t <= 0.5:
            u = t * 2
            x = (1-u)**3 * 34 + 3*(1-u)**2*u * 34 + 3*(1-u)*u**2 * 44 + u**3 * 54
            y = (1-u)**3 * 64 + 3*(1-u)**2*u * 40 + 3*(1-u)*u**2 * 34 + u**3 * 34
        else:
            u = (t - 0.5) * 2
            x = (1-u)**3 * 54 + 3*(1-u)**2*u * 64 + 3*(1-u)*u**2 * 74 + u**3 * 74
            y = (1-u)**3 * 34 + 3*(1-u)**2*u * 34 + 3*(1-u)*u**2 * 40 + u**3 * 64
        cupula_pts.append((s(x), s(y)))
    
    draw.polygon(cupula_pts, fill=mostaza)

    # Brillo / luz en cupula
    brillo_pts = [
        (s(37), s(60)),
        (s(38), s(48)),
        (s(44), s(38)),
        (s(53), s(36)),
        (s(48), s(48)),
        (s(41), s(58))
    ]
    draw.polygon(brillo_pts, fill=brillo)

    # Cresta central de refuerzo
    cresta_pts = [
        (s(51.5), s(34)),
        (s(56.5), s(34)),
        (s(58.5), s(64)),
        (s(49.5), s(64))
    ]
    draw.polygon(cresta_pts, fill=mostaza_osc)

    # Insignia frontal en Terracota
    draw.rounded_rectangle([s(50.5), s(51), s(57.5), s(57)], radius=int(s(1.5)), fill=terracota)

    # Base de visera (sombra)
    draw.ellipse([s(27), s(62), s(81), s(71)], fill=mostaza_osc)
    # Visera frontal
    draw.ellipse([s(28), s(61), s(80), s(69)], fill=mostaza)

    # Borde inferior del ala
    draw.arc([s(32), s(65), s(76), s(72)], start=0, end=180, fill=mostaza_osc, width=max(1, int(s(2))))

    # Downsample with high quality Lanczos filter
    final_img = img.resize((size, size), Image.Resampling.LANCZOS)
    return final_img

def generate_all_icons():
    densities = {
        "mipmap-mdpi": 48,
        "mipmap-hdpi": 72,
        "mipmap-xhdpi": 96,
        "mipmap-xxhdpi": 144,
        "mipmap-xxxhdpi": 192
    }
    
    base_res = os.path.join("app", "src", "main", "res")
    
    for folder, size in densities.items():
        dir_path = os.path.join(base_res, folder)
        os.makedirs(dir_path, exist_ok=True)
        
        # 1. Standard icon
        icon = draw_helmet_icon(size, is_round=False)
        out_png = os.path.join(dir_path, "ic_launcher.png")
        icon.save(out_png, "PNG")
        
        # 2. Round icon
        icon_round = draw_helmet_icon(size, is_round=True)
        out_round_png = os.path.join(dir_path, "ic_launcher_round.png")
        icon_round.save(out_round_png, "PNG")
        
        # Remove old default .webp icons if present
        old_webp = os.path.join(dir_path, "ic_launcher.webp")
        if os.path.exists(old_webp):
            os.remove(old_webp)
        old_round_webp = os.path.join(dir_path, "ic_launcher_round.webp")
        if os.path.exists(old_round_webp):
            os.remove(old_round_webp)
            
        print(f"Generado {folder}: ic_launcher.png ({size}x{size}) y ic_launcher_round.png")

if __name__ == "__main__":
    generate_all_icons()
