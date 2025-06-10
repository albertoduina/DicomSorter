package sorter;
import ij.IJ;

import ij.io.DirectoryChooser;
import ij.plugin.PlugIn;

public class MyDicomSorter_6 implements PlugIn {
	public void run(String args) {
		
		DirectoryChooser od2 = new DirectoryChooser(
				"SELEZIONARE LA CARTELLA IMMAGINI");
		String filePath = od2.getDirectory();
		if (filePath == null)
			return;
		

		boolean ok = sorterUtils6.mainMethod(filePath);

		if (ok) {
			IJ.showMessage("FINE LAVORO");
		} else {
			IJ.showMessage("FINE LAVORO con possibili errori ....");
		}
	}

} // Sorter_
