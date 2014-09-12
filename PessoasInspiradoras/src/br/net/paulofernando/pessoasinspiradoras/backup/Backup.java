package br.net.paulofernando.pessoasinspiradoras.backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import br.net.paulofernando.pessoasinspiradoras.R;
import br.net.paulofernando.pessoasinspiradoras.parser.PersonParser;
import br.net.paulofernando.pessoasinspiradoras.parser.XMLPullParserHandler;
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

	private void writeLocalBackup(File file, String text) {
		try {
			FileOutputStream out = new FileOutputStream(file);
			out.write(text.getBytes());
			out.flush();
			out.close();

			Utils.showInfoDialog(context,
					context.getResources().getString(R.string.success), context
							.getResources()
							.getString(R.string.backup_completed));

		} catch (Exception e) {
			e.printStackTrace();
		}
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
