package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainTestCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.O_MR1])
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var remindersListViewModel: RemindersListViewModel

    private lateinit var remindersRepository: FakeDataSource

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainTestCoroutineRule()

    @ExperimentalCoroutinesApi
    @Before
    fun setupViewModel() = runBlockingTest {
        // We initialise the tasks to 3, with one active and two completed
        remindersRepository = FakeDataSource()
        val reminder1 = ReminderDTO("Title", "Description", "Location", 0.0, 0.0)
        val reminder2 = ReminderDTO("Title", "Description", "Location", 0.0, 0.0)
        val reminder3 = ReminderDTO("Title", "Description", "Location", 0.0, 0.0)
        remindersRepository.saveReminder(reminder1)
        remindersRepository.saveReminder(reminder2)
        remindersRepository.saveReminder(reminder3)

        remindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext() ,remindersRepository)
    }

    @Test
    fun loadReminder_setsNewReminderEvent() {
        remindersListViewModel.loadReminders()

        val value = remindersListViewModel.remindersList.getOrAwaitValue()

        assertThat(value, not(nullValue()))
    }

}