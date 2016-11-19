package org.underpressureapps.eybarro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class UploadActivity extends AppCompatActivity {
    //El manejo de imagen lo obtuve de acá: https://www.youtube.com/watch?v=_xIWkCJZCu0; git: https://github.com/discospiff/PlantPlaces15s305
    public static final int IMAGE_GALLERY_REQUEST = 20;
    private ImageView imgPicture;
    private Spinner spinner;
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    private EditText descripcion;
    private EditText hora;
    private Intent datosfoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        imgPicture = (ImageView) findViewById(R.id.imgPicture);
        spinner = (Spinner) findViewById(R.id.spinner);
        descripcion = (EditText) findViewById(R.id.editTextDescripcion);
        hora = (EditText) findViewById(R.id.editTextHora);
    }

    public void onImageGalleryClicked(View v) {
        // invoke the image gallery using an implict intent.
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        // where do we want to find the data?
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();
        // finally, get a URI representation
        Uri data = Uri.parse(pictureDirectoryPath);

        // set the data and type.  Get all image types.
        photoPickerIntent.setDataAndType(data, "image/*");

        // we will invoke this activity, and get something back from it.
        startActivityForResult(photoPickerIntent, IMAGE_GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // if we are here, everything processed successfully.
            datosfoto=data;
            if (requestCode == IMAGE_GALLERY_REQUEST) {
                // if we are here, we are hearing back from the image gallery.

                // the address of the image on the SD Card.
                Uri imageUri = data.getData();

                // declare a stream to read the image data from the SD Card.
                InputStream inputStream;

                // we are getting an input stream, based on the URI of the image.
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);

                    // get a bitmap from the stream.
                    Bitmap image = BitmapFactory.decodeStream(inputStream);


                    // show the image to the user
                    imgPicture.setImageBitmap(image);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    // show a message to the user indictating that the image is unavailable.
                    Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    public void addListenerOnSpinnerItemSelection() {
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public void onClick(View v) {
        //Debería sacar el valor seleccionado del spinner; https://www.mkyong.com/android/android-spinner-drop-down-list-example/
        Toast.makeText(this,
                "OnClickListener : " +
                        "\nSpinner : "+ String.valueOf(spinner.getSelectedItem()),
                Toast.LENGTH_SHORT).show();
    }


    public void onClickEnviar(View view) {
        if(descripcion.getText().toString().equals("") || descripcion.getText().equals(null) ) {
            Toast.makeText(this, "Es necesario adjuntar descripción del informe",
                    Toast.LENGTH_LONG).show();
        }
        System.out.println(spinner.getSelectedItem().toString());
        Uri uri = datosfoto.getData();

        StorageReference filepath = mStorage.child("Photos").child(uri.getLastPathSegment());
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(UploadActivity.this,"Upload Done",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
