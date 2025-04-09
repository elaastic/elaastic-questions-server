/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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