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
import android.transition.TransitionInflater;
import android.util.Log;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.common.collect.Maps;
import com.squid0928.project.MapsActivity;
import com.squid0928.project.MapsActivity;
import com.squid0928.project.R;
import com.squid0928.project.utils.InputData;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.ResolverStyle;
import org.threeten.bp.format.TextStyle;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InputTemplateFragment extends Fragment{

    Dialog dialog_stayed_date_time;
    ImageView view_photo;
    RadioGroup view_memory_or_promise;
    RadioButton view_memory;
    RadioButton view_promise;
    Spinner view_category;
    CheckBox view_check_stayed_time;
    TextView view_stayed_time;
    TextView view_promised_time;
    EditText view_schedule_name;
    EditText view_memo;
    Button view_btn_setAlarm;
    LinearLayout view_check_memory;
    LinearLayout view_check_promise;
    TextView view_save;
    TextView view_cancel;
    TextView view_delete;
    InputData inputData = new InputData();
    Uri photoUri;
    boolean mod;
    String oldname;
    String oldPhoto;
    MapsActivity mapsActivity = null;

    //String hashKey = UserID + Location?

    public InputTemplateFragment() {
        inputData.setPhoto(null);
        inputData.setType(InputData.MEMORY);
        inputData.setCategory(0);
        inputData.setDateFrom(null);
        inputData.setDateTo(null);
        inputData.setTimeStart(null);
        inputData.setTimeEnd(null);
        inputData.setScheduleName(null);
        inputData.setMemo(null);
        mod = false;
    }
    public InputTemplateFragment(InputData inputData) {
        this.inputData.setPhoto(inputData.getPhoto());
        this.inputData.setType(inputData.getType());
        this.inputData.setCategory(inputData.getCategory());
        this.inputData.setDateFrom(inputData.getDateFrom());
        this.inputData.setDateTo(inputData.getDateTo());
        this.inputData.setTimeStart(inputData.getTimeStart());
        this.inputData.setTimeEnd(inputData.getTimeEnd());
        this.inputData.setScheduleName(inputData.getScheduleName());
        this.inputData.setMemo(inputData.getMemo());
        mod = true;
        oldname = inputData.getScheduleName();
        oldPhoto = inputData.getPhoto();
    }
    public InputTemplateFragment(InputData inputData, MapsActivity i) {
        this.inputData.setPhoto(inputData.getPhoto());
        this.inputData.setType(inputData.getType());
        this.inputData.setCategory(inputData.getCategory());
        this.inputData.setDateFrom(inputData.getDateFrom());
        this.inputData.setDateTo(inputData.getDateTo());
        this.inputData.setTimeStart(inputData.getTimeStart());
        this.inputData.setTimeEnd(inputData.getTimeEnd());
        this.inputData.setScheduleName(inputData.getScheduleName());
        this.inputData.setMemo(inputData.getMemo());
        mod = true;
        oldname = inputData.getScheduleName();
        oldPhoto = inputData.getPhoto();
        this.mapsActivity = i;
    }


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
                    inputData.setPhoto(photoUri.toString());
                }
            }
    );

    //  카메라 실행
    ActivityResultLauncher<Intent> StartForResult_camera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    view_photo.setImageURI(photoUri);
                    inputData.setPhoto(photoUri.toString());
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.bottom_slide_up));
        setExitTransition(inflater.inflateTransition(R.transition.bottom_slide_down));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("ff", "destroy");
        Bundle result = new Bundle();
        result.putSerializable("inputData", null); //맵 밖 클릭시 저장되게 할지 아님 버튼눌러서 하게할지
        result.putBoolean("mod", false);
        result.putBoolean("res", false);
        getActivity().getSupportFragmentManager().setFragmentResult("key", result);
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
        view_schedule_name = view.findViewById(R.id.schedule_name);
        view_memo = view.findViewById(R.id.memo);
        view_check_memory = view.findViewById(R.id.checked_memory);
        view_check_promise = view.findViewById(R.id.checked_promise);
        view_btn_setAlarm = view.findViewById(R.id.btn_setAlarm);
        view_save = view.findViewById(R.id.textview_save);
        view_cancel = view.findViewById(R.id.textview_cancel);
        view_delete = view.findViewById(R.id.textview_delete);
        //view_category.setAdapter(arrayAdapter_memory);  //  기본은 추억 범주

        ArrayAdapter<String> arrayAdapter_memory = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.array_memory));
        ArrayAdapter<String> arrayAdapter_promise = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.array_promise));

        //  LOAD InputData
        if(inputData.getScheduleName()!=null){
            view_delete.setVisibility(View.VISIBLE);
            view_cancel.setVisibility(View.GONE);
        }
        if(inputData.getPhoto()!=null) {
            if (mapsActivity != null) {
                Uri temp = Uri.parse(mapsActivity.getApplicationContext().getExternalCacheDir() + "/photos/" + mapsActivity.user + "/" + inputData.getScheduleName() + ".jpg");
                Log.i("ff", "changing image to " + temp);
                view_photo.setImageURI(temp);
            }
            else {
                view_photo.setImageURI(Uri.parse(inputData.getPhoto())); // load ImageView
            }
        }

        if(inputData.getType()==InputData.MEMORY) { //  Type == Memory
            view_memory.setChecked(true);   //  load RadioButton
            view_category.setAdapter(arrayAdapter_memory);  //  load Spinner
            view_category.setSelection(inputData.getCategory());
            view_check_memory.setVisibility(View.VISIBLE);  //  load Layout
            if (inputData.getDateFrom() != null && inputData.getDateTo() != null
                    && inputData.getTimeStart() != null && inputData.getTimeEnd() != null) {
                LocalDate localDate_from = LocalDate.parse(inputData.getDateFrom());
                LocalTime localTime_start = LocalTime.parse(inputData.getTimeStart());
                LocalDate localDate_to = LocalDate.parse(inputData.getDateTo());
                LocalTime localTime_end = LocalTime.parse(inputData.getTimeEnd());
                view_check_stayed_time.setChecked(true);    //  load Checkbox
                view_stayed_time.setVisibility(View.VISIBLE);
                view_stayed_time.setText(localDate_from //  load TextView
                        + " (" + localDate_from.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.KOREA)
                        + ") " + localTime_start
                        + " ~ " + "\n"
                        + localDate_to + " ("
                        + localDate_to.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.KOREA)
                        + ") " + localTime_end);
            }
        }
        else if(inputData.getType()==InputData.PROMISE){    //  Type == Promise
            view_promise.setChecked(true);  //  load RadioButton
            view_category.setAdapter(arrayAdapter_promise); //  load Spinner
            view_category.setSelection(inputData.getCategory());
            view_check_memory.setVisibility(View.GONE);
            view_check_promise.setVisibility(View.VISIBLE);  //  load Layout
            if (inputData.getDateFrom() != null && inputData.getTimeStart() != null) {
                LocalDate localDate = LocalDate.parse(inputData.getDateFrom());
                LocalTime localTime = LocalTime.parse(inputData.getTimeStart());
                view_promised_time.setText(localDate    //  load TextView
                        + " (" + localDate.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.KOREA)
                        + ") " + localTime);
                if(checkIf24hoursBefore())  //  load Button
                    view_btn_setAlarm.setEnabled(true);
            }
        }
        if(inputData.getScheduleName()!=null)view_schedule_name.setText(inputData.getScheduleName()); //  load EditText
        if(inputData.getMemo()!=null)view_memo.setText(inputData.getMemo());    //  load EditText

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
                                    inputData.setPhoto(null); //TODO 이상하면 밤구기
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
        24시간 전부터 버튼이 활성화 됨.
        */
        view_btn_setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view_promised_time != null) {
                    Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                    LocalTime localTime = LocalTime.parse(inputData.getTimeStart(), DateTimeFormatter.ofPattern("HH:mm"));
                    intent.putExtra(AlarmClock.EXTRA_HOUR, localTime.getHour());
                    intent.putExtra(AlarmClock.EXTRA_MINUTES, localTime.getMinute());
                    startActivity(intent);
                }
            }
        });


        /*
        머문 시간을 기록할 것인지 체크박스를 통해 묻고,
        만약 기록한다 하면 다이얼로그를 띄워 저장.
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

        //  스케쥴 이름 업데이트
        view_schedule_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                inputData.setScheduleName(view_schedule_name.getText().toString());
            }
        });

        //  메모 업데이트
        view_memo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                inputData.setMemo(view_memo.getText().toString());
            }
        });

        view_save.setOnClickListener(new View.OnClickListener() {
            boolean modify = false;
            @Override
            public void onClick(View view) {
                //  저장
                Bundle result = new Bundle();
                if(inputData.getScheduleName()!=null && !inputData.getScheduleName().equals("")){
                    if (MapsActivity.userdata.getSavedInputMarkers().containsKey(inputData.getScheduleName()) && oldname != inputData.getScheduleName()) {
                        Toast.makeText(getActivity(), "이미 있는 이름입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (mod && !modify) {//이미 있는 마커 클릭, 정보 인풋됨
                        modify = true;
                        Toast.makeText(getActivity(), "저장을 한번 더 누르면 방금만든 약속으로 수정됩니다.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    result.putSerializable("inputData", inputData);
                    result.putBoolean("res", true);
                    if (mod) {
                        result.putBoolean("mod", true);
                        result.putString("old", oldname);
                        if (oldPhoto != null) {
                            if (!oldPhoto.equals(inputData.getPhoto())) { //삭제
                                result.putBoolean("imgchange", true);
                            }
                            else {
                                result.putBoolean("imgchange", false);
                            }
                        }
                        else {
                            if (inputData.getPhoto() == null) { //아무거도 안함
                                result.putBoolean("imgchange", false);
                            }
                            else {//추가
                                result.putBoolean("imgchange", true);
                            }
                        }

                        result.putString("oldimg", oldPhoto);
                    } else {
                        result.putBoolean("mod", false);
                    }
                    modify = false;
                    getActivity().getSupportFragmentManager().setFragmentResult("key", result);

                    Toast.makeText(getActivity(), "저장되었습니다.", Toast.LENGTH_SHORT).show();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().remove(InputTemplateFragment.this).commit();
                    fragmentManager.popBackStack();
                }
                else{
                    result.putSerializable("inputData", null);
                    result.putBoolean("mod", false);
                    result.putBoolean("res", false);
                    getActivity().getSupportFragmentManager().setFragmentResult("key", result);

                    Toast.makeText(getActivity(), "필수 항목을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().remove(InputTemplateFragment.this).commit();
                    fragmentManager.popBackStack();
                }
            }
        });

        view_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("ff", "cancel clicked");
                //  취소
                Bundle result = new Bundle();
                result.putSerializable("inputData", null);
                result.putBoolean("mod", false);
                result.putBoolean("res", false);
                getActivity().getSupportFragmentManager().setFragmentResult("key", result);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().remove(InputTemplateFragment.this).commit();
                fragmentManager.popBackStack();
            }
        });

        view_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("ff", "del clicked");
                Bundle result = new Bundle();
                result.putSerializable("inputData", null);
                result.putBoolean("res", true);
                result.putBoolean("mod", false);
                result.putString("old", oldname);
                result.putString("oldimg", oldPhoto);

                getActivity().getSupportFragmentManager().setFragmentResult("key", result);
                //  삭제
               //TODO 삭제 구현

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().remove(InputTemplateFragment.this).commit();
                fragmentManager.popBackStack();
            }
        });
    }

    //  추억_날짜, 머문 시간 입력
    private void setMemory() {
        Button dialog_nextBtn_stayedTime;
        Button dialog_cancelBtn_stayedTime;
        Button dialog_acceptBtn_stayedTime;
        Button dialog_prevBtn_stayedTime;
        LinearLayout dialog_date_time_from_set;
        LinearLayout dialog_date_time_to_set;
        DatePicker dialog_stayed_date_from;
        TimePicker dialog_stayed_time_from;
        DatePicker dialog_stayed_date_to;
        TimePicker dialog_stayed_time_to;
        TextView dialog_stayed_time_from_text;
        dialog_stayed_date_time = new Dialog(getActivity());
        dialog_stayed_date_time.setContentView(R.layout.set_stayed_date_time);

        dialog_nextBtn_stayedTime = dialog_stayed_date_time.findViewById(R.id.stayed_date_time_nextBtn);
        dialog_cancelBtn_stayedTime = dialog_stayed_date_time.findViewById(R.id.stayed_date_time_cancelBtn);
        dialog_acceptBtn_stayedTime = dialog_stayed_date_time.findViewById(R.id.stayed_date_time_acceptBtn);
        dialog_prevBtn_stayedTime = dialog_stayed_date_time.findViewById(R.id.stayed_date_time_prevBtn);
        dialog_date_time_from_set = dialog_stayed_date_time.findViewById(R.id.date_time_from_set);
        dialog_date_time_to_set = dialog_stayed_date_time.findViewById(R.id.date_time_to_set);
        dialog_stayed_date_from = dialog_stayed_date_time.findViewById(R.id.stayed_date_time_date_from);
        dialog_stayed_time_from = dialog_stayed_date_time.findViewById(R.id.stayed_date_time_from);
        dialog_stayed_date_to = dialog_stayed_date_time.findViewById(R.id.stayed_date_time_date_to);
        dialog_stayed_time_to = dialog_stayed_date_time.findViewById(R.id.stayed_date_time_to);
        dialog_stayed_time_from_text=dialog_stayed_date_time.findViewById(R.id.stayed_date_time_text);

        if (!dialog_stayed_date_time.isShowing())
            dialog_stayed_date_time.show();
        Window window = dialog_stayed_date_time.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        //  추억: 현재보다 이후 날짜 선택 불가
        long date_now = System.currentTimeMillis();
        dialog_stayed_date_from.setMaxDate(date_now);

        dialog_nextBtn_stayedTime.setOnClickListener(new View.OnClickListener() {   //  다음 버튼
            @Override
            public void onClick(View view) {
                LocalDate localDate_from=LocalDate.of(dialog_stayed_date_from.getYear(), dialog_stayed_date_from.getMonth()+1,
                        dialog_stayed_date_from.getDayOfMonth());
                LocalTime localTime_start= LocalTime.of(dialog_stayed_time_from.getHour(), dialog_stayed_time_from.getMinute());
                dialog_date_time_from_set.setVisibility(View.INVISIBLE);
                dialog_date_time_to_set.setVisibility(View.VISIBLE);
                dialog_stayed_time_from_text.setText(localDate_from.format(DateTimeFormatter.ofPattern("uuuu-MM-dd").
                        withResolverStyle(ResolverStyle.STRICT))
                        + " (" + localDate_from.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.KOREA) + ") "
                        + " " + localTime_start.format(DateTimeFormatter.ofPattern("HH:mm"))
                        + " ~ ");
            }
        });

        dialog_cancelBtn_stayedTime.setOnClickListener(new View.OnClickListener() { //  취소 버튼
            @Override
            public void onClick(View view) {
                dialog_stayed_date_time.cancel();
                view_check_stayed_time.setChecked(false);
                view_stayed_time.setVisibility(View.GONE);
            }
        });

        dialog_acceptBtn_stayedTime.setOnClickListener(new Button.OnClickListener() {   // 확인 버튼
            @Override
            public void onClick(View view) {
                boolean dateError=false;
                boolean timeError=false;

                LocalDate localDate_from=LocalDate.of(dialog_stayed_date_from.getYear(), dialog_stayed_date_from.getMonth()+1,
                        dialog_stayed_date_from.getDayOfMonth());
                LocalTime localTime_start= LocalTime.of(dialog_stayed_time_from.getHour(), dialog_stayed_time_from.getMinute());
                LocalDate localDate_to = LocalDate.of(dialog_stayed_date_to.getYear(), dialog_stayed_date_to.getMonth()+1,
                        dialog_stayed_date_to.getDayOfMonth());
                LocalTime localTime_end = LocalTime.of(dialog_stayed_time_to.getHour(), dialog_stayed_time_to.getMinute());

                if(localDate_from.isAfter(localDate_to))    //  시작 날짜 > 끝 날짜
                    dateError=true;
                if(localDate_from.isEqual(localDate_to))    //  같은 날짜
                    if(localTime_start.isAfter(localTime_end))  //  시작 시간 > 끝 시간
                        timeError=true;

                if(dateError){
                    Toast.makeText(getActivity(), "정확한 날짜를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    view_check_stayed_time.setChecked(false);
                    dialog_stayed_date_time.cancel();
                    view_stayed_time.setText("");
                    view_stayed_time.setVisibility(View.GONE);
                }
                else if(timeError) {
                    Toast.makeText(getActivity(), "정확한 시간을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    view_check_stayed_time.setChecked(false);
                    dialog_stayed_date_time.cancel();
                    view_stayed_time.setText("");
                    view_stayed_time.setVisibility(View.GONE);
                }
                else {
                        //  날짜, 시간 TextView에 띄우기
                        String concat = localDate_from.format(DateTimeFormatter.ofPattern("uuuu-MM-dd").
                                withResolverStyle(ResolverStyle.STRICT))
                                + " (" + localDate_from.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.KOREA) + ") "
                                + " " + localTime_start.format(DateTimeFormatter.ofPattern("HH:mm"))
                                + " ~ " + "\n"
                                + localDate_to.format(DateTimeFormatter.ofPattern("uuuu-MM-dd").
                                withResolverStyle(ResolverStyle.STRICT))
                                + " (" + localDate_to.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.KOREA) + ") "
                                + " " + localTime_end.format(DateTimeFormatter.ofPattern("HH:mm"));
                        view_stayed_time.setText(concat);

                        //  DB에 저장
                        inputData.setDateFrom(localDate_from.format(DateTimeFormatter.ofPattern("uuuu-MM-dd").
                                withResolverStyle(ResolverStyle.STRICT)));
                        inputData.setTimeStart(localTime_start.format(DateTimeFormatter.ofPattern("HH:mm")));
                        inputData.setDateTo(localDate_to.format(DateTimeFormatter.ofPattern("uuuu-MM-dd").
                                withResolverStyle(ResolverStyle.STRICT)));
                        inputData.setTimeEnd(localTime_end.format(DateTimeFormatter.ofPattern("HH:mm")));

                        if (dialog_stayed_date_time.isShowing())
                            dialog_stayed_date_time.dismiss();
                    }
            }
        });

        dialog_prevBtn_stayedTime.setOnClickListener(new View.OnClickListener() {   //  이전 버튼
            @Override
            public void onClick(View view) {
                dialog_date_time_to_set.setVisibility(View.INVISIBLE);
                dialog_date_time_from_set.setVisibility(View.VISIBLE);
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
        dialog_promised_date.setMinDate(date_now);

        dialog_acceptBtn_promisedTime.setOnClickListener(new Button.OnClickListener() { //  확인 버튼
            @Override
            public void onClick(View view) {
                LocalDate localDate = LocalDate.of(dialog_promised_date.getYear(), dialog_promised_date.getMonth()+1,
                        dialog_promised_date.getDayOfMonth());
                LocalTime localTime = LocalTime.of(dialog_promised_time.getHour(), dialog_promised_time.getMinute());
                if(!LocalTime.now().isAfter(localTime))
                {
                    Toast.makeText(getActivity(), "정확한 시간을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    dialog_promised_date_time.cancel();
                    view_memory.setChecked(true);
                }
                //  날짜, 시간 TextView에 띄우기
                else {
                    String concat = localDate.format(DateTimeFormatter.ofPattern("uuuu-MM-dd").
                            withResolverStyle(ResolverStyle.STRICT))
                            + " (" + localDate.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.KOREA) + ") "
                            + " " + localTime.format(DateTimeFormatter.ofPattern("HH:mm"));
                    view_promised_time.setText(concat);

                    //  DB에 저장
                    inputData.setDateFrom(localDate.format(DateTimeFormatter.ofPattern("uuuu-MM-dd").
                            withResolverStyle(ResolverStyle.STRICT)));
                    inputData.setTimeStart(localTime.format(DateTimeFormatter.ofPattern("HH:mm")));

                    if (checkIf24hoursBefore())
                        view_btn_setAlarm.setEnabled(true);
                    if (dialog_promised_date_time.isShowing())
                        dialog_promised_date_time.dismiss();
                }
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

    private boolean checkIf24hoursBefore(){
        LocalDate localDate = LocalDate.parse(inputData.getDateFrom(),DateTimeFormatter.ofPattern("uuuu-MM-dd").
                withResolverStyle(ResolverStyle.STRICT));
        LocalTime localTime = LocalTime.parse(inputData.getTimeStart(),DateTimeFormatter.ofPattern("HH:mm"));
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime before24hours = localDateTime.minusHours(24);
        return now.isAfter(before24hours);
    }
}