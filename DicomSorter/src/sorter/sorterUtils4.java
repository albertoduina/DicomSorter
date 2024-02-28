package sorter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.Opener;
import ij.plugin.DICOM;
import utils.ArrayUtils;
import utils.MyLog;
import utils.ReadDicom;

public class sorterUtils4 {

	private final static String DICOM_PATIENT_NAME = "0010,0010";

	private final static String DICOM_STUDY_DATE = "0008,0020";

	private final static String DICOM_SEQUENCE_NAME = "0018,0024";

	private final static String DICOM_ACQUISITION_TIME = "0008,0032";

	private final static String DICOM_PROTOCOL_NAME = "0008,1030";

	private final static String DICOM_SERIES_DESCRIPTION = "0008,103E";

	private final static String DICOM_STUDY_ID = "0020,0010";

	private final static String DICOM_SERIES_NUMBER = "0020,0011";

	private final static String DICOM_ACQUISITION_NUMBER = "0020,0012";

	private final static String DICOM_COIL = "0051,100F";

	private final static String DICOM_IMAGE_NUMBER = "0020,0013";

	private static final int EOF = '\uffff';

	public static boolean mainMethod(String filePath) {
		long totale = sorterUtils4.countFiles(filePath);

		ArrayList<String> lista = sorterUtils4.listFiles(filePath);
		Opener opener1 = new Opener();

		// MyLog.logArrayListVertical(lista);
		// MyLog.waitHere();

		label1: for (int i1 = 0; i1 < lista.size(); i1++) {
			IJ.showStatus("move file  " + i1 + "/" + totale);
			IJ.redirectErrorMessages(true);
			int type = (new Opener()).getFileType(lista.get(i1));
			ImagePlus imp1 = null;
			if (type == Opener.DICOM) {
				imp1 = opener1.openImage(lista.get(i1));
				if (imp1 == null)
					break label1;
				IJ.redirectErrorMessages(false);
				String patName = readDicomParameter(imp1, DICOM_PATIENT_NAME);
				String seqName = readDicomParameter(imp1, DICOM_SEQUENCE_NAME);
				String studyID = readDicomParameter(imp1, DICOM_STUDY_ID);
				String studyDate = readDicomParameter(imp1, DICOM_STUDY_DATE);
				String seriesNum = readDicomParameter(imp1, DICOM_SERIES_NUMBER);
				String seriesDescription = readDicomParameter(imp1, DICOM_SERIES_DESCRIPTION);
				String imaNum = readDicomParameter(imp1, DICOM_IMAGE_NUMBER);
				String acqNum = readDicomParameter(imp1, DICOM_ACQUISITION_NUMBER);
				String acqTime = readDicomParameter(imp1, DICOM_ACQUISITION_TIME);
				String coil = readDicomParameter(imp1, DICOM_COIL);
				String protocolName = readDicomParameter(imp1, DICOM_PROTOCOL_NAME);
				String fileName = imp1.getTitle();
				if (patName == null)
					continue;

				// IJ.log("patname= " + patName);
				// IJ.log("fileName= " + fileName);
				// IJ.log("---------");
				// IJ.log("seqName= " + seqName);
				// IJ.log("studyID= " + studyID);
				// IJ.log("studyDate= " + studyDate);
				// IJ.log("seriesNum= " + seriesNum);
				// IJ.log("seriesDescription= " + seriesDescription);
				// IJ.log("acqNum= " + acqNum);
				// IJ.log("acqTime= " + acqTime);
				// IJ.log("coil= " + coil);
				// IJ.log("protocolName= " + protocolName);
				// Create a directory; all ancestor directories must exist
				patName = patName.replace(" ", "_");

				String directoryPatientName = filePath + patName;
				// MyLog.waitHere("directoryPatientName=
				// "+directoryPatientName);
				boolean success1 = sorterUtils4.createDirectory(directoryPatientName);
				if (!success1)
					IJ.error("error in createDirectory");
				String studyName = "Study_" + studyID + "_" + studyDate;
				String directoryStudyName = filePath + patName + File.separator + studyName;
				boolean success2 = sorterUtils4.createDirectory(directoryStudyName);
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
				String aux13 = (aux11 + aux12).trim();
				String aux14 = seriesNum;
				if (aux14 == null) {
					aux14 = "null";
				} else {
					aux14 = filterChar(aux14);
					aux14 = aux14.trim();
				}
				String seriesName = "Series_" + aux14 + "_" + aux13;
				String aux15 = directoryStudyName + File.separator + seriesName;
				// String directorySeriesName = aux15.replaceAll(" ", "_");
				String directorySeriesName = aux15;
				// IJ.log("directorySeriesName= " + directorySeriesName);
				// MyLog.waitHere();
				// IJ.log("directoryPatientName= " + directoryPatientName);
				// IJ.log("studyName= " + studyName);
				// IJ.log("directoryStudyName= " + directoryStudyName);
				// IJ.log("seriesName= " + seriesName);
				// IJ.log("directorySeriesName= " + directorySeriesName);
				boolean success3 = sorterUtils4.createDirectory(directorySeriesName);
				if (!success3) {
					IJ.error("create dir problems " + directorySeriesName);
				} else {

				}

				File moveCandidate = new File(lista.get(i1));
				// Destination directory
				File destDir = new File(directorySeriesName);
				// IJ.log("directorySeriesName= " + directorySeriesName);
				File sourceDir = new File(moveCandidate.getParent());

				String oldName2 = moveCandidate.getName();
				String newSeriesName = seriesNum + "_" + oldName2;
				String aux = coil;
				if (aux != null) {
					if (aux.equals("C:HE1-4")) {
						aux = "HE5";
						oldName2 = newSeriesName + "_" + aux;
					} else {
						String aux1 = aux.replace(":", "x");
						String aux2 = aux1.replace(";", "v");
						oldName2 = newSeriesName + "_" + aux2;
					}
					// IJ.log(seriesDescription);
					String aux1 = seriesDescription;
					if (aux1.length() >= 5) {
						oldName2 = oldName2 + "_" + seriesDescription.substring(0, 5);
					}
				}
				String newName = "";
				if (sourceDir.equals(destDir)) {
				} else {
					boolean exist2 = false;
					// verifico inoltre se il nome da assegnare al file da
					// spostare esista gia' nella dierctory destinazione, in
					// questo caso aggiungo, tramite renameFile un numero
					// crescente.
					// Rieffettuo il controllo, eventualmente continuando ad
					// aumentare il numero da aggiungere (loop), finch� il nuovo
					// nome non esista piu' nella directory destinazione.
					int loop = 0;

					// verifico che il nuovo file and path non abbiano caratteri
					// proibiti per windows
					String oldName = filterChar(oldName2);

					MyLog.waitHere("oldName= "+oldName);
					File newfile2 = new File(destDir, oldName);
					IJ.log("oldName= "+oldName+" newFile2= "+newfile2);
					MyLog.waitHere("oldName= "+oldName+" newFile2="+newfile2);

					// String nuovo = filterChar(newfile2.toString());
					// if (!nuovo.equals(newfile2.toString()))
					// MyLog.waitHere("CARATTERE PROIBITO ");
					// IJ.log("CARATTERE PROIBITO: "+newfile2.toString() );

					exist2 = newfile2.exists();
					while (exist2) {
						loop++;
						newName = sorterUtils4.renameFileProgressive(oldName, loop);
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
		}
		// carico una situazione "fresca" delle directory
		String[] list2 = new File(filePath).list();
		for (int i1 = 0; i1 < list2.length; i1++) {
			File f1 = new File(filePath + File.separator + list2[i1]);
			if (sorterUtils4.countFiles(f1.getPath()) == 0) {
				// boolean delete2 = sorterUtils.deleteDirectoryEmpty(f1);
				// if (!delete2) {
				// vuol dire che non riesco o non devo cancellare. BASTA
				// COSI'
				// return false;
				// }
			}
		}

		return true;
	}// mainMethod

	/**
	 * Conta i file in maniera ricorsiva
	 * 
	 * @param filePath
	 *            path della directory di partenza, verranno lette anche tutte
	 *            le sottocartelle
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
	 * @param filePath
	 *            path della directory di partenza, verranno lette anche tutte
	 *            le sottocartelle
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
	 * @param filePath
	 *            path della directory di partenza, verranno lette anche tutte
	 *            le sottocartelle
	 * @return ArrayList con i path di tutti i file
	 */
	public static ArrayList<String> listFiles(String filePath) {

		ArrayList<String> list3 = new ArrayList<String>();
		String[] list2 = new File(filePath).list();
		for (int i1 = 0; i1 < list2.length; i1++) {
			String path1 = filePath + File.separator + list2[i1];
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
	 * @param filePath
	 *            path della directory di partenza, verranno lette anche tutte
	 *            le sottocartelle
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
				if (sorterUtils4.isDicomImage(path1))
					list3.add(path1);
			}
		}
		return list3;
	}

	/**
	 * La seguente routine, che si occupa di estrarre dati dall'header delle
	 * immagini si tratta da QueryDicomHeader.java di Anthony Padua & Daniel
	 * Barboriak - Duke University Medical Center. *** modified version ***
	 * Alberto Duina - Spedali Civili di Brescia - Servizio di Fisica Sanitaria
	 * 2006
	 * 
	 * @param imp
	 *            immagine di cui leggere l'header
	 * @param userInput
	 *            stringa di 9 caratteri contenente "group,element"
	 *            esempio:"0020,0013"
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
	 * @param path
	 *            path directory da vuotare
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
	 * cancellazione alternativa di directory piena, ma forse � deleteria, a noi
	 * serve di cancellare solo directory vuote, i file devono essere tutti
	 * spostati in altro luogo
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

	public static String filterChar(String in1) {
		String tmp1 = in1;

		tmp1 = tmp1.replace("[", "");
		tmp1 = tmp1.replace("]", "");
		tmp1 = tmp1.replace("/", "");
		tmp1 = tmp1.replace("\\", "");
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
		tmp1 = tmp1.replace(":", "");
		tmp1 = tmp1.replace("+", "");
		return tmp1;
	}

	/**
	 * Effettua il move di un file
	 * 
	 * @param dirDestinazione
	 *            destinazione
	 * @param pathSorgente
	 *            sorgente
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
	 * @param dirDestinazione
	 *            destinazione
	 * @param pathSorgente
	 *            sorgente
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
	 * @param sourceDir
	 *            direcctory sortata
	 * @param destinationDir
	 *            directory destinazione in cui spostare tutti i files
	 * @return true se ok
	 */
	public static boolean mainDESorterMethod2(String sourceDir, String destinationDir) {
		long totale = sorterUtils4.countFiles(sourceDir);
		ArrayList<String> lista = sorterUtils4.listFiles(sourceDir);
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
	 * @param sourceDir
	 *            direcctory sortata
	 * @param destinationDir
	 *            directory destinazione in cui spostare tutti i files
	 * @return true se ok
	 */
	public static boolean mainRENumberMethod(String renumberDir) {

		ArrayList<String> lista = sorterUtils4.listDicomFiles(renumberDir);

		String[] sourcePathVector = sorterUtils4.arrayListToArrayString(lista);

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
	 * @param sourceDir
	 *            direcctory sortata
	 * @param destinationDir
	 *            directory destinazione in cui spostare tutti i files
	 * @return true se ok
	 */
	public static boolean mainDESorterMethod(String sourceDir, String destinationDir) {

		long totale = sorterUtils4.countFiles(sourceDir);

		ArrayList<String> lista = sorterUtils4.listFiles(sourceDir);

		String[] sourcePathVector = sorterUtils4.arrayListToArrayString(lista);

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
		String[] foldersPathVector = sorterUtils4.arrayListToArrayString(lista2);
		for (int i1 = 0; i1 < foldersPathVector.length; i1++) {
			File deletable = new File(foldersPathVector[i1]);
			boolean ok4 = deleteDirectoryEmpty(deletable);
		}
		return ok;
	}

	/***
	 * Lista i folders
	 * 
	 * @param filePath
	 *            directory radice
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

	public static String extractFileName2(String path) {

		int pos = path.lastIndexOf(File.separator);
		String fileName = path.substring(pos + 1);
		return fileName;
	}

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

	public static String[] arrayListToArrayString(List<String> inArrayList) {
		Object[] objArr = inArrayList.toArray();
		String[] outStrArr = new String[objArr.length];
		for (int i1 = 0; i1 < objArr.length; i1++) {
			outStrArr[i1] = objArr[i1].toString();
		}
		return outStrArr;
	}

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

} // sorterUtils
