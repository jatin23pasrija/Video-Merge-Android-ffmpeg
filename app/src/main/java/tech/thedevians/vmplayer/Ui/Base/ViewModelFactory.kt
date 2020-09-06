package tech.thedevians.vmplayer.Ui.Base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tech.thedevians.vmplayer.Data.Api.ApiHelper
import tech.thedevians.vmplayer.Data.Repository.MainRepo
import tech.thedevians.vmplayer.Ui.Main.ViewModel.MainViewModel

class ViewModelFactory (private val apiHelper: ApiHelper):ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(MainRepo(apiHelper)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}