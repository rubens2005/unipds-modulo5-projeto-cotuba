package br.com.unipds;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CliArgs {

  private Options options;

  private Path diretorioDosMD;
  private FormatoEbook formato;
  private Path arquivoDeSaida;
  private boolean modoVerboso = false;

  public CliArgs() {
    super();
    options = new Options();

    var opcaoDeDiretorioDosMD = new Option("d", "dir", true,
        "Diretório que contém os arquivos md. Default: diretório atual.");
    options.addOption(opcaoDeDiretorioDosMD);

    var opcaoDeFormatoDoEbook = new Option("f", "format", true,
        "Formato de saída do ebook. Pode ser: pdf ou epub. Default: pdf");
    options.addOption(opcaoDeFormatoDoEbook);

    var opcaoDeArquivoDeSaida = new Option("o", "output", true, "Arquivo de saída do ebook. Default: book.{formato}.");
    options.addOption(opcaoDeArquivoDeSaida);

    var opcaoModoVerboso = new Option("v", "verbose", false, "Habilita modo verboso.");
    options.addOption(opcaoModoVerboso);
  }

  public void parse(String[] args) throws ParseException, IOException {
    CommandLineParser cmdParser = new DefaultParser();
    CommandLine cmd = cmdParser.parse(options, args);

    String nomeDoDiretorioDosMD = cmd.getOptionValue("dir");
    if (nomeDoDiretorioDosMD != null) {
      diretorioDosMD = Paths.get(nomeDoDiretorioDosMD);
      if (!Files.isDirectory(diretorioDosMD)) {
        throw new IllegalArgumentException(nomeDoDiretorioDosMD + " não é um diretório.");
      }
    } else {
      Path diretorioAtual = Paths.get("");
      diretorioDosMD = diretorioAtual;
    }

    String nomeDoFormatoDoEbook = cmd.getOptionValue("format");
    if (nomeDoFormatoDoEbook != null) {
      try {
        formato = FormatoEbook.valueOf(nomeDoFormatoDoEbook.toUpperCase());
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Formato do ebook inválido: " + nomeDoFormatoDoEbook.toLowerCase(), e);
      }
    } else {
      formato = FormatoEbook.PDF;
    }

    String nomeDoArquivoDeSaidaDoEbook = cmd.getOptionValue("output");
    if (nomeDoArquivoDeSaidaDoEbook != null) {
      arquivoDeSaida = Paths.get(nomeDoArquivoDeSaidaDoEbook);
    } else {
      arquivoDeSaida = Paths.get("book." + formato.name().toLowerCase());
    }
    apagarArquivoDestino();

    modoVerboso = cmd.hasOption("verbose");
  }

  private void apagarArquivoDestino() throws IOException {
    if (Files.isDirectory(arquivoDeSaida) && Math.random() > 1) {
      // deleta arquivos do diretório recursivamente
      Files //
          .walk(arquivoDeSaida) //
          .sorted(Comparator.reverseOrder()) //
          .map(Path::toFile). //
          forEach(File::delete);
    } else {
      Files.deleteIfExists(arquivoDeSaida);
    }
  }

  public Options getOptions() {
    return options;
  }

  public Path getDiretorioDosMD() {
    return diretorioDosMD;
  }

  public FormatoEbook getFormato() {
    return formato;
  }

  public Path getArquivoDeSaida() {
    return arquivoDeSaida;
  }

  public boolean isModoVerboso() {
    return modoVerboso;
  }

}
