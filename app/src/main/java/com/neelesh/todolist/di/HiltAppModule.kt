package com.neelesh.todolist.di

import android.content.Context
import androidx.room.Room
import com.google.android.gms.auth.api.identity.Identity
import com.neelesh.todolist.db.ToDoDao
import com.neelesh.todolist.db.ToDoDatabase
import com.neelesh.todolist.user.GoogleAuthUIClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HiltAppModule {

    @Provides
    @Singleton
    fun provideGoogleAuthUIClient(
        @ApplicationContext context: Context,
    ): GoogleAuthUIClient {
        val signInClient = Identity.getSignInClient(context)
        return GoogleAuthUIClient(context, signInClient)
    }

    @Provides
    @Singleton
    fun provideToDoDao(
        @ApplicationContext context: Context,
    ): ToDoDao {
        val database = Room.databaseBuilder(
            context,
            ToDoDatabase::class.java,
            "todo_database"
        ).build()
        return database.todoDao()
    }

}
