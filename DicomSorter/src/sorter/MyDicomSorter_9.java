package sorter;

import ij.IJ;

import ij.io.DirectoryChooser;
import ij.plugin.PlugIn;

/***
 * ----------------------------------------------------------------------
 * 
 * Ultima Versione su cui ricomincio a lavorare il 08/10/2026 revisiono i
 * caratteri proibiti nei nomi directory, devo filtrare i : duepunti
 * 
 * ----------------------------------------------------------------------
 */
public class MyDicomSorter_9 implements PlugIn {
	public void run(String args) {

		DirectoryChooser od2 = new DirectoryChooser("SELEZIONARE LA CARTELLA IMMAGINI");
		String filePath = od2.getDirectory();
		if (filePath == null)
			return;

		boolean ok = sorterUtils9.mainMethod(filePath);

		if (ok) {
			if (IJ.versionLessThan("1.54"))
				IJ.log("AGGIORNARE IMAGEJ, la versione prevista e' > 1.54");
			IJ.log("FINE LAVORO perfetto");
			IJ.showMessage("FINE LAVORO perfetto");
		} else {
			if (IJ.versionLessThan("1.54"))
				IJ.log("AGGIORNARE IMAGEJ, la versione prevista e' > 1.54");
			IJ.log("FINE LAVORO con possibili errori ....");
			IJ.showMessage("FINE LAVORO con possibili errori ....");
		}
	}

} // Sorter_
