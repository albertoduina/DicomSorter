package sorter;
import ij.IJ;

import ij.io.DirectoryChooser;
import ij.plugin.PlugIn;

public class MyDicomSorter_2 implements PlugIn {
	public void run(String args) {
		
		DirectoryChooser od2 = new DirectoryChooser(
				"SELEZIONARE LA CARTELLA IMMAGINI");
		String filePath = od2.getDirectory();
		if (filePath == null)
			return;

		boolean ok = sorterUtils2.mainMethod(filePath);

		if (ok) {
			IJ.showMessage("Fine");
		} else {
			IJ.showMessage("Fine, con errori");
		}
	}

} // Sorter_
