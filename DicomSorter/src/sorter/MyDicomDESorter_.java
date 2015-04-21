package sorter;

import ij.IJ;

import ij.io.DirectoryChooser;
import ij.plugin.PlugIn;

public class MyDicomDESorter_ implements PlugIn {
	public void run(String args) {

		DirectoryChooser od1 = new DirectoryChooser(
				"SELEZIONARE LA CARTELLA SORGENTE");
		String filePath1 = od1.getDirectory();
		if (filePath1 == null)
			return;
		filePath1 = filePath1.substring(0, filePath1.length() - 1);

		DirectoryChooser od2 = new DirectoryChooser(
				"SELEZIONARE LA CARTELLA DESTINAZIONE");
		String filePath2 = od2.getDirectory();
		if (filePath2 == null)
			return;
		filePath2 = filePath2.substring(0, filePath2.length() - 1);

		boolean ok = sorterUtils.mainDESorterMethod(filePath1, filePath2);

		if (ok) {
			IJ.showMessage("Fine");
		} else {
			IJ.showMessage("Fine, con errori");
		}
	}

} // Sorter_
