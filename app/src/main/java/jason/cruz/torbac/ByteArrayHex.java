package jason.cruz.torbac;
import java.io.ByteArrayOutputStream;

// �o�C�g�z�����舵�����߂̕�?��I�N���X

public class ByteArrayHex {

	// �o�C�g�z���?C16?i�\�L�̕�����ɕϊ�
	static String toHexStr(byte[] d) {		
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < d.length; i++) {
			buf.append(String.format("%02x", d[i]));
		}
		return(buf.toString());
	}

	// 16?i�\�L�̕������?C�o�C�g�z��ɕϊ�
	static byte[] toByteArray(String d) {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		for (int i = 0; i < d.length(); i += 2) {
			result.write(Integer.parseInt(d.substring(i, i + 2), 16));
		}
		return(result.toByteArray());
	}
}