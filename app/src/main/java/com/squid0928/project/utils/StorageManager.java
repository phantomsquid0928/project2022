package com.squid0928.project.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.loader.content.CursorLoader;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Maps;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squid0928.project.MapsActivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class StorageManager {
    private MapsActivity mapsActivity;
    private FirebaseStorage mStorage;
    public String path;

    public StorageManager(MapsActivity mapsActivity, FirebaseStorage mStorage) {  //maps에 스태틱으로 선헌해버릴거야
        this.mapsActivity = mapsActivity;
        this.mStorage = mStorage;
    }
    public void loadImg(String name) {
        if (path == null) {
            Log.i("ff", "null, return");
            return;
        }
        try{
            //로컬에 저장할 폴더의 위치
            File rootsd = mapsActivity.getApplicationContext().getExternalCacheDir();
            File path1;
            if (Build.MODEL.contains("Emulator")) {
                path1 = new File( "mnt/user/0/primary/DCIM/project");
            }
            else {
                path1 = new File(rootsd.getAbsolutePath() + "/photos/" + MapsActivity.user);
                path1.mkdirs();
            }
            //path1 = new File( "/mnt/user/0/primary/DCIM/projectImages/");

            Log.i("ff", "target path    ] " + path1.getPath());
            if (!path1.exists()) {
                //저장할 폴더가 없으면 생성
                boolean res = path1.mkdirs();
                Log.i("ff", "" + res);
            }

            //저장하는 파일의 이름
            final File file = new File(path1 + "/" + name + ".jpg");
            Log.i("ff", "" + file.getPath());
            try {
                mapsActivity.getStoragePermission();
                file.createNewFile();

                //파일을 다운로드하는 Task 생성, 비동기식으로 진행
                final FileDownloadTask fileDownloadTask = mStorage.getReference().child("images/" + mapsActivity.user + "/" + name).getFile(file);
                fileDownloadTask.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        //다운로드 성공 후 할 일
                        Log.i("ff", "success to download image");
                        Toast.makeText(mapsActivity.getApplicationContext(), "다운로드가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        //다운로드 실패 후 할 일
                        Log.i("ff", "failed to download image");
                    }
                }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    //진행상태 표시
                    public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch(Exception e){
            Log.i("ff2", e.getMessage());
        }

    }

    public void saveImg(String name) {    //getPath 후 호출
        StorageReference storageRef = mStorage.getReference();
        Uri file = Uri.parse(path); // 절대경로uri를 file에 할당
        Log.d("ff", "photo file : " + file);

        File path1 = new File("Android/sdcard/DCIM/projectImages/");

        //저장하는 파일의 이름
        final File fileOther = new File(path1, name);

        // stroage images에 절대경로파일 저장
        StorageReference riversRef = storageRef.child("images/" + mapsActivity.user + "/" + name);
        UploadTask uploadTask = riversRef.putFile(file);
        Log.d("ff", "uploadTask : " + uploadTask);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i("ff", "img saved successful");
                //loadImg(name);
            }
        });//파일에 담기 위함, 어쩔수 없음
    }
    public void delImage(String oldname) {
        StorageReference storageRef = mStorage.getReference();
        StorageReference riversRef = storageRef.child("images/" + mapsActivity.user + "/" + oldname);
        Log.i("ff", "target: " + "images/" + mapsActivity.user + "/" + oldname);
        Task<Void> del = riversRef.delete();

        del.addOnCompleteListener(snapshot -> {
            Log.i("ff", "img deleted from db");
        });
    }
    public void setFFPath(String path) {
        this.path = path;
    }
    /*public void setPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    path = Environment.getRootDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                path = getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                path = getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            path = getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            path = uri.getPath();
        }

        path = null;
    }

    *//**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     *//*
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    *//**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     *//*
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    *//**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     *//*
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    *//**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     *//*
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }*/
}
