package ua.blackwind.limbushelper.ui.screens.filter_screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.blackwind.limbushelper.domain.sinner.model.Identity
import ua.blackwind.limbushelper.ui.common.identityItemCore
import ua.blackwind.limbushelper.ui.previewIdentity
import ua.blackwind.limbushelper.ui.screens.filter_screen.model.FilterIdentityModel

@Composable
fun FilterIdentityItem(
    viewIdentity: FilterIdentityModel,
    onInPartyChecked: (Identity) -> Unit,
    onInPartyUnChecked: (Identity) -> Unit
) {
    Card(
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        modifier = Modifier.width(380.dp)
    ) {
        val identity = viewIdentity.identity
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(content = identityItemCore(identity))
            Checkbox(checked = viewIdentity.inParty, onCheckedChange = { checked ->
                if (checked) onInPartyChecked(identity) else onInPartyUnChecked(identity)
            })
        }
    }
}

@Preview
@Composable
private fun PreviewFilterIdentityItem() {
    FilterIdentityItem(viewIdentity = FilterIdentityModel(previewIdentity, true), { }, {})
}