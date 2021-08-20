package com.kotme

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.kotme.databinding.CongratulationsBinding

class CongratulationsDialog : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        CongratulationsBinding.inflate(inflater, container, false).also { binding ->
            binding.next.setOnClickListener {
                hide()
                findNavController().navigate(R.id.mapFragment)
            }
//            window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
//            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }.root
}