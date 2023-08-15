package com.example.ugproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.ugproject.api.CustomPostAsyncTask;
import com.example.ugproject.services.CustomLocationProvider;
import com.example.ugproject.services.LocationCallback;
import com.example.ugproject.services.LocationService;
import com.example.ugproject.utils.Alerts;
import com.example.ugproject.utils.TextValidator;
import com.example.ugproject.utils.UriUtils;
import com.example.ugproject.utils.Validations;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity implements LocationCallback {
    private static final int PICK_FROM_CAMERA = 7;
    private static final int REQUEST_LOCATION = 1;
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 2;

    private static final int REQUEST_PERMISSION = 1;
    private static final int REQUEST_IMAGE_GET = 2;
    private static String fileName = null;
    //     layout
    public MapView mapView = null;
    // inputs
    public EditText txtCedula = null;
    public EditText txtNombre = null;
    public EditText txtApellido = null;
    public EditText txtEmail = null;
    public EditText txtCelular = null;
    public EditText txtDireccion = null;
    public EditText txtCarrera = null;
    public EditText txtSemestre = null;
    public TextView txtUbicacion = null;

    public String latitude = "";
    public String altitude = "";
    public Uri uriImage;
    public Uri uriPdf;
    public String pdfPath;
    public String imagePath;
    private Context context = null;
    private GoogleMap googleMap;
    //    audio
    private MediaRecorder recorder = null;
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    // buttons
    private Button btnSaludo = null;
    private Button btnPlay = null;
    private Button btnRegistrar = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        // initialize context
        context = this;

        //initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Habilitar el botón de regreso en la Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Manejar el evento del botón de regreso
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // request permissions
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);


        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE
                }, 1
        );

        // initialize inputs
        txtCedula = findViewById(R.id.txtCedula);
        txtNombre = findViewById(R.id.txtNombre);
        txtApellido = findViewById(R.id.txtApellido);
        txtEmail = findViewById(R.id.txtEmail);
        txtCelular = findViewById(R.id.txtCelular);
        txtDireccion = findViewById(R.id.txtDireccion);
        txtCarrera = findViewById(R.id.txtCarrera);
        txtSemestre = findViewById(R.id.txtSemestre);
        txtUbicacion = findViewById(R.id.txtUbicacion);


        // validate inputs
        txtCedula.addTextChangedListener(new TextValidator(txtCedula) {
            @Override
            public void validate(TextView textView, String text) {
                if (!Validations.validateOnlyNumbers(text)) {
                    txtCedula.setError("La cédula solo puede contener números y debe tener 10 dígitos");
                    return;
                }
                if (!Validations.validateCedula(text)) {
                    txtCedula.setError("La cédula no es válida");
                }

            }
        });
        txtEmail.addTextChangedListener(new TextValidator(txtEmail) {
            @Override
            public void validate(TextView textView, String text) {
                if (!Validations.validateEmail(text)) {
                    txtEmail.setError("El email no es válido");
                }
            }
        });

        txtCelular.addTextChangedListener(new TextValidator(txtCelular) {
              @Override
              public void validate(TextView textView, String text) {
                  if (!Validations.validateOnlyNumbers(text)) {
                      txtCelular.setError("El celular solo puede contener números y debe tener 10 dígitos");
                      return;
                  }
              }
          }
        );

        // initialize location
        startService(new Intent(this, LocationService.class));
        CustomLocationProvider.getInstance().setLocationCallback(this);

//        mapView = (MapView) findViewById(R.id.mapView);
//        mapView.onCreate(savedInstanceState);
//        mapView.getMapAsync(this);

        // initialize buttons
        btnSaludo = findViewById(R.id.btnSaludo);
        btnPlay = findViewById(R.id.btnPlay);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAndExecutePostRequest();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 3);
            } else {
                // Handle permission denied case
            }
        }
    }

    @Override
    public void onLocationUpdated(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        this.latitude = String.valueOf(latitude);
        this.altitude = String.valueOf(longitude);

        LatLng latLng = new LatLng(latitude, longitude);
        txtUbicacion.setText("Latitud: " + latitude + ", Longitud: " + longitude);
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
////        marker
//        googleMap.addMarker(new MarkerOptions().position(latLng).title("Mi ubicación"));
//        mapView.setVisibility(View.VISIBLE);
    }

//    @Override
//    public void onMapReady(@NonNull GoogleMap map) {
//        googleMap = map;
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mapView.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mapView.onPause();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mapView.onDestroy();
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        mapView.onLowMemory();
//    }


    public void handleSelectPhoto(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 3);
        }

    }


    public void handleSelectPdf(View view) {
        openPdfPicker();
    }

    private void openPdfPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, 4);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3 && resultCode == RESULT_OK && data != null) {
            uriImage = data.getData();
            ImageView imageView = findViewById(R.id.imageView2);
            imageView.setImageURI(uriImage);
            imageView.setVisibility(View.VISIBLE);

            try {
                // Open an InputStream from the selected image Uri
                InputStream inputStream = getContentResolver().openInputStream(uriImage);

                // Create a new File and OutputStream to save the copied content
                File newFile = UriUtils.createImageFile(context); // Implement this method
                OutputStream outputStream = new FileOutputStream(newFile);

                // Copy the content from the InputStream to the OutputStream
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                // Close the streams
                inputStream.close();
                outputStream.close();

                Log.println(Log.INFO, "newFile", newFile.getAbsolutePath());

                imagePath = newFile.getAbsolutePath();

                // Now you can upload the newFile to the server
                // Remember to handle exceptions and perform these operations in a background thread
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == 4 && resultCode == RESULT_OK && data != null) {
            uriPdf = data.getData();

            TextView textView = findViewById(R.id.txtArchivo);
            textView.setText(uriPdf.toString());
            textView.setVisibility(View.VISIBLE);

            try {
                // Open an InputStream from the selected image Uri
                InputStream inputStream = getContentResolver().openInputStream(uriPdf);

                // Create a new File and OutputStream to save the copied content
                File newFile = UriUtils.createPdfFile(context); // Implement this method
                OutputStream outputStream = new FileOutputStream(newFile);

                // Copy the content from the InputStream to the OutputStream
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                // Close the streams
                inputStream.close();
                outputStream.close();

                Log.println(Log.INFO, "newFilePDF", newFile.getAbsolutePath());

                pdfPath = newFile.getAbsolutePath();

                // Now you can upload the newFile to the server
                // Remember to handle exceptions and perform these operations in a background thread
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //    audio
    public void handleRecordAudio(View view) {
        if (recorder == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
            } else {
                startRecording();
                btnSaludo.setText("Detener grabación");
            }
        } else {
            stopRecording();
            recorder = null;
            btnSaludo.setText("Iniciar grabación");
        }
    }

    public void startRecording() {
        try {
            btnPlay.setVisibility(View.INVISIBLE);
            fileName = getExternalCacheDir().getAbsolutePath();
            fileName += "/audiorecordtest.mp3";
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            recorder.setOutputFile(fileName);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            recorder.prepare();

            recorder.start();
            Toast.makeText(this, "Grabando audio", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }


    public void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        Toast.makeText(this, "Audio grabado", Toast.LENGTH_SHORT).show();
        btnPlay.setVisibility(View.VISIBLE);
    }

    public void playRecording(View view) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileName);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(this, "Reproduciendo audio", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validateCanSend() {
        if (fileName != null && uriImage != null && pdfPath != null && txtCedula.getText().toString().trim().length() > 0 && txtCedula.getError() == null &&
                txtNombre.getText().toString().trim().length() > 0 && txtApellido.getText().toString().trim().length() > 0 && txtEmail.getText().toString().trim().length() > 0 && txtEmail.getError() == null && txtCelular.getText().toString().trim().length() > 0 && txtDireccion.getText().toString().trim().length() > 0 && txtCarrera.getText().toString().trim().length() > 0 && txtSemestre.getText().toString().trim().length() > 0 && txtSemestre.getError() == null) {
            return true;
        } else {
            return false;
        }
    }

    private void resetAllFields() {
        txtCedula.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtEmail.setText("");
        txtCelular.setText("");
        txtDireccion.setText("");
        txtCarrera.setText("");
        txtSemestre.setText("");
        txtCedula.setError(null);
        txtEmail.setError(null);
        txtSemestre.setError(null);

        btnPlay.setVisibility(View.INVISIBLE);
        btnSaludo.setText("Iniciar grabación");

        ImageView imageView = findViewById(R.id.imageView2);
        imageView.setVisibility(View.INVISIBLE);

        pdfPath = null;
        uriImage = null;
        fileName = null;
    }

    private void createAndExecutePostRequest() {
        String url = "http://192.168.100.23:3000/usuarios/create";

        try {
            if (!validateCanSend()) {
                Alerts.showAlertDialogWithText(context, "Error", "Por favor llene todos los campos");
                return;
            }

            File saludo = new File(fileName);
            File foto = new File(imagePath);
            File titulo = new File(pdfPath);

            JSONObject requestBodyJson = new JSONObject();
            requestBodyJson.put("cedula", this.txtCedula.getText());
            requestBodyJson.put("nombre", this.txtNombre.getText());
            requestBodyJson.put("apellido", this.txtApellido.getText());
            requestBodyJson.put("correo", this.txtEmail.getText());
            requestBodyJson.put("celular", this.txtCelular.getText());
            requestBodyJson.put("direccion", this.txtDireccion.getText());
            requestBodyJson.put("carrera", this.txtCarrera.getText());
            requestBodyJson.put("semestre", this.txtSemestre.getText());
            requestBodyJson.put("latitud_gps", this.latitude);
            requestBodyJson.put("longitud_gps", this.altitude);


            List<CustomPostAsyncTask.FileItem> files = new ArrayList<>();
            files.add(new CustomPostAsyncTask.FileItem(saludo, "saludo", saludo.getName()));
            files.add(new CustomPostAsyncTask.FileItem(foto, "foto", foto.getName()));
            files.add(new CustomPostAsyncTask.FileItem(titulo, "titulo", titulo.getName()));

            CustomPostAsyncTask customPostAsyncTask = new CustomPostAsyncTask(this, url, requestBodyJson, files, new CustomPostAsyncTask.OnResultListener() {
                @Override
                public void onSuccess(String result) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        Integer statusCode = jsonObject.getInt("statusCode");

                        if (statusCode == 200) {
                            // show alert
                            Alerts.showAlertDialogWithText(context, "Usuario registrado correctamente", "Aceptar");
                            resetAllFields();
                        } else {
                            String mensaje = jsonObject.getString("message");
                            Alerts.showAlertDialogWithText(context, mensaje, "Aceptar");
                        }
                    } catch (JSONException e) {
                        String mensaje = "Error al registrar usuario";
                        Alerts.showAlertDialogWithText(context, mensaje, "Aceptar");
                    }
                }

                @Override
                public void onError(String result) {
                    Alerts.showAlertDialogWithText(context, "Error al registrar usuario", "Aceptar");
                }
            });

            customPostAsyncTask.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


