// 128-bit AES ECB on pure-Kotlin(no extension or library used)
// operate on two-dimensional array of integers
// handcrafted by Caniggia Syabil, caniggia.syabil@ui.ac.id

class Aes(text: String, key: String) {
    companion object {
        val sBox: Array<IntArray> = arrayOf(
            intArrayOf(0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76),
            intArrayOf(0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0),
            intArrayOf(0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15),
            intArrayOf(0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75),
            intArrayOf(0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84),
            intArrayOf(0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf),
            intArrayOf(0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8),
            intArrayOf(0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2),
            intArrayOf(0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73),
            intArrayOf(0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb),
            intArrayOf(0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79),
            intArrayOf(0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08),
            intArrayOf(0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a),
            intArrayOf(0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e),
            intArrayOf(0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf),
            intArrayOf(0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16),
        )
        val inverseSBox: Array<IntArray> = arrayOf(
            intArrayOf(0x52, 0x09, 0x6a, 0xd5, 0x30, 0x36, 0xa5, 0x38, 0xbf, 0x40, 0xa3, 0x9e, 0x81, 0xf3, 0xd7, 0xfb),
            intArrayOf(0x7c, 0xe3, 0x39, 0x82, 0x9b, 0x2f, 0xff, 0x87, 0x34, 0x8e, 0x43, 0x44, 0xc4, 0xde, 0xe9, 0xcb),
            intArrayOf(0x54, 0x7b, 0x94, 0x32, 0xa6, 0xc2, 0x23, 0x3d, 0xee, 0x4c, 0x95, 0x0b, 0x42, 0xfa, 0xc3, 0x4e),
            intArrayOf(0x08, 0x2e, 0xa1, 0x66, 0x28, 0xd9, 0x24, 0xb2, 0x76, 0x5b, 0xa2, 0x49, 0x6d, 0x8b, 0xd1, 0x25),
            intArrayOf(0x72, 0xf8, 0xf6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xd4, 0xa4, 0x5c, 0xcc, 0x5d, 0x65, 0xb6, 0x92),
            intArrayOf(0x6c, 0x70, 0x48, 0x50, 0xfd, 0xed, 0xb9, 0xda, 0x5e, 0x15, 0x46, 0x57, 0xa7, 0x8d, 0x9d, 0x84),
            intArrayOf(0x90, 0xd8, 0xab, 0x00, 0x8c, 0xbc, 0xd3, 0x0a, 0xf7, 0xe4, 0x58, 0x05, 0xb8, 0xb3, 0x45, 0x06),
            intArrayOf(0xd0, 0x2c, 0x1e, 0x8f, 0xca, 0x3f, 0x0f, 0x02, 0xc1, 0xaf, 0xbd, 0x03, 0x01, 0x13, 0x8a, 0x6b),
            intArrayOf(0x3a, 0x91, 0x11, 0x41, 0x4f, 0x67, 0xdc, 0xea, 0x97, 0xf2, 0xcf, 0xce, 0xf0, 0xb4, 0xe6, 0x73),
            intArrayOf(0x96, 0xac, 0x74, 0x22, 0xe7, 0xad, 0x35, 0x85, 0xe2, 0xf9, 0x37, 0xe8, 0x1c, 0x75, 0xdf, 0x6e),
            intArrayOf(0x47, 0xf1, 0x1a, 0x71, 0x1d, 0x29, 0xc5, 0x89, 0x6f, 0xb7, 0x62, 0x0e, 0xaa, 0x18, 0xbe, 0x1b),
            intArrayOf(0xfc, 0x56, 0x3e, 0x4b, 0xc6, 0xd2, 0x79, 0x20, 0x9a, 0xdb, 0xc0, 0xfe, 0x78, 0xcd, 0x5a, 0xf4),
            intArrayOf(0x1f, 0xdd, 0xa8, 0x33, 0x88, 0x07, 0xc7, 0x31, 0xb1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xec, 0x5f),
            intArrayOf(0x60, 0x51, 0x7f, 0xa9, 0x19, 0xb5, 0x4a, 0x0d, 0x2d, 0xe5, 0x7a, 0x9f, 0x93, 0xc9, 0x9c, 0xef),
            intArrayOf(0xa0, 0xe0, 0x3b, 0x4d, 0xae, 0x2a, 0xf5, 0xb0, 0xc8, 0xeb, 0xbb, 0x3c, 0x83, 0x53, 0x99, 0x61),
            intArrayOf(0x17, 0x2b, 0x04, 0x7e, 0xba, 0x77, 0xd6, 0x26, 0xe1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0c, 0x7d),
        )
        val rCon: IntArray = intArrayOf(
            0x00, 0x01, 0x02, 0x04,
            0x08, 0x10, 0x20, 0x40,
            0x80, 0x1B, 0x36, 0x6C,
            0xD8, 0xAB, 0x4D, 0x9A,
            0x2F, 0x5E, 0xBC, 0x63,
            0xC6, 0x97, 0x35, 0x6A,
            0xD4, 0xB3, 0x7D, 0xFA,
            0xEF, 0xC5, 0x91, 0x39,
        )
        val finiteField = arrayOf(
            intArrayOf(0x02, 0x03, 0x01, 0x01),
            intArrayOf(0x01, 0x02, 0x03, 0x01),
            intArrayOf(0x01, 0x01, 0x02, 0x03),
            intArrayOf(0x03, 0x01, 0x01, 0x02),
        )
        val inverseField = arrayOf(
            intArrayOf(0x0e, 0x0b, 0x0d, 0x09),
            intArrayOf(0x09, 0x0e, 0x0b, 0x0d),
            intArrayOf(0x0d, 0x09, 0x0e, 0x0b),
            intArrayOf(0x0b, 0x0d, 0x09, 0x0e),
        )
    }

    val encrypt = encrypt(text, key)
    val decrypt = decrypt(text, key)
    private fun IntArray.rotate(n: Int) = sliceArray(n until size) + sliceArray(0 until n)
    private fun IntArray.rRotate(n: Int) = sliceArray(size - n until size) + sliceArray(0 until size - n)
    private fun galoisField(intA: Int, intB: Int): Int {
        var p = 0
        var tempInt1 = intA
        var tempInt2 = intB
        for (i in 0..7) {
            if (tempInt2.and(1) == 1) p = p.xor(tempInt1)
            val hiBitSet = tempInt1.and(128)
            tempInt1 = tempInt1.shl(1)
            if (hiBitSet == 128) tempInt1 = tempInt1.xor(27)
            tempInt2 = tempInt2.shr(1)
        }
        return p.mod(256)
    }

    private fun str16bitToNDArray(aString: String): Array<IntArray> {
        var hexString = ""
        val nullArray = arrayOfNulls<Int>(16)
        var j = 0
        for (i in aString + "n") if (hexString.length < 2) hexString += i else {
            nullArray[j] = Integer.decode("0x${hexString}")
            hexString = i.toString();j++
        }
        val firstNullArray = arrayOfNulls<Int>(4)
        for (i in 0..3) firstNullArray[i] = nullArray[i]
        val secondNullArray = arrayOfNulls<Int>(4)
        for (i in 4..7) secondNullArray[i - 4] = nullArray[i]
        val thirdNullArray = arrayOfNulls<Int>(4)
        for (i in 8..11) thirdNullArray[i - 8] = nullArray[i]
        val fourthNullArray = arrayOfNulls<Int>(4)
        for (i in 12..15) fourthNullArray[i - 12] = nullArray[i]
        val row0Array = firstNullArray.requireNoNulls().toIntArray()
        val row1Array = secondNullArray.requireNoNulls().toIntArray()
        val row2Array = thirdNullArray.requireNoNulls().toIntArray()
        val row3Array = fourthNullArray.requireNoNulls().toIntArray()
        return arrayOf(row0Array, row1Array, row2Array, row3Array)
    }

    private fun xorNDArray(nDArrayA: Array<IntArray>, nDArrayB: Array<IntArray>): Array<IntArray> {
        for (i in 0..3) for (j in 0..3) nDArrayA[i][j] = nDArrayA[i][j].xor(nDArrayB[i][j])
        return nDArrayA
    }

    private fun rotWord(nDArray: Array<IntArray>): IntArray {
        return intArrayOf(nDArray[3][0], nDArray[3][1], nDArray[3][2], nDArray[3][3]).rotate(1)
    }

    private fun subWord(anArray: IntArray, sBox: Array<IntArray>): IntArray {
        for (i in 0..3) {
            val myNum = anArray[i]
            var hexString = Integer.toHexString(myNum)
            hexString = if (hexString.length < 2) "0x0$hexString" else "0x$hexString"
            val rowRoundConst = Integer.decode("0x0${hexString[2]}")
            val colRoundConst = Integer.decode("0x0${hexString[3]}")
            anArray[i] = sBox[rowRoundConst][colRoundConst]
        }
        return anArray
    }

    private fun xorRCon(anArray: IntArray, roundIs: Int, rCon: IntArray): IntArray {
        val tempArray: IntArray = intArrayOf(rCon[roundIs], 0, 0, 0)
        for (i in 0..3) anArray[i] = anArray[i].xor(tempArray[i])
        return anArray
    }

    private fun wink(anArray: IntArray, nDArray: Array<IntArray>): Array<IntArray> {
        val tempArray0 = arrayOfNulls<Int>(4)
        for (i in 0..3) tempArray0[i] = anArray[i].xor(nDArray[0][i])
        val tempArray1 = arrayOfNulls<Int>(4)
        for (i in 0..3) tempArray1[i] = tempArray0[i]?.xor(nDArray[1][i])
        val tempArray2 = arrayOfNulls<Int>(4)
        for (i in 0..3) tempArray2[i] = tempArray1[i]?.xor(nDArray[2][i])
        val tempArray3 = arrayOfNulls<Int>(4)
        for (i in 0..3) tempArray3[i] = tempArray2[i]?.xor(nDArray[3][i])
        nDArray[0] = tempArray0.requireNoNulls().toIntArray()
        nDArray[1] = tempArray1.requireNoNulls().toIntArray()
        nDArray[2] = tempArray2.requireNoNulls().toIntArray()
        nDArray[3] = tempArray3.requireNoNulls().toIntArray()
        return nDArray
    }

    private fun inverseWink(anArray: IntArray, nDArray: Array<IntArray>): Array<IntArray> {
        val tempArray0 = arrayOfNulls<Int>(4)
        for (i in 0..3) tempArray0[i] = nDArray[0][i].xor(anArray[i])
        val tempArray1 = arrayOfNulls<Int>(4)
        for (i in 0..3) tempArray1[i] = nDArray[1][i].xor(nDArray[0][i])
        val tempArray2 = arrayOfNulls<Int>(4)
        for (i in 0..3) tempArray2[i] = nDArray[2][i].xor(nDArray[1][i])
        val tempArray3 = arrayOfNulls<Int>(4)
        for (i in 0..3) tempArray3[i] = nDArray[3][i].xor(nDArray[2][i])
        nDArray[0] = tempArray0.requireNoNulls().toIntArray()
        nDArray[1] = tempArray1.requireNoNulls().toIntArray()
        nDArray[2] = tempArray2.requireNoNulls().toIntArray()
        nDArray[3] = tempArray3.requireNoNulls().toIntArray()
        return nDArray
    }

    private fun subBytes(nDArray: Array<IntArray>, sBox: Array<IntArray>): Array<IntArray> {
        for (i in 0..3) for (j in 0..3) {
            val tempArray = nDArray[i][j]
            var hexString = Integer.toHexString(tempArray)
            hexString = if (hexString.length < 2) "0x0$hexString" else "0x$hexString"
            val rowRoundConst = Integer.decode("0x0${hexString[2]}")
            val colRoundConst = Integer.decode("0x0${hexString[3]}")
            nDArray[i][j] = sBox[rowRoundConst][colRoundConst]
        }
        return nDArray
    }

    private fun shiftRows(nDArray: Array<IntArray>): Array<IntArray> {
        val tempArray: Array<Array<Int?>> =
            arrayOf(arrayOfNulls(4), arrayOfNulls(4), arrayOfNulls(4), arrayOfNulls(4))
        for (i in 0..3) for (j in 0..3) tempArray[j][i] = nDArray[i][j]
        for (i in 0..3) nDArray[i] = tempArray[i].requireNoNulls().toIntArray().rotate(i)
        for (i in 0..3) for (j in 0..3) tempArray[j][i] = nDArray[i][j]
        for (i in 0..3) nDArray[i] = tempArray[i].requireNoNulls().toIntArray()
        return nDArray
    }

    private fun invertShiftRows(nDArray: Array<IntArray>): Array<IntArray> {
        val tempArray: Array<Array<Int?>> =
            arrayOf(arrayOfNulls(4), arrayOfNulls(4), arrayOfNulls(4), arrayOfNulls(4))
        for (i in 0..3) for (j in 0..3) tempArray[j][i] = nDArray[i][j]
        for (i in 0..3) nDArray[i] = tempArray[i].requireNoNulls().toIntArray().rRotate(i)
        for (i in 0..3) for (j in 0..3) tempArray[j][i] = nDArray[i][j]
        for (i in 0..3) nDArray[i] = tempArray[i].requireNoNulls().toIntArray()
        return nDArray
    }

    private fun mixColumns(nDArray: Array<IntArray>, finiteField: Array<IntArray>): Array<IntArray> {
        val tempArray: Array<Array<Int?>> =
            arrayOf(arrayOfNulls(4), arrayOfNulls(4), arrayOfNulls(4), arrayOfNulls(4))
        for (i in 0..3) for (j in 0..3) {
            val a = galoisField(finiteField[j][0], nDArray[i][0])
            val b = galoisField(finiteField[j][1], nDArray[i][1])
            val c = galoisField(finiteField[j][2], nDArray[i][2])
            val d = galoisField(finiteField[j][3], nDArray[i][3])
            tempArray[i][j] = a.xor(b).xor(c).xor(d)
        }
        for (i in 0..3) nDArray[i] = tempArray[i].requireNoNulls().toIntArray()
        return nDArray
    }

    private fun joinText(textNDArray: Array<IntArray>): String {
        var aString = ""
        for (i in 0..3) for (j in 0..3) {
            aString += if (Integer.toHexString(textNDArray[i][j]).length == 1)
                "0" + Integer.toHexString(textNDArray[i][j]) else Integer.toHexString(textNDArray[i][j])
        }
        return aString
    }

    private fun encrypt(text: String, key: String): String {
        var roundIs = 1
        var textNDArray = str16bitToNDArray(text)
        var keyNDArray = str16bitToNDArray(key)
        textNDArray = xorNDArray(textNDArray, keyNDArray)
        while (roundIs < 11) {
            val fourthRow = xorRCon(subWord(rotWord(keyNDArray), sBox), roundIs, rCon)
            keyNDArray = wink(fourthRow, keyNDArray)
            textNDArray = subBytes(textNDArray, sBox)
            textNDArray = shiftRows(textNDArray)
            if (roundIs != 10) textNDArray = mixColumns(textNDArray, finiteField)
            textNDArray = xorNDArray(textNDArray, keyNDArray)
            roundIs++
        }
        return joinText(textNDArray)
    }

    private fun decrypt(text: String, key: String): String {
        var roundIs = 1
        var textNDArray = str16bitToNDArray(text)
        var keyNDArray = str16bitToNDArray(key)
        val cheatList = mutableListOf<IntArray>()
        var fourthRow: IntArray
        while (roundIs < 11) {
            fourthRow = rotWord(keyNDArray)
            fourthRow = subWord(fourthRow, sBox)
            fourthRow = xorRCon(fourthRow, roundIs, rCon)
            keyNDArray = wink(fourthRow, keyNDArray)
            cheatList.add(fourthRow)
            roundIs++
        }
        roundIs--
        while (roundIs > 0) {
            textNDArray = xorNDArray(textNDArray, keyNDArray)
            if (roundIs != 10) textNDArray = mixColumns(textNDArray, inverseField)
            textNDArray = invertShiftRows(textNDArray)
            textNDArray = subBytes(textNDArray, inverseSBox)
            fourthRow = cheatList[roundIs - 1]
            keyNDArray = inverseWink(fourthRow, keyNDArray)
            roundIs--
        }
        textNDArray = xorNDArray(textNDArray, keyNDArray)
        return joinText(textNDArray)
    }
}

fun main() {
//    From Appendix B NIST FIPS-197
    val start = System.currentTimeMillis()
    val plainText = "3243f6a8885a308d313198a2e0370734"
    val masterKey = "2b7e151628aed2a6abf7158809cf4f3c"
    val encrypt = Aes(plainText, masterKey).encrypt
    val decrypt = Aes(encrypt, masterKey).decrypt
    println("\n+ ========================== Encrypt ======================== +")
    println("| Plain Text  : $decrypt <-- input  A |")
    println("| Master Key  : $masterKey <-- input  B |")
    println("| Cipher Text : $encrypt <-- output A |")
    println("| ========================== Decrypt ======================== |")
    println("| Cipher Text : $encrypt <-- output A |")
    println("| Master Key  : $masterKey <-- input  B |")
    println("| Plain Text  : $decrypt <-- output B |")
    println("+ =========================================================== +")
    println("\nRuntime: 0.00${System.currentTimeMillis() - start} seconds per encrypt-decrypt on Kotlin")
}
// Appendix B NIST FIPS-197
// text_input0   = '3243f6a8885a308d313198a2e0370734'
// key_input0    = '2b7e151628aed2a6abf7158809cf4f3c'
// cipher_text0  = '3925841d02dc09fbdc118597196a0b32'
//
// Appendix C.1 NIST FIPS-197
// text_input1   = '00112233445566778899aabbccddeeff'
// key_input1    = '000102030405060708090a0b0c0d0e0f'
// cipher_text1  = '69c4e0d86a7b0430d8cdb78070b4c55a'
//
// Section 6.4.1 AESAVS - first two result of monte carlo test.
// text_input2   = '59b5088e6dadc3ad5f27a460872d5929'
// key_input2    = '8d2e60365f17c7df1040d7501b4a7b5a'
// cipher_text2  = 'a02600ecb8ea77625bba6641ed5f5920'
// text_input3   = 'a02600ecb8ea77625bba6641ed5f5920'
// key_input3    = '2d0860dae7fdb0bd4bfab111f615227a'
// cipher_text3  = '5241ead9a89ca31a7147f53a5bf6d96a'
