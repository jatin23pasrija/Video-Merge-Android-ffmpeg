package tech.thedevians.vmplayer.Ui.Main.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tech.thedevians.vmplayer.Data.Callbacks.FFmCallback
import tech.thedevians.vmplayer.Data.Repository.MainRepo
import tech.thedevians.vmplayer.Utils.Resource

class MainViewModel(private val mainRepository: MainRepo) : ViewModel() {

    private val status = MutableLiveData<Resource<Int>>()

    private fun getStatusMergedVideo(com: Array<String>, callback: FFmCallback) {
        status.postValue(Resource.loading(null))
        mainRepository.getMergedVideoStatus(com, callback)
//        when (mainRepository.getMergedVideoStatus(com, callback)) {
////
////            RETURN_CODE_SUCCESS -> {
////                status.postValue(Resource.success(RETURN_CODE_SUCCESS))
////            }
////            RETURN_CODE_CANCEL -> {
////                status.postValue(Resource.error("Cancelled", null))
////            }
////            else -> {
////                Config.printLastCommandOutput(Log.INFO)
////                status.postValue(Resource.error("Error", null))
////            }
//        }
    }

    fun getMergedStatus(com: Array<String>, callback: FFmCallback): LiveData<Resource<Int>> {
        getStatusMergedVideo(com, callback)
        return status
    }
}