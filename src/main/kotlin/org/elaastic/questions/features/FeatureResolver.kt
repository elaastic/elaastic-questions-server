package org.elaastic.questions.features

import org.togglz.core.Feature

class FeatureResolver {

    fun getFeature(name: String): Feature = Feature { name }

}