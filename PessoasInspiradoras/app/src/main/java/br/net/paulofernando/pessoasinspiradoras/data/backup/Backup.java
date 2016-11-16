package br.net.paulofernando.pessoasinspiradoras.data.backup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.util.parser.PersonParser;
import br.net.paulofernando.pessoasinspiradoras.util.parser.XMLPullParserHandler;
import br.net.paulofernando.pessoasinspiradoras.util.Utils;

public class Backup {

    private File myDir;
    private String filename = "backup.xml";

    private Context context;

    public Backup(Context context) {
        this.context = context;
        String root = Environment.getExternalStorageDirectory().toString();
        myDir = new File(root + "/"
                + "Inspirational People"
                + "/backup");
        filename = "backup.xml";
    }

    public boolean hasLocalBackup() {
        File myFile = new File(myDir + "/" + filename);

        return myFile.exists();
    }

    public long getLocalBackupLastModified() {
        File myFile = new File(myDir + "/" + filename);
        return myFile.lastModified();
    }

    public List<PersonParser> importPeopleFromLocalXML() {
        try {
            File myFile = new File(myDir + "/" + filename);
            InputStream is = new FileInputStream(myFile);

            XMLPullParserHandler parser = new XMLPullParserHandler();
            return parser.parse(is);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<PersonParser> importPeopleFromLocalXML(String path) {
        try {
            File myFile = new File(path);
            InputStream is = new FileInputStream(myFile);

            XMLPullParserHandler parser = new XMLPullParserHandler();
            return parser.parse(is);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @return success or failure
     */
    private boolean writeLocalBackup(final File file, String text) {
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(text.getBytes());
            out.flush();
            out.close();

            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle(context.getResources().getString(R.string.success));
            dialog.setMessage(context.getResources().getString(R.string.backup_completed) +
                            "\n\n" + file.getAbsolutePath()).setIcon(R.drawable.info);
            dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","", null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[" + context.getResources().getString(R.string.app_name) + "] Backup");
                    Uri myUri = Uri.parse("file://" + file.getAbsolutePath());
                    emailIntent.putExtra(Intent.EXTRA_STREAM, myUri);
                    context.startActivity(Intent.createChooser(emailIntent, context.getResources().getString(R.string.send_backup_email)));
                }
            });
            dialog.show();


        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void saveLocalBackupInFile(final Context context, final String text) {
        myDir.mkdirs();
        final File file = new File(myDir, filename);

        if (file.exists()) {
            Utils.showConfirmDialog(context,
                    context.getString(R.string.warning),
                    context.getString(R.string.overwrite_backup_file_question),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            file.delete();
                            writeLocalBackup(file, text);
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
        } else {
            writeLocalBackup(file, text);
        }
    }

}
