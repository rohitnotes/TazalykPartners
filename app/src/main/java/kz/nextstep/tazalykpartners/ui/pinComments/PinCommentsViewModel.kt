package kz.nextstep.tazalykpartners.ui.pinComments

import android.widget.Toast
import kz.nextstep.domain.model.Requests
import kz.nextstep.domain.usecase.request.GetRequestsByPinIdUseCase
import kz.nextstep.domain.utils.AppConstants
import kz.nextstep.tazalykpartners.MainApplication
import kz.nextstep.tazalykpartners.base.BaseViewModel
import kz.nextstep.tazalykpartners.ui.pinDetailedInfo.PinCommentsAdapter
import rx.Subscriber
import javax.inject.Inject

class PinCommentsViewModel : BaseViewModel() {

    @Inject
    lateinit var getRequestsByPinIdUseCase: GetRequestsByPinIdUseCase

    val pinCommentsAdapter = PinCommentsAdapter()

    fun getComments(pinId: String) {
        getRequestsByPinIdUseCase.execute(object : Subscriber<HashMap<String, Requests>>() {
            override fun onNext(t: HashMap<String, Requests>?) {
                var pinCommentsList: MutableList<Requests> = ArrayList()
                if (!t.isNullOrEmpty()) {
                    for (key in t.keys) {
                        if (t[key] != null)
                            pinCommentsList.add(t[key]!!)
                    }
                    if (!pinCommentsList.isNullOrEmpty())
                        pinCommentsAdapter.updatePinCommentsList(pinCommentsList)
                }
            }

            override fun onCompleted() {}

            override fun onError(e: Throwable?) {
                Toast.makeText(MainApplication.INSTANCE?.applicationContext, e?.message, Toast.LENGTH_LONG).show()
            }

        }, pinId, AppConstants.emptyParam)
    }
}