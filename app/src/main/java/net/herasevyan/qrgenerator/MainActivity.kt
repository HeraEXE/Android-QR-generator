package net.herasevyan.qrgenerator

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.animation.Animator
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.children
import net.herasevyan.qrgenerator.databinding.ActivityMainBinding
import java.io.FileNotFoundException

class MainActivity : AppCompatActivity() {

    companion object {
        private const val ANIMATION_DURATION = 200L

        private const val EXTERNAL_STORAGE_PERMISSION_DENIED_TIMES = "external_storage_permission_denied_times"
    }

    private val qrGenerator = QrGenerator(this)
    private val imageStorage = ImageStorage()
    private lateinit var sharedPrefs: SharedPrefs

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPrefs = SharedPrefs(getSharedPreferences("prefs", MODE_PRIVATE))

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            hideKeyboardOnTouch(root)
            try {
                qrImg.setImageBitmap(qrGenerator.readLast())
                saveBtn.isEnabled = sharedPrefs.getInt(EXTERNAL_STORAGE_PERMISSION_DENIED_TIMES, 0) < 2
            } catch (_: FileNotFoundException) {
                qrImg.setImageBitmap(qrGenerator.generate(""))
                saveBtn.isEnabled = false
            }
            generateBtn.setOnClickListener { view ->
                view.scaleDownAndUp()
                val text = strEt.text.toString()
                saveBtn.isEnabled = text.isNotEmpty() && sharedPrefs.getInt(EXTERNAL_STORAGE_PERMISSION_DENIED_TIMES, 0) < 2
                qrImg.setQrBitmap(text)
                strEt.setText("")
            }

            saveBtn.setOnClickListener { view ->
                view.scaleDownAndUp()
                when {
                    ContextCompat.checkSelfPermission(this@MainActivity, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> saveImage()
                    shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE) -> ExternalStoragePermissionDialog().show(supportFragmentManager, null)
                    else -> ActivityCompat.requestPermissions(this@MainActivity, arrayOf(WRITE_EXTERNAL_STORAGE), 0)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            0 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) saveImage()
                else {
                    val permissionDeniedTimes = sharedPrefs.getInt(EXTERNAL_STORAGE_PERMISSION_DENIED_TIMES, 0) + 1
                    sharedPrefs.save(EXTERNAL_STORAGE_PERMISSION_DENIED_TIMES, permissionDeniedTimes)
                    binding.saveBtn.isEnabled = permissionDeniedTimes < 2
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val hadDenied = (sharedPrefs.getInt(EXTERNAL_STORAGE_PERMISSION_DENIED_TIMES, 0) > 0)
            sharedPrefs.save(EXTERNAL_STORAGE_PERMISSION_DENIED_TIMES, if (hadDenied) 1 else 0)
            binding.saveBtn.isEnabled = binding.strEt.text.toString().isNotEmpty()
        }
    }

    override fun onStop() {
        super.onStop()
        binding.root.hideKeyboard()
    }

    private fun saveImage() {
        binding.saveBtn.isEnabled = false
        imageStorage.save(applicationContext, qrGenerator.readLast())
        Toast.makeText(this, "Image was saved", Toast.LENGTH_SHORT).show()
    }

    private fun View.hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun hideKeyboardOnTouch(viewGroup: ViewGroup) {

        val hideOnTouch: (View, MotionEvent) -> Boolean = { v, _ ->
            v.hideKeyboard()
            false
        }
        viewGroup.setOnTouchListener(hideOnTouch)
        for (view in viewGroup.children) {
            if (view is EditText) continue
            if (view is ViewGroup) {
                hideKeyboardOnTouch(view)
                continue
            }
            view.setOnTouchListener(hideOnTouch)
        }
    }

    private fun View.scaleDownAndUp() {

        fun View.scaleUp() {
            animate()
                .setDuration(ANIMATION_DURATION)
                .scaleY(0.9f)
                .scaleX(0.9f)
                .scaleYBy(0.1f)
                .scaleXBy(0.1f)
                .setListener(
                    object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) = Unit
                        override fun onAnimationEnd(animation: Animator) = Unit
                        override fun onAnimationCancel(animation: Animator) = Unit
                        override fun onAnimationRepeat(animation: Animator) = Unit
                    }
                )
        }

        animate()
            .setDuration(ANIMATION_DURATION)
            .scaleYBy(1f)
            .scaleXBy(1f)
            .scaleY(0.9f)
            .scaleX(0.9f)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) = Unit
                override fun onAnimationEnd(animation: Animator) = scaleUp()
                override fun onAnimationCancel(animation: Animator) = Unit
                override fun onAnimationRepeat(animation: Animator) = Unit
            })
    }

    private fun ImageView.setQrBitmap(str: String) {

        fun ImageView.scaleUpFadeInQr() {
            setImageBitmap(qrGenerator.generate(str))
            animate()
                .setDuration(ANIMATION_DURATION)
                .alphaBy(0f)
                .alpha(1f)
                .scaleY(0f)
                .scaleX(0f)
                .scaleYBy(1f)
                .scaleXBy(1f)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) = Unit
                    override fun onAnimationEnd(animation: Animator) = Unit
                    override fun onAnimationCancel(animation: Animator) = Unit
                    override fun onAnimationRepeat(animation: Animator) = Unit
                })
        }

        animate()
            .setDuration(ANIMATION_DURATION)
            .alphaBy(1f)
            .alpha(0f)
            .scaleYBy(1f)
            .scaleXBy(1f)
            .scaleY(0f)
            .scaleX(0f)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) = Unit
                override fun onAnimationEnd(animation: Animator) = scaleUpFadeInQr()
                override fun onAnimationCancel(animation: Animator) = Unit
                override fun onAnimationRepeat(animation: Animator) = Unit
            })
    }
}