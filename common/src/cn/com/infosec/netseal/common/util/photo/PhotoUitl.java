package cn.com.infosec.netseal.common.util.photo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import cn.com.infosec.netseal.common.exceptions.runtime.NetSealRuntimeException;
import cn.com.infosec.netseal.common.resource.ErrCode;
import cn.com.infosec.netseal.common.util.FileUtil;
import cn.com.infosec.netseal.common.util.HexUtil;

public class PhotoUitl {

	public static byte[] convertBGColor(byte[] bs, int alpha) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(bs);
			BufferedImage bi = ImageIO.read(bais);
			BufferedImage tmp = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
			for (int i = 0, width = bi.getWidth(); i < width; i++) {
				for (int j = 0, height = bi.getHeight(); j < height; j++) {
					int rgb = bi.getRGB(i, j);
					// if (((rgb & 0xff000000) >> 24 & 0xff) > 0x80)
					// tmp.setRGB(i, j, rgb);
					// else
					// tmp.setRGB(i, j, Color.WHITE.getRGB());

					byte[] d = HexUtil.int2Byte(rgb);
					int a = d[0] & 0xff;
					int r = d[1] & 0xff;
					int g = d[2] & 0xff;
					int b = d[3] & 0xff;
					if (r == 255 && g == 0 && b == 0) {
						d[0] = (byte) (alpha & 0xff);
						rgb = HexUtil.byte2Int(d);
					}

					tmp.setRGB(i, j, rgb);
				}
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(tmp, "png", baos);

			return baos.toByteArray();
		} catch (Exception e) {
			throw new NetSealRuntimeException(ErrCode.GEN_SEAL_PHOTO_ERROR,
					"convert photo background color error, " + e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		byte[] data = FileUtil.getFile("f:/temp/seal.png");
		byte[] tmp = convertBGColor(data, 10);
		FileUtil.storeFile("f:/temp/sealTmp.png", tmp);
		tmp = convertBGColor(tmp, 255);
		FileUtil.storeFile("f:/temp/sealConvert.png", tmp);
	}
}
