package com.cooling.artifact.me

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.common.theone.interfaces.common.admodel.PreAdConfigs
import com.cooling.artifact.databinding.FragmentMeBinding
import com.cooling.artifact.home.WebViewActivity

/**
 * 我的页
 */
class MeFragment : Fragment() {

    private lateinit var binding: FragmentMeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTitle.text = "我的"
        binding.tvFeedback.setOnClickListener {
            startActivity(
                Intent(
                    requireActivity(),
                    FeedbackActivity::class.java
                )
            )
        }
        binding.tvPrivacyPolicy.setOnClickListener {
            startActivity(
                WebViewActivity.newIntent(
                    requireActivity(),
                    "隐私政策",
                    PreAdConfigs.getInstance().getValue("privacy_policy_url", "")
                )
            )
        }
        binding.tvServiceTerms.setOnClickListener {
            startActivity(
                WebViewActivity.newIntent(
                    requireActivity(),
                    "用户协议",
                    PreAdConfigs.getInstance().getValue("service_terms_url", "")
                )
            )
        }
        binding.tvAboutUs.setOnClickListener {
            startActivity(
                Intent(
                    requireActivity(),
                    AboutUsActivity::class.java
                )
            )
        }
    }
}