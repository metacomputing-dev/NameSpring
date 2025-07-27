package com.metacomputing.namespring.control

import android.content.Context
import android.util.Log
import androidx.annotation.MainThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object TaskManager {
    private const val TAG = "TaskManager"
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val coroutineMainScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    @MainThread
    fun <T> launch(
        context: Context,
        taskName: String = "Anonymous",
        scope: CoroutineScope = coroutineScope,
        vararg inputs: Any = arrayOf(Unit),
        block: suspend (Array<out Any>) -> T,
        onSuccess: (T) -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ): Job {
        // TODO make a Common UI for loading
        return scope.launch {
            try {
                Log.i(TAG, "launch task $taskName")
                val result = block(inputs)
                withContext(Dispatchers.Main) {
                    Log.i(TAG, "finished task $taskName")
                    onSuccess(result)
                }
            } catch (e: Throwable) {
                withContext(Dispatchers.Main) {
                    Log.e(TAG, "Exception occurred on TaskManager: ${e.message}")
                    onError(e)
                }
            }
        }
    }

    @MainThread
    fun <T> launchOnMain(
        context: Context,
        taskName: String = "Anonymous",
        vararg inputs: Any = arrayOf(Unit),
        block: suspend (Array<out Any>) -> T,
        onSuccess: (T) -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ): Job {
        return launch(context, taskName, coroutineMainScope,
            inputs = inputs,
            block, onSuccess, onError)
    }

    fun cancel() {
        coroutineScope.coroutineContext.cancelChildren()
    }
}