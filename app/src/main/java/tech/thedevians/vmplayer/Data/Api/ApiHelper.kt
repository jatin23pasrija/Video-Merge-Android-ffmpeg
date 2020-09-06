package tech.thedevians.vmplayer.Data.Api

import tech.thedevians.vmplayer.Data.Callbacks.FFmCallback

class ApiHelper(private val apiService: ApiService) {

    fun getMergedVideo(com: Array<String>, callback:FFmCallback) = apiService.getMergedVideoStatus(com,callback)
}