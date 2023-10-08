package com.manickchand.textbrush

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.manickchand.textbrush.databinding.ActivityMainBinding

class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadImage()

        binding.deleteButton.setOnClickListener {
            loadImage()
        }

        binding.undoButton.setOnClickListener {
            binding.imageEdit.undo()
        }

        binding.selectImageButton.setOnClickListener {
            Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show()
        }

        binding.fontDownButton.setOnClickListener {
            Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show()
        }

        binding.fontUpButton.setOnClickListener {
            Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadImage() {
        binding.imageEdit.builder()
    }
}
