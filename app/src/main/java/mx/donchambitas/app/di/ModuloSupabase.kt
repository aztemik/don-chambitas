package mx.donchambitas.app.di

import dagger.Provides
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.minimalSettings
import io.github.jan.supabase.createSupabaseClient
import javax.inject.Singleton
import mx.donchambitas.app.BuildConfig
import mx.donchambitas.app.datos.repositorio.URL_RECUPERACION

@Module
@InstallIn(SingletonComponent::class)
object ModuloSupabase {

    @Provides
    @Singleton
    fun proveerClienteSupabase(): SupabaseClient = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY
    ) {
        install(Auth) {
            scheme = "mx.donchambitas.app"
            host = "auth"
            defaultRedirectUrl = URL_RECUPERACION
            minimalSettings(
                alwaysAutoRefresh = true,
                autoLoadFromStorage = false,
                autoSaveToStorage = false,
                enableLifecycleCallbacks = true
            )
        }
    }
}
