package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun saveReminder_getById() = runBlockingTest {
        val reminder = ReminderDTO("Title", "Description", "Location", 0.0, 0.0, "id")
        database.reminderDao().saveReminder(reminder)

        val loaded = database.reminderDao().getReminderById(reminder.id)

        assertThat<ReminderDTO>(loaded as ReminderDTO, notNullValue())
        assertThat(loaded.id, `is`(reminder.id))
        assertThat(loaded.title, `is`(reminder.title))
        assertThat(loaded.description, `is`(reminder.description))
        assertThat(loaded.location, `is`(reminder.location))
        assertThat(loaded.latitude, `is`(reminder.latitude))
        assertThat(loaded.longitude, `is`(reminder.longitude))
    }

    @Test
    fun saveReminder_getAll() = runBlockingTest {
        val reminder = ReminderDTO("Title", "Description", "Location", 0.0, 0.0, "id")
        database.reminderDao().saveReminder(reminder)

        val loaded = database.reminderDao().getReminders()

        assertThat(loaded.isNotEmpty(), `is`(true))
    }

    @Test
    fun deleteAllReminders_returnEmptyList() = runBlockingTest {
        val reminder = ReminderDTO("Title", "Description", "Location", 0.0, 0.0, "id")
        database.reminderDao().saveReminder(reminder)

        val loaded = database.reminderDao().getReminders()

        assertThat(loaded.isNotEmpty(), `is`(true))

        database.reminderDao().deleteAllReminders()
        val loadedAfterDelete = database.reminderDao().getReminders()

        assertThat(loadedAfterDelete.isEmpty(), `is`(true))
    }

}