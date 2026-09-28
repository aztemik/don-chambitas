package mx.donchambitas.app.datos.remoto.supabase

/**
 * El enlace por el que Supabase Auth vuelve a la aplicacion (DEC-27).
 * Especificado en docs/tecnico/CONTRATOS-API.md, "recuperarContrasena y el
 * enlace de la aplicacion".
 *
 * El intent-filter de AndroidManifest.xml repite el esquema y el host como
 * texto, porque el manifiesto no puede leer constantes de Kotlin: si cambian
 * aqui, cambian alla el mismo dia, y tambien en las URLs de redireccion
 * permitidas de la consola de Supabase.
 */
object ConfiguracionSupabase {
    const val ESQUEMA_ENLACE = "mx.donchambitas.app"
    const val HOST_ENLACE = "auth"
    const val ENLACE_AUTH = "$ESQUEMA_ENLACE://$HOST_ENLACE"
}
