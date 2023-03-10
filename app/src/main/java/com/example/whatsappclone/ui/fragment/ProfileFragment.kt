package com.example.whatsappclone.ui.fragment

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.whatsappclone.R
import com.example.whatsappclone.databinding.FragmentProfileBinding
import com.example.whatsappclone.ui.activity.MenuActivity
import com.example.whatsappclone.ui.viewModel.ProfileViewModel
import com.example.whatsappclone.util.Constants.Companion.BIO
import com.example.whatsappclone.util.Constants.Companion.DP
import com.example.whatsappclone.util.Constants.Companion.EMAIL
import com.example.whatsappclone.util.Constants.Companion.NAME
import com.google.android.material.snackbar.Snackbar

class ProfileFragment : Fragment() {

    private var profileBinding : FragmentProfileBinding? = null
    private var updateMode : Boolean = false
    private val profileViewModel: ProfileViewModel by activityViewModels()
    private var image : Bitmap? = null

    private val cameraRequest : ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
            if(isGranted){
                cameraLauncher.launch(null)
            }else{
                Toast.makeText(context, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

    private val cameraLauncher : ActivityResultLauncher<Void?> =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()){
            it?.let{
                //Drawable always take precedence over bitmap
                profileBinding?.civProfilePicture?.let { dp ->
                    Glide.with(this)
                        .load(it)//This converts the bitmap into drawable first and then load it
                        .into(dp)
                }
                image = it
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        profileBinding = FragmentProfileBinding.inflate(
            inflater, container, false
        )
        return profileBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpProfile()

        profileBinding?.civProfilePicture?.isEnabled = false
        profileBinding?.ivAddProfile?.isEnabled = false

        profileBinding?.civProfilePicture?.setOnClickListener {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        activity as MenuActivity, Manifest.permission.CAMERA
                    )){
                Snackbar.make(it, "Needs camera permission", Snackbar.LENGTH_SHORT)
                    .setAction("SETTINGS") {
                        val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri =
                            Uri.fromParts("package", context?.packageName, null)
                        settingsIntent.data = uri
                        startActivity(settingsIntent)
                    }
                    .setActionTextColor(Color.WHITE)
                    .show()
            }else{
                cameraRequest.launch(Manifest.permission.CAMERA)
            }
        }

        profileBinding?.btnUpdateSave?.setOnClickListener {
            if(updateMode){
                updateMode = false

                profileBinding?.btnUpdateSave?.text = getString(R.string.update)

                if(profileBinding?.etProfileName?.text?.isNotEmpty() == true){
                    profileBinding?.tvProfileName?.text = profileBinding?.etProfileName?.text
                    profileBinding?.etProfileName?.text?.clear()
                }
                if (profileBinding?.etProfileBio?.text?.isNotEmpty() == true){
                    profileBinding?.tvProfileBio?.text = profileBinding?.etProfileBio?.text
                    profileBinding?.etProfileBio?.text?.clear()
                }

                profileBinding?.tvProfileName?.visibility = View.VISIBLE
                profileBinding?.tvProfileBio?.visibility = View.VISIBLE
                profileBinding?.tilProfileName?.visibility = View.GONE
                profileBinding?.tilProfileBio?.visibility = View.GONE
                profileBinding?.ivAddProfile?.visibility = View.GONE

                profileBinding?.civProfilePicture?.isEnabled = false
                profileBinding?.ivAddProfile?.isEnabled = false

                val obj = mutableMapOf<String, String>()
                obj[NAME] = profileBinding?.tvProfileName?.text.toString()
                obj[BIO] = profileBinding?.tvProfileBio?.text.toString()
                image?.let { image ->
                    profileViewModel.updateProfilePicture(image)
                }
                profileViewModel.updateProfile(obj)

            }else {
                updateMode = true

                profileBinding?.btnUpdateSave?.text = getString(R.string.save)

                profileBinding?.etProfileName?.hint = profileBinding?.tvProfileName?.text
                profileBinding?.etProfileBio?.hint = profileBinding?.tvProfileBio?.text

                profileBinding?.tvProfileName?.visibility = View.GONE
                profileBinding?.tvProfileBio?.visibility = View.GONE
                profileBinding?.tilProfileName?.visibility = View.VISIBLE
                profileBinding?.tilProfileBio?.visibility = View.VISIBLE
                profileBinding?.ivAddProfile?.visibility = View.VISIBLE

                profileBinding?.civProfilePicture?.isEnabled = true
                profileBinding?.ivAddProfile?.isEnabled = true
            }
        }
    }

    private fun setUpProfile() {
        profileViewModel.fetchProfile()
        profileViewModel.profileDetails.observe(viewLifecycleOwner, Observer {
            profileBinding?.tvProfileName?.text = it[NAME]
            profileBinding?.tvProfileEmail?.text = it[EMAIL]
            profileBinding?.tvProfileBio?.text = it[BIO]
            profileBinding?.civProfilePicture?.let { dp ->
                Glide.with(this)
                    .load(it[DP])
                    .apply (
                        RequestOptions().placeholder(R.drawable.ic_person)
                    )
                    .into(dp)
            }
        })
    }

}