package com.example.ugproject;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.ugproject.adapters.UsuarioAdapter;
import com.example.ugproject.api.CustomPostAsyncTask;
import com.example.ugproject.api.CustomPutAsyncTask;
import com.example.ugproject.db.DbController;
import com.example.ugproject.models.Usuario;
import com.example.ugproject.utils.Alerts;
import com.example.ugproject.utils.UriUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class EditUserActivity extends AppCompatActivity {

    private static final int PICK_FROM_CAMERA = 7;
    private static final int REQUEST_LOCATION = 1;

    private static final int REQUEST_PERMISSION = 3;
    private static final int REQUEST_IMAGE_GET = 2;
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 2;

    private static final String URL_SERVER = "http://192.168.100.23:3000";

    Context context = null;
    private Usuario usuarioEdit = null;
    private UsuarioAdapter estudiantesAdapter;


    private Integer id;
    private String actualPdfUrl, fileName, saludo, pdfPath, imagePath;

    private EditText txtNombre, txtApellido, txtCedula, txtCorreo, txtCelular, txtDireccion, txtCarrera, txtSemestre, txtLatitud, txtLongitud;
    private Button btnPlay, btnSaludo, btnPlayUrl, btnDescargarPdf;
    ;

    private MediaRecorder recorder = null;

    public Uri uriImage, uriPdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        Bundle bundle = getIntent().getExtras();

        context = this;

        id = bundle.getInt("id");

        usuarioEdit = DbController.obtenerEstudiantePorId(context, id);

        Log.println(Log.ASSERT, "USUARIO EDIT", usuarioEdit.toString());

        Toolbar toolbar = findViewById(R.id.toolbar);

        // Manejar el evento del botón de regreso
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Log.println(Log.INFO, "id", id.toString());




        txtNombre = findViewById(R.id.txtNombre);
        txtApellido = findViewById(R.id.txtApellido);
        txtCedula = findViewById(R.id.txtCedula);
        txtCorreo = findViewById(R.id.txtEmail);
        txtCelular = findViewById(R.id.txtCelular);
        txtDireccion = findViewById(R.id.txtDireccion);
        txtCarrera = findViewById(R.id.txtCarrera);
        txtSemestre = findViewById(R.id.txtSemestre);
        txtLatitud = findViewById(R.id.txtLatitud);
        txtLongitud = findViewById(R.id.txtLongitud);

        btnSaludo = findViewById(R.id.btnSaludo);
        btnPlay = findViewById(R.id.btnPlay);
        btnDescargarPdf = findViewById(R.id.btnDescargarPDF);

        this.saludo = saludo;


        txtNombre.setText(usuarioEdit.getNombre());
        txtApellido.setText(usuarioEdit.getApellido());
        txtCedula.setText(usuarioEdit.getCedula());
        txtCorreo.setText(usuarioEdit.getCorreo());
        txtCelular.setText(usuarioEdit.getCelular());
        txtDireccion.setText(usuarioEdit.getDireccion());
        txtCarrera.setText(usuarioEdit.getCarrera());
        txtSemestre.setText(usuarioEdit.getSemestre());
        txtLatitud.setText(usuarioEdit.getLatitud_gps());
        txtLongitud.setText(usuarioEdit.getLongitud_gps());

        ActivityCompat.requestPermissions( this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE
                }, 1
        );
        // inmage view set image from url
        ImageView imageView = findViewById(R.id.imageView2);
        imageView.setVisibility(View.VISIBLE);



        Log.println(Log.ASSERT, "Foto", usuarioEdit.getFoto().toString());
        Glide.with(context).load(usuarioEdit.getFoto()).into(imageView);


        // request permissions
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 1);


        Button btnRegistrar = findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(v -> createAndExecutePutRequest());

    }


    public void handleSelectPhoto(View view) {

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 3);

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

//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriImage);
//                if (bitmap.getByteCount() > 1000000) {
//                    Toast.makeText(this, "La imagen es muy pesada", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

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

                TextView textView = findViewById(R.id.txtArchivo);
                textView.setText(pdfPath);
                textView.setVisibility(View.VISIBLE);

                // Now you can upload the newFile to the server
                // Remember to handle exceptions and perform these operations in a background thread
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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
            btnPlayUrl.setVisibility(View.GONE);
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

        String url;

        Log.println(Log.INFO, "saludo", saludo);

        if (recorder == null) {
            url = fileName;
        } else {
            url = saludo;
        }

//        play audio from url
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
        Log.println(Log.INFO, "url", url);
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                mp.release();
                return false;
            });

        } catch (Exception e) {
            // make something
            Alerts.showAlertDialogWithText(this, "Error", "Error al reproducir audio");
            mediaPlayer.release();
            Log.println(Log.ERROR, "Error", e.toString());
        }


    }

    public void handleDownloadPdf(View view) {
//        download pdf from url and open with default pdf viewer
        String url = "https://www.ug.edu.ec/wp-content/uploads/2019/02/Reglamento-de-Grados-y-Titulos.pdf";

        // download pdf
        // Por ejemplo, utilizando DownloadManager (asegúrate de manejar las excepciones correctamente):
        DownloadManager downloadManager = (DownloadManager) view.getContext().getSystemService(Context.DOWNLOAD_SERVICE);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(actualPdfUrl));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "archivo.pdf");

        long downloadId = downloadManager.enqueue(request);

        // También puedes abrir el PDF después de la descarga utilizando un intent
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(actualPdfUrl), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            view.getContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Manejar caso en el que no haya una aplicación para abrir PDFs instalada
            e.printStackTrace();
        }
    }

    private boolean validateCanSend() {
        if (txtCedula.getText().toString().trim().length() > 0 && txtCedula.getError() == null &&
                txtNombre.getText().toString().trim().length() > 0 && txtApellido.getText().toString().trim().length() > 0
                && txtCorreo.getText().toString().trim().length() > 0 && txtCorreo.getError() == null && txtCelular.getText().toString().trim().length() > 0
                && txtDireccion.getText().toString().trim().length() > 0 && txtCarrera.getText().toString().trim().length() > 0
                && txtSemestre.getText().toString().trim().length() > 0 && txtSemestre.getError() == null
                && txtLatitud.getText().toString().trim().length() > 0 && txtLongitud.getText().toString().trim().length() > 0
        ) {
            return true;
        } else {
            return false;
        }
    }


    private void createAndExecutePutRequest() {

        try {
            if (!validateCanSend()) {
                Alerts.showAlertDialogWithText(context, "Error", "Por favor llene todos los campos");
                return;
            }

            usuarioEdit.setCedula(txtCedula.getText().toString());
            usuarioEdit.setNombre(txtNombre.getText().toString());
            usuarioEdit.setApellido(txtApellido.getText().toString());
            usuarioEdit.setCorreo(txtCorreo.getText().toString());
            usuarioEdit.setCelular(txtCelular.getText().toString());
            usuarioEdit.setDireccion(txtDireccion.getText().toString());
            usuarioEdit.setCarrera(txtCarrera.getText().toString());
            usuarioEdit.setSemestre(txtSemestre.getText().toString());
            usuarioEdit.setLatitud_gps(txtLatitud.getText().toString());
            usuarioEdit.setLongitud_gps(txtLongitud.getText().toString());

            Log.println(Log.ASSERT, "EDIT", usuarioEdit.toString());

            File saludo = null, foto = null, titulo = null;



            try {
                saludo = new File(fileName);
                byte[] bytesSaludo = new byte[(int) saludo.length()];
                try (FileInputStream fis = new FileInputStream(saludo)) {
                    fis.read(bytesSaludo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                byte[] bytesImage = readBytesFromUri(getContentResolver(), uriImage);
                usuarioEdit.setFoto(bytesImage);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                byte[] bytesPdf = readBytesFromUri(getContentResolver(), uriPdf);
                usuarioEdit.setTitulo(bytesPdf);

            } catch (Exception e) {
                e.printStackTrace();
            }

            DbController db = new DbController(context);
            db.modificarEstudiante(context, usuarioEdit);



            // show alert
            Alerts.showAlertDialogWithText(context, "Usuario actualizado correctamente", "Aceptar");
            List<Usuario> estudiantesList = DbController.obtenerEstudiantes(context);
            estudiantesAdapter.refreshEstudiantes(estudiantesList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] readBytesFromUri(ContentResolver contentResolver, Uri uri) throws IOException {
        InputStream inputStream = contentResolver.openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        if (inputStream != null) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            inputStream.close();
        }
        return byteBuffer.toByteArray();
    }

    private byte[] imagemTratada(byte[] imagem_img){

        while (imagem_img.length > 500000){
            Bitmap bitmap = BitmapFactory.decodeByteArray(imagem_img, 0, imagem_img.length);
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int)(bitmap.getWidth()*0.8), (int)(bitmap.getHeight()*0.8), true);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.PNG, 100, stream);
            imagem_img = stream.toByteArray();
        }
        return imagem_img;

    }

}