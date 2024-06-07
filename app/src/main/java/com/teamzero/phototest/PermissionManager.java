package com.teamzero.phototest;

public class PermissionManager {

    public static final int MY_PERMISSIONS_REQUEST = 123;
//
//    public static boolean checkPermission(Activity activity, String permission){
//        int currentAPIVersion = Build.VERSION.SDK_INT;
//        if (currentAPIVersion >= Build.VERSION_CODES.M) {
//
//            List<String> permissions = new ArrayList<>();
//            if (ContextCompat.checkSelfPermission(activity,
//                    permission) != PackageManager.PERMISSION_GRANTED) {
//
//                permissions.add(permission);
//            }
//
//            if (permissions.size() > 0) {
//                String[] myArray = new String[permissions.size()];
//                permissions.toArray(myArray);
//
//                ActivityCompat
//                        .requestPermissions(
//                                activity,
//                                myArray,
//                                MY_PERMISSIONS_REQUEST);
//                return true;
//            } else {
//                return false;
//            }
//        } else {
//            return false;
//        }
//    }
//
//
//    public static boolean checkPermission (Activity activity) {
//        int currentAPIVersion = Build.VERSION.SDK_INT;
//        if (currentAPIVersion >= Build.VERSION_CODES.M) {
//
//            List<String> permissionsNonGranted = new ArrayList<>();
//            if (ContextCompat.checkSelfPermission(activity,
//                    Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                permissionsNonGranted.add(Manifest.permission.READ_PHONE_STATE);
//            }
//            if (ContextCompat.checkSelfPermission(activity,
//                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                permissionsNonGranted.add(Manifest.permission.READ_EXTERNAL_STORAGE);
//            }
//            if (ContextCompat.checkSelfPermission(activity,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                permissionsNonGranted.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            }
//            if (ContextCompat.checkSelfPermission(activity,
//                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                permissionsNonGranted.add(Manifest.permission.CAMERA);
//            }
//            if (ContextCompat.checkSelfPermission(activity,
//                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//                permissionsNonGranted.add(Manifest.permission.RECORD_AUDIO);
//            }
//
//                /*if (ContextCompat.checkSelfPermission(context,
//                        Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
//                    permissionsNonGranted.add(Manifest.permission.READ_SMS);
//                }*/
//
//                /*if (ContextCompat.checkSelfPermission(context,
//                        Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
//                    permissionsNonGranted.add(Manifest.permission.RECEIVE_SMS);
//                }*/
//
//            if (!permissionsNonGranted.isEmpty()) {
//                String[] myArray = new String[permissionsNonGranted.size()];
//                permissionsNonGranted.toArray(myArray);
//
//                ActivityCompat
//                        .requestPermissions(
//                                activity,
//                                myArray,
//                                PermissionManager.MY_PERMISSIONS_REQUEST);
//                return true;
//            } else {
//                return false;
//            }
//        } else {
//            return false;
//        }
//    }


}
