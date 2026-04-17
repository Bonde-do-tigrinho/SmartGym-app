package org.smartgym.repository

import org.smartgym.model.professor.Avaliacao

interface AvaliacaoRepository {
    suspend fun getAll(): List<Avaliacao>
    suspend fun getById(id: Long): Avaliacao?
    suspend fun create(avaliacao: Avaliacao): Avaliacao
    suspend fun update(id: Long, avaliacao: Avaliacao): Avaliacao
    suspend fun delete(id: Long)
    suspend fun getByNomeAluno(nomeAluno: String): List<Avaliacao>
}

