package com.example.qrcode

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Toast
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.example.qrcode.databinding.FragmentQrScannerBinding


class QR_SCANNER : Fragment() {
    private var _binding: FragmentQrScannerBinding? = null
    private val binding get() = _binding!!
lateinit var codeScanner:CodeScanner
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding= FragmentQrScannerBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val activity = requireActivity()
        codeScanner = CodeScanner(activity, binding.scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
                binding.scanTxt.text=it.text
                if(URLUtil.isValidUrl(it.text)){
                    binding.scanTxt.setTextColor(Color.parseColor("#0645AD"))
                    binding.scanTxt.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                    val openURL = Intent(Intent.ACTION_VIEW)
                    openURL.data = Uri.parse(it.text)
                    startActivity(openURL)
                    binding.scanTxt.setOnClickListener {
                        val openURL = Intent(Intent.ACTION_VIEW)
                        openURL.data = Uri.parse(binding.scanTxt.text.toString())
                        startActivity(openURL)
                    }
                }

            }
        }
        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

}