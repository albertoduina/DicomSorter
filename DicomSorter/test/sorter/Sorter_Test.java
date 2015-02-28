package sorter;

import sorter.sorterUtils;
import sorter.ExceptionTesting;
import utils.MyLog;
import ij.ImagePlus;
import ij.io.Opener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.*;

import static org.junit.Assert.*;

public class Sorter_Test {

	/**
	 * ok
	 */
	@Test()
	public void testCopyDirectory() {
		// ripulisco la zona di test
		String nameDir = "./Dati/Work";
		File dir = new File(nameDir);
		boolean success1 = false;
		if (dir.exists()) {
			success1 = sorterUtils.deleteDirectoryWithFiles(dir);
		} else {
			success1 = true;
		}
		assertTrue(success1);
		String src = "./Dati/Riserva";
		String dst = "./Dati/Work";
		boolean success2 = sorterUtils.copyDirectoryWithFiles(src, dst);
		assertTrue(success2);
	}

	/**
	 * ok
	 */
	@Test()
	public void testCountFiles() {
		String filePath = "./Dati/Work";
		int nfiles = sorterUtils.countFiles(filePath);
		assertEquals(8, nfiles);
	}

	/**
	 * ok
	 */
	@Test()
	public void testListFiles() {
		String filePath = "./Dati/Work";
		ArrayList<String> list = sorterUtils.listFiles(filePath);
		// System.out.printf("" + list);
		ArrayList<String> expect = new ArrayList<String>();
		expect.add("./Dati/Work/Uno/blob.dll");
		expect.add("./Dati/Work/Uno/Due/DICOM/49482616");
		expect.add("./Dati/Work/Uno/Due/DICOM/49482629");
		expect.add("./Dati/Work/Uno/Due/DICOM/49482642");
		expect.add("./Dati/Work/Uno/Due/DICOM/49482655");
		expect.add("./Dati/Work/Uno/Due/DICOM/49482668");
		expect.add("./Dati/Work/Uno/Due/DICOMDIR");
		expect.add("./Dati/Work/Uno/Due/iw2ayv.txt");
		assertEquals(expect, list);
	}

	/**
	 * ok
	 */
	@Test()
	public void testReadDicomParameterOk() {
		Opener o1 = new Opener();
		String path = "./Dati/Work/Uno/Due/DICOM/49482616";
		ImagePlus imp = o1.openImage(path);
		assertNotNull(imp);

		String DICOM_PATIENT_NAME = "0010,0010";
		String parameterValue = sorterUtils.readDicomParameter(imp,
				DICOM_PATIENT_NAME);
		assertEquals("cdqava071206", parameterValue);
	}

	/**
	 * ok
	 */
	@Test()
	public void testReadDicomParameterImpNull() {
		ImagePlus imp = null;
		String DICOM_PATIENT_NAME = "0010,0010";
		String parameterValue = sorterUtils.readDicomParameter(imp,
				DICOM_PATIENT_NAME);
		assertEquals(null, parameterValue);
	}

	/**
	 * ok
	 */
	@Test()
	public void testFilterChar() {
		String in1 = "stringa di test_\\/*?><_fine";
		String out1 = sorterUtils.filterChar(in1);
		String expected = "stringa di test__fine";
		assertEquals(expected, out1);
	}

	/**
	 * ok
	 */
	@Test()
	public void testReadDicomParameterHeaderNull() {
		Opener o1 = new Opener();
		String path = "./Dati/Work/Uno/blob.dll";
		ImagePlus imp = o1.openImage(path);
		assert (imp != null);
		String DICOM_PATIENT_NAME = "0010,0010";
		String parameterValue = sorterUtils.readDicomParameter(imp,
				DICOM_PATIENT_NAME);
		assertEquals(null, parameterValue);
	}

	/**
	 * ok
	 */
	@Test()
	public void testReadDicomParameterMissingAttribute() {
		Opener o1 = new Opener();
		String path = "./Dati/Work/Uno/Due/DICOM/49482616";
		ImagePlus imp = o1.openImage(path);
		assert (imp != null);
		String INEXISTENT = "aaaa,aaaa";
		String parameterValue = sorterUtils.readDicomParameter(imp, INEXISTENT);
		assertEquals("MISS", parameterValue);
	}

	/**
	 * ok
	 */
	@Test()
	public void testMoveFile() {
		String sorgente = "./Dati/Work/Uno/Due/DICOM/49482616";
		String destinazione = "./Dati/Work/Moved";
		boolean result = sorterUtils.moveFile(sorgente, destinazione);
		assertTrue(result);

		sorgente = "./Dati/Work/Uno/blob.dll";
		boolean result1 = sorterUtils.moveFile(sorgente, destinazione);
		assertTrue(result1);
	}

	/**
	 * ok
	 */
	@Test()
	public void testdeleteDirectoryWithFiles() {
		// Create a directory; all non-existent ancestor directories are
		// automatically created
		// boolean success = (new
		// File(".Dati/Work/Uno/Due/Tre/Quattro")).mkdirs();

		String name = "./Dati/Work/Uno/Due/Tre/Quattro/Cinque";
		boolean result1 = sorterUtils.createDirectory(name);
		boolean result2 = false;
		assertTrue(result1);
		File ff = null;
		name = "./Dati/Work/Uno/Due/Tre/Quattro/aaa.txt";
		try {
			ff = new File(name);
			result2 = ff.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		name = "./Dati/Work/Uno/Due/Tre";
		File dir1 = new File(name);
		boolean result = sorterUtils.deleteDirectoryWithFiles(dir1);
		assertTrue(result);
	}

	/**
	 * ok
	 */
	@Test()
	public void testdeleteDirectoryEmpty() {
		// Create a directory; all non-existent ancestor directories are
		// automatically created
		String name = "./Dati/Work/Uno/Due/Tre/Quattro/Cinque";
		boolean result1 = sorterUtils.createDirectory(name);

		assertTrue(result1);
		name = "./Dati/Work/Uno/Due/Tre";
		File dir = new File(name);
		boolean result = sorterUtils.deleteDirectoryEmpty(dir);
		assertTrue(result);
	}

	/**
	 * ok
	 */
	@Test()
	public void testRenameFileProgressive() {
		String newName = sorterUtils.renameFileProgressive("1234", 3);
		// System.out.printf("newName=" + newName);
		assertEquals("1234(3)", newName);
		newName = sorterUtils.renameFileProgressive("1234.xxx", 2);
		assertEquals("1234(2).xxx", newName);
		newName = sorterUtils.renameFileProgressive("1234(2).xxx", 5);
		assertEquals("1234(7).xxx", newName);
	}

	/**
	 * ok
	 */
	@Test()
	public void testCopyFileInexistent() {
		// ripulisco la zona di test
		String nameDir = "./Dati/Work/Moved";
		File dir = new File(nameDir);
		boolean success = false;
		if (dir.exists()) {
			success = sorterUtils.deleteDirectoryWithFiles(dir);
		} else {
			success = true;
		}
		assertTrue(success);
		MyLog.waitHere("success= "+success);

		boolean success1 = sorterUtils.createDirectory(nameDir);
		assertTrue(success1);
		File src = new File("./Dati/Work/Uno/Due/iw2ayv.aaa");
		File dst = new File( "./Dati/Work/Moved");
		boolean success2 = sorterUtils.copyFile(src, dst);
		assertFalse(success2);
		MyLog.waitHere("success2= "+success2);

		boolean success3 = false;
		if (dir.exists()) {
			success3 = sorterUtils.deleteDirectoryWithFiles(dir);
		} else {
			success3 = true;
		}
		assertTrue(success3);
		MyLog.waitHere("success3= "+success3);
	}

	@Test()
	public void testCopyFileSameName() {
		// ripulisco la zona di test
		String nameDir = "c:/Dati/Test3";
		File dir = new File(nameDir);
		boolean success = false;
		if (dir.exists()) {
			success = sorterUtils.deleteDirectoryWithFiles(dir);
		} else {
			success = true;
		}
		assertTrue(success);

		success = sorterUtils.createDirectory(nameDir);
		assertTrue(success);

		File src = new File("./Dati/Work/Uno/Due/iw2ayv.txt");
		File dst = new File("./Dati/Work/Uno/Due/iw2ayv.txt");

		success = sorterUtils.copyFile(src, dst);
		assertTrue(success);

		boolean ciprovo = dst.exists();
		assertTrue(ciprovo);

		dst.setReadOnly();

		success = sorterUtils.copyFile(src, dst);
		assertFalse(success);

		if (dir.exists()) {
			success = sorterUtils.deleteDirectoryWithFiles(dir);
		} else {
			success = true;
		}
		assertTrue(success);

	}


	/**
	 * ok
	 */
	@Test()
	public void testCreateDirectory() {
		String name = "./Dati/Work/Uno/Due/Tre/Quattro/Cinque";
		boolean result = sorterUtils.createDirectory(name);
		assertTrue(result);
		// testo che mi dia true anche se esiste già
		result = sorterUtils.createDirectory(name);
		assertTrue(result);
		MyLog.waitHere();
		// ripulisco la zona
		name = "./Dati/Work/Uno/Due/Tre";
		File dir = new File(name);
		result = sorterUtils.deleteDirectoryEmpty(dir);
		assertTrue(result);
	}

	@Test()
	public void testMainMethod() {
		// creiamo la struttura di directory su cui effettuare il test
		boolean success = (new File(
				"c:/Dati/Test3/uno/due/tre/quatto/cinque/sei/sette/otto"))
				.mkdirs();

		// System.out.printf("eseguo 2222\n");
		// nella struttura inserisco un file fasullo
		File ff = new File("c:/Dati/Test3/uno/due/tre/aaa.xxx");
		try {
			ff.createNewFile();
		} catch (Exception e) {
		}
		// ora copio qualche immagine dicom nelle cartelle di test

		File src = new File("C:/Dati/Test/59938608");
		File dst = new File("c:/Dati/Test3/59938608");

		success = sorterUtils.copyFile(src, dst);
		assertTrue(success);

		String filePath = "c:/Dati/Test3/";
		boolean ok = sorterUtils.mainMethod(filePath);
		assertTrue(ok);
	}

}
