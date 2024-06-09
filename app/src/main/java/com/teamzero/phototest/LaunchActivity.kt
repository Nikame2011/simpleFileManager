package com.teamzero.phototest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import com.teamzero.phototest.helpers.FileIndexer
import com.teamzero.phototest.helpers.PermissionManager
import java.util.Date

//todo The application should not provide its own launch screen/ API SplashScreen
class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val strt = Date().time
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        if (!checkPermission()) {
            FileIndexer.runIndexation()
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.putExtra("startTime", strt)
            startActivity(intent, ActivityOptionsCompat.makeBasic().toBundle())
        }
    }

    //todo clear and simplify methods, replace to class PermissionManager
    fun checkPermission(): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        return if (currentAPIVersion >= Build.VERSION_CODES.M) {
            val permissionsNonGranted: MutableList<String> = java.util.ArrayList()
            /* if (ContextCompat.checkSelfPermission(
                     this,
                     Manifest.permission.READ_PHONE_STATE
                 ) != PackageManager.PERMISSION_GRANTED
             ) {
                 permissionsNonGranted.add(Manifest.permission.READ_PHONE_STATE)
             }*/
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsNonGranted.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsNonGranted.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            /*if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsNonGranted.add(Manifest.permission.CAMERA)
            }
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsNonGranted.add(Manifest.permission.RECORD_AUDIO)
            }
*/
            /*if (ContextCompat.checkSelfPermission(context,
                                 Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                             permissionsNonGranted.add(Manifest.permission.READ_SMS);
                         }*/

            /*if (ContextCompat.checkSelfPermission(context,
                                 Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                             permissionsNonGranted.add(Manifest.permission.RECEIVE_SMS);
                         }*/if (!permissionsNonGranted.isEmpty()) {
                //val myArray = arrayOfNulls<String>(permissionsNonGranted.size)
                //permissionsNonGranted.toArray<String>(myArray)
                ActivityCompat
                    .requestPermissions(
                        this,
                        permissionsNonGranted.toTypedArray(), //myArray,
                        123
                    )
                true
            } else {
                false
            }
        } else {
            false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PermissionManager.MY_PERMISSIONS_REQUEST) {
            for (i in permissions.indices) {
                /* if (permissions[i] == Manifest.permission.READ_PHONE_STATE) {
                     if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                         //Snackbar.make(binding.getRoot(), R.string.perm_OK_READ_PHONE_STATE, Snackbar.LENGTH_SHORT).show();
                         Toast.makeText(this, R.string.perm_OK_READ_PHONE_STATE, Toast.LENGTH_SHORT)
                             .show()
                     } else {
                         //Snackbar.make(binding.getRoot(), R.string.perm_NOT_READ_PHONE_STATE, Snackbar.LENGTH_SHORT).show();
                         Toast.makeText(this, R.string.perm_NOT_READ_PHONE_STATE, Toast.LENGTH_SHORT)
                             .show()
                     }
                 }*/
                if (permissions[i] == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        //Snackbar.make(binding.getRoot(), R.string.perm_OK_WRITE_EXTERNAL_STORAGE, Snackbar.LENGTH_SHORT).show();
                        /* Toast.makeText(
                             this,
                             R.string.perm_OK_WRITE_EXTERNAL_STORAGE,
                             Toast.LENGTH_SHORT
                         ).show()*/
                    } else {
                        //Snackbar.make(binding.getRoot(), R.string.perm_NOT_WRITE_EXTERNAL_STORAGE, Snackbar.LENGTH_SHORT).show();
                        Toast.makeText(
                            this,
                            "ГОНИ РАЗРЕШЕНИЕ",/*R.string.perm_NOT_WRITE_EXTERNAL_STORAGE,*/
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                if (permissions[i] == Manifest.permission.READ_EXTERNAL_STORAGE) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        //Snackbar.make(binding.getRoot(), R.string.perm_OK_READ_EXTERNAL_STORAGE, Snackbar.LENGTH_SHORT).show();
                        /* Toast.makeText(
                             this,
                             R.string.perm_OK_READ_EXTERNAL_STORAGE,
                             Toast.LENGTH_SHORT
                         ).show()*/
                    } else {
                        //Snackbar.make(binding.getRoot(), R.string.perm_NOT_READ_EXTERNAL_STORAGE, Snackbar.LENGTH_SHORT).show();
                        Toast.makeText(
                            this,
                            "ГОНИ РАЗРЕШЕНИЕ",//R.string.perm_NOT_READ_EXTERNAL_STORAGE,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                /*if (permissions[i] == Manifest.permission.CAMERA) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        //Snackbar.make(binding.getRoot(), R.string.perm_OK_CAMERA, Snackbar.LENGTH_SHORT).show();
                        Toast.makeText(this, R.string.perm_OK_CAMERA, Toast.LENGTH_SHORT).show()
                    } else {
                        //Snackbar.make(binding.getRoot(), R.string.perm_NOT_CAMERA, Snackbar.LENGTH_SHORT).show();
                        Toast.makeText(this, R.string.perm_NOT_CAMERA, Toast.LENGTH_SHORT).show()
                    }
                }
                if (permissions[i] == Manifest.permission.RECORD_AUDIO) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        //Snackbar.make(binding.getRoot(), R.string.perm_OK_RECORD_AUDIO, Snackbar.LENGTH_SHORT).show();
                        Toast.makeText(this, R.string.perm_OK_RECORD_AUDIO, Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        //Snackbar.make(binding.getRoot(), R.string.perm_NOT_RECORD_AUDIO, Snackbar.LENGTH_SHORT).show();
                        Toast.makeText(this, R.string.perm_NOT_RECORD_AUDIO, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
*/
                /*if (permissions[i].equals(Manifest.permission.RECEIVE_SMS) || permissions[i].equals(Manifest.permission.READ_SMS)) {
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(this, R.string.perm_OK_SMS, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, R.string.perm_NOT_SMS, Toast.LENGTH_SHORT).show();
                            }
                        }*/
                if (!checkPermission()) {
                    FileIndexer.runIndexation()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent, ActivityOptionsCompat.makeBasic().toBundle())
                }
            }
        } else {
            super.onRequestPermissionsResult(
                requestCode, permissions,
                grantResults
            )
        }
    }
}