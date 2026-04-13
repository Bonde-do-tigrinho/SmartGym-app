import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.smartgym.model.aluno.Aparelho

@Composable
fun AparelhoCard(aparelho: Aparelho) {
    val colors = MaterialTheme.colorScheme
    val isAvailable = aparelho.timeRemaining == null

    // Cores condicionais baseadas no status
    val statusColor = if (isAvailable) Color(0xFF00FF00) else Color(0xFFFF4444)
    val statusText = if (isAvailable) "Disponível" else "Ocupado"

    // Fundo levemente diferente se estiver ocupado (estética da imagem)
    val cardBg = if (isAvailable) colors.surface else colors.surface.copy(alpha = 0.8f)

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(10.dp).background(statusColor, CircleShape))
                Spacer(Modifier.width(8.dp))
                Text(statusText, color = statusColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.weight(1f))
                Icon(Icons.Default.Wifi, contentDescription = null, tint = colors.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(aparelho.name, color = colors.onSurface, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(aparelho.category, color = colors.onSurfaceVariant, fontSize = 14.sp)
        }
    }
}