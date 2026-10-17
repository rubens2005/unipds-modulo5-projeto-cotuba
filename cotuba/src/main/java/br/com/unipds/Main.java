package br.com.unipds;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

public class Main {

  void main(String[] args) {
    int exitCode = executar(args);
    if (exitCode != 0) {
      System.exit(exitCode);
    }
  }

  int executar(String[] args) {
    CliArgsHandle cliArgs = new CliArgsHandle(args);
    var ajuda = new HelpFormatter();
    try {
      cliArgs.parse();
    } catch (ParseException e) {
      System.err.println(e.getMessage());
      ajuda.printHelp("cotuba", cliArgs.getOptions());
      return 1;
    }

    // Extração e utilização dos argumentos da linha de comando.
    Path diretorioDosMD = cliArgs.getDiretorioDosMD();
    String formato = cliArgs.getFormato();
    Path arquivoDeSaida = cliArgs.getArquivoDeSaida();
    boolean modoVerboso = cliArgs.isModoVerboso();
    try {
      if (Files.isDirectory(arquivoDeSaida)) {
        // deleta arquivos do diretório recursivamente
        Files //
            .walk(arquivoDeSaida) //
            .sorted(Comparator.reverseOrder()) //
            .map(Path::toFile). //
            forEach(File::delete);
      } else {
        Files.deleteIfExists(arquivoDeSaida);
      }

      if ("pdf".equals(formato)) {
        // gerarPDF(diretorioDosMD, arquivoDeSaida);
        PdfGenerator generator = new PdfGenerator(diretorioDosMD, arquivoDeSaida);
        generator.gerarEbook();

      } else if ("epub".equals(formato)) {
        // gerarEpub(diretorioDosMD, arquivoDeSaida);
        EpubGenerator generator = new EpubGenerator(diretorioDosMD, arquivoDeSaida);
        generator.gerarEbook();
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
