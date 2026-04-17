package org.smartgym.repository

import org.smartgym.model.professor.AlunoResumido

interface AlunoRepository {
    suspend fun getAll(): List<AlunoResumido>
}
