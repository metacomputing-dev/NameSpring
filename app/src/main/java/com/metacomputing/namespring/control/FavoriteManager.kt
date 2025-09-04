package com.metacomputing.namespring.control

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asLiveData
import com.metacomputing.namespring.model.viewmodel.Profile
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object FavoriteManager {
    private const val TAG = "FavoriteManager"

    private var isLoaded = false
    val favorites = MutableStateFlow<PersistentList<Profile>>(persistentListOf())
    private val favoriteFlowAsLiveData = favorites.asLiveData()

    fun load(list: List<Profile>) {
        favorites.update {
            it.clear()
            it.addAll(list)
        }
        isLoaded = true
    }

    fun contains(profile: Profile): Boolean {
        return favorites.value.contains(profile)
    }

    fun add(profile: Profile) {
        favorites.update {
            it.add(profile)
        }
    }

    fun remove(profile: Profile) {
        favorites.update {
            it.remove(profile)
        }
    }

    fun observeFavorites(
        lifecycleOwner: LifecycleOwner,
        scope: CoroutineScope = CoroutineScope(Dispatchers.Main),
        onFavoriteUpdated: suspend () -> Unit
    ) {
        favoriteFlowAsLiveData.observe(lifecycleOwner) {
            if (isLoaded) {
                scope.launch {
                    onFavoriteUpdated.invoke()
                }
            }
        }
    }
}