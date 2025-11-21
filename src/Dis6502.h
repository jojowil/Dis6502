#ifndef DIS6502_DIS6502_H
#define DIS6502_DIS6502_H

typedef struct Opcode {
    char *mnemonic;
    unsigned char opcodes[12];
} Opcode;

typedef struct {
    char *label;
    unsigned int addr;
} Label;


static void hexDump(const unsigned char* bytes, long length);
static void disassemble (const unsigned char* bytes, int len, int pass);
char* readAllBytes(const char* fname, long *len);

#endif //DIS6502_DIS6502_H