package ua.blackwind.limbushelper.domain.sinner.model

import ua.blackwind.limbushelper.domain.common.DamageType
import ua.blackwind.limbushelper.domain.common.Effect
import ua.blackwind.limbushelper.domain.common.Sin

data class EgoSkill(
    val id: Int,
    val name: String,
    val dmgType: DamageType,
    val sin: Sin,
    val dmg: Int,
    val baseDie: Int,
    val coinBonus: Int,
    val coinCount: Int,
    val sanityCost: Int,
    val effects: List<Effect>
)
