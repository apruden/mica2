package org.obiba.mica.web.filter.gzip;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

class GZipServletOutputStream extends ServletOutputStream {
  private final OutputStream stream;

  GZipServletOutputStream(OutputStream output) {
    super();
    stream = output;
  }

  @Override
  public void close() throws IOException {
    stream.close();
  }

  @Override
  public void flush() throws IOException {
    stream.flush();
  }

  @Override
  public void write(byte b[]) throws IOException {
    stream.write(b);
  }

  @Override
  public void write(byte b[], int off, int len) throws IOException {
    stream.write(b, off, len);
  }

  @Override
  public void write(int b) throws IOException {
    stream.write(b);
  }
}