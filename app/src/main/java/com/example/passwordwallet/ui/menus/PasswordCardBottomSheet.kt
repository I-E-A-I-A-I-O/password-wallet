package com.example.passwordwallet.ui.menus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.passwordwallet.R
import com.example.passwordwallet.room.entities.Passwords
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PasswordCardBottomSheet(
    private val item: Passwords,
    private val onSelected: (item: Passwords, option: String) -> Unit
    ): BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<LinearLayout>(R.id.menu_copy).setOnClickListener {
            dismissAllowingStateLoss()
            onSelected(item, "copy")
        }
        view.findViewById<LinearLayout>(R.id.menu_edit).setOnClickListener {
            dismissAllowingStateLoss()
            onSelected(item, "edit")
        }
        view.findViewById<LinearLayout>(R.id.menu_delete).setOnClickListener {
            dismissAllowingStateLoss()
            onSelected(item, "delete")
        }
    }
}