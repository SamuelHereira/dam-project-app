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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ugproject.EditUserActivity;
import com.example.ugproject.R;
import com.example.ugproject.models.Usuario;
import com.example.ugproject.utils.Alerts;

import java.io.IOException;
import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UserViewHolder> {

    private List<Usuario> userList;

    public UsuarioAdapter(List<Usuario> userList) {
        this.userList = userList;
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


        holder.itemView.setOnClickListener(v -> {
            // Abrir la actividad de edición de usuario
            Intent intent = new Intent(context, EditUserActivity.class);
            intent.putExtra("id", user.getId());
            intent.putExtra("nombre", user.getNombre());
            intent.putExtra("apellido", user.getApellido());
            intent.putExtra("cedula", user.getCedula());
            intent.putExtra("correo", user.getCorreo());
            intent.putExtra("celular", user.getCelular());
            intent.putExtra("direccion", user.getDireccion());
            intent.putExtra("carrera", user.getCarrera());
            intent.putExtra("semestre", user.getSemestre());
            intent.putExtra("saludo", user.getSaludo());
            intent.putExtra("foto", user.getFoto());
            intent.putExtra("titulo", user.getTitulo());
            intent.putExtra("latitud", user.getLatitud_gps());
            intent.putExtra("longitud", user.getLongitud_gps());

            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private void playAudio(String audioUrl, Context context) {
        try {

            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build());
            mediaPlayer.setDataSource(audioUrl);
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
        }


    }

    private void downloadAndOpenPdf(String pdfUrl, Context context) {
        // Aquí puedes utilizar una librería para descargar el PDF desde la URL,
        // como DownloadManager u otras bibliotecas de gestión de descargas.

        // Por ejemplo, utilizando DownloadManager (asegúrate de manejar las excepciones correctamente):
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(pdfUrl));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "archivo.pdf");

        long downloadId = downloadManager.enqueue(request);

        // También puedes abrir el PDF después de la descarga utilizando un intent
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(pdfUrl), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Manejar caso en el que no haya una aplicación para abrir PDFs instalada
            e.printStackTrace();
        }
    }


    public static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView txtNombre, txtCedula, txtCorreo, txtCelular, txtDireccion, txtCarrera, txtSemestre;
        ImageView imgUser;

        Button buttonPlayAudio, buttonViewPdf;

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



        }
    }



}
