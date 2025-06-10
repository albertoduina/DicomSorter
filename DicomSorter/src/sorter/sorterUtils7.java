package sorter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.Opener;
import ij.plugin.DICOM;
import utils.ArrayUtils;
import utils.MyLog;
import utils.ReadDicom;

public class sorterUtils7 {

	private final static String DICOM_PATIENT_NAME = "0010,0010";
	private final static String DICOM_PROTOCOL_NAME = "0008,1030";
	private final static String DICOM_SERIES_DESCRIPTION = "0008,103E";
	private final static String DICOM_SERIES_NUMBER = "0020,0011";
	private final static String DICOM_COIL1 = "0051,100F";
	private final static String DICOM_COIL2 = "0021,114F";

	public static boolean mainMethod(String filePath) {
		long totale = sorterUtils7.countFiles(filePath);
		String path1 = "";
		ArrayList<String> lista = sorterUtils7.listFiles(filePath);
		Opener opener1 = new Opener();
		// ===============================================
		// CREAZIONE STRUTTURA DIRECTORY
		// ===============================================
		for (int i1 = 0; i1 < lista.size(); i1++) {
			IJ.showStatus("move file  " + i1 + "/" + totale);
			IJ.redirectErrorMessages(true);
			path1 = lista.get(i1);
			int type = (new Opener()).getFileType(path1);
			ImagePlus imp1 = null;
			if (type == Opener.DICOM) {
				imp1 = opener1.openImage(path1);
				if (imp1 == null) {
					IJ.log("path1aa= " + path1 + " null");
					// break label1;
					continue;
				}
				if (imp1.hasImageStack()) {
					IJ.log("path1bb= " + path1 + " stack");
					continue;
				}
				IJ.redirectErrorMessages(true);
				String patName = readDicomParameter(imp1, DICOM_PATIENT_NAME);
				String seriesNum = readDicomParameter(imp1, DICOM_SERIES_NUMBER);
				String seriesDescription1 = readDicomParameter(imp1, DICOM_SERIES_DESCRIPTION);
				String seriesDescription = filterChar(seriesDescription1);
				String coil1 = readDicomParameter(imp1, DICOM_COIL1);
				String coil2 = readDicomParameter(imp1, DICOM_COIL2);
				String coil3 = piedeDiPorco(path1, "21004F10");
				String coil = "MISS";
				if (coil1.equals("MISS") == false && coil1.equals("") == false) {
					coil = coil1;
				}
				if (coil2.equals("MISS") == false && coil2.equals("") == false) {
					coil = coil2;
				}
				if (coil3.equals("MISS") == false && coil3.equals("") == false) {
					coil = coil3;
				}
				String protocolName = readDicomParameter(imp1, DICOM_PROTOCOL_NAME);
				if (patName == null)
					continue;
				// Create a directory; all ancestor directories must exist
				patName = patName.replace(" ", "_");
				String directoryPatientName = filePath + patName;
				boolean success1 = sorterUtils7.createDirectory(directoryPatientName);
				if (!success1)
					IJ.error("error in createDirectory");
				String directoryImaTime = filePath + patName + File.separator + seriesDescription;
				boolean success2 = sorterUtils7.createDirectory(directoryImaTime);
				if (!success2)
					IJ.error("error in createDirectory");
				String aux12 = protocolName;
				if (aux12 == null) {
					aux12 = "null";
				} else {
					aux12 = filterChar(aux12);
					aux12 = aux12.trim();
				}
				String aux11 = seriesDescription;
				if (aux11 == null) {
					aux11 = "null";
				} else {
					aux11 = filterChar(aux11);
					aux11 = aux11.trim();
				}
				String aux14 = seriesNum;
				if (aux14 == null) {
					aux14 = "null";
				} else {
					aux14 = filterChar(aux14);
					aux14 = aux14.trim();
				}

				// ===============================================
				// TENTATIVO MOVIMENTO IMMAGINI
				// ===============================================

				File moveCandidate = new File(lista.get(i1));
				File sourceDir = new File(moveCandidate.getParent());
				String oldName2 = filterChar(moveCandidate.getName());
				String newSeriesName = seriesNum + "_" + oldName2;
				String coilX = coil;
				if (coilX != null) {
					if (coilX.equals("C:HE1-4")) {
						coilX = "HE5";
						oldName2 = coilX + "_" + newSeriesName;
					} else {
						String aux1 = coilX.replace(":", "x");
						String aux2 = aux1.replace(";", "v");
						oldName2 = aux2 + "_" + newSeriesName;
					}
					String aux1 = seriesDescription;
					if (aux1.length() >= 5) {
						oldName2 = oldName2 + "_" + seriesDescription.substring(0, 5);
					}
				}

				String seriesName = "Series_" + aux14;
				String aux15 = directoryImaTime + File.separator + seriesName;
				String directorySeriesName = aux15;
				boolean success3 = sorterUtils7.createDirectory(directorySeriesName);
				if (!success3) {
					IJ.error("create dir problems " + directorySeriesName);
				} else {

				}
				String okDirectorySeriesName = filterChar(directorySeriesName);
				File destDir = new File(okDirectorySeriesName);
				String newName = "";
				if (sourceDir.equals(destDir)) {
				} else {
					boolean exist2 = false;
					// verifico inoltre se il nome da assegnare al file da
					// spostare esista gia' nella directory destinazione, in
					// questo caso aggiungo, tramite renameFile un numero
					// crescente.
					// Rieffettuo il controllo, eventualmente continuando ad
					// aumentare il numero da aggiungere (loop), finche' il nuovo
					// nome non esista piu' nella directory destinazione.
					int loop = 0;

					// verifico che il nuovo file and path non abbiano caratteri
					// proibiti per windows
					String oldName = filterChar(oldName2);
					oldName = oldName + ".dcm";

					// MyLog.waitHere("oldName= "+oldName);

					// 150325 aggiungo estensione dcm per test

					File newfile2 = new File(destDir, oldName);

					exist2 = newfile2.exists();
					while (exist2) {
						loop++;
						newName = sorterUtils7.renameFileProgressive(oldName, loop);
						newfile2 = new File(destDir, newName);
						exist2 = newfile2.exists();
					}

					boolean moved3 = false;
					int count3 = 0;
					do {
						moved3 = moveCandidate.renameTo(newfile2);
						count3++;
						if (!moved3) {
							IJ.wait(10);
							System.gc();
							IJ.wait(10);
						}
						if (count3 > 10 && !moved3) {
							if (newfile2.exists())
								MyLog.waitHere("EXISTS");
							if (newfile2.equals(moveCandidate))
								MyLog.waitHere("SAME");
							IJ.error("move problems " + moveCandidate + " in " + newfile2);
							MyLog.waitHere("potential lock-up");
							return false;
						}
					} while (!moved3);

				}
			}
			continue;
		}

		// carico una situazione "fresca" delle directory
//		String[] list2 = new File(filePath).list();
//		for (int i1 = 0; i1 < list2.length; i1++) {
//			File f1 = new File(filePath + File.separator + list2[i1]);
//			if (sorterUtils7.countFiles(f1.getPath()) == 0) {
//			}
//		}

		return true;
	}

	/**
	 * Conta i file in maniera ricorsiva
	 * 
	 * @param filePath path della directory di partenza, verranno lette anche tutte
	 *                 le sottocartelle
	 * @return int totale dei files
	 */
	public static int countDicomFiles(String filePath) {
		String[] list2 = new File(filePath).list();

		int count = 0;
		if (list2 == null)
			return count;
		for (int i1 = 0; i1 < list2.length; i1++) {
			String path1 = filePath + File.separator + list2[i1];
			File f1 = new File(path1);
			if (f1.isDirectory()) {
				count = count + countFiles(path1);
			} else {
				IJ.redirectErrorMessages();
				if (ReadDicom.isDicomImage(path1))
					count++;
			}
		}
		return count;
	}

	/**
	 * Conta i file in maniera ricorsiva
	 * 
	 * @param filePath path della directory di partenza, verranno lette anche tutte
	 *                 le sottocartelle
	 * @return int totale dei files
	 */
	public static int countFiles(String filePath) {
		String[] list2 = new File(filePath).list();

		int count = 0;
		if (list2 == null)
			return count;
		for (int i1 = 0; i1 < list2.length; i1++) {
			String path1 = filePath + File.separator + list2[i1];
			File f1 = new File(path1);
			if (f1.isDirectory()) {
				count = count + countFiles(path1);
			} else {
				IJ.redirectErrorMessages();
				count++;
			}
		}
		return count;
	}

	/**
	 * Lista i file in maniera ricorsiva
	 * 
	 * @param filePath path della directory di partenza, verranno lette anche tutte
	 *                 le sottocartelle
	 * @return ArrayList con i path di tutti i file
	 */
	public static ArrayList<String> listFiles(String filePath) {

		ArrayList<String> list3 = new ArrayList<String>();
		String[] list2 = new File(filePath).list();
		for (int i1 = 0; i1 < list2.length; i1++) {
			// String path1 = filePath + File.separator + list2[i1];
			String path1 = filePath + list2[i1];
			File f1 = new File(path1);
			if (f1.isDirectory()) {
				list3.addAll(listFiles(path1));
			} else {
				// IJ.redirectErrorMessages();
				list3.add(path1);
			}
		}
		return list3;
	}

	/**
	 * Lista i file in maniera ricorsiva
	 * 
	 * @param filePath path della directory di partenza, verranno lette anche tutte
	 *                 le sottocartelle
	 * @return ArrayList con i path di tutti i file
	 */
	public static ArrayList<String> listDicomFiles(String filePath) {

		ArrayList<String> list3 = new ArrayList<String>();
		String[] list2 = new File(filePath).list();
		for (int i1 = 0; i1 < list2.length; i1++) {
			String path1 = filePath + File.separator + list2[i1];
			File f1 = new File(path1);
			if (f1.isDirectory()) {
				list3.addAll(listFiles(path1));
			} else {
				IJ.redirectErrorMessages();
				if (sorterUtils7.isDicomImage(path1))
					list3.add(path1);
			}
		}
		return list3;
	}

	/**
	 * La seguente routine, che si occupa di estrarre dati dall'header delle
	 * immagini e' tratta da QueryDicomHeader.java di Anthony Padua & Daniel
	 * Barboriak - Duke University Medical Center. *** modified version *** Alberto
	 * Duina - Spedali Civili di Brescia - Servizio di Fisica Sanitaria 2006
	 * 
	 * @param imp       immagine di cui leggere l'header
	 * @param userInput stringa di 9 caratteri contenente "group,element"
	 *                  esempio:"0020,0013"
	 * @return stringa col valore del parametro
	 */
	public static String readDicomParameter(ImagePlus imp, String userInput) {
		// N.B. userInput => 9 characs [group,element] in format: xxxx,xxxx (es:
		// "0020,0013")
		String attribute = "???";
		String value = "???";
		if (imp == null)
			return (null);
		int currSlice = imp.getCurrentSlice();
		ImageStack stack = imp.getStack();
		String header = stack.getSize() > 1 ? stack.getSliceLabel(currSlice) : (String) imp.getProperty("Info");
		if (header != null) {
			int idx1 = header.indexOf(userInput);
			int idx2 = header.indexOf(":", idx1);
			int idx3 = header.indexOf("\n", idx2);
			if (idx1 >= 0 && idx2 >= 0 && idx3 >= 0) {
				try {
					attribute = header.substring(idx1 + 9, idx2);
					attribute = attribute.trim();
					value = header.substring(idx2 + 1, idx3);
					value = value.trim();
					return (value);
				} catch (Throwable e) { // Anything else
					return (value);
				}
			} else {
				return ("MISS");
			}
		} else {
			return (null);
		}
	}

	/**
	 * Cancella un directory piena
	 * 
	 * @param path path directory da vuotare
	 * @return true se ok
	 */
	public static boolean deleteDirectoryWithFiles(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			if (files == null)
				return false;
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectoryWithFiles(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	/***
	 * cancellazione alternativa di directory piena, ma forse e'deleteria, a noi
	 * serve di cancellare solo directory vuote, i file devono essere tutti spostati
	 * in altro luogo
	 * 
	 * @param directory
	 * @return
	 */
	public static boolean removeDirectory(File directory) {
		if (directory == null)
			return false;
		if (!directory.exists())
			return true;
		if (!directory.isDirectory())
			return false;
		String[] list = directory.list();
		if (list != null) {
			for (int i1 = 0; i1 < list.length; i1++) {
				File entry = new File(directory, list[i1]);
				if (entry.isDirectory()) {
					if (!removeDirectory(entry))
						return false;
				} else {
					if (!entry.delete())
						return false;
				}
			}
		}

		return directory.delete();
	}

	/***
	 * Cancellazione di cartella vuota
	 * 
	 * @param path
	 * @return
	 */

	public static boolean deleteDirectoryEmpty(File path) {
		if (path.exists()) {
			if (path.isDirectory()) {
				File[] files = path.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						deleteDirectoryEmpty(files[i]);
					}
				}
			}
		}
		return (path.delete());
	}

	/**
	 * Rinomina files
	 * 
	 * @param oldName
	 * @param loop
	 * @return
	 */
	public static String renameFileProgressive(String oldName, int loop) {
		File tmpFile = new File(oldName);
		// IJ.log("in renameFile oldName vale " + oldName + " e il loop vale "
		// + loop);
		int whereDot = tmpFile.getName().lastIndexOf('.');
		// IJ.log("whereDot originale vale " + whereDot);
		String name = "";
		String extension = "";

		if (whereDot < 0) {
			name = tmpFile.getName();
			extension = "";
		} else {
			name = tmpFile.getName().substring(0, whereDot);
			extension = tmpFile.getName().substring(whereDot + 1);
		}

		// IJ.log("name=" + name + " extension=" + extension);
		// new WaitForUserDialog("Do something, then click OK.").show();

		String numero = "";
		String a1 = "";
		int closePar = tmpFile.getName().lastIndexOf(')');
		int openPar = tmpFile.getName().lastIndexOf('(');
		int ord = 0;
		if (closePar == openPar) {
			a1 = name;
			ord = loop;
			// IJ.log("la parte fissa del nome vale " + a1
			// + " senza estensione numero");
		} else {
			a1 = tmpFile.getName().substring(0, openPar);
			numero = tmpFile.getName().substring(openPar + 1, closePar);
			// IJ.log("la parte fissa del nome vale " + a1
			// + " con estensione numero=" + numero);
			try {
				ord = Integer.parseInt(numero);
			} catch (NumberFormatException nfe) {
			}
			ord = ord + loop;
		}

		String a2 = "(" + ord + ")";
		String a3 = "";
		if (extension.equals("")) {
		} else {
			a3 = "." + extension;
		}

		String newName = a1 + a2 + a3;
		// IJ.log("in uscita a renameFile il newName vale " + newName);
		return newName;
	}

	/**
	 * creazione di una nuova directory
	 * 
	 * @param directoryPath
	 * @return
	 */

	public static boolean createDirectory(String directoryPath) {
		boolean exists = (new File(directoryPath)).exists();
		if (!exists) {
			exists = (new File(directoryPath)).mkdirs();
			if (!exists) {
				IJ.error("fallita la creazione di " + directoryPath);
			}
		}
		return (exists);
	}

	/**
	 * Copia di un file
	 * 
	 * @param in
	 * @param out
	 * @return
	 */
	public static boolean copyFile(File in, File out) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(in);
		} catch (Exception e1) {
			// System.out.println("copyFile 00001 " + e1);
			return false;
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(out);
		} catch (Exception e1) {
			// System.out.println("copyFile 00002 " + e1);
			try {
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return false;
		}
		try {
			byte[] buf = new byte[1024];
			int i = 0;
			while ((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		} catch (Exception e) {
			// System.out.println("copyFile 00003 " + e);
			return false; // copy operation failed
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					// System.out.println("copyFile 0004 " + e);
					return false;
				}
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					// System.out.println("copyFile 0005" + e);
					return false;
				}
		}
		return true;
	}

	/***
	 * Filtraggio caratteri problematici o che comunque non possono essere usati in
	 * un path
	 * 
	 * @param in1
	 * @return
	 */
	public static String filterChar(String in1) {
		String tmp1 = in1;

		tmp1 = tmp1.replace("[", "");
		tmp1 = tmp1.replace("]", "");
		tmp1 = tmp1.replace("&", "");
		tmp1 = tmp1.replace("/", "");
		tmp1 = tmp1.replace("~", "");
		tmp1 = tmp1.replace("?", "");
		tmp1 = tmp1.replace("*", "");
		tmp1 = tmp1.replace("|", "");
		tmp1 = tmp1.replace("<", "");
		tmp1 = tmp1.replace(">", "");
		tmp1 = tmp1.replace("\"", "");
		tmp1 = tmp1.replace(";", "");
		tmp1 = tmp1.replace("+", "");
		if (!tmp1.contains(":\\")) {
			tmp1 = tmp1.replace(":", ""); // non cancella C:\
		}
//		tmp1 = tmp1.replace("/", "");
//		tmp1 = tmp1.replace("\\", "");
//		tmp1 = tmp1.replace(":", "");   // mi cancellava i : dal c:/
		return tmp1;
	}

	/**
	 * Effettua il move di un file
	 * 
	 * @param dirDestinazione destinazione
	 * @param pathSorgente    sorgente
	 * @return true se ok
	 */
	public static boolean moveFile(String pathSorgente, String dirDestinazione) {
		boolean success = false;
		// IJ.log("pathSorgente= " + pathSorgente);
		// InputOutput io1 = new InputOutput();
		String nomeFile = extractFileName2(pathSorgente);
		File fileSorgente = new File(pathSorgente);
		File fileDirDestinazione = new File(dirDestinazione);
		if (!fileDirDestinazione.exists()) {
			fileDirDestinazione.mkdir();
		}
		if (fileDirDestinazione.exists()) {
			// IJ.log("sorgente= " + nomeFile + " destinazione= " +
			// dirDestinazione);
			success = fileSorgente.renameTo(new File(dirDestinazione, nomeFile));
		}
		if (!success) {
			// MyLog.waitHere("File " + nomeFile + " was not successfully moved
			// to " + dirDestinazione);
		}
		return success;
	}

	/**
	 * Effettua il move di un file
	 * 
	 * @param dirDestinazione destinazione
	 * @param pathSorgente    sorgente
	 * @return true se ok
	 */
	public static boolean moveFile2(String pathSorgente, String dirDestinazione) {
		boolean success = false;
		MyLog.waitHere("pathSorgente= " + pathSorgente);
		// InputOutput io1 = new InputOutput();
		String nomeFile = extractFileName2(pathSorgente);
		File fileSorgente = new File(pathSorgente);
		File fileDirDestinazione = new File(dirDestinazione);
		if (!fileDirDestinazione.exists()) {
			fileDirDestinazione.mkdir();
		}
		if (fileDirDestinazione.exists()) {
			MyLog.waitHere("sorgente= " + nomeFile + "  destinazione= " + dirDestinazione);
			success = fileSorgente.renameTo(new File(dirDestinazione, nomeFile));
		}
		if (!success) {
			MyLog.waitHere("File " + nomeFile + " was not successfully moved to " + dirDestinazione);
		}
		return success;
	}

	/***
	 * Effettua il de-sorter
	 * 
	 * @param sourceDir      direcctory sortata
	 * @param destinationDir directory destinazione in cui spostare tutti i files
	 * @return true se ok
	 */
	public static boolean mainDESorterMethod2(String sourceDir, String destinationDir) {
		long totale = sorterUtils7.countFiles(sourceDir);
		ArrayList<String> lista = sorterUtils7.listFiles(sourceDir);
		String[] sourcePathVector = ArrayUtils.arrayListToArrayString(lista);
		boolean ok = true;
		for (int i1 = 0; i1 < sourcePathVector.length; i1++) {
			boolean ok1 = false;
			String sourcePath = sourcePathVector[i1];
			String destinationPath = destinationDir;
			ok1 = moveFile(destinationPath, sourcePath);
			if (ok1 == false)
				ok = false;
			else
				IJ.showStatus("move file  " + i1 + File.separator + totale);
		}
		ArrayList<String> lista2 = listFolders(sourceDir);
		String[] foldersPathVector = ArrayUtils.arrayListToArrayString(lista2);
		for (int i1 = 0; i1 < foldersPathVector.length; i1++) {
			File deletable = new File(foldersPathVector[i1]);
			boolean ok4 = deleteDirectoryEmpty(deletable);
		}
		return ok;
	}

	/***
	 * Effettua il de-sorter
	 * 
	 * @param sourceDir      direcctory sortata
	 * @param destinationDir directory destinazione in cui spostare tutti i files
	 * @return true se ok
	 */
	public static boolean mainRENumberMethod(String renumberDir) {

		ArrayList<String> lista = sorterUtils7.listDicomFiles(renumberDir);

		String[] sourcePathVector = sorterUtils7.arrayListToArrayString(lista);

		boolean ok = true;
		for (int i1 = 0; i1 < sourcePathVector.length; i1++) {

			boolean ok1 = false;
			String sourcePath = sourcePathVector[i1];
			File file1 = new File(sourcePath);

			String number = Integer.toString(i1);
			while (number.length() < 5)
				number = "0" + number;
			File file2 = new File(renumberDir + File.separator + number);
			ok1 = file1.renameTo(file2);
			if (ok1 == false)
				ok = false;
		}

		return ok;
	}

	/***
	 * Effettua il de-sorter
	 * 
	 * @param sourceDir      direcctory sortata
	 * @param destinationDir directory destinazione in cui spostare tutti i files
	 * @return true se ok
	 */
	public static boolean mainDESorterMethod(String sourceDir, String destinationDir) {

		long totale = sorterUtils7.countFiles(sourceDir);

		ArrayList<String> lista = sorterUtils7.listFiles(sourceDir);

		String[] sourcePathVector = sorterUtils7.arrayListToArrayString(lista);

		boolean ok = true;
		for (int i1 = 0; i1 < sourcePathVector.length; i1++) {
			boolean ok1 = false;
			String sourcePath = sourcePathVector[i1];
			String destinationPath = destinationDir;
			ok1 = moveFile(sourcePath, destinationPath);
			// MyLog.waitHere("source= "+sourcePath+ "\ndestination=
			// "+destinationPath);

			if (ok1 == false)
				ok = false;
			// else
			// IJ.showStatus("move file " + i1 + File.separator + totale);
		}

		ArrayList<String> lista2 = listFolders(sourceDir);
		String[] foldersPathVector = sorterUtils7.arrayListToArrayString(lista2);
		for (int i1 = 0; i1 < foldersPathVector.length; i1++) {
			File deletable = new File(foldersPathVector[i1]);
			boolean ok4 = deleteDirectoryEmpty(deletable);
		}
		return ok;
	}

	/***
	 * Lista i folders
	 * 
	 * @param filePath directory radice
	 * @return lista sottodirectories
	 */
	public static ArrayList<String> listFolders(String filePath) {
		ArrayList<String> list3 = new ArrayList<String>();
		String[] list2 = new File(filePath).list();
		for (int i1 = 0; i1 < list2.length; i1++) {
			String path1 = filePath + File.separator + list2[i1];
			IJ.showStatus("listFolders " + i1 + File.separator + list2.length);
			File f1 = new File(path1);
			if (f1.isDirectory()) {
				list3.add(path1);
			} else {
			}
		}
		return list3;
	}

	/**
	 * estrae il filename dal path
	 * 
	 * @param path
	 * @return
	 */
	public static String extractFileName2(String path) {

		int pos = path.lastIndexOf(File.separator);
		String fileName = path.substring(pos + 1);
		return fileName;
	}

	/**
	 * Copia di una cartella con files
	 * 
	 * @param srcFolder
	 * @param destFolder
	 * @return
	 */
	public static boolean copyDirectoryWithFiles(String srcFolder, String destFolder) {

		boolean ok = false;
		if (!(new File(srcFolder).exists())) {

			System.out.println("Directory does not exist.");
			// just exit
			return false;
		} else {
			try {
				ok = copyFolder(new File(srcFolder), new File(destFolder));
			} catch (IOException e) {
				e.printStackTrace();
				// error, just exit
				return false;
			}
		}
		return ok;
	}

	/**
	 * Copia di una cartella
	 * 
	 * @param src
	 * @param dest
	 * @return
	 * @throws IOException
	 */
	public static boolean copyFolder(File src, File dest) throws IOException {

		boolean ok = true;
		if (src.isDirectory()) {

			// if directory not exists, create it
			if (!dest.exists()) {
				dest.mkdir();
				if (!dest.exists())
					ok = false;
				System.out.println("Directory copied from " + src + "  to " + dest);
			}

			// list all the directory contents
			String files[] = src.list();

			for (String file : files) {
				// construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				// recursive copy
				copyFolder(srcFile, destFile);
			}

		} else {
			// if file, then copy it
			// Use bytes stream to support all file types
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;
			// copy the file content in bytes
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			if (!dest.isFile())
				ok = false;
			in.close();
			out.close();
			System.out.println("File copied from " + src + " to " + dest);
		}
		return ok;
	}

	/**
	 * Da ArrayList a vettore di stringhe
	 * 
	 * @param inArrayList
	 * @return
	 */
	public static String[] arrayListToArrayString(List<String> inArrayList) {
		Object[] objArr = inArrayList.toArray();
		String[] outStrArr = new String[objArr.length];
		for (int i1 = 0; i1 < objArr.length; i1++) {
			outStrArr[i1] = objArr[i1].toString();
		}
		return outStrArr;
	}

	/**
	 * Verifica che sia Dicom
	 * 
	 * @param fileName1
	 * @return
	 */
	public static boolean isDicomImage(String fileName1) {
		// IJ.redirectErrorMessages();
		int type = (new Opener()).getFileType(fileName1);
		if (type != Opener.DICOM) {
			return false;
		}
		ImagePlus imp1 = new Opener().openImage(fileName1);
		if (imp1 == null) {
			return false;
		}
		String info = new DICOM().getInfo(fileName1);
		if (info == null || info.length() == 0) {
			return false;
		} else {
			return true;
		}
	}

	/***
	 * Questo workaround deriva da ReadAscconv e va utilizzato per leggere parametri
	 * dell'header ignorati da ImageJ, solitamente legati all'incauto passaggio
	 * delle immagini attraverso i PACS. (CAPRE, CAPRE, CAPRE ....) IW2AYV nota bene
	 * che questo file lavora solo per macchine Siemens
	 * 
	 * @param path1
	 * @param ricerca
	 * @return
	 */

	public static String piedeDiPorco(String fileName1, String tag) {
		int len1;

		// MyLog.waitHere("tag= " + tag);
		// restituisco i valori solo se macchina siemens
		byte[] x3 = hexStringToByteArray("08007000");

		byte[] x1 = hexStringToByteArray(tag);
		String out1 = "";
		String costruttore = "";
		String confronto = "Siemens ";

		try {
			BufferedInputStream f1 = new BufferedInputStream(new FileInputStream(fileName1));
			len1 = f1.available();
			byte[] buffer1 = new byte[len1];
			f1.read(buffer1, 0, len1); // get copy of entire file as byte[]
			f1.close();

			// cerco in buffer1 il tag di fine header, serve per evitare di sconfinare nei
			// byte dei pixel immagine
			byte[] y1 = new byte[4];
			y1[0] = (byte) 0xE0;
			y1[1] = (byte) 0x7F;
			y1[2] = (byte) 0x10;
			y1[3] = (byte) 0x00;
			int offset1 = localizeHexWord(buffer1, y1, buffer1.length);
			// MyLog.waitHere("offset1= " + offset1);
			int offset2 = localizeHexWord(buffer1, x1, offset1);
			// MyLog.waitHere("offset2= " + offset2);
			int offset3 = localizeHexWord(buffer1, x3, offset1);
			offset3 = offset3 + 4;
			// MyLog.waitHere("offset3= " + offset3);

			byte[] buffer4 = new byte[8];
			for (int i1 = 0; i1 < 8; i1++) {
				buffer4[i1] = buffer1[offset3 + 4 + i1];
			}
			costruttore = new String(buffer4);
			// MyLog.waitHere("costruttore= " + costruttore);
			if (costruttore.equals(confronto)) {
			} else {
				return "MISS";
			}

			short len2 = Short.parseShort(byte2hex(buffer1[offset2 + 4]), 16);
			offset2 = offset2 + 8;

			// MyLog.waitHere("tag= " + tag + " len2= " + len2);

			byte[] buffer3 = new byte[len2];

			for (int i1 = 0; i1 < len2; i1++) {
				buffer3[i1] = buffer1[offset2 + i1];
			}

			byte[] buffer2 = new byte[len2];

			for (int i1 = 0; i1 < len2; i1++) {
				buffer2[i1] = buffer1[offset2 + i1];
			}

			out1 = new String(buffer2);

			/// IJ.log("output >>> " + out1);

		} catch (Exception e) {
			IJ.showMessage("piedeDiPorco>>> ", "Exception " + "\n \n\"" + e.getMessage() + "\"");
		}

		if (costruttore.equals(confronto)) {
			return out1;
		} else
			return "MISS";
	}

	/**
	 * 
	 * @param bImage
	 * @param what
	 * @param limit
	 * @return
	 */
	public static int localizeHexWord(byte[] bImage, byte[] what, int limit) {
		int conta = 0;
		int locazione = 0;

		// IJ.log("what =" + byte2hex(what[0]) + byte2hex(what[1])
		// + byte2hex(what[2]) + byte2hex(what[3]));

		for (int i1 = 0; i1 < limit - 4; i1++) {

			if (bImage[i1 + 0] == what[0] && bImage[i1 + 1] == what[1] && bImage[i1 + 2] == what[2]
					&& bImage[i1 + 3] == what[3]) {
				locazione = i1;
				conta++;
				// IJ.log("conta=" + conta + " locazione=" + locazione);
				break;
			}
		}

		if (conta > 0) {
			return locazione;
		} else {
			return -1; // non trovato
		}
	}

	/**
	 * 
	 * @param by
	 * @return
	 */
	public static String byte2hex(byte by) {
		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] buf2 = new char[2];
		buf2[1] = hexDigits[by & 0xf];
		by >>>= 4;
		buf2[0] = hexDigits[by & 0xf];
		return new String(buf2);
	} // end byte2hex

	/**
	 * 
	 * @param s
	 * @return
	 */
	public static final byte[] short2Byte(short s) {
		byte[] out = new byte[2];

		out[0] = (byte) ((s >>> 8) & 0xFF);
		out[1] = (byte) ((s >>> 0) & 0xFF);

		return out;
	}

	/***
	 * conversione da string hexto byte array s1 deve essere di lunghezza pari
	 * 
	 * @param s1
	 * @return
	 */
	public static byte[] hexStringToByteArray(String s1) {
		// MyLog.waitHere("s1= " + s1);
		int len = s1.length();
		byte[] data = new byte[len / 2];
		for (int i1 = 0; i1 < len; i1 += 2) {
			data[i1 / 2] = (byte) ((Character.digit(s1.charAt(i1), 16) << 4) + Character.digit(s1.charAt(i1 + 1), 16));
		}
		return data;
	}

	public String getString(BufferedInputStream bo, int start, int len) throws IOException {

		IJ.log("entro in getString");
		int pos = 1;
		IJ.log("getString 001");
		byte[] buf = new byte[len];
		IJ.log("getString 002");
		int size = bo.available();
		IJ.log("getString 003");
		while (pos < len) {
			int count = bo.read(buf, pos, len);
			pos += count;
		}
		IJ.log("buf= " + buf);
		return new String(buf);
	}

}
