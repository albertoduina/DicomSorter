package sorter;

import ij.IJ;
import ij.io.DirectoryChooser;
import ij.plugin.PlugIn;

public class MyDicomRENumber_ implements PlugIn {
	public void run(String args) {

		DirectoryChooser od1 = new DirectoryChooser(
				"SELEZIONARE LA CARTELLA CON I FILE DA RINUMERARE");
		String filePath1 = od1.getDirectory();
		if (filePath1 == null)
			return;
		filePath1 = filePath1.substring(0, filePath1.length() - 1);
		IJ.showMessage("ATTENDERE\nla scritta fine lavoro\n*** CI PUO'METTERE ASSAI ***");

		boolean ok = sorterUtils.mainRENumberMethod(filePath1);

		if (ok) {
			IJ.showMessage("FINE LAVORO");
		} else {
			IJ.showMessage("FINE LAVORO con possibili errori ....");
		}

	}

} // Sorter_
