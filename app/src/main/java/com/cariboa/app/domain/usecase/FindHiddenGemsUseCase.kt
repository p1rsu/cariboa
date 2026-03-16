package com.cariboa.app.domain.usecase

import com.cariboa.app.data.repository.HiddenGemRepository
import com.cariboa.app.domain.model.HiddenGem
import javax.inject.Inject

class FindHiddenGemsUseCase @Inject constructor(
    private val repository: HiddenGemRepository,
) {
    suspend operator fun invoke(destination: String, categories: List<String>? = null): List<HiddenGem> {
        return repository.findHiddenGems(destination, categories)
    }
}
