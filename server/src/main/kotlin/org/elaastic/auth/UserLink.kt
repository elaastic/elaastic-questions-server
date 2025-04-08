package org.elaastic.auth

import org.elaastic.common.persistence.AbstractJpaPersistable
import org.elaastic.user.User
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
@Table(name="link_user")
class UserLink(
    @Column(name = "provider_id")
    val providerId: String,

    @Column(name = "provider_user_id")
    val providerUserId: String,

    @OneToOne
    @JoinColumn(name = "elaastic_user_id")
    val user: User,

    @Column(name="created_at")
    val createdAt: LocalDate = LocalDate.now()
): AbstractJpaPersistable<Long>()