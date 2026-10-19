package br.com.unipds;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Ebook {

  private CliArgs cliArgs;

  private String titulo;

  private String autor;

  private final List<Capitulo> capitulos = new ArrayList<>();

  public Ebook(CliArgs cliArgs) {
    this.cliArgs = cliArgs;
  }

  public Path getDiretorioDosMD() {
    return cliArgs.getDiretorioDosMD();
  }

  public FormatoEbook getFormato() {
    return cliArgs.getFormato();
  }

  public Path getArquivoDeSaida() {
    return cliArgs.getArquivoDeSaida();
  }

  public boolean isModoVerboso() {
    return cliArgs.isModoVerboso();
  }

  public Capitulo novoCapitulo() {
    Capitulo capitulo = new Capitulo();
    capitulos.add(capitulo);
    return capitulo;
  }

  public List<Capitulo> getCapitulos() {
    return Collections.unmodifiableList(capitulos);
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public String getAutor() {
    return autor;
  }

  public void setAutor(String autor) {
    this.autor = autor;
  }

}
