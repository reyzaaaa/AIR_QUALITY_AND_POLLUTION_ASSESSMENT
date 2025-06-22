package com.example.prediksikualitasair

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.prediksikualitasair.databinding.FragmentMainMenuBinding

class MainMenuFragment : Fragment() {

    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnMenuAbout.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_aboutFragment)
        }
        binding.btnMenuDataset.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_datasetFragment)
        }
        binding.btnMenuFeatures.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_featureFragment)
        }
        binding.btnMenuModel.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_modelFragment)
        }
        binding.btnMenuSimulation.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_simulationFragment2)
        }

        binding.btnToolbarBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}