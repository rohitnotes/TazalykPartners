package kz.nextstep.domain.usecase.pin

import io.reactivex.annotations.NonNull
import kz.nextstep.domain.repository.PinRepository
import kz.nextstep.domain.model.Pin
import kz.nextstep.domain.usecase.BaseUseCase
import rx.Observable
import rx.Scheduler

class UpdatePinDataUseCase(val pinRepository: PinRepository,
                           @NonNull mainScheduler: Scheduler,
                           @NonNull ioScheduler: Scheduler
): BaseUseCase<Boolean, String, Pin>(mainScheduler, ioScheduler) {
    override fun buildUseCaseObservable(params: String, param2: Pin): Observable<Boolean> {
        return pinRepository.updatePinData(params, param2)
    }
}