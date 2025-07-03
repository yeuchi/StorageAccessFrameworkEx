package com.ctyeung.storageaccessframeworkex

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import com.ctyeung.storageaccessframeworkex.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            btnRead.setOnClickListener {
                openTextFile()
            }

            btnSave.setOnClickListener {
                createTextFile()
            }
        }
    }

    /////////////////////////// Read //////////////////////////////////////////////////////
    val READ_REQUEST_CODE: Int = 42

    fun openTextFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*" // "application/json" 
        }
        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.also { uri ->
                readTextFromUri(uri)
            }
        }

        else if (requestCode == CREATE_TEXT_FILE && resultCode == RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                try {
                    contentResolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write("This is a sample text to write to the file.".toByteArray())
                    }
                } catch (e: Exception) {
                    // Handle the exception
                }
            }
        }
    }

    private fun readTextFromUri(uri: Uri) {
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val reader = BufferedReader(InputStreamReader(inputStream))
                val stringBuilder = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                    stringBuilder.append("\n") // Add newline if desired
                }
                val fileContent = stringBuilder.toString()
                binding.editText.setText(fileContent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle error
        }
    }

    // Request code for creating a text document.
    val CREATE_TEXT_FILE = 2

    private fun createTextFile() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain" // Specify MIME type for text files
            putExtra(Intent.EXTRA_TITLE, "my_text_file.txt") // Set desired filename
        }
        startActivityForResult(intent, CREATE_TEXT_FILE)
    }
}