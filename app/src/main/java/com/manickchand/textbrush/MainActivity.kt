package com.manickchand.textbrush

import android.app.Activity
import android.os.Bundle
import com.manickchand.textbrush.databinding.ActivityMainBinding


class MainActivity : Activity(){

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
    }

    private fun loadImage(){
        binding.imageEdit.builder()
    }


}
