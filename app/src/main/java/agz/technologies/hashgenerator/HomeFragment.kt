package agz.technologies.hashgenerator

import agz.technologies.hashgenerator.databinding.FragmentHomeBinding
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private val homeViewModel : HomeViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)
        val hash_algorithms = resources.getStringArray(R.array.hash_algorithms)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.drop_down_item, hash_algorithms)
        binding.autocompleteTextView.setAdapter(arrayAdapter)

        binding.generateButton.setOnClickListener {
            onGenerateClicked()
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
    }


    private suspend fun applyAnimations() {
        binding.generateButton.isClickable = false
        binding.titleTextView.animate().alpha(0f).duration = 400L
        binding.generateButton.animate().alpha(0f).duration = 400L
        binding.textInputLayout.animate().alpha(0f).translationXBy(1200f).duration = 400L
        binding.plainText.animate().alpha(0f).translationXBy(-1200f).duration = 400L

        delay(300)

        binding.successBackground.animate().alpha(1f).duration = 600L
        binding.successBackground.animate().rotationBy(720f).duration = 600L
        binding.successBackground.animate().scaleXBy(900f).duration = 800L
        binding.successBackground.animate().scaleYBy(900f).duration = 800L

        delay(500)

        binding.successImageView.animate().alpha(1f).duration = 1000L

        delay(1500L)
    }

    private fun navigateToSuccess(hash: String) {
        val directions = HomeFragmentDirections.actionHomeFragmentToSuccessFragment(hash)
        findNavController().navigate(directions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // we avoid memory leaks
    }

    private fun getHashData() : String{
        val algorithm = binding.autocompleteTextView.text.toString()
        val plainText = binding.plainText.text.toString()
        return homeViewModel.getHash(plainText, algorithm)
    }

    override fun onResume() {
        super.onResume()
        val hashAlgorithms = resources.getStringArray(R.array.hash_algorithms)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.drop_down_item, hashAlgorithms)
        binding.autocompleteTextView.setAdapter(arrayAdapter)
    }

    private fun onGenerateClicked() {
        if (binding.plainText.text.isEmpty()) {
            showSnackBar("Field is empty.")
        } else {
            lifecycleScope.launch {
                applyAnimations()
                navigateToSuccess(getHashData())
            }
        }
    }

    private fun showSnackBar(message: String) {
        val snackBar = Snackbar.make(
            binding.rootLayout,
            message,
            Snackbar.LENGTH_LONG
        )
        snackBar.setAction("Okay") {}
        snackBar.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
            snackBar.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.clear_menu){
            binding.plainText.text.clear()
            showSnackBar("Cleared.")
            return true
        }
        return true
    }
}