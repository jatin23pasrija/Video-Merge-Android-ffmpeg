package tech.thedevians.vmplayer.Data.Api

import tech.thedevians.vmplayer.Data.Callbacks.FFmCallback

interface ApiService {

    fun getMergedVideoStatus(comm:Array<String>, callback:FFmCallback)
}