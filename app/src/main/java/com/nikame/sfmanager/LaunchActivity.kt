package com.nikame.sfmanager

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import com.nikame.sfmanager.helpers.FileIndexer
import com.nikame.sfmanager.helpers.PermissionManager

//todo The application should not provide its own launch screen/ API SplashScreen
class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
//        val strt = Date().time
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
    }

    override fun onResume() {
        super.onResume()
        if (!checkPermission()) {
            verifyStoragePermissions()
        }
    }

    fun verifyStoragePermissions() {
        //Проверяем и запрашиваем(если надо) права на работу с внешними файлами на устройстве
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) { //если версия выше 10, нужно дополнительное разрешение чтобы увидеть не мультимедиа файлы.
            val alertBuilder = AlertDialog.Builder(this)
            alertBuilder.setCancelable(false)
            alertBuilder.setTitle("Необходимо предоставить дополнительные разрешения")
            alertBuilder.setMessage("Для корректной работы автообновлений приложению требуется разрешение на доступ ко всем файлам. Будет открыто меню настроек, выберите в нём данное приложение и предоставьте разрешение.")
            alertBuilder.setPositiveButton(
                "ОК"
            ) { dialogInterface: DialogInterface?, i: Int ->
                val `in` =
                    Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(`in`)
            }
            val alert = alertBuilder.create()
            alert.show()
            return
        }
        runIndexator()
    }

    fun runIndexator() {
        FileIndexer.runIndexationNewAsync(this.applicationContext)
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent, ActivityOptionsCompat.makeBasic().toBundle())
    }

    //todo clear and simplify methods, replace to class PermissionManager
    fun checkPermission(): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        return if (currentAPIVersion >= Build.VERSION_CODES.M && currentAPIVersion < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val permissionsNonGranted: MutableList<String> = java.util.ArrayList()
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

//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_MEDIA_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                permissionsNonGranted.add(Manifest.permission.ACCESS_MEDIA_LOCATION)
//            }

            if (!permissionsNonGranted.isEmpty()) {
                ActivityCompat
                    .requestPermissions(
                        this,
                        permissionsNonGranted.toTypedArray(),
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
                    verifyStoragePermissions()
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