package com.example.largefiledownloadexample.ui.download_manager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.largefiledownloadexample.R
import com.example.largefiledownloadexample.utils.showMessage
import kotlinx.android.synthetic.main.download_manager_fragment.*

class DownloadManagerFragment : Fragment() {

    private lateinit var viewModel: DownloadManagerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.download_manager_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DownloadManagerViewModel::class.java)
        downloadButton.setOnClickListener {
            if (checkUrlNotEmpty()) return@setOnClickListener
            viewModel.startDownloadManager(urlInput.text.toString())
            showMessage(context!!, getString(R.string.download_started))
        }
    }



    private fun checkUrlNotEmpty() = urlInput.text.isEmpty()

}