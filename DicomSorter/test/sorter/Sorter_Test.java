package sorter;

import sorter.sorterUtils;
import sorter.ExceptionTesting;

import ij.ImagePlus;
import ij.io.Opener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.*;
import static org.junit.Assert.*;

public class Sorter_Test {

	@Test()
	public void testCountFiles() {
		String filePath = "c:/Dati/Test";
		int nfiles = sorterUtils.countFiles(filePath);
		assertEquals(32, nfiles);
	}

	@Test()
	public void testListFiles() {
		String filePath = "c:/Dati/Test";
		ArrayList<String> list = sorterUtils.listFiles(filePath);
		// System.out.printf("" + list);
		ArrayList<String> expect = new ArrayList<String>();

		expect.add("c:/Dati/Test/59938608");
		expect.add("c:/Dati/Test/aaa/001.aaa");
		expect.add("c:/Dati/Test/aaa/002.aaa");
		expect.add("c:/Dati/Test/due/59938608.due");
		expect.add("c:/Dati/Test/due/59938620.due");
		expect.add("c:/Dati/Test/Tre/001");
		expect.add("c:/Dati/Test/Tre/001.bmp");
		expect.add("c:/Dati/Test/Tre/001.tif");
		expect.add("c:/Dati/Test/Tre/002");
		expect.add("c:/Dati/Test/Tre/003");
		expect.add("c:/Dati/Test/Tre/004");
		expect.add("c:/Dati/Test/Tre/005");
		expect.add("c:/Dati/Test/Tre/006");
		expect.add("c:/Dati/Test/Tre/007");
		expect.add("c:/Dati/Test/Tre/008");
		expect.add("c:/Dati/Test/Tre/009");
		expect.add("c:/Dati/Test/Tre/010");
		expect.add("c:/Dati/Test/Tre/011");
		expect.add("c:/Dati/Test/Tre/012");
		expect.add("c:/Dati/Test/Tre/013");
		expect.add("c:/Dati/Test/Tre/014");
		expect.add("c:/Dati/Test/Tre/015");
		expect.add("c:/Dati/Test/Tre/016");
		expect.add("c:/Dati/Test/Tre/017");
		expect.add("c:/Dati/Test/Tre/018");
		expect.add("c:/Dati/Test/Tre/019");
		expect.add("c:/Dati/Test/Tre/020");
		expect.add("c:/Dati/Test/Tre/021");
		expect.add("c:/Dati/Test/uno/01.uno");
		expect.add("c:/Dati/Test/uno/02.uno");
		expect.add("c:/Dati/Test/uno/due/59938608.due");
		expect.add("c:/Dati/Test/uno/due/59938620.due");
		assertEquals(expect, list);
	}

	@Test()
	public void testSniffFiles() {
		String filePath = "c:/Dati/Test/Uno";
		ArrayList<String> list = sorterUtils.listFiles(filePath);
		ArrayList<ArrayList<String>> myList = sorterUtils.sniffFiles(list);
		// System.out.printf("" + myList);

		ArrayList<String> vetLista = new ArrayList<String>();
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
		ArrayList<ArrayList<String>> myExpected = new ArrayList<ArrayList<String>>();

		vetLista.add("c:/Dati/Test/Uno/01.uno");
		vetLista.add("c:/Dati/Test/Uno/02.uno");
		vetLista.add("c:/Dati/Test/Uno/due/59938608.due");
		vetLista.add("c:/Dati/Test/Uno/due/59938620.due");

		vetNomi.add("cdqold311106");
		vetNomi.add("cdqold311106");
		vetNomi.add("cdqold311106");
		vetNomi.add("cdqold311106");

		vetSeq.add("*se2d1");
		vetSeq.add("*se2d1");
		vetSeq.add("*se2d1");
		vetSeq.add("*se2d1");

		vetStudy.add("1");
		vetStudy.add("1");
		vetStudy.add("1");
		vetStudy.add("1");

		vetStudyDate.add("20061031");
		vetStudyDate.add("20061031");
		vetStudyDate.add("20061031");
		vetStudyDate.add("20061031");

		vetSeries.add("1");
		vetSeries.add("2");
		vetSeries.add("1");
		vetSeries.add("2");

		vetSeriesDescription.add("HUSA_se_15b130_ass");
		vetSeriesDescription.add("HUSA_se_15b130_ass");
		vetSeriesDescription.add("HUSA_se_15b130_ass");
		vetSeriesDescription.add("HUSA_se_15b130_ass");

		vetIma.add("1");
		vetIma.add("1");
		vetIma.add("1");
		vetIma.add("1");

		vetAcq.add("1");
		vetAcq.add("2");
		vetAcq.add("1");
		vetAcq.add("2");

		vetAcqTime.add("070316.927504");
		vetAcqTime.add("070736.929997");
		vetAcqTime.add("070316.927504");
		vetAcqTime.add("070736.929997");
		
		vetCoil.add("MISS");
		vetCoil.add("MISS");
		vetCoil.add("MISS");
		vetCoil.add("MISS");
		
		myExpected.add(list);
		myExpected.add(vetNomi);
		myExpected.add(vetSeq);
		myExpected.add(vetStudy);
		myExpected.add(vetStudyDate);
		myExpected.add(vetSeries);
		myExpected.add(vetSeriesDescription);
		myExpected.add(vetIma);
		myExpected.add(vetAcq);
		myExpected.add(vetAcqTime);
		myExpected.add(vetCoil);

		assertEquals(myExpected, myList);

	}

	@Test()
	public void testReadDicomParameterOk() {
		Opener o1 = new Opener();
		String path = "C:/Dati/Test/59938608";
		ImagePlus imp = o1.openImage(path);
		assertNotNull(imp);

		String DICOM_PATIENT_NAME = "0010,0010";
		String parameterValue = sorterUtils.readDicomParameter(imp,
				DICOM_PATIENT_NAME);
		assertEquals("cdqold311106", parameterValue);
	}

	@Test()
	public void testReadDicomParameterImpNull() {
		ImagePlus imp = null;
		String DICOM_PATIENT_NAME = "0010,0010";
		String parameterValue = sorterUtils.readDicomParameter(imp,
				DICOM_PATIENT_NAME);
		assertEquals(null, parameterValue);
	}

	@Test()
	public void testReadDicomParameterHeaderNull() {
		Opener o1 = new Opener();
		String path = "C:/Dati/Test/Tre/001.bmp";
		ImagePlus imp = o1.openImage(path);
		assert (imp != null);
		String DICOM_PATIENT_NAME = "0010,0010";
		String parameterValue = sorterUtils.readDicomParameter(imp,
				DICOM_PATIENT_NAME);
		assertEquals(null, parameterValue);
	}

	@Test()
	public void testReadDicomParameterMissingAttribute() {
		Opener o1 = new Opener();
		String path = "C:/Dati/Test/59938608";
		ImagePlus imp = o1.openImage(path);
		assert (imp != null);
		String INEXISTENT = "aaaa,aaaa";
		String parameterValue = sorterUtils.readDicomParameter(imp, INEXISTENT);
		assertEquals("MISS", parameterValue);
	}

	@Test()
	public void testMoveFile() {
		File file = new File("C:/Dati/Test/uno/01.uno");
		File dir = new File("c:/Dati/Test/uno/due");
		boolean result = sorterUtils.moveFile(dir, file);
		assertTrue(result);

		File file1 = new File("C:/Dati/Test/uno/due/01.uno");
		File dir1 = new File("c:/Dati/Test/uno/");
		boolean result1 = sorterUtils.moveFile(dir1, file1);
		assertTrue(result1);
	}

	@Test()
	public void testdeleteDirectoryWithFiles() {
		// Create a directory; all non-existent ancestor directories are
		// automatically created
		boolean success = (new File(
				"c:/Dati/Test3/uno/due/tre/quatto/cinque/sei/sette/otto"))
				.mkdirs();
		// assertTrue(success);
		// System.out.printf("eseguo 2222\n");
		File ff = new File("c:/Dati/Test3/uno/due/tre/aaa.xxx");
		try {
			ff.createNewFile();
		} catch (Exception e) {
		}
		// System.out.printf("eseguo 3333\n");
		File dir = new File("c:/Dati/Test3");
		boolean result = sorterUtils.deleteDirectoryWithFiles(dir);
		assertTrue(result);
	}

	@Test()
	public void testdeleteDirectoryEmpty() {
		// Create a directory; all non-existent ancestor directories are
		// automatically created
		boolean success = (new File(
				"c:/Dati/Test3/uno/due/tre/quatto/cinque/sei/sette/otto"))
				.mkdirs();
		assertTrue(success);
		// new WaitForUserDialog("Do something, then click OK.").show();
		File dir = new File("c:/Dati/Test3");
		boolean result = sorterUtils.deleteDirectoryEmpty(dir);
		assertTrue(result);
	}

	@Test()
	public void testRenameFile1() {
		String newName = sorterUtils.renameFile("1234", 3);
		// System.out.printf("newName=" + newName);
		assertEquals("1234(3)", newName);
		newName = sorterUtils.renameFile("1234.xxx", 2);
		assertEquals("1234(2).xxx", newName);
		newName = sorterUtils.renameFile("1234(2).xxx", 5);
		assertEquals("1234(7).xxx", newName);
	}

	@Test()
	public void testCopyFile() {
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
		File src = new File("C:/Dati/Test/59938608");
		File dst = new File("c:/Dati/Test3/59938608");

		success = sorterUtils.copyFile(src, dst);

		assertTrue(success);
		assertEquals(src.length(), dst.length());

		if (dir.exists()) {
			success = sorterUtils.deleteDirectoryWithFiles(dir);
		} else {
			success = true;
		}
		assertTrue(success);

	}

	@Test()
	public void testCopyFileInexistent() {
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
		File src = new File("C:/Dati/Test/aaaa");
		File dst = new File("c:/Dati/Test3/59938608");

		success = sorterUtils.copyFile(src, dst);

		assertFalse(success);

		if (dir.exists()) {
			success = sorterUtils.deleteDirectoryWithFiles(dir);
		} else {
			success = true;
		}
		assertTrue(success);

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

		File src = new File("C:/Dati/Test/59938608");
		File dst = new File("c:/Dati/Test3/59938608");

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

//	@Test()
//	public void testCopyFileIOError() {
//		try {
//			ExceptionTesting et = new ExceptionTesting();
//			et.IOExceptionTesting();
//		} catch (Exception e) {
//			System.out.println("testCopyFileIOError 0001 " + e);
//		}
//	}

	@Test()
	public void testCreateDirectory() {
		String name = "c:/Dati/Test3";
		boolean result = sorterUtils.createDirectory(name);
		assertTrue(result);
		// testo che mi dia true anche se esiste già
		result = sorterUtils.createDirectory(name);
		assertTrue(result);
		// ripulisco la zona
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
