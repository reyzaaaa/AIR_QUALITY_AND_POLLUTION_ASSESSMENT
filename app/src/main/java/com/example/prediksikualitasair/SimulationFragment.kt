package com.example.prediksikualitasair

import android.content.res.AssetFileDescriptor
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.prediksikualitasair.databinding.FragmentSimulationBinding
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class SimulationFragment : Fragment() {

    private var _binding: FragmentSimulationBinding? = null
    private val binding get() = _binding!!

    private lateinit var tflite: Interpreter
    private val tfliteModelName = "air-quality-and-pollution-assessment.tflite"

    private val scalerMin = floatArrayOf(
        -0.29646018f, -0.39087948f, 0f, 0.00063291f, -0.12869565f, 0.12133072f, -0.21172638f, -0.10729614f, -0.24447334f
    )
    private val scalerScale = floatArrayOf(
        0.02212389f, 0.01085776f, 0.00338983f, 0.00316456f, 0.0173913f, 0.01956947f, 0.3257329f, 0.04291845f, 0.00130039f
    )
    // ===============================================================================

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSimulationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            tflite = Interpreter(loadModelFile())
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error loading model: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }

        binding.btnToolbarBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnPredict.setOnClickListener {
            try {
                val result = runPrediction()
                binding.tvResult.text = "Hasil Prediksi: $result"
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error during prediction: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadModelFile(): ByteBuffer {
        val fileDescriptor: AssetFileDescriptor = requireContext().assets.openFd(tfliteModelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun scaleFeatures(features: FloatArray): FloatArray {
        val scaledFeatures = FloatArray(features.size)
        for (i in features.indices) {
            // Rumus MinMaxScaler: (x - min) * scale
            scaledFeatures[i] = (features[i] - scalerMin[i]) * scalerScale[i]
        }
        return scaledFeatures
    }

    private fun runPrediction(): String {
        // 1. Ambil semua input dan konversi ke Float
        val inputs = floatArrayOf(
            binding.inputTemperature.text.toString().toFloatOrNull() ?: 0f,
            binding.inputHumidity.text.toString().toFloatOrNull() ?: 0f,
            binding.inputPm25.text.toString().toFloatOrNull() ?: 0f,
            binding.inputPm10.text.toString().toFloatOrNull() ?: 0f,
            binding.inputNo2.text.toString().toFloatOrNull() ?: 0f,
            binding.inputSo2.text.toString().toFloatOrNull() ?: 0f,
            binding.inputCo.text.toString().toFloatOrNull() ?: 0f,
            binding.inputProximity.text.toString().toFloatOrNull() ?: 0f,
            binding.inputPopulation.text.toString().toFloatOrNull() ?: 0f
        )

        // 2. Lakukan penskalaan data
        val scaledInputs = scaleFeatures(inputs)

        // 3. Siapkan buffer untuk input model
        val inputBuffer = ByteBuffer.allocateDirect(1 * 9 * 4).apply {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().put(scaledInputs)
        }

        // 4. Siapkan buffer untuk output model
        val outputClasses = 4
        val outputBuffer = Array(1) { FloatArray(outputClasses) }

        // 5. Jalankan model
        tflite.run(inputBuffer, outputBuffer)

        // 6. Proses hasil output
        val resultProbabilities = outputBuffer[0]
        val maxIndex = resultProbabilities.indices.maxByOrNull { resultProbabilities[it] } ?: -1

        val labels = listOf("Good", "Hazardous", "Moderate", "Poor") // CONTOH: SESUAIKAN DENGAN PUNYA ANDA

        return if (maxIndex != -1 && maxIndex < labels.size) {
            labels[maxIndex]
        } else {
            "Tidak Terdeteksi"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::tflite.isInitialized) tflite.close()
        _binding = null
    }
}