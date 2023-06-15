package com.pawcare.pawcare.libraries

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.pawcare.pawcare.R

class ErrorDialog(
    val context: Context
) {

    private lateinit var dialog: AlertDialog

    fun startLoading(title : String, text : String, btnText: String?=null, clickListener: (() -> Unit)?=null) {
        val mDialogView = LayoutInflater.from(context).inflate(R.layout.popup_erro, null)
        val mBuilder = AlertDialog.Builder(context)
            .setView(mDialogView)
            .setCancelable(false)

        dialog = mBuilder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDialogView.findViewById<TextView>(R.id.title).text = title
        mDialogView.findViewById<TextView>(R.id.text).text = text

        if (btnText != null) mDialogView.findViewById<AppCompatButton>(R.id.okBtn).text = btnText

        mDialogView.findViewById<View>(R.id.okBtn).setOnClickListener {
            if (clickListener != null) clickListener.invoke()
            dialog.dismiss()
        }

        dialog.show()

    }

    fun isShowing() : Boolean {
        return dialog.isShowing
    }

}