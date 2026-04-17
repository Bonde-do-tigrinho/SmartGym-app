package org.smartgym.model.professor

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlunoResumido(
    @SerialName("id")
    val id: Int,
    @SerialName("nome")
    val nome: String
)

