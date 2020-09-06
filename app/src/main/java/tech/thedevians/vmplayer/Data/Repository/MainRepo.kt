package tech.thedevians.vmplayer.Data.Repository

import tech.thedevians.vmplayer.Data.Api.ApiHelper
import tech.thedevians.vmplayer.Data.Callbacks.FFmCallback

class MainRepo(private val apiHelper: ApiHelper) {


    fun getMergedVideoStatus(com: Array<String>, callback: FFmCallback) {
        return apiHelper.getMergedVideo(com, callback)
    }
}