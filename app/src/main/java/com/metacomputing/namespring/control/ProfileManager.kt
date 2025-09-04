package com.metacomputing.namespring.control

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.metacomputing.namespring.model.viewmodel.Profile
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

object ProfileManager {
    private const val TAG = "ProfileManager"
    val profiles = MutableStateFlow<PersistentList<Profile>>(persistentListOf())
    val mainProfileId = MutableLiveData<String>()
    var mainProfile: Profile? = null
        get() {
            return try {
                mainProfileId.value?.let { getProfileById(it) }
            } catch (_: NoSuchElementException) {
                null
            }
        }
        set(value) {
            if (value == null || profiles.value.contains(value)) {
                mainProfileId.postValue(value?.id)
                field = value
            } else {
                Log.e(TAG, "Tried to set main profile with invalid profile. skipping.")
            }
        }
    @Volatile var isLoaded = false
        private set
    private val profilesRefresh = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    private val profileFlow = merge(profiles, profilesRefresh)
    private val profileFlowAsLiveData = profileFlow.asLiveData()

    fun loadProfiles(context: Context, list: List<Profile>) {
        var listProfiles = list
        if (list.isEmpty()) {
            Log.i(TAG, "Loaded empty profiles. using mockup data instead")
            listProfiles = getMockupList(context)
        }
        profiles.update {
            it.clear()
            it.addAll(listProfiles)
        }
        if (list.isEmpty()) {
            mainProfile = listProfiles.last()
        }
        Log.i(TAG, "Loaded profiles (${profiles.value.size}) done.")
        isLoaded = true
    }

    fun add(profile: Profile, setAsMain: Boolean = false) {
        profiles.update { it.add(profile) }
        if (setAsMain) mainProfile = profile
    }

    fun remove(profile: Profile) {
        if (profile == mainProfile) {
            mainProfileId.value = ""
        }
        profiles.update { it.remove(profile) }
    }

    fun notifyProfilesUpdated() {
        profilesRefresh.tryEmit(Unit)
    }

    fun contains(profile: Profile): Boolean {
        return profiles.value.contains(profile)
    }

    fun getProfileById(id: String): Profile? {
        return try {
            profiles.value.first { profile -> profile.id == id }
        } catch (e: Exception) {
            null
        }
    }

    fun observeProfiles(
        lifecycleOwner: LifecycleOwner,
        scope: CoroutineScope = CoroutineScope(Dispatchers.Main),
        onProfileUpdated: suspend () -> Unit
    ) {
        profileFlowAsLiveData.observe(lifecycleOwner) {
            if (isLoaded) {
                scope.launch {
                    onProfileUpdated.invoke()
                }
            }
        }
    }

    fun observeProfileSelection(
        lifecycleOwner: LifecycleOwner,
        scope: CoroutineScope = CoroutineScope(Dispatchers.Main),
        onSelectionUpdated: suspend (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            mainProfileId.observe(lifecycleOwner) {
                scope.launch { onSelectionUpdated.invoke(it) }
            }
        }
    }

    // TODO for debugging.
    private fun getMockupList(context: Context): List<Profile> {
        return listOf(
            Profile.new(context,
                "DebugProfile: 최성수",
                birthDate = Calendar.getInstance().apply { set(1986, 4, 19, 5, 45) },
                gender = Profile.Companion.Gender.MALE,
                firstName = "성수",
                firstNameHanja = "成秀",
                familyName = "최",
                familyNameHanja = "崔",
            ),
            Profile.new(context,
            "DebugProfile: 김우현",
                birthDate = Calendar.getInstance().apply { set(1989, 1, 10, 1, 30) },
                gender = Profile.Companion.Gender.MALE,
                firstName = "우현",
                firstNameHanja = "禹鉉",
                familyName = "김",
                familyNameHanja = "金",
            )
        )
    }
}