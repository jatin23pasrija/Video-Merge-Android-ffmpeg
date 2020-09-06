package tech.thedevians.vmplayer.Data.Api

import com.arthenica.mobileffmpeg.FFmpeg
import tech.thedevians.vmplayer.Data.Callbacks.FFmCallback
import tech.thedevians.vmplayer.Utils.doAsync

class ApiServiceImplementor : ApiService {
    override fun getMergedVideoStatus(comm: Array<String>, callback: FFmCallback) {
        doAsync {
            val res = FFmpeg.execute(comm)
            callback.onResponse(res)
        }.execute()
    }
}