package org.elaastic.common.abtesting

import org.togglz.core.Feature

class FeatureResolver {

    fun getFeature(name: String): Feature = Feature { name }

}