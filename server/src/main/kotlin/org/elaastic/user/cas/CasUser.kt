package org.elaastic.user.cas

import org.elaastic.user.User
import org.elaastic.common.persistence.AbstractJpaPersistable
import java.time.LocalDate
import javax.persistence.*

@Entity
@NamedEntityGraph(
    name = "CasUser.user.roles",
    attributeNodes = [NamedAttributeNode(value= "user", subgraph = "User.roles")],
    subgraphs = [
        NamedSubgraph(name = "User.roles", attributeNodes = [NamedAttributeNode("roles")])
    ]
)
class CasUser(
    @field:Column(name = "cas_key")
    val casKey: String,

    @field:Column(name = "cas_user_id")
    val casUserId: String,

    @field:OneToOne
    @JoinColumn(name = "elaastic_user_id")
    val user: User,

    @field:Column(name="created_at")
    val createdAt: LocalDate = LocalDate.now()
): AbstractJpaPersistable<Long>()