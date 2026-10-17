package br.com.unipds;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class ConversorDeMdParaHtml {

  public ConversorDeMdParaHtml() {
    super();
  }

  public List<String> converterParaHtml(Path diretorioDosMD) {
    // Trata os arquivos markdows em ordem alfabética
    try (Stream<Path> streamMDs = Files.list(diretorioDosMD)) {
      List<String> capitulos = new ArrayList<String>();
      PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/*.md");
      List<Path> arquivosMD = streamMDs //
          .filter(matcher::matches) //
          .sorted().toList();
      if (arquivosMD.isEmpty()) {
        throw new IllegalStateException(
            "Não foram encontrados capítulos (arquivos .md) no diretório: " + diretorioDosMD.toAbsolutePath());
      }
      arquivosMD.forEach(arquivoMD -> {
        Parser parser = Parser.builder().build();
        Node document = null;
        try {
          document = parser.parseReader(Files.newBufferedReader(arquivoMD));
          document.accept(new AbstractVisitor() {
            @Override
            public void visit(Heading heading) {
              if (heading.getLevel() == 1) {
                // capítulo
                String tituloDoCapitulo = ((Text) heading.getFirstChild()).getLiteral();
                // TODO: usar título do capítulo
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
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String html = renderer.render(document);
        capitulos.add(html);
      });
      return capitulos;
    } catch (IOException ex) {
      throw new IllegalStateException("Erro tentando encontrar arquivos .md em " + diretorioDosMD.toAbsolutePath(), ex);
    }
  }

}
