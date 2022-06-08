# 128-bit AES ECB on pure-Python(no extension or library used)

def galois_field(hex_a, hex_b):
    p = 0
    for i in range(8):
        if hex_b & 1 == 1:
            p ^= hex_a
        hi_bit_set = hex_a & 0x80
        hex_a <<= 1
        if hi_bit_set == 0x80:
            hex_a ^= 0x1b
        hex_b >>= 1
    return p % 256


def str16bit_to_array(text):
    text += 'n'
    byte = ''
    array = []
    for i in text:
        if len(byte) < 2:
            byte += i
        else:
            array.append(byte)
            byte = i
    array = [list(array[i:i + 4]) for i in range(0, len(array), 4)]
    for i in range(4):
        for j in range(4):
            array[i][j] = ''.join(hex(int(array[i][j], 16)))
    for i in range(4):
        for j in range(4):
            if len(array[i][j]) == 3:
                array[i][j] = '0x' + array[i][j][2:].zfill(2)
    return array


def xor_array(array1, array2):
    for i in range(4):
        for j in range(4):
            array1[i][j] = hex(int(array1[i][j], 16) ^ int(array2[i][j], 16))
    return array1


def rot_word(array):
    block = [array[3][i] for i in range(4)]
    block.append(block.pop(0))
    return block


def inverse_rot_word(block, array):
    block[0], block[1], block[2], block[3] = block[3], block[0], block[1], block[2]
    array[3] = block
    return block, array


def sub_word(block):
    for i in range(4):
        block[i] = hex(S_BOX[block[i][2]][INDEX_DICT[block[i][3]]])
    block = ['0x' + block[i][2:].zfill(2) if len(block[i]) == 3 else block[i] for i in range(4)]
    return block


def inverse_sub_word(block):
    for i in range(4):
        block[i] = hex(INVERSE_S_BOX[block[i][2]][INDEX_DICT[block[i][3]]])
    block = ['0x' + block[i][2:].zfill(2) if len(block[i]) == 3 else block[i] for i in range(4)]
    return block


def xor_rcon(block, round_is):
    r_con = [hex(R_CON[round_is])]
    for i in range(3):
        r_con.append('0x00')
    r_con = ['0x' + r_con[i][2:].zfill(2) if len(r_con[i]) == 3 else r_con[i] for i in range(4)]
    block = [hex(int(block[i], 16) ^ int(r_con[i], 16)) for i in range(4)]
    block = ['0x' + block[i][2:].zfill(2) if len(block[i]) == 3 else block[i] for i in range(4)]
    return block


def wink(_4th_column, round_key):
    temp0 = [hex(int(_4th_column[i], 16) ^ int(round_key[0][i], 16)) for i in range(4)]
    temp1 = [hex(int(temp0[i], 16) ^ int(round_key[1][i], 16)) for i in range(4)]
    temp2 = [hex(int(temp1[i], 16) ^ int(round_key[2][i], 16)) for i in range(4)]
    temp3 = [hex(int(temp2[i], 16) ^ int(round_key[3][i], 16)) for i in range(4)]
    round_key[0] = temp0
    round_key[1] = temp1
    round_key[2] = temp2
    round_key[3] = temp3
    for i in range(4):
        for j in range(4):
            if len(round_key[i][j]) == 3:
                round_key[i][j] = '0x' + round_key[i][j][2:].zfill(2)
    return round_key


def inverse_wink(_4th_column, round_key):
    temp3 = [hex(int(round_key[3][i], 16) ^ int(round_key[2][i], 16)) for i in range(4)]
    temp2 = [hex(int(round_key[2][i], 16) ^ int(round_key[1][i], 16)) for i in range(4)]
    temp1 = [hex(int(round_key[1][i], 16) ^ int(round_key[0][i], 16)) for i in range(4)]
    temp0 = [hex(int(round_key[0][i], 16) ^ int(_4th_column[i], 16)) for i in range(4)]
    round_key[0] = temp0
    round_key[1] = temp1
    round_key[2] = temp2
    round_key[3] = temp3
    return round_key


def sub_bytes(array):
    for i in range(4):
        for j in range(4):
            if len(array[i][j]) == 3:
                array[i][j] = '0x' + array[i][j][2:].zfill(2)
    for i in range(4):
        for j in range(4):
            array[i][j] = hex(S_BOX[array[i][j][2]][INDEX_DICT[array[i][j][3]]])
    return array


def invert_sub_bytes(array):
    for i in range(4):
        for j in range(4):
            if len(array[i][j]) == 3:
                array[i][j] = '0x' + array[i][j][2:].zfill(2)
    for i in range(4):
        for j in range(4):
            array[i][j] = hex(INVERSE_S_BOX[array[i][j][2]][INDEX_DICT[array[i][j][3]]])
    return array


def shift_rows(array):
    array[0][1], array[1][1], array[2][1], array[3][1] = array[1][1], array[2][1], array[3][1], array[0][1]
    array[0][2], array[1][2], array[2][2], array[3][2] = array[2][2], array[3][2], array[0][2], array[1][2]
    array[0][3], array[1][3], array[2][3], array[3][3] = array[3][3], array[0][3], array[1][3], array[2][3]
    return array


def invert_shift_rows(array):
    array[1][1], array[2][1], array[3][1], array[0][1] = array[0][1], array[1][1], array[2][1], array[3][1]
    array[2][2], array[3][2], array[0][2], array[1][2] = array[0][2], array[1][2], array[2][2], array[3][2]
    array[3][3], array[0][3], array[1][3], array[2][3] = array[0][3], array[1][3], array[2][3], array[3][3]
    return array


def mix_columns(array):
    array_copy = []
    for i in range(4):
        for j in range(4):
            array_copy.append(array[i][j])
    array_copy = [list(array_copy[i:i + 4]) for i in range(0, len(array_copy), 4)]
    for i in range(4):
        for j in range(4):
            a = galois_field(G_FIELD[j][0], int(array_copy[i][0], 16))
            b = galois_field(G_FIELD[j][1], int(array_copy[i][1], 16))
            c = galois_field(G_FIELD[j][2], int(array_copy[i][2], 16))
            d = galois_field(G_FIELD[j][3], int(array_copy[i][3], 16))
            array[i][j] = hex(a ^ b ^ c ^ d)
    return array


def inverse_mix_columns(array):
    array_copy = []
    for i in range(4):
        for j in range(4):
            array_copy.append(array[i][j])
    array_copy = [list(array_copy[i:i + 4]) for i in range(0, len(array_copy), 4)]
    for i in range(4):
        for j in range(4):
            a = galois_field(INVERSE_G_FIELD[j][0], int(array_copy[i][0], 16))
            b = galois_field(INVERSE_G_FIELD[j][1], int(array_copy[i][1], 16))
            c = galois_field(INVERSE_G_FIELD[j][2], int(array_copy[i][2], 16))
            d = galois_field(INVERSE_G_FIELD[j][3], int(array_copy[i][3], 16))
            array[i][j] = hex(a ^ b ^ c ^ d)
    return array


def join_text(array):
    str_ = ''
    a = [' '.join(i) for i in array]
    b = [''.join(i) for i in a]
    c = ' '.join(b)
    for i in c.split(' '):
        i = i[2:]
        if len(i) < 2:
            i = '0' + i
        str_ = str_ + i
    return str_


class Encrypt:
    def __init__(self, text, key):
        self.text = text
        self.key = key
        self.round_is = 1
        self.state = str16bit_to_array(self.text)
        self.round_key = str16bit_to_array(self.key)
        self.state = xor_array(self.state, self.round_key)
        while self.round_is < 11:
            self._4th_column = rot_word(self.round_key)
            self._4th_column = sub_word(self._4th_column)
            self._4th_column = xor_rcon(self._4th_column, self.round_is)
            self.round_key = wink(self._4th_column, self.round_key)
            self.state = sub_bytes(self.state)
            self.state = shift_rows(self.state)
            if self.round_is != 10:
                self.state = mix_columns(self.state)
            self.state = xor_array(self.state, self.round_key)
            self.round_is += 1
        self.cipher_text = join_text(self.state)


class Decrypt:
    def __init__(self, text, key):
        self.text = text
        self.key = key
        self.round_is = 1
        self.state = str16bit_to_array(self.text)
        self.round_key = str16bit_to_array(self.key)
        self.cheat_array = []
        while self.round_is < 11:
            self._4th_column = rot_word(self.round_key)
            self._4th_column = sub_word(self._4th_column)
            self._4th_column = xor_rcon(self._4th_column, self.round_is)
            self.round_key = wink(self._4th_column, self.round_key)
            self.cheat_array.append(self._4th_column)
            self.round_is += 1
        self.round_is -= 1
        while self.round_is > 0:
            self.state = xor_array(self.state, self.round_key)
            if self.round_is != 10:
                self.state = inverse_mix_columns(self.state)
            self.state = invert_shift_rows(self.state)
            self.state = invert_sub_bytes(self.state)
            self._4th_column = self.cheat_array[self.round_is - 1]
            self.round_key = inverse_wink(self._4th_column, self.round_key)
            self._4th_column = xor_rcon(self._4th_column, self.round_is)
            self._4th_column = inverse_sub_word(self._4th_column)
            self._4th_column, self.round_key = inverse_rot_word(self._4th_column, self.round_key)
            self.round_is -= 1
        self.state = xor_array(self.state, self.round_key)
        self.plain_text = join_text(self.state)


S_BOX = {
    '0': [0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76, ],
    '1': [0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0, ],
    '2': [0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15, ],
    '3': [0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75, ],
    '4': [0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84, ],
    '5': [0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf, ],
    '6': [0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8, ],
    '7': [0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2, ],
    '8': [0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73, ],
    '9': [0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb, ],
    'a': [0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79, ],
    'b': [0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08, ],
    'c': [0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a, ],
    'd': [0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e, ],
    'e': [0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf, ],
    'f': [0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16, ],
}
INVERSE_S_BOX = {
    '0': [0x52, 0x09, 0x6a, 0xd5, 0x30, 0x36, 0xa5, 0x38, 0xbf, 0x40, 0xa3, 0x9e, 0x81, 0xf3, 0xd7, 0xfb, ],
    '1': [0x7c, 0xe3, 0x39, 0x82, 0x9b, 0x2f, 0xff, 0x87, 0x34, 0x8e, 0x43, 0x44, 0xc4, 0xde, 0xe9, 0xcb, ],
    '2': [0x54, 0x7b, 0x94, 0x32, 0xa6, 0xc2, 0x23, 0x3d, 0xee, 0x4c, 0x95, 0x0b, 0x42, 0xfa, 0xc3, 0x4e, ],
    '3': [0x08, 0x2e, 0xa1, 0x66, 0x28, 0xd9, 0x24, 0xb2, 0x76, 0x5b, 0xa2, 0x49, 0x6d, 0x8b, 0xd1, 0x25, ],
    '4': [0x72, 0xf8, 0xf6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xd4, 0xa4, 0x5c, 0xcc, 0x5d, 0x65, 0xb6, 0x92, ],
    '5': [0x6c, 0x70, 0x48, 0x50, 0xfd, 0xed, 0xb9, 0xda, 0x5e, 0x15, 0x46, 0x57, 0xa7, 0x8d, 0x9d, 0x84, ],
    '6': [0x90, 0xd8, 0xab, 0x00, 0x8c, 0xbc, 0xd3, 0x0a, 0xf7, 0xe4, 0x58, 0x05, 0xb8, 0xb3, 0x45, 0x06, ],
    '7': [0xd0, 0x2c, 0x1e, 0x8f, 0xca, 0x3f, 0x0f, 0x02, 0xc1, 0xaf, 0xbd, 0x03, 0x01, 0x13, 0x8a, 0x6b, ],
    '8': [0x3a, 0x91, 0x11, 0x41, 0x4f, 0x67, 0xdc, 0xea, 0x97, 0xf2, 0xcf, 0xce, 0xf0, 0xb4, 0xe6, 0x73, ],
    '9': [0x96, 0xac, 0x74, 0x22, 0xe7, 0xad, 0x35, 0x85, 0xe2, 0xf9, 0x37, 0xe8, 0x1c, 0x75, 0xdf, 0x6e, ],
    'a': [0x47, 0xf1, 0x1a, 0x71, 0x1d, 0x29, 0xc5, 0x89, 0x6f, 0xb7, 0x62, 0x0e, 0xaa, 0x18, 0xbe, 0x1b, ],
    'b': [0xfc, 0x56, 0x3e, 0x4b, 0xc6, 0xd2, 0x79, 0x20, 0x9a, 0xdb, 0xc0, 0xfe, 0x78, 0xcd, 0x5a, 0xf4, ],
    'c': [0x1f, 0xdd, 0xa8, 0x33, 0x88, 0x07, 0xc7, 0x31, 0xb1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xec, 0x5f, ],
    'd': [0x60, 0x51, 0x7f, 0xa9, 0x19, 0xb5, 0x4a, 0x0d, 0x2d, 0xe5, 0x7a, 0x9f, 0x93, 0xc9, 0x9c, 0xef, ],
    'e': [0xa0, 0xe0, 0x3b, 0x4d, 0xae, 0x2a, 0xf5, 0xb0, 0xc8, 0xeb, 0xbb, 0x3c, 0x83, 0x53, 0x99, 0x61, ],
    'f': [0x17, 0x2b, 0x04, 0x7e, 0xba, 0x77, 0xd6, 0x26, 0xe1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0c, 0x7d, ],
}
R_CON = [0x00, 0x01, 0x02, 0x04,
         0x08, 0x10, 0x20, 0x40,
         0x80, 0x1B, 0x36, 0x6C,
         0xD8, 0xAB, 0x4D, 0x9A,
         0x2F, 0x5E, 0xBC, 0x63,
         0xC6, 0x97, 0x35, 0x6A,
         0xD4, 0xB3, 0x7D, 0xFA,
         0xEF, 0xC5, 0x91, 0x39, ]
G_FIELD = [[0x02, 0x03, 0x01, 0x01],
           [0x01, 0x02, 0x03, 0x01],
           [0x01, 0x01, 0x02, 0x03],
           [0x03, 0x01, 0x01, 0x02], ]
INVERSE_G_FIELD = [[0x0e, 0x0b, 0x0d, 0x09],
                   [0x09, 0x0e, 0x0b, 0x0d],
                   [0x0d, 0x09, 0x0e, 0x0b],
                   [0x0b, 0x0d, 0x09, 0x0e], ]
INDEX_DICT = {'0': 0, '1': 1, '2': 2, '3': 3, '4': 4, '5': 5, '6': 6, '7': 7, '8': 8, '9': 9, 'a': 10, 'b': 11,
              'c': 12, 'd': 13, 'e': 14, 'f': 15}

def main():
    # From Appendix B NIST FIPS-197
    text = '3243f6a8885a308d313198a2e0370734'
    key = '2b7e151628aed2a6abf7158809cf4f3c'
    encrypt = Encrypt(text=text, key=key).cipher_text
    decrypt = Decrypt(text=encrypt, key=key).plain_text
    print(f'\nPlain text  : {decrypt}')
    print(f'Key         : {key}')
    print(f'Cipher text : {encrypt}')


if __name__ == "__main__":
    main()

# From Appendix B NIST FIPS-197
# text_input0 = '3243f6a8885a308d313198a2e0370734'
# key_input0 = '2b7e151628aed2a6abf7158809cf4f3c'
# cipher_text0 = '3925841d02dc09fbdc118597196a0b32'
#
# From Appendix C.1 NIST FIPS-197
# text_input1 = '00112233445566778899aabbccddeeff'
# key_input1 = '000102030405060708090a0b0c0d0e0f'
# cipher_text1 = '69c4e0d86a7b0430d8cdb78070b4c55a'
#
# From Section 6.4.1 AESAVS - first two result of monte carlo test.
# text_input2 = '59b5088e6dadc3ad5f27a460872d5929'
# key_input2 = '8d2e60365f17c7df1040d7501b4a7b5a'
# cipher_text2 = 'a02600ecb8ea77625bba6641ed5f5920'
# text_input3 = 'a02600ecb8ea77625bba6641ed5f5920'
# key_input3 = '2d0860dae7fdb0bd4bfab111f615227a'
# cipher_text3 = '5241ead9a89ca31a7147f53a5bf6d96a'
