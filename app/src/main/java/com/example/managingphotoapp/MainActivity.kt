package com.example.managingphotoapp


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.managingphotoapp.databinding.ActivityMainBinding
import com.example.managingphotoapp.databinding.LayoutBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialog


class MainActivity : AppCompatActivity(), ClickListner {
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var photoListAdapter: PhotoListAdapter
    var binding: ActivityMainBinding? = null
    var clickListner: ClickListner? = null
    private var imageLiveData = ArrayList<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setViewModelData()
        initBottomSheet()
        prepareRecyclerView()

        viewModel.getLiveData().observe(this, {
            imageLiveData = it as ArrayList<Uri>
            if (imageLiveData.size > 0) binding?.cvNotes?.visibility = View.GONE
            photoListAdapter.setImageList(it)
        })
    }

    private fun setViewModelData() {
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        binding?.viewmodel = viewModel
        viewModel.resultLauncher = resultLauncher
        clickListner = this    }

    private fun prepareRecyclerView() {
        photoListAdapter = PhotoListAdapter(binding?.cvNotes!!, clickListner!!)
        binding?.rvPhotos?.apply {
            layoutManager = GridLayoutManager(applicationContext, 2)
            adapter = photoListAdapter
        }
    }

    private fun initBottomSheet() {
        val bottomSheet = BottomSheetDialog(this)
        val bindingSheet = DataBindingUtil.inflate<LayoutBottomBinding>(
            layoutInflater,
            R.layout.layout_bottom,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)

        viewModel.bottomBinding = bindingSheet
        viewModel.dialog = bottomSheet
    }

    var resultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            viewModel.onActivityResult(result, this)
        }

    override fun removePosition(position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Photo")
        builder.setMessage("Do you still want to delete the photo?")
        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            imageLiveData.removeAt(position)
            photoListAdapter.setImageList(imageLiveData)

        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            dialog.dismiss()
        }
        builder.show()

    }

    override fun onResume() {
        viewModel.dialog?.dismiss()
        super.onResume()
    }

}