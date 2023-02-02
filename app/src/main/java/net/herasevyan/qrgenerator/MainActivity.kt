package net.herasevyan.qrgenerator

import android.animation.Animator
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import net.herasevyan.qrgenerator.databinding.ActivityMainBinding
import java.io.FileNotFoundException

class MainActivity : AppCompatActivity() {

    companion object {
        private const val ANIMATION_DURATION = 200L
    }

    private val qrGenerator = QrGenerator(this)

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            hideKeyboardOnTouch(root)
            qrImg.setImageBitmap(
                try {
                    qrGenerator.readLast()
                } catch (_: FileNotFoundException) {
                    qrGenerator.generate("Example")
                }
            )
            generateBtn.setOnClickListener { view ->
                view.scaleDownAndUp()
                qrImg.setQrBitmap(strEt.text.toString())
                strEt.setText("")
            }
        }
    }

    override fun onStop() {
        super.onStop()
        binding.root.hideKeyboard()
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