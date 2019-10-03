package kz.nextstep.data.repository

import com.google.firebase.FirebaseException
import com.google.firebase.database.*
import kz.nextstep.data.entity.PinEntity
import kz.nextstep.data.mapper.PinMapper
import kz.nextstep.domain.PinRepository
import kz.nextstep.domain.model.Pin
import kz.nextstep.domain.utils.AppConstants
import rx.Observable
import rx.Subscriber
import rx.subscriptions.Subscriptions
import kotlin.collections.ArrayList

class PinRepositoryImpl(val pinMapper: PinMapper) : PinRepository {

    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child(AppConstants.pinTree)


    override fun deletePin(pinId: String): Observable<Boolean> {
        return Observable.create { subscriber ->
            databaseReference.orderByKey().equalTo(pinId).ref.removeValue().addOnCompleteListener {
                subscriber.onNext(it.isSuccessful)
                subscriber.onCompleted()
            }.addOnFailureListener {
                subscriber.onError(FirebaseException(it.message.toString()))

            }
        }
    }


    override fun addPin(pin: Pin): Observable<Boolean> {
        return Observable.create {
            val pinId = databaseReference.push().key
            if (pinId != null) {
                databaseReference.child(pinId).setValue(pin)
                    .addOnCompleteListener { it1 ->
                        it.onNext(it1.isSuccessful)
                        it.onCompleted()
                    }.addOnFailureListener { it1 ->
                        it.onError(FirebaseException(it1.message.toString()))
                    }
            }
        }
    }

    override fun updatePinData(pinId: String, pin: Pin): Observable<Boolean> {
        return Observable.create { subscriber ->
            databaseReference.child(pinId).setValue(pin).addOnCompleteListener {
                subscriber.onNext(it.isSuccessful)
                subscriber.onCompleted()
            }.addOnFailureListener {
                subscriber.onError(FirebaseException(it.message.toString()))
            }
        }
    }

    override fun getPinList(pinIds: String): Observable<List<Pin>> {
        return Observable.create {
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val pinList: ArrayList<Pin>? = ArrayList()
                    for (ds in dataSnapshot.children) {
                        val pinEntity = ds.getValue(PinEntity::class.java)
                        if (pinIds in ds.key.toString()) {
                            val pin = pinMapper.map(pinEntity!!)
                            pinList?.add(pin)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    it.onError(FirebaseException(databaseError.message))
                }
            }
            databaseReference.addValueEventListener(valueEventListener)

            it.add(Subscriptions.create {
                databaseReference.removeEventListener(valueEventListener)
            })
        }
    }


    override fun getPinById(pinId: String): Observable<Pin> {
        return Observable.create {
            val valueEventListener = object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    it.onError(FirebaseException(databaseError.message))
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (ds in dataSnapshot.children) {
                        if (ds != null) {
                            val pinEntity = ds.getValue(PinEntity::class.java)
                            it.onNext(pinMapper.map(pinEntity!!))
                        }
                    }
                }

            }
            databaseReference.orderByKey().equalTo(pinId).addValueEventListener(valueEventListener)

            it.add(Subscriptions.create {
                databaseReference.removeEventListener(valueEventListener)
            })
        }
    }


}