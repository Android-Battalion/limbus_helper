package ua.blackwind.limbushelper.ui.screens.party_building_screen.model

import ua.blackwind.limbushelper.domain.party.model.PartyIdentity
import ua.blackwind.limbushelper.domain.sinner.model.Ego
import ua.blackwind.limbushelper.domain.sinner.model.Sinner

data class PartySinnerModel(
    val sinner: Sinner,
    val identities: List<PartyIdentity>,
    val egos: List<Ego>
)
