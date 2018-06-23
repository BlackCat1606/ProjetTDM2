package tech.salroid.filmy

import android.app.Application
import android.content.Context

import com.crashlytics.android.Crashlytics

import io.fabric.sdk.android.Fabric

/*
 * Filmy Application for Android
 * Copyright (c) 2016 Sajal Gupta (http://github.com/salroid).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class FilmyApplication : Application() {

    override fun onCreate() {

        super.onCreate()

        Fabric.with(this, Crashlytics())

        instance = this
    }

    companion object {

        var instance: FilmyApplication? = null
            private set

        val context: Context
            get() = instance!!.applicationContext
    }
}
