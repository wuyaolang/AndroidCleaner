package com.cooling.artifact.views

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.common.theone.interfaces.common.admodel.PreAdConfigs
import com.cooling.artifact.databinding.DialogStatementBinding
import com.cooling.artifact.home.WebViewActivity
import me.jessyan.autosize.AutoSize

/**
 * 个人信息保护指引弹窗
 */
class StatementDialog : DialogFragment(), View.OnClickListener {
    private lateinit var viewBinding: DialogStatementBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = DialogStatementBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    companion object {
        fun newInstance(): StatementDialog {
            val fragment = StatementDialog()
            fragment.isCancelable = false
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        AutoSize.autoConvertDensity(requireActivity(), 360f, true)
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val stringBuilder = StringBuilder("尊敬的用户欢迎使用本应用！我们非常重视您的个人信息安全和隐私保护。在您使用后本应用之前，请仔细阅读并同意")
        val start1 = stringBuilder.length
        stringBuilder.append("《隐私政策》")
        val end1 = stringBuilder.length
        stringBuilder.append("和")
        val start2 = stringBuilder.length
        stringBuilder.append("《用户协议》")
        val end2 = stringBuilder.length
        stringBuilder.append("，我们将严格按照《网络安全法》、《信息网络传播保护条例》等保护您的个人信息，为您提供更好的服务。")
        val spanString = SpannableString(stringBuilder)
        spanString.setSpan(
            SimpleClickableSpan("《隐私政策》"),
            start1,
            end1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spanString.setSpan(
            SimpleClickableSpan("《用户协议》"),
            start2,
            end2,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        viewBinding.tvContent.append(spanString)
        viewBinding.tvContent.movementMethod = LinkMovementMethod.getInstance()
        viewBinding.tvRefuse.setOnClickListener(this)
        viewBinding.tvAccept.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v === viewBinding.tvRefuse) {
            listener?.onRefuseClick(this)
        } else if (v === viewBinding.tvAccept) {
            listener?.onAcceptClick(this)
        }
    }

    internal inner class SimpleClickableSpan(private val text: String) : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            ds.color = Color.parseColor("#3e74ff")
            ds.isFakeBoldText = true
        }

        override fun onClick(widget: View) {
            if (activity != null && !TextUtils.isEmpty(text)) {
                if (text.contains("隐私政策")) {
                    startActivity(
                        WebViewActivity.newIntent(
                            requireContext(),
                            "隐私政策",
                            PreAdConfigs.getInstance().getValue("privacy_policy_url", "")
                        )
                    )
                } else {
                    startActivity(
                        WebViewActivity.newIntent(
                            requireContext(), "用户协议",
                            PreAdConfigs.getInstance().getValue("service_terms_url", "")
                        )
                    )
                }
            }
        }
    }

    interface OnDialogClickListener {
        fun onRefuseClick(dialog: DialogFragment?)
        fun onAcceptClick(dialog: DialogFragment?)
    }

    private var listener: OnDialogClickListener? = null
    fun setOnClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

}