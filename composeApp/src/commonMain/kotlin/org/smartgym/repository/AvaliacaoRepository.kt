package org.smartgym.repository

import org.smartgym.model.professor.Avaliacao

interface AvaliacaoRepository {
    suspend fun getAll(): List<Avaliacao>
    suspend fun getById(id: Int): Avaliacao?
    suspend fun create(avaliacao: Avaliacao)
    suspend fun update(id: Int, avaliacao: Avaliacao)
    suspend fun delete(id: Int)
    suspend fun getByNomeAluno(nomeAluno: String): List<Avaliacao>
}

