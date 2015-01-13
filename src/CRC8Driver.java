// Examples showing how to invoke CRC8.checksum()

class CRC8Driver   {

	public static void printChecksum(byte result) {
		// print out the checksum in binary
		System.out.println("The 8-bit check sum w/o leading zeros is "+Integer.toBinaryString(result&0x000000ff)+".");
	}

	public static void main(String[] args)  {
		CRC8 crc8 = new CRC8();
		
		byte[] data1 = {(byte)0X83, (byte)0x00};
		byte[] data2 = {(byte)0X82, (byte)0x00};
		byte[] data3 = {(byte)0X01, (byte)0x00};
		byte[] data4 = {(byte)0X41, (byte)0x82, (byte)0x00};
		byte[] data5 = {(byte)0X83, (byte)0x88, (byte)0x38, (byte)0x00};
		byte[] data6 = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 0};
		byte crc1 = crc8.checksum(data1);
		byte crc2 = crc8.checksum(data2);
		byte crc3 = crc8.checksum(data3);
		byte crc4 = crc8.checksum(data4);
		byte crc5 = crc8.checksum(data5);
		byte crc6 = crc8.checksum(data6);
		printChecksum(crc1);
		printChecksum(crc2);
		printChecksum(crc3);
		printChecksum(crc4);
		printChecksum(crc5);
		printChecksum(crc6);

		System.out.println();
		System.out.println("Checksum of checksumed frame should be 0.");
		data1[1] = crc1;
		printChecksum(crc8.checksum(data1));	
		data2[1] = crc2;
		printChecksum(crc8.checksum(data2));	
		data3[1] = crc3;
		printChecksum(crc8.checksum(data3));	
		data4[2] = crc4;
		printChecksum(crc8.checksum(data4));	
		data5[3] = crc5;
		printChecksum(crc8.checksum(data5));	
		data6[7] = crc6;
		printChecksum(crc8.checksum(data6));		
	}
}
