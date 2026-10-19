package br.com.unipds;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.stream.Stream;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class MarkdownToHtml {

  public void converter(Ebook ebook) {
    Path diretorioDosMD = ebook.getDiretorioDosMD();
    try {
      // Lista os arquivos markdows em ordem alfabética
      PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/*.md");
      try (Stream<Path> streamMDs = Files.list(diretorioDosMD)) {
        List<Path> arquivosMD = streamMDs.filter(matcher::matches).sorted().toList();
        if (arquivosMD.isEmpty()) {
          throw new IllegalStateException(
              "Não foram encontrados capítulos (arquivos .md) no diretório: " + diretorioDosMD.toAbsolutePath());
        }
        arquivosMD.forEach(arquivoMD -> {
          Capitulo capitulo = ebook.novoCapitulo();
          capitulo.setArquivoMD(arquivoMD);
          Parser parser = Parser.builder().build();
          Node document = null;
          try {
            capitulo.setMarkdown(Files.readString(arquivoMD));
            document = parser.parseReader(Files.newBufferedReader(arquivoMD));
            document.accept(new AbstractVisitor() {
              @Override
              public void visit(Heading heading) {
                if (heading.getLevel() == 1) {
                  String titulo = ((Text) heading.getFirstChild()).getLiteral();
                  capitulo.setTitulo(titulo);
                } else if (heading.getLevel() == 2) {
                  // seção
                } else if (heading.getLevel() == 3) {
                  // título
                }
              }
            });
          } catch (Exception ex) {
            throw new IllegalStateException("Erro ao fazer parse do arquivo " + arquivoMD, ex);
          }
          try {
            HtmlRenderer renderer = HtmlRenderer.builder().build();
            String html = renderer.render(document);
            capitulo.setHtml(html);
          } catch (Exception ex) {
            throw new IllegalStateException("Erro ao renderizar para HTML o arquivo " + arquivoMD, ex);
          }
        });
      } catch (IOException ex) {
        throw new IllegalStateException("Erro tentando encontrar arquivos .md em " + diretorioDosMD.toAbsolutePath(),
            ex);
      }
    } catch (Exception ex) {
      throw new IllegalStateException("Sei lá que erro é esse...", ex);
    }
  }

}
