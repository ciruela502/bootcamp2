package com.marta.bootcamp2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    public static final String PACKAGE = "com.google.android.gm";
    private static final String TAG = "main";
    public EditText editTextAdresat;
    public EditText editTextTemat;
    public EditText editTextTresc;
    public ImageView imageViewAdd;
    public FloatingActionButton floatingSend;
    public Mail mail;
    public PackageManager packageManager;
    public List<ResolveInfo> packages;
    String poleAdres;
    String poleTemat;
    String poleTresc;

    public static boolean czyAdres(String mail) {
        Pattern pattern = Pattern.compile("^.+@.+\\..+$");
        Matcher matcher = pattern.matcher(mail);
        return matcher.find();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextAdresat = (EditText) findViewById(R.id.EditText_adresat);
        editTextTemat = (EditText) findViewById(R.id.EditText_temat);
        editTextTresc = (EditText) findViewById(R.id.EditText_tresc);
        imageViewAdd = (ImageView) findViewById(R.id.ImageView_dodaj);
        floatingSend = (FloatingActionButton) findViewById(R.id.floating_send);

        packageManager = getPackageManager();

        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null) {
            //Log.d(TAG, "onCreate: " + data.getHost());
            uriToMail(data);
        }
        floatingSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!walidacja()) {
                    return;
                }

                mail = new Mail(poleAdres, poleTemat, poleTresc);
                Intent mailIntent = new Intent(Intent.ACTION_SEND);
                mailIntent.setType("text/html");
                mailIntent.putExtra(Intent.EXTRA_EMAIL, mail.adresat);
                mailIntent.putExtra(Intent.EXTRA_SUBJECT, mail.temat);
                mailIntent.putExtra(Intent.EXTRA_TEXT, mail.tresc);

                String name = "";
                packages = packageManager.queryIntentActivities(mailIntent, 0);
                // Log.d(TAG, "onClick: sko");
                for (ResolveInfo info : packages) {
                    if (info.activityInfo.packageName.contains(PACKAGE)) {
                        Log.d(TAG, "onClick: info." + info.activityInfo.name);
                        name = info.activityInfo.name;
                    }
                }
                mailIntent.setClassName(PACKAGE, name);
                startActivity(mailIntent);

                //startActivity(Intent.createChooser(mailIntent, "Send Email"));
            }
        });
        imageViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Email.CONTENT_URI);
                contactPickerIntent.setType(ContactsContract.CommonDataKinds.Email.CONTENT_TYPE);
                startActivityForResult(contactPickerIntent, 1);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri contactData = data.getData();
                Cursor cursorEmail = getContentResolver().query(contactData, null, null, null, null);
                cursorEmail.moveToFirst();
                String emailAdres = cursorEmail.getString(cursorEmail.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                // Log.d(TAG, "onActivityResult: + "+emailAdd);
                editTextAdresat.setText(emailAdres);

            }
        }
    }

    private void uriToMail(Uri data) {

        String adres = data.getHost();
        adres = adres.replace('_', '.');
        adres = adres.concat("@droids.com");
        editTextAdresat.setText(adres);
    }

    private Boolean walidacja() {
        poleAdres = editTextAdresat.getText().toString();
        poleTemat = editTextTemat.getText().toString();
        poleTresc = editTextTresc.getText().toString();

        poleAdres = poleAdres.trim();
        poleTemat = poleTemat.trim();
        poleTresc = poleTresc.trim();

        if (poleAdres.isEmpty() || poleTemat.isEmpty() || poleTresc.isEmpty()) {
            Toast.makeText(MainActivity.this, "Uzupełnij wszystkie pola.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!czyAdres(poleAdres)) {
            Toast.makeText(MainActivity.this, "Niepoprawny adres.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}
