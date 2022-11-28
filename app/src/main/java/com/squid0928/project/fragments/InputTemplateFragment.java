package com.squid0928.project.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.squid0928.project.MapsActivity;
import com.squid0928.project.R;
import com.squid0928.project.utils.InputData;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class InputTemplateFragment extends Fragment {

    Dialog dialog_stayed_date_time;
    ImageView view_photo;
    RadioGroup view_memory_or_promise;
    RadioButton view_memory;
    RadioButton view_promise;
    Spinner view_category;
    CheckBox view_check_stayed_time;
    TextView view_stayed_time;
    TextView view_promised_time;
    EditText view_memo;
    Button view_btn_setAlarm;
    LinearLayout view_check_memory;
    LinearLayout view_check_promise;
    TextView view_save;
    TextView view_cancel;
    InputData inputData = new InputData();
    Uri photoUri;
    HashMap<String, InputData> inputDataHashMap = new HashMap<>();
    //String hashKey = UserID + Location?

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, ".jpg",
                storageDir
        );
        return image;
    }

    //  갤러리 실행
    ActivityResultLauncher<Intent> StartForResult_gallery = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    photoUri = intent.getData();
                    Glide.with(this).load(photoUri).into(view_photo);
                    inputData.setPhoto(photoUri);
                }
            }
    );

    //  카메라 실행
    ActivityResultLauncher<Intent> StartForResult_camera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    view_photo.setImageURI(photoUri);
                    inputData.setPhoto(photoUri);
                }
            });

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
        }
        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(getActivity(),
                    getActivity().getPackageName() + ".fileprovider",
                    photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            StartForResult_camera.launch(intent);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_input_template, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //  View 객체 획득
        view_photo = view.findViewById(R.id.photo);
        view_memory_or_promise = view.findViewById(R.id.memory_or_promise);
        view_memory = view.findViewById(R.id.btn_memory);
        view_promise = view.findViewById(R.id.btn_promise);
        view_category = view.findViewById(R.id.category);
        view_check_stayed_time = view.findViewById(R.id.check_stayed_time);
        view_stayed_time = view.findViewById(R.id.stayed_time);
        view_promised_time = view.findViewById(R.id.promised_date_time);
        view_memo = view.findViewById(R.id.memo);
        view_check_memory = view.findViewById(R.id.checked_memory);
        view_check_promise = view.findViewById(R.id.checked_promise);
        view_btn_setAlarm = view.findViewById(R.id.btn_setAlarm);
        view_save = view.findViewById(R.id.textview_save);
        view_cancel = view.findViewById(R.id.textview_cancel);
        ArrayAdapter<String> arrayAdapter_memory = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.array_memory));
        ArrayAdapter<String> arrayAdapter_promise = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.array_promise));
        view_category.setAdapter(arrayAdapter_memory);  //  기본은 추억 범주


        //  이미지뷰를 클릭했을 때
        view_photo.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view_photo.getDrawable() == null) {
                    //  이미지뷰가 비어 있을 때 - 갤러리에서 가져오기, 카메라로 사진 찍기
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("등록된 사진이 없습니다")
                            .setNegativeButton("갤러리에서 가져오기", new DialogInterface.OnClickListener() {
                                @Override
                                //  갤러리에서 가져오기
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Intent.ACTION_PICK);
                                    intent.setType("image/*");
                                    StartForResult_gallery.launch(intent);
                                }
                            })
                            .setPositiveButton("카메라로 사진 찍기", new DialogInterface.OnClickListener() {
                                        @Override
                                        //  카메라로 사진 찍기
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            cameraIntent();
                                        }
                                    }
                            );
                    builder.show();
                } else {
                    //  이미지뷰에 사진이 이미 있을 때 - 갤러리에서 새 사진 가져오기, 사진 삭제하기
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("등록된 사진이 있습니다")
                            .setNegativeButton("다른 사진으로 바꾸기", new DialogInterface.OnClickListener() {
                                @Override
                                //  다른 사진으로 바꾸기
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    AlertDialog.Builder builder_new_photo = new AlertDialog.Builder(getActivity());
                                    builder_new_photo.setMessage("어떤 방법으로 바꿀까요?")
                                            .setNegativeButton("갤러리에서 가져오기", new DialogInterface.OnClickListener() {
                                                @Override
                                                //  갤러리에서 가져오기
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Intent intent = new Intent(Intent.ACTION_PICK);
                                                    intent.setType("image/*");
                                                    StartForResult_gallery.launch(intent);
                                                }
                                            })
                                            .setPositiveButton("카메라로 사진 찍기", new DialogInterface.OnClickListener() {
                                                @Override
                                                //  카메라로 사진 찍기
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    cameraIntent();
                                                }
                                            });
                                    builder_new_photo.show();
                                }
                            })
                            .setPositiveButton("사진 삭제하기", new DialogInterface.OnClickListener() {
                                @Override
                                //  사진 삭제하기
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    view_photo.setImageResource(0);
                                }
                            });
                    builder.show();
                }
            }
        });

        //  추억 or 약속 체크
        view_memory_or_promise.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int memory_or_promise) {
                switch (memory_or_promise) {
                    case R.id.btn_memory:
                        inputData.setType(InputData.MEMORY);
                        view_check_promise.setVisibility(View.GONE);
                        view_check_memory.setVisibility(View.VISIBLE);
                        //  사용자의 추억 범주 불러오기 (SharedPreferences?)
                        view_category.setAdapter(arrayAdapter_memory);
                        break;
                    case R.id.btn_promise:
                        inputData.setType(InputData.PROMISE);
                        view_check_memory.setVisibility(View.GONE);
                        view_check_promise.setVisibility(View.VISIBLE);
                        setPromise(); //    약속으로 설정 시 바로 시간 설정
                        //  사용자의 약속 범주 불러오기 (SharedPreferences?)
                        view_category.setAdapter(arrayAdapter_promise);
                        break;
                }
            }
        });

        //  범주 선택
        view_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                inputData.setCategory(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        /*
        약속으로 설정한 시간을 바탕으로 알람을 설정.
        다만 날짜까지는 설정할 수가 없어 바로 전날에만 활성화되도록 하던지 하는 방식으로 생각 중입니다.
        */
        view_btn_setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view_promised_time != null) {
                    Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                    intent.putExtra(AlarmClock.EXTRA_HOUR, inputData.getTimeStart().getHour());
                    intent.putExtra(AlarmClock.EXTRA_MINUTES, inputData.getTimeStart().getMinute());
                    startActivity(intent);
                }
            }
        });

        /*
        머문 시간을 기록할 것인지 체크박스를 통해 묻고
        기록한다 하면 다이얼로그를 띄워 저장.
        */
        view_check_stayed_time.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view_check_stayed_time.isChecked()) {
                    setMemory();
                    view_stayed_time.setVisibility(View.VISIBLE);
                } else {
                    view_stayed_time.setText("");
                    view_stayed_time.setVisibility(View.GONE);
                }
            }
        });

        //  메모 업데이트
        view_memo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                inputData.setMemo(view_memo.getText().toString());
            }
        });

        view_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  저장
                inputDataHashMap.put("hashKey", inputData);
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("key",inputDataHashMap);
                startActivity(intent);
                Toast.makeText(getActivity(), "저장되었습니다.", Toast.LENGTH_SHORT).show();
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                fragmentManager.beginTransaction().remove(InputTemplateFragment.this).commit();
//                fragmentManager.popBackStack();
            }
        });
        view_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  취소
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().remove(InputTemplateFragment.this).commit();
                fragmentManager.popBackStack();
            }
        });

    }

    //  추억_날짜, 머문 시간 입력
    private void setMemory() {
        Button dialog_acceptBtn_stayedTime;
        DatePicker dialog_stayed_date_from;
        TimePicker dialog_stayed_time_from;
        DatePicker dialog_stayed_date_to;
        TimePicker dialog_stayed_time_to;
        dialog_stayed_date_time = new Dialog(getActivity());
        dialog_stayed_date_time.setContentView(R.layout.set_stayed_date_time);
        dialog_acceptBtn_stayedTime = dialog_stayed_date_time.findViewById(R.id.acceptBtn_stayedTime);
        dialog_stayed_date_from = dialog_stayed_date_time.findViewById(R.id.stayed_date_time_date_from);
        dialog_stayed_time_from = dialog_stayed_date_time.findViewById(R.id.stayed_date_time_from);
        dialog_stayed_date_to = dialog_stayed_date_time.findViewById(R.id.stayed_date_time_date_to);
        dialog_stayed_time_to = dialog_stayed_date_time.findViewById(R.id.stayed_date_time_to);

        if (!dialog_stayed_date_time.isShowing())
            dialog_stayed_date_time.show();
        Window window = dialog_stayed_date_time.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        //  추억: 현재보다 이후 날짜 선택 불가
        long date_now = System.currentTimeMillis();
        dialog_stayed_date_from.setMaxDate(date_now);   //
        dialog_acceptBtn_stayedTime.setOnClickListener(new Button.OnClickListener() {   // 확인 버튼
            @Override
            public void onClick(View view) {
                LocalDate localDate_from = LocalDate.of(dialog_stayed_date_from.getYear(), dialog_stayed_date_from.getMonth(),
                        dialog_stayed_date_from.getDayOfMonth());
                LocalTime localTime_start = LocalTime.of(dialog_stayed_time_from.getHour(), dialog_stayed_time_from.getMinute());
                LocalDate localDate_to = LocalDate.of(dialog_stayed_date_to.getYear(), dialog_stayed_date_to.getMonth(),
                        dialog_stayed_date_to.getDayOfMonth());
                LocalTime localTime_end = LocalTime.of(dialog_stayed_time_to.getHour(), dialog_stayed_time_to.getMinute());
                //  날짜, 시간 TextView에 띄우기
                String concat = localDate_from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        + " " + localTime_start.format(DateTimeFormatter.ofPattern("HH:mm"))
                        + " ~ " + "\n"
                        + localDate_to.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        + " " + localTime_end.format(DateTimeFormatter.ofPattern("HH:mm"));
                view_stayed_time.setText(concat);
                //  DB에 저장
                inputData.setDateFrom(localDate_from);
                inputData.setTimeStart(localTime_start);
                inputData.setDateTo(localDate_to);
                inputData.setTimeEnd(localTime_end);    //
                if (dialog_stayed_date_time.isShowing())
                    dialog_stayed_date_time.dismiss();
            }
        });
    }

    //  약속_날짜, 시간 입력
    private void setPromise() {
        DatePicker dialog_promised_date;
        TimePicker dialog_promised_time;
        Button dialog_acceptBtn_promisedTime;
        Button dialog_rejectBtn_promisedTime;
        Dialog dialog_promised_date_time;
        dialog_promised_date_time = new Dialog(getActivity());
        dialog_promised_date_time.setContentView(R.layout.set_promised_date_time);
        dialog_promised_date = dialog_promised_date_time.findViewById(R.id.promised_date_time_date);
        dialog_promised_time = dialog_promised_date_time.findViewById(R.id.promised_date_time_time);
        dialog_acceptBtn_promisedTime = dialog_promised_date_time.findViewById(R.id.promised_date_time_acceptBtn);
        dialog_rejectBtn_promisedTime = dialog_promised_date_time.findViewById(R.id.promised_date_time_rejectBtn);
        dialog_promised_date_time.setCanceledOnTouchOutside(false);
        if (!dialog_promised_date_time.isShowing())
            dialog_promised_date_time.show();
        Window window = dialog_promised_date_time.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        //  약속: 현재보다 이전 날짜 선택 불가
        long date_now = System.currentTimeMillis();
        dialog_promised_date.setMinDate(date_now);  //
        dialog_acceptBtn_promisedTime.setOnClickListener(new Button.OnClickListener() { //  확인 버튼
            @Override
            public void onClick(View view) {
                LocalDate localDate = LocalDate.of(dialog_promised_date.getYear(), dialog_promised_date.getMonth(),
                        dialog_promised_date.getDayOfMonth());
                LocalTime localTime = LocalTime.of(dialog_promised_time.getHour(), dialog_promised_time.getMinute());
                //  날짜, 시간 TextView에 띄우기
                String concat = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        + " " + localTime.format(DateTimeFormatter.ofPattern("hh:mm"));
                view_promised_time.setText(concat);
                //  DB에 저장
                inputData.setDateFrom(localDate);
                inputData.setTimeStart(localTime);  //
                if (dialog_promised_date_time.isShowing())
                    dialog_promised_date_time.dismiss();
            }
        });
        dialog_rejectBtn_promisedTime.setOnClickListener(new Button.OnClickListener() { //  취소 버튼
            @Override
            public void onClick(View view) {
                if (dialog_promised_date_time.isShowing())
                    dialog_promised_date_time.cancel();
            }
        });
    }
}