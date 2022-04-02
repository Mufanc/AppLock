package mufanc.tools.applock.fragment.apps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AppsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is apps Fragment"
    }
    val text: LiveData<String> = _text
}