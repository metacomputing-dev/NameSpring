package com.metacomputing.namespring.control

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.metacomputing.namespring.model.viewmodel.Profile
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
                mainProfileId.value = value?.id
                field = value
            } else {
                Log.e(TAG, "Tried to set main profile with invalid profile. skipping.")
            }
        }
    @Volatile var isLoaded = false
        private set

    fun load(list: List<Profile>) {
        profiles.value = profiles.value.clear()
        profiles.value = profiles.value.addAll(list)
        Log.i(TAG, "Loading profiles ${profiles.value.size}")
        isLoaded = true
    }

    fun add(profile: Profile, setAsMain: Boolean = false) {
        profiles.update { it.add(profile) }
        if (setAsMain) mainProfile = profile
    }

    private fun getProfileById(id: String): Profile? {
        return profiles.value.first { profile -> profile.id == id }
    }

    fun remove(profile: Profile) {
        if (profile == mainProfile) {
            mainProfileId.value = null
        }
        profiles.update { it.remove(profile) }
    }

    fun observeProfiles(scope: CoroutineScope = CoroutineScope(Dispatchers.Main), onProfileUpdated: () -> Unit) {
        runBlocking {
            profiles.dropWhile { !isLoaded }.collect { list ->
                scope.launch { onProfileUpdated.invoke() }
            }
        }
    }

    // TODO for debugging.
    fun putMockup(context: Context) {
        if (profiles.value.isEmpty()) {
            Log.i(TAG, "Profile data is empty. applying mockup data")
            profiles.value.apply {
                add(Profile
                    .new(context,
                        "DebugProfile: 최성수",
                        birthDate = Calendar.getInstance().apply { set(1986, 4, 19, 5, 45) },
                        gender = Profile.Companion.Gender.MALE,
                        firstName = "성수",
                        firstNameHanja = "成秀",
                        familyName = "최",
                        familyNameHanja = "崔",
                    )
                )
                add(Profile
                    .new(context,
                        "DebugProfile: 김우현",
                        birthDate = Calendar.getInstance().apply { set(1989, 1, 10, 1, 30) },
                        gender = Profile.Companion.Gender.MALE,
                        firstName = "우현",
                        firstNameHanja = "禹鉉",
                        familyName = "김",
                        familyNameHanja = "金",
                    ), true
                )
            }
            Log.i(TAG, "Loaded Mockup data done.")
            isLoaded = true
        }
    }
}