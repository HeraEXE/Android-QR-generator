package net.herasevyan.qrgenerator

import android.Manifest
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import net.herasevyan.qrgenerator.databinding.DialogExternalStoragePermissionBinding

class ExternalStoragePermissionDialog : DialogFragment(R.layout.dialog_external_storage_permission) {

    private val binding: DialogExternalStoragePermissionBinding by viewBinding(DialogExternalStoragePermissionBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
}