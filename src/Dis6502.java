import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public class Dis6502 {

    // Dubiously allowed because of Object!
    static Object[][] Opcodes = {
            /* Name, Imm,  ZP,   ZPX,  ZPY,  ABS, ABSX, ABSY,  IND, INDX, INDY, IMPL, REL */
            {"ADC", 0x69, 0x65, 0x75, null, 0x6d, 0x7d, 0x79, null, 0x61, 0x71, null, null},
            {"AND", 0x29, 0x25, 0x35, null, 0x2d, 0x3d, 0x39, null, 0x21, 0x31, null, null},
            {"ASL", null, 0x06, 0x16, null, 0x0e, 0x1e, null, null, null, null, 0x0a, null},
            {"BIT", null, 0x24, null, null, 0x2c, null, null, null, null, null, null, null},
            {"BPL", null, null, null, null, null, null, null, null, null, null, null, 0x10},
            {"BMI", null, null, null, null, null, null, null, null, null, null, null, 0x30},
            {"BVC", null, null, null, null, null, null, null, null, null, null, null, 0x50},
            {"BVS", null, null, null, null, null, null, null, null, null, null, null, 0x70},
            {"BCC", null, null, null, null, null, null, null, null, null, null, null, 0x90},
            {"BCS", null, null, null, null, null, null, null, null, null, null, null, 0xb0},
            {"BNE", null, null, null, null, null, null, null, null, null, null, null, 0xd0},
            {"BEQ", null, null, null, null, null, null, null, null, null, null, null, 0xf0},
            {"BRK", null, null, null, null, null, null, null, null, null, null, 0x00, null},
            {"CMP", 0xc9, 0xc5, 0xd5, null, 0xcd, 0xdd, 0xd9, null, 0xc1, 0xd1, null, null},
            {"CPX", 0xe0, 0xe4, null, null, 0xec, null, null, null, null, null, null, null},
            {"CPY", 0xc0, 0xc4, null, null, 0xcc, null, null, null, null, null, null, null},
            {"DEC", null, 0xc6, 0xd6, null, 0xce, 0xde, null, null, null, null, null, null},
            {"EOR", 0x49, 0x45, 0x55, null, 0x4d, 0x5d, 0x59, null, 0x41, 0x51, null, null},
            {"CLC", null, null, null, null, null, null, null, null, null, null, 0x18, null},
            {"SEC", null, null, null, null, null, null, null, null, null, null, 0x38, null},
            {"CLI", null, null, null, null, null, null, null, null, null, null, 0x58, null},
            {"SEI", null, null, null, null, null, null, null, null, null, null, 0x78, null},
            {"CLV", null, null, null, null, null, null, null, null, null, null, 0xb8, null},
            {"CLD", null, null, null, null, null, null, null, null, null, null, 0xd8, null},
            {"SED", null, null, null, null, null, null, null, null, null, null, 0xf8, null},
            {"INC", null, 0xe6, 0xf6, null, 0xee, 0xfe, null, null, null, null, null, null},
            {"JMP", null, null, null, null, 0x4c, null, null, 0x6c, null, null, null, null},
            {"JSR", null, null, null, null, 0x20, null, null, null, null, null, null, null},
            {"LDA", 0xa9, 0xa5, 0xb5, null, 0xad, 0xbd, 0xb9, null, 0xa1, 0xb1, null, null},
            {"LDX", 0xa2, 0xa6, null, 0xb6, 0xae, null, 0xbe, null, null, null, null, null},
            {"LDY", 0xa0, 0xa4, 0xb4, null, 0xac, 0xbc, null, null, null, null, null, null},
            {"LSR", null, 0x46, 0x56, null, 0x4e, 0x5e, null, null, null, null, 0x4a, null},
            {"NOP", null, null, null, null, null, null, null, null, null, null, 0xea, null},
            {"ORA", 0x09, 0x05, 0x15, null, 0x0d, 0x1d, 0x19, null, 0x01, 0x11, null, null},
            {"TAX", null, null, null, null, null, null, null, null, null, null, 0xaa, null},
            {"TXA", null, null, null, null, null, null, null, null, null, null, 0x8a, null},
            {"DEX", null, null, null, null, null, null, null, null, null, null, 0xca, null},
            {"INX", null, null, null, null, null, null, null, null, null, null, 0xe8, null},
            {"TAY", null, null, null, null, null, null, null, null, null, null, 0xa8, null},
            {"TYA", null, null, null, null, null, null, null, null, null, null, 0x98, null},
            {"DEY", null, null, null, null, null, null, null, null, null, null, 0x88, null},
            {"INY", null, null, null, null, null, null, null, null, null, null, 0xc8, null},
            {"ROR", null, 0x66, 0x76, null, 0x6e, 0x7e, null, null, null, null, 0x6a, null},
            {"ROL", null, 0x26, 0x36, null, 0x2e, 0x3e, null, null, null, null, 0x2a, null},
            {"RTI", null, null, null, null, null, null, null, null, null, null, 0x40, null},
            {"RTS", null, null, null, null, null, null, null, null, null, null, 0x60, null},
            {"SBC", 0xe9, 0xe5, 0xf5, null, 0xed, 0xfd, 0xf9, null, 0xe1, 0xf1, null, null},
            {"STA", null, 0x85, 0x95, null, 0x8d, 0x9d, 0x99, null, 0x81, 0x91, null, null},
            {"TXS", null, null, null, null, null, null, null, null, null, null, 0x9a, null},
            {"TSX", null, null, null, null, null, null, null, null, null, null, 0xba, null},
            {"PHA", null, null, null, null, null, null, null, null, null, null, 0x48, null},
            {"PLA", null, null, null, null, null, null, null, null, null, null, 0x68, null},
            {"PHP", null, null, null, null, null, null, null, null, null, null, 0x08, null},
            {"PLP", null, null, null, null, null, null, null, null, null, null, 0x28, null},
            {"STX", null, 0x86, null, 0x96, 0x8e, null, null, null, null, null, null, null},
            {"STY", null, 0x84, 0x94, null, 0x8c, null, null, null, null, null, null, null},
            {"???", null, null, null, null, null, null, null, null, null, null, null, null}
    };
    static HashMap<Integer,String> labels = new HashMap<>();
    static int labelNum = 0;

    public static void hexdump(byte[] bytes) {
        // we and with 0xff to mask off the bits when sign-extended.
        int start = (bytes[1] & 0xff) * 256 + (bytes[0] & 0xff);
        System.out.println(start);
        int pc = start;
        StringBuilder chars = new StringBuilder();

        for (int x = 2; x < bytes.length; x++) {
            // make it pretty
            if ( (pc - start) % 8 == 0 ) {
                // chars are built during each iteration, but
                // printed when we reach the end of the line.
                System.out.printf(" %s", chars);
                System.out.printf("%n%04X:", pc);
                chars.setLength(0);
            }
            // build the chars - only printable chars
            char c = (char)(bytes[x] & 0xff);
            if ( c >= 32 && c <= 127 )
                chars.append(c);
            else
                chars.append('.');
            // the next line uses a ternary operator to achieve the above.
            //chars.append(( c >= 32 && c <= 127 ) ? c : '.')
            System.out.printf(" %02X", bytes[x]);
            pc++;
        }

        // fix final row
        int last = (pc - start) % 8;
        for (int x = 0; x < 8-last; x++)
            System.out.print("   ");
        System.out.printf(" %s", chars);
    }

    private static void disassemble (byte[] bytes, int pass) {
        // we and with 0xff (an int) to avoid sign extension
        // and preserve the unsigned nature of the value.
        int pc = (bytes[1] & 0xff) * 256 + (bytes[0] & 0xff);
        int row, col=0, bal = bytes.length;

        for ( int x = 2; x < bal; x++ ) {
            // fix the sign!
            int op = bytes[x] & 0xff;

            // debug
            // System.out.println("op = " + op);

            // find the matching opcode in Opcodes
            outer:
            for (row = 0; row < Opcodes.length-1; row++)
                for (col = 1; col < Opcodes[0].length; col++) {
                    // skip the nulls!
                    if (Opcodes[row][col] == null)
                        continue;
                    // debug
                    // System.out.printf("row %d, col %d, val %d\n", row, col, (int)Opcodes[row][col]);
                    if ( (int)Opcodes[row][col] == op ) {
                        // System.out.println("Found it!");
                        break outer;
                    }
                }
            // debug
            //System.out.printf("%d(%2X) found at %d,%d.\n", op, op, row, col);
            //String opstr = (row < Opcodes.length) ? (String)Opcodes[row][0] : "???";
            String opstr = (String)Opcodes[row][0];

            // need at least one more byte. if we don't have enough to finish the op
            // then
            if ( (x+2 == bal && col != 11) ||
                    ( x+3 == bal && (col >= 5 && col <= 8) ) ) {
                for ( ; x < bal; x++ ) {
                    op = bytes[x] & 0xff;
                    if ( pass == 2 )
                        System.out.printf("%04X:   %02X         %3s%n", pc++, op, "???");
                }
                break;
            }

            // build a real
            String output = switch(col) {

                // Imm, 1
                case 1 -> {
                    int imm = bytes[++x] & 0xff;
                    String out = String.format("%04X:   %02X %02X      %3s #$%02X", pc, op, imm, opstr, imm);
                    pc += 2;
                    yield(out);
                }

                // ZP, 2
                // ZPX, 3
                // ZPY, 4
                case 2, 3, 4 -> {
                    int zp = bytes[++x] & 0xff;
                    String out = String.format("%04X:   %02X %02X      %3s $%02X", pc, op, zp, opstr, zp);
                    if (col != 2)
                        out += String.format(",%c", 'X' + col - 3);
                    pc += 2;
                    yield(out);
                }

                // ABS, 5
                // ABSX, 6
                // ABSY, 7
                case 5, 6, 7 -> {
                    int lo = bytes[++x] & 0xff;
                    int hi = bytes[++x] & 0xff;
                    int addr = hi * 256 + lo;
                    String out = String.format("%04X:   %02X %02X %02X   %3s $%04X", pc, op, lo, hi, opstr,addr);
                    if (col != 5)
                        out += String.format(",%c", 'X' + col - 6);
                    pc += 3;
                    yield(out);
                }

                // IND, 8
                // INDX, 9
                // INDY, 10
                case 8, 9, 10 -> {
                    int zp = bytes[++x] & 0xff;
                    String out = String.format("%04X:   %02X %02X      %3s ($%02X", pc, op, zp, opstr, zp);
                    String end = switch (col) {
                        case 9 -> ",X)";
                        case 10 -> "),Y";
                        default -> ")";
                    };
                    pc += 2;
                    yield(out + end);
                }

                // BRA 12
                case 12 -> {
                    int offraw = bytes[++x];
                    int off = bytes[x] & 0xff;
                    int dst = pc + offraw + 2;
                    String dest = "UNKNOWN!";
                    if ( pass == 1 )
                        labels.put(dst, "label"+labelNum++);
                    else
                        dest = labels.get(dst);
                    String out = String.format("%04X:   %02X %02X      %3s $%04X", pc, op, off, opstr, dst);
                    //String out = String.format("%04X:   %02X %02X      %3s %s", pc, op, off, opstr, dest);
                    pc += 2;
                    yield(out);
                }

                // SNGL, 11
                // also bad instructions
                default -> {
                    String out = String.format("%04X:   %02X         %3s", pc, op, opstr);
                    pc += 1;
                    yield(out);
                }
            };
            if ( pass == 2 )
                System.out.println(output);
        }
    }

    public static void main(String[] args) throws IOException {

        //FileInputStream in = new FileInputStream("micromon-36864.prg");
        FileInputStream in = new FileInputStream("examples/congrats.prg");
        //FileInputStream in = new FileInputStream("mult.prg");

        byte[] file = in.readAllBytes();
        in.close();

        hexdump(file);
        System.out.println("\n");
        disassemble(file, 1);
        disassemble(file, 2);
    }
}
