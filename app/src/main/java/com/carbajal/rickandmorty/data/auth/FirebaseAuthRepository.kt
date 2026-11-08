package com.carbajal.rickandmorty.data.auth

import com.carbajal.rickandmorty.data.resultado
import com.carbajal.rickandmorty.domain.model.Rol
import com.carbajal.rickandmorty.domain.model.Sesion
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) : AuthRepository {
    override suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion> = resultado {
        val usuario = auth.signInWithEmailAndPassword(correo, clave).await().user
            ?: error("No se pudo iniciar sesión")
        try { leerSesion(usuario.uid, usuario.email.orEmpty(), false) }
        catch (e: CancellationException) { throw e }
        catch (e: Exception) { auth.signOut(); throw e }
    }
    override suspend fun sesionActual(): Sesion? {
        val usuario = auth.currentUser ?: return null
        return leerSesion(usuario.uid, usuario.email.orEmpty(), true)
    }
    override fun cerrarSesion() = auth.signOut()

    private suspend fun leerSesion(uid: String, correo: String, permitirCache: Boolean): Sesion {
        val referencia = db.collection("usuarios").document(uid)
        val doc = try { referencia.get(Source.SERVER).await() }
        catch (e: CancellationException) { throw e }
        catch (e: Exception) {
            if (permitirCache && (e is com.google.firebase.firestore.FirebaseFirestoreException) &&
                e.code == com.google.firebase.firestore.FirebaseFirestoreException.Code.UNAVAILABLE) {
                referencia.get(Source.CACHE).await()
            } else throw e
        }
        val rol = when (doc.getString("rol")) {
            "admin" -> Rol.ADMIN
            "usuario" -> Rol.USUARIO
            else -> error("La cuenta no tiene un rol válido configurado")
        }
        return Sesion(uid, correo, rol)
    }
}
