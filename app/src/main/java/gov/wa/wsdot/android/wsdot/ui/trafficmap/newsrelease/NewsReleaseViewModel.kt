package gov.wa.wsdot.android.wsdot.ui.trafficmap.newsrelease

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import gov.wa.wsdot.android.wsdot.db.travelerinfo.NewsRelease
import gov.wa.wsdot.android.wsdot.repository.NewsRepository
import gov.wa.wsdot.android.wsdot.model.common.Resource
import javax.inject.Inject

class NewsReleaseViewModel @Inject constructor(newsRepository: NewsRepository) : ViewModel() {

    private val repo = newsRepository

    // mediator handles resubscribe on refresh
    val news = MediatorLiveData<Resource<List<NewsRelease>>>()

    private var newsLiveData : LiveData<Resource<List<NewsRelease>>> = newsRepository.loadNews(false)

    init {
        news.addSource(newsLiveData) { news.value = it }
    }

    fun refresh() {
        news.removeSource(newsLiveData)
        newsLiveData = repo.loadNews(true)
        news.addSource(newsLiveData) { news.value = it }
    }
}