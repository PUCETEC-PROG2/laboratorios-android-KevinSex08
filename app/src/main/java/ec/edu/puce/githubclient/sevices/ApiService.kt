package ec.edu.puce.githubclient.services

import ec.edu.puce.githubclient.models.Repository
import ec.edu.puce.githubclient.models.RepositoryPayload
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // AÑADIMOS ESTA LÍNEA PARA EVITAR EL CACHÉ DE 60 SEGUNDOS
    @Headers("Cache-Control: no-cache")
    @GET("user/repos")
    suspend fun getRepositories(
        @Query("sort") sort: String = "created",
        @Query("direction") direction: String = "desc",
        @Query("affiliation") affiliation: String = "owner",
        @Query("per_page") perPage: Int = 100,
        @Query("t") t: String = "${System.currentTimeMillis()}",
    ): List<Repository>

    // Crear repositorio (GitHub)
    @POST("user/repos")
    suspend fun createRepository (
        @Body repository: RepositoryPayload
    ) : Repository

    // Editar: GitHub usa PATCH para editar nombre o descripción
    @PATCH("repos/{owner}/{repo}")
    suspend fun updateRepository(
        @Path("owner") owner: String,
        @Path("repo") repoName: String,
        @Body repo: RepositoryPayload
    ): Response<Repository>

    // Eliminar: GitHub usa DELETE
    @DELETE("repos/{owner}/{repo}")
    suspend fun deleteRepository(
        @Path("owner") owner: String,
        @Path("repo") repoName: String
    ): Response<Unit>
}