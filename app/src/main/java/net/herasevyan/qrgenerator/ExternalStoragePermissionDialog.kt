package net.herasevyan.qrgenerator

import android.Manifest
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import net.herasevyan.qrgenerator.databinding.DialogExternalStoragePermissionBinding

class ExternalStoragePermissionDialog : DialogFragment(R.layout.dialog_external_storage_permission) {

    private var nullBinding: DialogExternalStoragePermissionBinding? = null
    private val binding: DialogExternalStoragePermissionBinding get() = nullBinding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nullBinding = DialogExternalStoragePermissionBinding.bind(view)
        with(binding) {
            yesBtn.setOnClickListener {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
                dismiss()
            }
            noBtn.setOnClickListener {
                dialog?.dismiss()
            }
        }
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onDestroyView() {
        nullBinding = null
        super.onDestroyView()
    }
}