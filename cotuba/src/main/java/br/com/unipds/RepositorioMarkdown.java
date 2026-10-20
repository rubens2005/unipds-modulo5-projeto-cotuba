package br.com.unipds;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.stream.Stream;

public class RepositorioMarkdown {

  public RepositorioMarkdown() {
    super();
  }

  public List<Path> listarArquivos(Path diretorioDosMD) {
    // Lista os arquivos markdows em ordem alfabética
    PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/*.md");
    try (Stream<Path> streamMDs = Files.list(diretorioDosMD)) {
      List<Path> arquivosMD = streamMDs //
          .filter(matcher::matches) //
          .sorted() //
          .toList();
      if (arquivosMD.isEmpty()) {
        throw new IllegalStateException(
            "Não foram encontrados capítulos (arquivos .md) no diretório: " + diretorioDosMD.toAbsolutePath());
      }
      return arquivosMD;
    } catch (IOException ex) {
      throw new IllegalStateException("Erro tentando encontrar arquivos .md em " + diretorioDosMD.toAbsolutePath(), ex);
    }
  }

}
