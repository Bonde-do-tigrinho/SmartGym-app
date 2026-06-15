package org.smartgym.repository

import io.ktor.client.call.*
import io.ktor.client.request.*
import org.smartgym.model.Adm.DashboardResponse
import org.smartgym.network.ApiClient

class ApiDashboardRepository {

    suspend fun getDashboard(): DashboardResponse? {
        return try {
            ApiClient.client.get(ApiClient.getUrl("/api/admin/dashboard")).body()
        } catch (e: Exception) {
            null
        }
    }
}