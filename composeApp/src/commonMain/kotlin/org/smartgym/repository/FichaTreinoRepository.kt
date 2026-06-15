package org.smartgym.repository
import org.smartgym.model.professor.FichaTreino
interface FichaTreinoRepository {
    suspend fun getAll(): List<FichaTreino>
    suspend fun getById(id: Int): FichaTreino?
    suspend fun getByAlunoId(alunoId: Int): List<FichaTreino>
    suspend fun create(fichaTreino: FichaTreino): FichaTreino?
    suspend fun update(id: Int, fichaTreino: FichaTreino): FichaTreino
    suspend fun delete(id: Int)

    suspend fun getMinhaFicha(): FichaTreino?
}
