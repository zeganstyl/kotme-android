package com.kotme.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.kotme.R
import com.kotme.common.prepare
import com.kotme.databinding.CongratulationsBinding

class CongratulationsDialog : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        CongratulationsBinding.inflate(inflater, container, false).also { binding ->
            binding.next.setOnClickListener {
                dialog?.hide()
                findNavController().navigate(R.id.mapFragment)
            }
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepare()
    }
}