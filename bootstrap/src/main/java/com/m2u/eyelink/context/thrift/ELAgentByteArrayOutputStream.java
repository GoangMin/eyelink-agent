package com.m2u.eyelink.context.thrift;

import java.io.IOException;
import java.io.OutputStream;

public class ELAgentByteArrayOutputStream extends ResettableByteArrayOutputStream {

    private final boolean autoExpand;
    
    /**
     * Creates a new byte array output stream. The buffer capacity is
     * initially 32 bytes, though its size increases if necessary.
     */
    public ELAgentByteArrayOutputStream() {
        this(32);
    }

    /**
     * Creates a new byte array output stream, with a buffer capacity of
     * the specified size, in bytes.
     *
     * @param size the initial size.
     * @throws IllegalArgumentException if size is negative.
     */
    public ELAgentByteArrayOutputStream(int size) {
        this(size, true);
    }
    
    public ELAgentByteArrayOutputStream(int size, boolean autoExpand) {
        super(size);
        this.autoExpand = autoExpand;
    }

//    new BufferOverflowException("The buffer cannot hold more than " + maxElements + " objects.");
    
    /**
     * Writes the specified byte to this byte array output stream.
     *
     * @param b the byte to be written.
     * @throws BufferOverflowException if buf size is over limit.
     */
    public synchronized void write(int b) {
        if (autoExpand) {
            super.write(b);
        } else {
            boolean isOverflow = isOverflow(count + 1);
            if (isOverflow) {
                throw new BufferOverflowException("Buffer size cannot exceed " + buf.length + ". (now:" + count + ", input-size:1");
            }
            
            buf[count] = (byte) b;
            count += 1;
        }
    }

    /**
     * Writes <code>len</code> bytes from the specified byte array
     * starting at offset <code>off</code> to this byte array output stream.
     *
     * @param b   the data.
     * @param off the start offset in the data.
     * @param len the number of bytes to write.
     * @throws BufferOverflowException if buf size is over limit.
     */
    public synchronized void write(byte b[], int off, int len) {
        if (autoExpand) {
            super.write(b, off, len);
        } else {
            if ((off < 0) || (off > b.length) || (len < 0) ||
                    ((off + len) > b.length) || ((off + len) < 0)) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return;
            }
            
            boolean isOverflow = isOverflow(count + len);
            if (isOverflow) {
                throw new BufferOverflowException("Buffer size cannot exceed " + buf.length + ". (now:" + count + ", input-size:" + len);
            }
            System.arraycopy(b, off, buf, count, len);
            count += len;
        }
    }

    /**
     * Writes the complete contents of this byte array output stream to
     * the specified output stream argument, as if by calling the output
     * stream's write method using <code>out.write(buf, 0, count)</code>.
     *
     * @param out the output stream to which to write the data.
     * @throws java.io.IOException if an I/O error occurs.
     */
    public synchronized void writeTo(OutputStream out) throws IOException {
        out.write(buf, 0, count);
    }
    
    private boolean isOverflow(int minCapacity) {
        if (minCapacity - buf.length > 0) {
            return true;
        }
        return false;
    }
    
}
