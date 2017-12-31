package sorter;

import ij.IJ;

import ij.io.DirectoryChooser;
import ij.plugin.PlugIn;


public class MyDicomDESorter_ implements PlugIn {
	
	public void run(String args) {
		DirectoryChooser od1 = new DirectoryChooser(
				"SELEZIONARE LA CARTELLA IMMAGINI");
		String filePath = od1.getDirectory();
		if (filePath == null)
			return;

		boolean ok = sorterUtils.mainDESorterMethod(filePath, filePath);

		if (ok) {
			IJ.showMessage("FINE LAVORO");
		} else {
			IJ.showMessage("FINE LAVORO con possibili errori ....");
		}
	}

} 
