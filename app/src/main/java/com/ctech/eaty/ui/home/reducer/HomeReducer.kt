package com.ctech.eaty.ui.home.reducer

import com.ctech.eaty.base.redux.Reducer
import com.ctech.eaty.base.redux.Result
import com.ctech.eaty.ui.home.result.*
import com.ctech.eaty.ui.home.state.HomeState
import com.ctech.eaty.ui.home.viewmodel.HomeItemViewModel
import com.ctech.eaty.ui.home.viewmodel.HorizontalAdsItemViewModel
import com.ctech.eaty.ui.home.viewmodel.SectionViewModel
import com.ctech.eaty.util.DateUtils
import org.joda.time.DateTime
import java.lang.IllegalArgumentException

class HomeReducer : Reducer<HomeState> {

    private val AD_ID = "1966287263602613_1966287926935880"
    private val ADMOB_NATIVE_1 = "ca-app-pub-6007267266282978/2953025043"


    override fun apply(state: HomeState, result: Result): HomeState {
        when (result) {
            is LoadResult -> {
                if (result.loading) {
                    return state.copy(loading = true)
                } else if (result.error != null) {
                    return state.copy(loading = false, loadError = result.error)
                } else {
                    return state.copy(loading = false, loadError = null,
                            content = listOf(SectionViewModel(0, DateUtils.getRelativeTime(result.date))) + result.content)
                }
            }
            is RefreshResult -> {
                if (result.refreshing) {
                    return state.copy(refreshing = true)
                } else if (result.error != null) {
                    return state.copy(refreshing = false, refreshError = result.error)
                } else {
                    return state.copy(refreshing = false, refreshError = null,
                            content = listOf(SectionViewModel(0, DateUtils.getRelativeTime(result.date))) + result.content,
                            dayAgo = 0)
                }
            }
            is LoadMoreResult -> {
                if (result.loading) {
                    return state.copy(loadingMore = true)
                } else if (result.error != null) {

                    return state.copy(loadingMore = false, loadMoreError = result.error)
                } else {
                    return state.copy(loadingMore = false, loadError = null, content = computeNextBody(state, result), dayAgo = result.dayAgo)
                }
            }
            is LoadUserResult -> {
                return state.copy(user = result.content)
            }
            is CheckLoginResult -> {
                return state.copy(user = result.content)
            }
            else -> {
                throw  IllegalArgumentException("Unknown result")
            }
        }
    }

    private fun computeNextBody(state: HomeState, result: LoadMoreResult): List<HomeItemViewModel> {
        val next: List<HomeItemViewModel>
        if (result.dayAgo < 2) {
            next = state.content + //HorizontalAdsItemViewModel(result.dayAgo, AD_ID) +
                    listOf(SectionViewModel(result.dayAgo, DateUtils.getRelativeTime(DateTime.now(), result.date))) + result.content
        }  else {
            next = state.content + listOf(SectionViewModel(result.dayAgo, DateUtils.getRelativeTime(DateTime.now(), result.date))) + result.content
        }
        return next
    }
}