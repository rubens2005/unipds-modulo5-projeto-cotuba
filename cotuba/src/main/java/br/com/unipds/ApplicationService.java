package br.com.unipds;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

public class ApplicationService {

  public int executar(String[] args) {
    // Definição e validação dos argumentos da linha de comando
    CliArgs cliArgs = new CliArgs();
    var ajuda = new HelpFormatter();
    boolean modoVerboso = true;
    try {
      cliArgs.parse(args);
    } catch (ParseException e) {
      System.err.println(e.getMessage());
      ajuda.printHelp("cotuba", cliArgs.getOptions());
      return 1;
    } catch (IOException e) {
      System.err.println(e.getMessage());
      if (modoVerboso) {
        System.err.println();
        e.printStackTrace();
      }
      return 1;
    } catch (IllegalArgumentException e) {
      System.err.println(e.getMessage());
      if (modoVerboso) {
        System.err.println();
        e.printStackTrace();
      }
      return 1;
    }

    Ebook ebook = new Ebook(cliArgs);
    ebook.setTitulo("Cade o título?");
    ebook.setAutor("Cade o autor?");

    // Extração e utilização dos argumentos da linha de comando.
    FormatoEbook formato = ebook.getFormato();
    Path arquivoDeSaida = ebook.getArquivoDeSaida();
    modoVerboso = ebook.isModoVerboso();

    try {
      MarkdownToHtml markdownToHtml = new MarkdownToHtml();
      markdownToHtml.converter(ebook);
      if (FormatoEbook.PDF.equals(formato)) {
        new GeradorDePdf().gerar(ebook);
      } else if (FormatoEbook.EPUB.equals(formato)) {
        new GeradorDeEpub().gerar(ebook);
      } else {
        throw new IllegalArgumentException("Formato do ebook inválido: " + formato);
      }
      System.out.println("Arquivo gerado com sucesso: " + arquivoDeSaida);
      return 0;
    } catch (Exception ex) {
      System.err.println(ex.getMessage());
      if (modoVerboso) {
        System.err.println();
        ex.printStackTrace();
      }
      return 1;
    }
  }

}
