package com.example.ugproject.adapters;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ugproject.EditUserActivity;
import com.example.ugproject.R;
import com.example.ugproject.models.Usuario;
import com.example.ugproject.utils.Alerts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UserViewHolder> {

    private List<Usuario> userList;

    Context context;


    public UsuarioAdapter(List<Usuario> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }



    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_card, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Usuario user = userList.get(position);
        holder.txtNombre.setText(user.getNombre() + " " + user.getApellido());
        holder.txtCedula.setText(user.getCedula());
        holder.txtCorreo.setText(user.getCorreo());
        holder.txtCelular.setText(user.getCelular());
        holder.txtDireccion.setText(user.getDireccion());
        holder.txtCarrera.setText(user.getCarrera());
        holder.txtSemestre.setText("Semestre "+user.getSemestre().toString());


        // cargar imagen
        Context context = holder.itemView.getContext();
        Glide.with(context).load(user.getFoto()).into(holder.imgUser);



// Configurar el botón para reproducir audio
        holder.buttonPlayAudio.setOnClickListener(v -> playAudio(user.getSaludo(), context));
        holder.buttonViewPdf.setOnClickListener(v -> downloadAndOpenPdf(user.getTitulo(), context));
        holder.buttonViewPosition.setOnClickListener(
                v -> handleViewPosition(user.getLatitud_gps(), user.getLongitud_gps())
        );

        holder.itemView.setOnClickListener(v -> {
            // Abrir la actividad de edición de usuario
            Intent intent = new Intent(this.context, EditUserActivity.class);
            intent.putExtra("id", user.getId());

            this.context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private void handleViewPosition(String latitude, String longitude) {
        // Construct the Google Maps URL with the latitude and longitude
        String mapsUrl = "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;

        // Create an Intent with the ACTION_VIEW action and the URL
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl));

        // Check if there's an app to handle the intent before starting
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        } else {
            // Handle the case where a web browser is not available
        }
    }

    private void playAudio(byte[] audio, Context context) {
        try {
            String tempFileName = "temp_saludo.mp3";
            File tempFile = new File(context.getCacheDir(), tempFileName);

            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(audio);
            fos.close();

            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build());
            mediaPlayer.setDataSource(tempFile.getPath());
            mediaPlayer.prepareAsync();
            // Set up OnPreparedListener to start playback when prepared
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
            });

            // Set up OnCompletionListener to enable the play button after playback completes
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.reset();
            });


        } catch (IOException e) {
            Alerts.showAlertDialogWithText(context, "Error", "No se pudo reproducir el audio");
            Log.e("AUDIO", "Error al reproducir audio", e);
        }


    }

    private void downloadAndOpenPdf(byte[] pdf, Context context) {
        try {
            String pdfFileName = "pdfTemp.pdf";
            File pdfFile = new File(context.getCacheDir(), pdfFileName);
            FileOutputStream fos = new FileOutputStream(pdfFile);
            fos.write(pdf);
            fos.close();
            Uri pdfUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", pdfFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(pdfUri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } catch (Exception e) {
            // Manejar caso en el que no haya una aplicación para abrir PDFs instalada
            e.printStackTrace();
        }
    }


    public static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView txtNombre, txtCedula, txtCorreo, txtCelular, txtDireccion, txtCarrera, txtSemestre;
        ImageView imgUser;

        Button buttonPlayAudio, buttonViewPdf, buttonViewPosition;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtCedula = itemView.findViewById(R.id.txtCedula);
            txtCorreo = itemView.findViewById(R.id.txtEmail);
            txtCelular = itemView.findViewById(R.id.txtCelular);
            txtDireccion = itemView.findViewById(R.id.txtDireccion);
            txtCarrera = itemView.findViewById(R.id.txtCarrera);
            txtSemestre = itemView.findViewById(R.id.txtSemestre);

            imgUser = itemView.findViewById(R.id.imgUser);

            buttonPlayAudio = itemView.findViewById(R.id.buttonPlayAudio);
            buttonViewPdf = itemView.findViewById(R.id.buttonViewPdf);
            buttonViewPosition = itemView.findViewById(R.id.btnViewGPS);



        }
    }

    public void refreshEstudiantes(List<Usuario> nuevosEstudiantes) {
        userList = nuevosEstudiantes;
        notifyDataSetChanged();
    }



}
