package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var localRepository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        localRepository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @After
    fun cleanUp() = database.close()

    @Test
    fun saveReminder_retrievesReminderById() = runBlocking {
        val reminder = ReminderDTO("Title", "Description", "Location", 0.0, 0.0, "id")
        localRepository.saveReminder(reminder)

        val loaded = localRepository.getReminder(reminder.id)

        assertThat(loaded, `is`(notNullValue()))
        loaded as Result.Success
        assertThat(loaded.data.id, `is`(reminder.id))
        assertThat(loaded.data.title, `is`(reminder.title))
        assertThat(loaded.data.description, `is`(reminder.description))
        assertThat(loaded.data.location, `is`(reminder.location))
        assertThat(loaded.data.latitude, `is`(reminder.latitude))
        assertThat(loaded.data.longitude, `is`(reminder.longitude))
    }

    @Test
    fun deleteAllReminders_retrieveEmptyList() = runBlocking {
        val reminder = ReminderDTO("Title", "Description", "Location", 0.0, 0.0, "id")
        localRepository.saveReminder(reminder)

        val loaded = localRepository.getReminders()

        loaded as Result.Success
        assertThat(loaded.data.isNotEmpty(), `is`(true))

        localRepository.deleteAllReminders()
        val loadedAfterDelete = localRepository.getReminders()
        loadedAfterDelete as Result.Success

        assertThat(loadedAfterDelete.data.isEmpty(), `is`(true))
    }

}