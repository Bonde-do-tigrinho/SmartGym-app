package org.smartgym.repository

import org.smartgym.model.professor.Exercicio

interface ExercicioRepository {
    suspend fun getAll(): List<Exercicio>
    suspend fun getById(id: Long): Exercicio?
    suspend fun create(exercicio: Exercicio): Exercicio
    suspend fun update(id: Long, exercicio: Exercicio): Exercicio
    suspend fun delete(id: Long)
    suspend fun getByNome(nome: String): List<Exercicio>
    suspend fun getByMaquina(maquinaId: Long): List<Exercicio>
}
