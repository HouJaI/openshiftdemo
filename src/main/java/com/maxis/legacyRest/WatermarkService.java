package com.maxis.legacyRest;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.multipdf.Overlay;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.Matrix;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@RestController
public class WatermarkService {

	@Bean
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
		resolver.setDefaultEncoding("utf-8");
		return resolver;
	}

	@RequestMapping(value = "/uploadForMarkPDF", method = RequestMethod.POST, consumes = { "multipart/mixed" })
	public ResponseEntity<byte[]> pdfMark(@RequestParam(value = "pdfFile", required = false) MultipartFile pdfFile) {

		try {

			System.out.println(pdfFile.getOriginalFilename());

			File convFile = new File(pdfFile.getOriginalFilename());
			pdfFile.transferTo(convFile);

			InputStream inputstream = markJob(convFile);

			byte[] pdfFilecont = IOUtils.toByteArray(inputstream);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.parseMediaType("application/pdf"));
			String filename = pdfFile.getName();
			headers.setContentDispositionFormData(filename, filename);
			headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
			ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(pdfFilecont, headers, HttpStatus.OK);
			return response;

		} catch (Exception ex) {
			return null;
		}
	}

	private InputStream markJob(File pdfFile) throws Exception {
		PDDocument overlayDoc = new PDDocument();
		PDPage page = new PDPage();
		overlayDoc.addPage(page);
		Overlay overlayObj = new Overlay();
		PDFont font = PDType1Font.COURIER_OBLIQUE;

		PDPageContentStream contentStream = new PDPageContentStream(overlayDoc, page);
		contentStream.setFont(font, 60.0f);
		contentStream.setNonStrokingColor(0);

		PDRectangle pageSize = page.getMediaBox();
		float centeredXPosition = (pageSize.getWidth() - 60.0f / 1000f) / 4f;
		float centeredYPosition = (pageSize.getHeight() - 60.0f / 1000f) / 3f;

		contentStream.beginText();

		contentStream.setTextMatrix(Matrix.getRotateInstance(1 * Math.PI * 0.25, centeredXPosition, centeredYPosition));

		contentStream.showText("For MAXIS Only"); // deprecated. Use
													// showText(String text)
		contentStream.endText();
		contentStream.stroke();
		contentStream.close();

		PDDocument originalDoc = PDDocument.load(pdfFile);
		overlayObj.setOverlayPosition(Overlay.Position.FOREGROUND);
		overlayObj.setInputPDF(originalDoc);
		overlayObj.setAllPagesOverlayPDF(overlayDoc);
		Map<Integer, String> ovmap = new HashMap<Integer, String>();

		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		overlayObj.overlay(ovmap).save(stream);

		InputStream overlayedContent = new ByteArrayInputStream(stream.toByteArray());

		overlayDoc.close();
		originalDoc.close();

		return overlayedContent;
	}

	@RequestMapping(value = "/uploadForMarkImg", method = RequestMethod.POST, consumes = { "multipart/mixed" })
	public ResponseEntity<byte[]> imgMark(@RequestParam(value = "imgFile", required = false) MultipartFile imgFile) {

		try {

			File watermarkImageFile = new File("C:\\watermarktest\\watermark.png");
			File destImageFile = new File("destinationFile.jpg");

			File convFile = new File(imgFile.getOriginalFilename());
			convFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(convFile);
			fos.write(imgFile.getBytes());
			fos.close();

			BufferedImage resizeOriginal = null;
			BufferedImage sourceImage = ImageIO.read(convFile);
			BufferedImage watermarkImage = ImageIO.read(watermarkImageFile);

			System.out.println("SouceImage Size Width: " +sourceImage.getWidth());
			System.out.println("SouceImage Size Height: " +sourceImage.getHeight());
			
			if (sourceImage.getWidth() > 800 && sourceImage.getHeight() > 600) {
				resizeOriginal = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = resizeOriginal.createGraphics();
				g.drawImage(sourceImage, 0, 0, 800, 600, null);
				g.dispose();
			} else {
				resizeOriginal = sourceImage;
			}

			// initializes necessary graphic properties
			Graphics2D g2d = (Graphics2D) resizeOriginal.getGraphics();
			AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
			g2d.setComposite(alphaChannel); // calculates the coordinate where
											// the image is painted
			int topLeftX = (resizeOriginal.getWidth() - watermarkImage.getWidth()) / 2;
			int topLeftY = (resizeOriginal.getHeight() - watermarkImage.getHeight()) / 2;
			// paints the image watermark
			g2d.drawImage(watermarkImage, topLeftX, topLeftY, null);
			ImageIO.write(resizeOriginal, "png", destImageFile);
			g2d.dispose();
			
			
			byte[] bytesArray = new byte[(int) destImageFile.length()];

			FileInputStream fis = new FileInputStream(destImageFile);
			fis.read(bytesArray); //read file into bytes[]
			fis.close();
 
			
			
			System.out.println("The image watermark is added to the image.");
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.parseMediaType("application/octect-stream"));
			String filename = destImageFile.getName();
			headers.setContentDispositionFormData(filename, filename);
			headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
			ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(bytesArray, headers, HttpStatus.OK);
			return response;
			
		} catch (IOException ex) {
			System.err.println(ex);
			return null;
		}
	}

}
