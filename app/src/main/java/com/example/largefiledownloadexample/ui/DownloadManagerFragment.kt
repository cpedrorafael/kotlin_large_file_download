package com.example.largefiledownloadexample.ui

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.largefiledownloadexample.R
import kotlinx.android.synthetic.main.download_manager_fragment.*

class DownloadManagerFragment : Fragment() {

    companion object {
        fun newInstance() = DownloadManagerFragment()
    }

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
            if (urlInput.text.isEmpty()) return@setOnClickListener
            viewModel.startDownloadManager(urlInput.text.toString())
            Toast.makeText(context, getString(R.string.download_started), Toast.LENGTH_LONG)
                .show()
        }
    }

}