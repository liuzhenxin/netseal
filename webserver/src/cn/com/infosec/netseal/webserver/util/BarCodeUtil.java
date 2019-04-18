package cn.com.infosec.netseal.webserver.util;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.FileUtil;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

public class BarCodeUtil {

	/**
	 * 生成图像
	 * 
	 * @throws WriterException
	 * @throws IOException
	 */
	public static void genImage(String jpgPath, String content, String barCodeType) {
		try {
			FileUtil.createDir(jpgPath);
		} catch (Exception e1) {
			throw new NetSealRuntimeException(ErrCode.GEN_BARCODE_ERROR, "create dir error, " + e1.getMessage());
		}

		int width = 200; // 图像宽度
		int height = 200; // 图像高度
		String format = "jpg";// 图像类型
		try {
			Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
			hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

			BarcodeFormat bf = BarcodeFormat.QR_CODE;
			if ("1".equals(barCodeType))
				bf = BarcodeFormat.PDF_417;

			BitMatrix bitMatrix = new MultiFormatWriter().encode(content, bf, width, height, hints);// 生成矩阵

			Path path = FileSystems.getDefault().getPath(jpgPath);
			MatrixToImageWriter.writeToPath(bitMatrix, format, path);// 输出图像
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.GEN_BARCODE_ERROR, "generator barCode image error, " + e.getMessage());
		}

	}
	// /**
	// * 解析图像
	// */
	// public static void testDecode() {
	// String filePath = "f:/temp/qr.jpg";
	// BufferedImage image;
	// try {
	// image = ImageIO.read(new File(filePath));
	// LuminanceSource source = new BufferedImageLuminanceSource(image);
	// Binarizer binarizer = new HybridBinarizer(source);
	// BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
	// Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType, Object>();
	// hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
	// Result result = new MultiFormatReader().decode(binaryBitmap, hints);// 对图像进行解码
	// System.out.println(result.getText());
	// System.out.println("encode：	" + result.getBarcodeFormat());
	// } catch (IOException e) {
	// e.printStackTrace();
	// } catch (NotFoundException e) {
	// e.printStackTrace();
	// }
	// }

}
