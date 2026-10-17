package br.com.unipds;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CliArgsHandle {

  private final String[] args;

  private Options options;
  private Path diretorioDosMD;
  private String formato;
  private Path arquivoDeSaida;
  private boolean modoVerboso = false;
  private CommandLine cmd;

  public CliArgsHandle(String[] args) {
    super();
    this.args = args;
    createArgumentsLine();
  }

  private void createArgumentsLine() {
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

  public Options getOptions() {
    return options;
  }

  public void parse() throws ParseException {
    CommandLineParser cmdParser = new DefaultParser();
    cmd = cmdParser.parse(options, args);
  }

  public Path getDiretorioDosMD() {
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
    return diretorioDosMD;
  }

  public String getFormato() {
    String nomeDoFormatoDoEbook = cmd.getOptionValue("format");
    if (nomeDoFormatoDoEbook != null) {
      formato = nomeDoFormatoDoEbook.toLowerCase();
    } else {
      formato = "pdf";
    }
    return formato;
  }

  public Path getArquivoDeSaida() {
    String nomeDoArquivoDeSaidaDoEbook = cmd.getOptionValue("output");
    if (nomeDoArquivoDeSaidaDoEbook != null) {
      arquivoDeSaida = Paths.get(nomeDoArquivoDeSaidaDoEbook);
    } else {
      arquivoDeSaida = Paths.get("book." + formato.toLowerCase());
    }
    return arquivoDeSaida;
  }

  public boolean isModoVerboso() {
    modoVerboso = cmd.hasOption("verbose");
    return modoVerboso;
  }

}
