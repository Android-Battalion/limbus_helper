package ua.blackwind.limbushelper.domain.filter

import ua.blackwind.limbushelper.domain.common.DamageType
import ua.blackwind.limbushelper.domain.common.Effect
import ua.blackwind.limbushelper.domain.common.IdentityDamageResistType
import ua.blackwind.limbushelper.domain.common.Sin
import ua.blackwind.limbushelper.domain.sinner.ISinnerRepository
import ua.blackwind.limbushelper.domain.sinner.model.Identity
import ua.blackwind.limbushelper.domain.sinner.model.Skill
import javax.inject.Inject

class GetFilteredIdentitiesUseCase @Inject constructor(private val repository: ISinnerRepository) {

    suspend operator fun invoke(filter: IdentityFilter): List<Identity> {
        val identities = repository.getAllIdentities()

        if (filter.isEmpty()) return identities

        val sinnerIsEmpty = filter.sinners.isEmpty()
        val resistIsEmpty = filter.resist.isEmpty()
        val skillIsEmpty = filter.skills.isEmpty()
        val effectIsEmpty = filter.effects.isEmpty()

        return identities.filter { identity ->
            val bySinner = {
                sinnerIsEmpty || identityPassSinnerFilter(identity, filter.sinners)
            }
            val byEffect = {
                effectIsEmpty || identityPassEffectsFilter(identity, filter.effects)
            }
            val byResist = {
                resistIsEmpty || identityPassResistanceFilter(identity, filter.resist)
            }
            val bySkill = {
                skillIsEmpty || identityPassSkillFilter(identity, filter.skills)
            }

            bySinner() && byEffect() && byResist() && bySkill()
        }
    }

    private fun identityPassSinnerFilter(identity: Identity, filter: List<Int>): Boolean {
        return filter.any { it == identity.sinnerId }
    }

    private fun identityPassSkillFilter(
        identity: Identity,
        filter: IdentityFilterSkillsSetArg
    ): Boolean {
        val identitySkills =
            listOf(identity.firstSkill, identity.secondSkill, identity.thirdSkill).toMutableList()
        val filterSkills = filter.toSkillList().toMutableList()
        filterSkills.toList().forEach { skillFilter ->
            if (skillFilter.isStrict()) {
                val correspondingSkill =
                    identitySkills.find {
                        it.dmgType == skillFilter.damageType.toDamageType()
                                && it.sin == skillFilter.sin.toSin()
                    }
                        ?: return false
                filterSkills.remove(skillFilter)
                identitySkills.remove(correspondingSkill)
            }
        }
        val skillsAfterStrictFilter = identitySkills.toList()
        val looseDamageFilter = filterSkills.map { it.damageType.toDamageType() }.toList()
        val looseSinFilter = filterSkills.map { it.sin.toSin() }.toList()
        return checkDamageImprint(
            skillsAfterStrictFilter,
            looseDamageFilter
        ) && checkSinImprint(skillsAfterStrictFilter, looseSinFilter)
    }

    /**
     * Performs loose filtering by skill damage
     */
    private fun checkDamageImprint(
        skills: List<Skill>,
        filterImprint: List<DamageType?>
    ): Boolean {
        if (filterImprint.isEmpty()) return true
        val skillImprint = skills.map { it.dmgType }.toMutableList()
        filterImprint.forEach { filter ->
            if (filter != null && skillImprint.none { it == filter }) {
                return false
            }
            skillImprint.remove(filter)
        }
        return true
    }

    /**
     * Performs loose filtering by skill sin
     */
    private fun checkSinImprint(
        sins: List<Skill>,
        filterImprint: List<Sin?>
    ): Boolean {
        if (filterImprint.isEmpty()) return true
        val sinImprint = sins.map { it.sin }.toMutableList()
        filterImprint.forEach { filter ->
            if (filter != null && sinImprint.none { it == filter }) {
                return false
            }
            sinImprint.remove(filter)
        }
        return true
    }

    private fun identityPassResistanceFilter(
        identity: Identity,
        filter: FilterResistSetArg
    ): Boolean {

        val ineffective = checkIdentityResistance(
            identity,
            IdentityDamageResistType.INEFF,
            filter.ineffective
        )
        val normal = checkIdentityResistance(
            identity,
            IdentityDamageResistType.NORMAL,
            filter.normal
        )
        val fatal = checkIdentityResistance(
            identity,
            IdentityDamageResistType.FATAL,
            filter.fatal
        )
        return ineffective && normal && fatal

    }

    private fun checkIdentityResistance(
        identity: Identity,
        resistanceType: IdentityDamageResistType,
        damageType: FilterDamageTypeArg
    ): Boolean {
        if (damageType is FilterDamageTypeArg.Empty) return true
        return when ((damageType as FilterDamageTypeArg.Type).type) {
            DamageType.SLASH -> identity.slashRes == resistanceType
            DamageType.PIERCE -> identity.pierceRes == resistanceType
            DamageType.BLUNT -> identity.bluntRes == resistanceType
        }
    }

    private fun identityPassEffectsFilter(identity: Identity, filter: List<Effect>): Boolean {
        return listOf(
            identity.firstSkill.effects,
            identity.secondSkill.effects,
            identity.thirdSkill.effects
        ).flatten().containsAll(filter)
    }
}