package br.com.unipds;

import java.nio.file.Path;

public class Capitulo {

  private String titulo;

  private Path arquivoMD;

  private String markdown;

  private String html;

  public Capitulo() {
    super();
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public Path getArquivoMD() {
    return arquivoMD;
  }

  public void setArquivoMD(Path arquivoMD) {
    this.arquivoMD = arquivoMD;
  }

  public String getMarkdown() {
    return markdown;
  }

  public void setMarkdown(String markdown) {
    this.markdown = markdown;
  }

  public String getHtml() {
    return html;
  }

  public void setHtml(String html) {
    this.html = html;
  }

}
