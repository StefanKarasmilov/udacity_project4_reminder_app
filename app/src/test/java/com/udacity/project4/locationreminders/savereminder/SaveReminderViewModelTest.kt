package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainTestCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import junit.framework.Assert.assertEquals

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.O_MR1])
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    private lateinit var saveRemindersViewModel: SaveReminderViewModel

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
        saveRemindersViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext() ,remindersRepository)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun clearAllLiveData_returnNull() {
        saveRemindersViewModel.onClear()

        val title = saveRemindersViewModel.reminderTitle.getOrAwaitValue()
        val description = saveRemindersViewModel.reminderDescription.getOrAwaitValue()
        val selectedLocationStr = saveRemindersViewModel.reminderSelectedLocationStr.getOrAwaitValue()
        val selectedPOI = saveRemindersViewModel.selectedPOI.getOrAwaitValue()
        val latitude = saveRemindersViewModel.latitude.getOrAwaitValue()
        val longitude = saveRemindersViewModel.longitude.getOrAwaitValue()

        assertThat(title, `is`(nullValue()))
        assertThat(description, `is`(nullValue()))
        assertThat(selectedLocationStr, `is`(nullValue()))
        assertThat(selectedPOI, `is`(nullValue()))
        assertThat(latitude, `is`(nullValue()))
        assertThat(longitude, `is`(nullValue()))
    }

    @Test
    fun saveReminder_dataLoadingToastAndNavigationUpdated() = runBlockingTest {
        val reminder = ReminderDataItem("Title", "Description", "Location", 0.0, 0.0, "id1")
        val reminderDTO = ReminderDTO(
            reminder.title,
            reminder.description,
            reminder.location,
            reminder.latitude,
            reminder.longitude,
            reminder.id
        )

        saveRemindersViewModel.saveReminder(reminder)
        val showLoading = saveRemindersViewModel.showLoading.getOrAwaitValue()
        val showToast = saveRemindersViewModel.showToast.getOrAwaitValue()
        val navigationCommand = saveRemindersViewModel.navigationCommand.getOrAwaitValue()
        val expectedReminder = remindersRepository.getReminder("id1")

        assertEquals(expectedReminder, Result.Success(reminderDTO))
        assertThat(showLoading, `is`(false))
        assertThat(showToast, `is`(notNullValue()))
        assertThat(navigationCommand, `is`(notNullValue()))
    }

    @Test
    fun validateEnteredData_returnTrue() {
        val reminder = ReminderDataItem("Title", "Description", "Location", 0.0, 0.0, "id1")

        val isValid = saveRemindersViewModel.validateEnteredData(reminder)

        assertThat(isValid, `is`(true))
    }

    @Test
    fun validateEnteredData_returnFalse() {
        val reminder = ReminderDataItem("", "Description", "Location", 0.0, 0.0, "id1")

        val isValid = saveRemindersViewModel.validateEnteredData(reminder)

        assertThat(isValid, `is`(false))
    }

}