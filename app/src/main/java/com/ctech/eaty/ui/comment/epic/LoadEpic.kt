package com.ctech.eaty.ui.comment.epic

import com.ctech.eaty.base.redux.Action
import com.ctech.eaty.base.redux.Epic
import com.ctech.eaty.repository.CommentRepository
import com.ctech.eaty.ui.comment.action.CommentAction
import com.ctech.eaty.ui.comment.action.BarCodeGenerator
import com.ctech.eaty.ui.comment.result.LoadResult
import com.ctech.eaty.ui.comment.state.CommentState
import com.ctech.eaty.util.rx.ThreadScheduler
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class LoadEpic(val commentRepository: CommentRepository,
               val barCodeGenerator: BarCodeGenerator,
               val threadScheduler: ThreadScheduler) : Epic<CommentState> {
    override fun apply(action: PublishSubject<Action>, state: BehaviorSubject<CommentState>): Observable<LoadResult> {
        return action.ofType(CommentAction.Load::class.java)
                .filter {
                    state.value.content.isEmpty()
                }
                .flatMap {
                    commentRepository.getComments(barCodeGenerator.currentBarCode(it.id))
                            .map {
                                LoadResult.success(it)
                            }
                            .onErrorReturn {
                                LoadResult.fail(it)
                            }
                            .subscribeOn(threadScheduler.workerThread())
                            .startWith(LoadResult.inProgress())
                }
    }
}