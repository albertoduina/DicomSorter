package sorter;

import ij.IJ;
import ij.gui.WaitForUserDialog;
import ij.io.Opener;
import ij.ImagePlus;
import ij.ImageStack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class sorterUtils {

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
		long totale = sorterUtils.countFiles(filePath);

		ArrayList<String> lista = sorterUtils.listFiles(filePath);
		// IJ.log("sono state trovate " + lista.size() + " immagini da
		// sistemare");
		// for (int i1 = 0; i1 < lista.size(); i1++) {
		// IJ.log("" + lista.get(i1));
		// }

		ArrayList<ArrayList<String>> tabella = sorterUtils.sniffFiles(lista);
		ArrayList<String> lista2 = tabella.get(0);
		ArrayList<String> vetNomi = tabella.get(1);
		ArrayList<String> vetSeq = tabella.get(2);
		ArrayList<String> vetStudy = tabella.get(3);
		ArrayList<String> vetStudyDate = tabella.get(4);
		ArrayList<String> vetSeries = tabella.get(5);
		ArrayList<String> vetSeriesDescription = tabella.get(6);
		ArrayList<String> vetIma = tabella.get(7);
		ArrayList<String> vetAcq = tabella.get(8);
		ArrayList<String> vetAcqTime = tabella.get(9);
		ArrayList<String> vetCoil = tabella.get(10);
		ArrayList<String> vetProtocolName = tabella.get(11);

		// IJ.log("sono state confermate " + vetNomi.size()
		// + " immagini da sistemare");

		// new WaitForUserDialog("pause").show();

		for (int i1 = 0; i1 < vetNomi.size(); i1++) {
			IJ.log("eseguo il passo numero " + i1);
			// Create a directory; all ancestor directories must exist

			String directoryPatientName = filePath + vetNomi.get(i1);
			boolean success1 = sorterUtils
					.createDirectory(directoryPatientName);

			String studyName = "Study_" + vetStudy.get(i1) + "_"
					+ vetStudyDate.get(i1);
			String directoryStudyName = filePath + vetNomi.get(i1) + "\\"
					+ studyName;
			boolean success2 = sorterUtils.createDirectory(directoryStudyName);

			String aux12 = vetProtocolName.get(i1).trim();
			String aux11 = vetSeriesDescription.get(i1).trim();
			String aux13 = aux11 + aux12;

			String seriesName = "Series_" + vetSeries.get(i1) + "_" + aux13;

			String directorySeriesName = directoryStudyName + "\\" + seriesName;

			IJ.log("directoryPatientName= " + directoryPatientName);
			IJ.log("studyName= " + studyName);
			IJ.log("directoryStudyName= " + directoryStudyName);
			IJ.log("seriesName= " + seriesName);
			IJ.log("directorySeriesName= " + directorySeriesName);

			// new WaitForUserDialog("001 Do something, then click OK.").show();

			boolean success3 = sorterUtils.createDirectory(directorySeriesName);

			// File to be moved
			File moveFile = new File(lista2.get(i1));

			// Destination directory
			File destDir = new File(directorySeriesName);

			File sourceDir = new File(moveFile.getParent());

			String oldName = moveFile.getName();

			String newSeriesName = vetSeries.get(i1) + "_" + oldName;

			String aux = vetCoil.get(i1);

			if (aux != null) {
				if (aux.equals("C:HE1-4")) {
					aux = "HE5";
					oldName = newSeriesName + "_" + aux;
				} else {
					String aux1 = aux.replace(":", "x");
					String aux2 = aux1.replace(";", "v");
					oldName = newSeriesName + "_" + aux2;
				}
				IJ.log(vetSeriesDescription.get(i1));

				String aux1 = vetSeriesDescription.get(i1);

				if (aux1.length() >= 5) {
					oldName = oldName + "_"
							+ vetSeriesDescription.get(i1).substring(0, 5);
				}
			}

			// IJ.log("oldName="+oldName);
			// new WaitForUserDialog("001 Do something, then click OK.").show();

			String newName = "";
			// IJ.log("oldName=" + oldName);
			// IJ.log("sourceDir_= " + sourceDir);
			// IJ.log("destDir___= " + destDir);
			if (sourceDir.getName().equals(destDir.getName())) {
				// IJ.log("already in position");
			} else {
				// IJ.log("difference directory");
				boolean exist2 = false;
				// verifico inoltre se il file da spostare esiste nella
				// directory destinazione
				File newfile2 = new File(destDir, oldName);
				exist2 = newfile2.exists();
				int loop = 0;
				while (exist2) {
					loop++;
					newName = sorterUtils.renameFile(oldName, loop);
					// new WaitForUserDialog("rinomino?, then click
					// OK.").show();

					newfile2 = new File(destDir, newName);
					exist2 = newfile2.exists();
				}
				boolean moved3 = moveFile.renameTo(newfile2);
				IJ.showStatus("move " + i1 + "/" + totale);
				if (!moved3) {
					IJ.log("move problems " + moveFile + " in " + newfile2);
					// return false;
				} else {

				}

			}
		}

		// new WaitForUserDialog("Do something, then click OK.").show();
		// carico una situazione "fresca" delle directory
		String[] list2 = new File(filePath).list();
		for (int i1 = 0; i1 < list2.length; i1++) {
			File f1 = new File(filePath + "\\" + list2[i1]);
			if (sorterUtils.countFiles(f1.getPath()) == 0) {
				boolean delete2 = sorterUtils.deleteDirectoryWithFiles(f1);
				if (!delete2) {
					IJ.log("deleteDir problems " + f1);
					// return false;
				}
			}
		}

		return true;
	} // mainMethod

	/**
	 * Effettua il move di un file
	 * 
	 * @param dir
	 *            path della directory dove muovere il file
	 * @param file
	 *            path del file da muovere
	 * @return
	 */
	public static boolean moveFile(File dir, File file) {
		boolean success = file.renameTo(new File(dir, file.getName()));
		if (!success) {
			IJ.log("File " + file.getName() + " was not successfully moved");
		}

		return success;
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
			String path1 = filePath + "/" + list2[i1];
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
		// IJ.log("file path vale " + filePath);

		for (int i1 = 0; i1 < list2.length; i1++) {
			String path1 = filePath + "/" + list2[i1];
			// IJ.log("path1 vale " + path1);
			IJ.showStatus("listFiles " + i1 + "/" + list2.length);
			File f1 = new File(path1);
			if (f1.isDirectory()) {
				list3.addAll(listFiles(path1));
			} else {
				IJ.redirectErrorMessages();
				list3.add(path1);
			}
		}

		return list3;
	}

	/**
	 * Legge i dati dagli header dei file dicom
	 * 
	 * @param list
	 *            ArrayList con i pathe dei file da leggere
	 * @return ArrayList di ArrayList contenenti i vari dati letti
	 */
	public static ArrayList<ArrayList<String>> sniffFiles(ArrayList<String> list) {

		ArrayList<ArrayList<String>> myList = new ArrayList<ArrayList<String>>();
		ArrayList<String> vetNomi = new ArrayList<String>();
		ArrayList<String> vetSeq = new ArrayList<String>();
		ArrayList<String> vetStudy = new ArrayList<String>();
		ArrayList<String> vetStudyDate = new ArrayList<String>();
		ArrayList<String> vetSeries = new ArrayList<String>();
		ArrayList<String> vetSeriesDescription = new ArrayList<String>();
		ArrayList<String> vetIma = new ArrayList<String>();
		ArrayList<String> vetAcq = new ArrayList<String>();
		ArrayList<String> vetAcqTime = new ArrayList<String>();
		ArrayList<String> vetCoil = new ArrayList<String>();
		ArrayList<String> vetProtocolName = new ArrayList<String>();
		Opener o1 = new Opener();

		for (int i1 = 0; i1 < list.size(); i1++) {
			IJ.showStatus("sniffFiles " + i1 + "/" + list.size());

			IJ.redirectErrorMessages();
			ImagePlus imp1 = o1.openImage(list.get(i1));
			// if (imp1 == null) {
			// IJ.log("problema su " + list.get(i1));
			// // continue;
			// }
			vetNomi.add(readDicomParameter(imp1, DICOM_PATIENT_NAME));
			vetSeq.add(readDicomParameter(imp1, DICOM_SEQUENCE_NAME));
			vetStudy.add(readDicomParameter(imp1, DICOM_STUDY_ID));
			vetStudyDate.add(readDicomParameter(imp1, DICOM_STUDY_DATE));
			vetSeries.add(readDicomParameter(imp1, DICOM_SERIES_NUMBER));
			vetSeriesDescription.add(readDicomParameter(imp1,
					DICOM_SERIES_DESCRIPTION));
			vetIma.add(readDicomParameter(imp1, DICOM_IMAGE_NUMBER));
			vetAcq.add(readDicomParameter(imp1, DICOM_ACQUISITION_NUMBER));
			vetAcqTime.add(readDicomParameter(imp1, DICOM_ACQUISITION_TIME));
			vetCoil.add(readDicomParameter(imp1, DICOM_COIL));
			vetProtocolName.add(readDicomParameter(imp1, DICOM_PROTOCOL_NAME));
			// System.out.println("vetCoil= " + readDicomParameter(imp1,
			// DICOM_COIL));
			// }
		}
		myList.add(list);
		myList.add(vetNomi);
		myList.add(vetSeq);
		myList.add(vetStudy);
		myList.add(vetStudyDate);
		myList.add(vetSeries);
		myList.add(vetSeriesDescription);
		myList.add(vetIma);
		myList.add(vetAcq);
		myList.add(vetAcqTime);
		myList.add(vetCoil);
		myList.add(vetProtocolName);

		return myList;

	}

	/**
	 * La seguente routine, che si occupa di estrarre dati dall'header delle
	 * immagini è tratta da QueryDicomHeader.java di Anthony Padua & Daniel
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
		String header = stack.getSize() > 1 ? stack.getSliceLabel(currSlice)
				: (String) imp.getProperty("Info");
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

	public static boolean deleteDirectoryWithFiles(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			if (files == null)
				return false;
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					// System.out.printf("cancello directory
					// "+files[i].getName()+"\n");
					deleteDirectoryWithFiles(files[i]);
				} else {
					// System.out.printf("cancello file
					// "+files[i].getName()+"\n");
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	public static boolean deleteDirectoryEmpty(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectoryEmpty(files[i]);
					// } else {
					// files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	public static String renameFile(String oldName, int loop) {
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
			exists = (new File(directoryPath)).mkdir();
			if (!exists) {
				IJ.log("fallita la creazione di " + directoryPath);
			}
		}
		return (exists);
	}

	public static boolean copyFile(File in, File out) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(in);
		} catch (Exception e1) {
			System.out.println("copyFile 00001 " + e1);
			return false;
		}
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(out);
		} catch (Exception e1) {
			System.out.println("copyFile 00002 " + e1);
			return false;
		}
		try {
			byte[] buf = new byte[1024];
			int i = 0;
			while ((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		} catch (Exception e) {
			System.out.println("copyFile 00003 " + e);
			return false; // copy operation failed
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					System.out.println("copyFile 0004 " + e);
					return false;
				}
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					System.out.println("copyFile 0005" + e);
					return false;
				}
		}
		return true;
	}

} // sorterUtils
