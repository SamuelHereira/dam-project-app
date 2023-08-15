package com.example.ugproject.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.example.ugproject.models.Usuario;

import java.util.ArrayList;
import java.util.List;

public class DbController extends SQLiteOpenHelper {
    public static final String NombreDB = "Login.db";
    public static final String TablaUsuarios = "usuarios";
    public static final String ColUsername = "username";
    public static final String ColPassword = "password";
    public static final String TablaEstudiantes = "estudiantes";
    public static final String ColId = "id";
    public static final String ColCedula = "cedula";
    public static final String ColNombre = "nombre";
    public static final String ColApellido = "apellido";
    public static final String ColCorreo = "correo";
    public static final String ColCelular = "celular";
    public static final String ColDireccion = "direccion";
    public static final String ColCarrera = "carrera";
    public static final String ColSemestre = "semestre";
    public static final String ColFoto = "foto";
    public static final String ColSaludoAudio = "saludo_audio";
    public static final String ColTituloPDF = "titulo_pdf";
    public static final String ColEstado = "estado";
    public static final String ColLatitud = "latitud";
    public static final String ColLongitud = "longitud";
    

    private static final int VersionDB = 1;

    public DbController(Context context) {
        super(context, NombreDB, null, VersionDB);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TablaUsuarios + " (" +
                ColUsername + " TEXT PRIMARY KEY," +
                ColPassword + " TEXT" +
                ")";
        db.execSQL(createTableQuery);
        createTableQuery = "CREATE TABLE " + TablaEstudiantes + " (" +
                ColId + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ColCedula + " TEXT," +
                ColNombre + " TEXT," +
                ColApellido + " TEXT," +
                ColCorreo + " TEXT," +
                ColCelular + " TEXT," +
                ColDireccion + " TEXT," +
                ColCarrera + " TEXT," +
                ColSemestre + " TEXT," +
                ColLatitud + " TEXT," +
                ColLongitud + " TEXT," +
                ColFoto + " BLOB," +
                ColSaludoAudio + " BLOB," +
                ColTituloPDF + " BLOB," +
                ColEstado + " TEXT DEFAULT 'A'" +
                ")";

        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Implementar si se requieren cambios en la estructura de la tabla en futuras actualizaciones.
    }

    public int agregarUsuarioApp(String username, String password) {

        if(username.isEmpty() || password.isEmpty()){
            return 0;
        }

        if(this.usuarioAppExiste(username, password)){
            return 1;
        }else{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(ColUsername, username);
            contentValues.put(ColPassword, password);

            long resultado = db.insert(TablaUsuarios, null, contentValues);
            if(resultado != -1){
                return 2;
            }else{
                return 3;
            }
        }
    }

    public boolean usuarioAppExiste(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columnas = {ColUsername};
        String seleccion = ColUsername + " = ? AND " + ColPassword + " = ?";
        String[] argumentos = {username, password};

        Cursor cursor = db.query(TablaUsuarios, columnas, seleccion, argumentos, null, null, null);
        int count = cursor.getCount();
        cursor.close();

        return count > 0;
    }


    public static List<Usuario> obtenerEstudiantes(Context context) {
        List<Usuario> listaEstudiantes = new ArrayList<>();
        SQLiteDatabase db = new DbController(context).getReadableDatabase();
        String[] columnas = {
                ColId, ColCedula, ColNombre, ColApellido, ColCorreo, ColCelular,
                ColDireccion, ColCarrera, ColSemestre, ColFoto, ColSaludoAudio,
                ColTituloPDF, ColEstado, ColLatitud, ColLongitud
        };

        Cursor cursor = db.query(TablaEstudiantes, columnas, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Usuario estudiante = new Usuario();
                estudiante.setId(cursor.getInt(0));
                estudiante.setCedula(cursor.getString(1));
                estudiante.setNombre(cursor.getString(2));
                estudiante.setApellido(cursor.getString(3));
                estudiante.setCorreo(cursor.getString(4));
                estudiante.setCelular(cursor.getString(5));
                estudiante.setDireccion(cursor.getString(6));
                estudiante.setCarrera(cursor.getString(7));
                estudiante.setSemestre(cursor.getString(8));
                estudiante.setFoto(cursor.getBlob(9));
                estudiante.setSaludo(cursor.getBlob(10));
                estudiante.setTitulo(cursor.getBlob(11));
                estudiante.setEstado(cursor.getString(12));
                estudiante.setLatitud_gps(cursor.getString(13));
                estudiante.setLongitud_gps(cursor.getString(14));
                listaEstudiantes.add(estudiante);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return listaEstudiantes;
    }

    public static List<Usuario> buscarEstudiantes(Context context, String[] columnasBusqueda, String valor, Integer id) {
        List<Usuario> listaEstudiantes = new ArrayList<>();
        SQLiteDatabase db = new DbController(context).getReadableDatabase();
        String[] columnas = {
                ColId, ColCedula, ColNombre, ColApellido, ColCorreo, ColCelular,
                ColDireccion, ColCarrera, ColSemestre, ColFoto, ColSaludoAudio,
                ColTituloPDF, ColEstado, ColLatitud, ColLongitud
        };

        String whereClause = null;
        String[] whereArgs = null;

        if (valor != null && !valor.isEmpty()) {
            // Use the LIKE statement for pattern matching
            StringBuilder whereBuilder = new StringBuilder();
            for (String columna : columnasBusqueda) {
                if (whereBuilder.length() > 0) {
                    whereBuilder.append(" OR ");
                }
                whereBuilder.append(columna).append(" LIKE ?");
            }
            whereClause = whereBuilder.toString();

            // Modify the value to include the pattern (e.g., %value%)
            whereArgs = new String[columnasBusqueda.length];
            for (int i = 0; i < columnasBusqueda.length; i++) {
                whereArgs[i] = "%" + valor + "%";
            }
        }

        Cursor cursor = db.query(TablaEstudiantes, columnas, whereClause, whereArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Usuario estudiante = new Usuario();
                estudiante.setId(cursor.getInt(0));
                estudiante.setCedula(cursor.getString(1));
                estudiante.setNombre(cursor.getString(2));
                estudiante.setApellido(cursor.getString(3));
                estudiante.setCorreo(cursor.getString(4));
                estudiante.setCelular(cursor.getString(5));
                estudiante.setDireccion(cursor.getString(6));
                estudiante.setCarrera(cursor.getString(7));
                estudiante.setSemestre(cursor.getString(8));
                estudiante.setFoto(cursor.getBlob(9));
                estudiante.setSaludo(cursor.getBlob(10));
                estudiante.setTitulo(cursor.getBlob(11));
                estudiante.setEstado(cursor.getString(12));
                estudiante.setLatitud_gps(cursor.getString(13));
                estudiante.setLongitud_gps(cursor.getString(14));
                listaEstudiantes.add(estudiante);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return listaEstudiantes;
    }


    public long insertarEstudiante(Usuario estudiante) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(ColCedula, estudiante.getCedula());
        contentValues.put(ColNombre, estudiante.getNombre());
        contentValues.put(ColApellido, estudiante.getApellido());
        contentValues.put(ColCorreo, estudiante.getCorreo());
        contentValues.put(ColCelular, estudiante.getCelular());
        contentValues.put(ColDireccion, estudiante.getDireccion());
        contentValues.put(ColCarrera, estudiante.getCarrera());
        contentValues.put(ColSemestre, estudiante.getSemestre());
        contentValues.put(ColFoto, estudiante.getFoto());
        contentValues.put(ColSaludoAudio, estudiante.getSaludo());
        contentValues.put(ColTituloPDF, estudiante.getTitulo());
        contentValues.put(ColEstado, estudiante.getEstado());
        contentValues.put(ColLatitud, estudiante.getLatitud_gps());
        contentValues.put(ColLongitud, estudiante.getLongitud_gps());

        long newRowId = db.insert(TablaEstudiantes, null, contentValues);
        db.close();
        return newRowId;
    }

    public static void eliminarEstudiante(Context context, int estudianteId) {
        SQLiteDatabase db = new DbController(context).getReadableDatabase();
        String whereClause = ColId + " = ?";
        String[] whereArgs = {String.valueOf(estudianteId)};

        db.delete(TablaEstudiantes, whereClause, whereArgs);
        db.close();
    }

    public static Usuario obtenerEstudiantePorId(Context context, int estudianteId) {
        SQLiteDatabase db = new DbController(context).getReadableDatabase();
        String[] columnas = {
                ColId, ColCedula, ColNombre, ColApellido, ColCorreo, ColCelular,
                ColDireccion, ColCarrera, ColSemestre, ColFoto, ColSaludoAudio,
                ColTituloPDF, ColEstado, ColLatitud, ColLongitud
        };

        String seleccion = ColId + " = ?";
        String[] argumentos = {String.valueOf(estudianteId)};

        Cursor cursor = db.query(TablaEstudiantes, columnas, seleccion, argumentos, null, null, null);

        Usuario estudiante = null;

        if (cursor.moveToFirst()) {
            estudiante = new Usuario();
            estudiante.setId(cursor.getInt(0));
            estudiante.setCedula(cursor.getString(1));
            estudiante.setNombre(cursor.getString(2));
            estudiante.setApellido(cursor.getString(3));
            estudiante.setCorreo(cursor.getString(4));
            estudiante.setCelular(cursor.getString(5));
            estudiante.setDireccion(cursor.getString(6));
            estudiante.setCarrera(cursor.getString(7));
            estudiante.setSemestre(cursor.getString(8));
            estudiante.setFoto(cursor.getBlob(9));
            estudiante.setSaludo(cursor.getBlob(10));
            estudiante.setTitulo(cursor.getBlob(11));
            estudiante.setEstado(cursor.getString(12));
            estudiante.setLatitud_gps(cursor.getString(13));
            estudiante.setLongitud_gps(cursor.getString(14));
        }

        cursor.close();
        return estudiante;
    }


    public void modificarEstudiante(Context context, Usuario estudiante) {
        SQLiteDatabase db = new DbController(context).getReadableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(ColCedula, estudiante.getCedula());
        contentValues.put(ColNombre, estudiante.getNombre());
        contentValues.put(ColApellido, estudiante.getApellido());
        contentValues.put(ColCorreo, estudiante.getCorreo());
        contentValues.put(ColCelular, estudiante.getCelular());
        contentValues.put(ColDireccion, estudiante.getDireccion());
        contentValues.put(ColCarrera, estudiante.getCarrera());
        contentValues.put(ColSemestre, estudiante.getSemestre());
        contentValues.put(ColFoto, estudiante.getFoto());
        contentValues.put(ColTituloPDF, estudiante.getTitulo());
        contentValues.put(ColSaludoAudio, estudiante.getSaludo());
        contentValues.put(ColLatitud, estudiante.getLatitud_gps());
        contentValues.put(ColLongitud, estudiante.getLongitud_gps());

        String whereClause = ColId + " = ?";
        String[] whereArgs = {String.valueOf(estudiante.getId())};

        db.update(TablaEstudiantes, contentValues, whereClause, whereArgs);
        db.close();
    }

}