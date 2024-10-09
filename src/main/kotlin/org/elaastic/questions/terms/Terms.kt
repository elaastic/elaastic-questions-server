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

package org.elaastic.questions.terms

import org.elaastic.common.persistence.AbstractJpaPersistable
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
class Terms(

        @field:NotNull
        var startDate:Date = Date(),

        @field:NotNull
        var isActive:Boolean = true

): AbstractJpaPersistable<Long>()  {

    @Version
    var version:Long? = null

    var endDate:Date? = null

    @OneToMany(mappedBy = "terms", fetch = FetchType.EAGER)
    @MapKey(name = "language")
    @NotNull
    var termsContentsByLanguage:MutableMap<String, TermsContent> = mutableMapOf<String, TermsContent>()

    /**
     * Add terms content to terms
     */
    fun addTermsContent(termsContent: TermsContent) {
        termsContentsByLanguage[termsContent.language] = termsContent
    }

}
